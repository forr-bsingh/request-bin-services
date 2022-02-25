package com.github.request.bin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PackageUtil {

    public static final Logger logger = LoggerFactory.getLogger(PackageUtil.class);

    private PackageUtil() throws IllegalAccessException {
        throw new IllegalAccessException("For static use only");
    }

    public static final List<Class<?>> scanByAnnotation(String packageName, List<Class<? extends Annotation>> includeClasses, List<Class<? extends Annotation>> excludeClasses) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        excludeClasses.forEach(c -> scanner.addExcludeFilter(new AnnotationTypeFilter(c)));
        includeClasses.forEach(c -> scanner.addIncludeFilter(new AnnotationTypeFilter(c)));

        return scanner.findCandidateComponents(packageName).stream().map(rb -> {
            try {
                return cl.loadClass(rb.getBeanClassName());
            } catch (ClassNotFoundException e) {
                logger.error("Unable to load class {}.", rb.getBeanClassName());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static final List<Class<?>> scanByClass(String packageName, List<Class<? extends Object>> includeClasses, List<Class<? extends Object>> excludeClasses) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        excludeClasses.forEach(c -> scanner.addExcludeFilter(new AssignableTypeFilter(c)));
        includeClasses.forEach(c -> scanner.addIncludeFilter(new AssignableTypeFilter(c)));
        if (includeClasses.isEmpty()) {
            scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
        }

        return scanner.findCandidateComponents(packageName).stream().map(rb -> {
            try {
                return  cl.loadClass(rb.getBeanClassName());
            } catch (ClassNotFoundException e) {
                logger.error("Unable to load class {}.", rb.getBeanClassName());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
