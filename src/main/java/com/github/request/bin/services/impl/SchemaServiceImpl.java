package com.github.request.bin.services.impl;

import com.github.request.bin.data.Attribute;
import com.github.request.bin.data.Schema;
import com.github.request.bin.services.SchemaService;
import com.github.request.bin.utils.LogThis;
import com.github.request.bin.utils.RegexFactory;
import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SchemaServiceImpl implements SchemaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaServiceImpl.class);
    private static final List<Method> UNPACKED;
    private static final Map<String, Map<String, Optional<Method>>> LOOKUP;
    private static final Function<Method, String> PARAMS_EXTRACTOR = method -> String.format("(%s)", method.getParameterCount() == 0 ? "" : Arrays.stream(method.getParameters()).map(Parameter::getType)
            .map(Class::getSimpleName).map(String::toLowerCase).collect(Collectors.joining(",")));
    private static final Function<Method, String> CLASS_METHOD_EXTRACTOR = method -> String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName());

    private static final Faker FAKER = new Faker(Locale.US, new RandomService());

    static {
        UNPACKED = unpackClass(FAKER.getClass());
        LOOKUP = buildLookup();
    }

    private final Schema schema;

    public SchemaServiceImpl(Schema schema) {
        this.schema = schema;
    }

    private static List<Method> unpackClass(Class<?> aClass) {
        return Stream.of(aClass.getDeclaredMethods())
                .filter(aMethod -> aMethod.getModifiers() == Modifier.PUBLIC)
                .filter(aMethod -> aMethod.getDeclaringClass().getPackage() == FAKER.getClass().getPackage())
                //TODO Add support for DateAndTime and List<String>
                //NOTE: with params: 392/395/398, without params: 343.
                //.filter(aMethod -> aMethod.getParameterCount() == 0)
                .map(aMethod -> {
                    if ((aMethod.getReturnType().isPrimitive() || aMethod.getReturnType() == String.class || aMethod.getReturnType() == List.class || aMethod.getReturnType() == Date.class)
                            && Stream.of(aMethod.getParameterTypes()).noneMatch(Class::isEnum)) {
                        return Collections.singletonList(aMethod);
                    }
                    return unpackClass(aMethod.getReturnType());
                }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private static Map<String, Map<String, Optional<Method>>> buildLookup() {
        Map<String, Map<String, Optional<Method>>> parent = new TreeMap<>();
        for (Method method : SchemaServiceImpl.UNPACKED) {
            Map<String, Optional<Method>> child = new TreeMap<>();
            if (null != parent.get(CLASS_METHOD_EXTRACTOR.apply(method))) {
                child = parent.get(CLASS_METHOD_EXTRACTOR.apply(method));
            }
            child.put(PARAMS_EXTRACTOR.apply(method), Optional.ofNullable(method));
            parent.put(CLASS_METHOD_EXTRACTOR.apply(method), child);
        }
        return parent;
    }

    @LogThis
    @Override
    public List<Attribute> attributes() {
        return schema.getAttributes().stream().filter(Attribute::isEnabled)
                .sorted(Comparator.comparing(Attribute::getName)).collect(Collectors.toList());
    }

    @LogThis
    @Override
    public Map<String, Object> resolve(Map<String, Object> schema) {
        return resolveKeys(resolveValues(schema));
    }

    private Map<String, Object> resolveValues(Map<String, Object> schema) {
        return schema.entrySet().stream().map(mEntry -> {
            if (mEntry.getValue() instanceof Map) {
                return Collections.singletonMap(mEntry.getKey(), resolveValues((Map<String, Object>) mEntry.getValue()));
            } else if(mEntry.getValue() instanceof List) {
                return Collections.singletonMap(mEntry.getKey(), ((List<Map<String, Object>>) mEntry.getValue()).stream().map(this::resolve).collect(Collectors.toList()));
            } else if (mEntry.getValue() instanceof String) {
                String value = (String) mEntry.getValue();
                Matcher methodMatcher = RegexFactory.VALUE_FINDER.matcher(value);
                while (methodMatcher.find()) {
                    value = value.replace(methodMatcher.group(), resolveValue(methodMatcher.group()));
                }
                return Collections.singletonMap(mEntry.getKey(), value);
            }
            return Collections.singletonMap(mEntry.getKey(), mEntry.getValue());
        }).map(Map::entrySet).flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, LinkedHashMap::new));
    }

    private Map<String, Object> resolveKeys(Map<String, Object> payload) {
        return payload.entrySet().stream().map(mEntry -> {
            if (mEntry.getValue() instanceof Map) {
                return Collections.singletonMap(mEntry.getKey(), resolveKeys((Map<String, Object>) mEntry.getValue()));
            } else if (mEntry.getValue() instanceof String) {
                String value = (String) mEntry.getValue();
                Matcher methodMatcher = RegexFactory.KEY_FINDER.matcher(value);
                while (methodMatcher.find()) {
                    value = value.replace(methodMatcher.group(), resolveKey(methodMatcher.group(), payload));
                }
                return Collections.singletonMap(mEntry.getKey(), value);
            }
            return Collections.singletonMap(mEntry.getKey(), mEntry.getValue());
        }).map(Map::entrySet).flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, LinkedHashMap::new));
    }

    private String resolveKey(String key, Map<String, Object> payload) {
        Matcher keyMatcher = RegexFactory.KEY_FINDER.matcher(key);
        if (keyMatcher.matches()) {
            if (StringUtils.isNotBlank(keyMatcher.group(3))) {
                return resolveKey(key.replace(keyMatcher.group(1), keyMatcher.group(3)),
                        (Map<String, Object>) payload.getOrDefault(keyMatcher.group(2), new HashMap<>()));
            }
            return String.valueOf(payload.get(keyMatcher.group(1)));
        }
        return key;
    }

    private String resolveValue(String value) {
        Matcher valueMatcher = RegexFactory.VALUE_FINDER.matcher(value);
        if (valueMatcher.matches()) {
            String outerKey = valueMatcher.group(1);
            String innerKey = valueMatcher.group(2);
            return resolveByInvocation(outerKey, innerKey);
        }
        return value;
    }

    private String resolveByInvocation(String outerKey, String innerKey) {
        try {
            if (StringUtils.isBlank(innerKey)) {
                Optional<Method> foundMethod = LOOKUP.getOrDefault(outerKey, new HashMap<>()).getOrDefault("()", Optional.empty());
                if (foundMethod.isPresent()) {
                    Object instance = FAKER;
                    if (foundMethod.get().getDeclaringClass() != Faker.class) {
                        Constructor<?> constructor = ReflectionUtils.accessibleConstructor(foundMethod.get().getDeclaringClass(), Faker.class);
                        instance = constructor.getParameterCount() == 1 ? constructor.newInstance(FAKER) : constructor.newInstance();
                    }
                    return String.valueOf(ReflectionUtils.invokeMethod(foundMethod.get(), instance));
                }
            } else {
                String[] params = innerKey.split(",", -1);
                String paramsKey = Stream.of(params).map(this::castMyType).map(this::findMyType)
                        .map(Class::getSimpleName).map(String::toLowerCase).collect(Collectors.joining(",", "(", ")"));
                Optional<Method> foundMethod = LOOKUP.getOrDefault(outerKey, new HashMap<>()).getOrDefault(paramsKey, Optional.empty());
                if (foundMethod.isPresent()) {
                    Object instance = FAKER;
                    if (foundMethod.get().getDeclaringClass() != Faker.class) {
                        Constructor<?> constructor = ReflectionUtils.accessibleConstructor(foundMethod.get().getDeclaringClass(), Faker.class);
                        instance = constructor.getParameterCount() == 1 ? constructor.newInstance(FAKER) : constructor.newInstance();
                    }
                    return String.valueOf(ReflectionUtils.invokeMethod(foundMethod.get(), instance, Stream.of(params).map(String::trim).map(this::castMyType).toArray(Object[]::new)));
                }
            }
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cannot invoke method and resolve. Stacktrace: ", e);
            }
        }
        return StringUtils.EMPTY;
    }

    private Class<?> findMyType(Object object) {
        if (object instanceof Boolean) {
            return boolean.class;
        } else if (object instanceof Integer) {
            return int.class;
        } else if (object instanceof Long) {
            return long.class;
        } else if (object instanceof Double) {
            return double.class;
        } else if (object instanceof String) {
            return String.class;
        }
        return String.class;
    }

    private Object castMyType(String value) {
        if (RegexFactory.BOOLEANS.matcher(value).matches()) {
            if (value.equalsIgnoreCase(Boolean.TRUE.toString())) {
                return Boolean.TRUE;
            } else if (value.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return Boolean.FALSE;
            }
        } else if (RegexFactory.NUMBERS.matcher(value).matches()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
            }
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {
            }
        } else if (RegexFactory.DECIMALS.matcher(value).matches()) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException ignored) {
            }
        } else if (RegexFactory.STRINGS.matcher(value).matches()) {
            return StringUtils.replaceEachRepeatedly(value, new String[]{"\"", "'"}, new String[]{StringUtils.EMPTY, StringUtils.EMPTY});
        }
        return StringUtils.replaceEachRepeatedly(value, new String[]{"\"", "'"}, new String[]{StringUtils.EMPTY, StringUtils.EMPTY});
    }
}
