package com.github.request.bin.utils;

import java.util.regex.Pattern;

public final class RegexFactory {

    public static final Pattern NUMBERS = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE);
    public static final Pattern DECIMALS = Pattern.compile("\\d+\\.\\d+", Pattern.CASE_INSENSITIVE);
    public static final Pattern STRINGS = Pattern.compile("['|\"]?(.*)['|\"]?", Pattern.CASE_INSENSITIVE);
    public static final Pattern BOOLEANS = Pattern.compile("True|False", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALUE_FINDER = Pattern.compile("\\{{2}(\\w+\\.\\w+)\\((.*)\\)\\}{2}", Pattern.CASE_INSENSITIVE);
    public static final Pattern KEY_FINDER = Pattern.compile("\\$\\{((\\w+)[\\.]?(\\w+)*)\\}", Pattern.CASE_INSENSITIVE);
    private RegexFactory() throws IllegalAccessException {
        throw new IllegalAccessException("For static use only");
    }
}
