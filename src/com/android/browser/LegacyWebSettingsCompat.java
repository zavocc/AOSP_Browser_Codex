/*
 * Copyright (C) 2026
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.android.browser;

import android.webkit.WebSettings;

import java.lang.reflect.Method;

/**
 * Optional legacy WebSettings API wrappers.
 */
final class LegacyWebSettingsCompat {

    private LegacyWebSettingsCompat() {
    }

    static void setLightTouchEnabled(WebSettings settings, boolean enabled) {
        invokeBoolean(settings, "setLightTouchEnabled", enabled);
    }

    static void setNavDump(WebSettings settings, boolean enabled) {
        invokeBoolean(settings, "setNavDump", enabled);
    }

    static void setPluginState(WebSettings settings, String stateName) {
        try {
            Class<?> pluginClass = Class.forName("android.webkit.WebSettings$PluginState");
            @SuppressWarnings({"rawtypes", "unchecked"})
            Object enumValue = Enum.valueOf((Class) pluginClass, stateName);
            Method method = WebSettings.class.getMethod("setPluginState", pluginClass);
            method.invoke(settings, enumValue);
        } catch (Throwable ignored) {
        }
    }

    static void setSavePassword(WebSettings settings, boolean enabled) {
        invokeBoolean(settings, "setSavePassword", enabled);
    }

    static void setEnableSmoothTransition(WebSettings settings, boolean enabled) {
        invokeBoolean(settings, "setEnableSmoothTransition", enabled);
    }

    static void setAppCacheEnabled(WebSettings settings, boolean enabled) {
        invokeBoolean(settings, "setAppCacheEnabled", enabled);
    }

    static void setAppCacheMaxSize(WebSettings settings, long size) {
        invokeLong(settings, "setAppCacheMaxSize", size);
    }

    static void setAppCachePath(WebSettings settings, String path) {
        invokeString(settings, "setAppCachePath", path);
    }

    static void setDatabasePath(WebSettings settings, String path) {
        invokeString(settings, "setDatabasePath", path);
    }

    static void setGeolocationDatabasePath(WebSettings settings, String path) {
        invokeString(settings, "setGeolocationDatabasePath", path);
    }

    static void setInvertedRendering(WebSettings settings, boolean enabled) {
        if (settings == null) {
            return;
        }
        if (!setForceDark(settings, enabled)) {
            setAlgorithmicDarkeningAllowed(settings, enabled);
        }
    }

    private static boolean setForceDark(WebSettings settings, boolean enabled) {
        Integer on = getStaticInt(WebSettings.class, "FORCE_DARK_ON");
        Integer off = getStaticInt(WebSettings.class, "FORCE_DARK_OFF");
        if (on == null || off == null) {
            return false;
        }
        Integer strategy = getStaticInt(WebSettings.class,
                "FORCE_DARK_STRATEGY_USER_AGENT_DARKENING_ONLY");
        if (strategy == null) {
            strategy = getStaticInt(WebSettings.class,
                    "FORCE_DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING");
        }
        if (strategy != null) {
            invokeInt(settings, "setForceDarkStrategy", strategy);
        }
        return invokeInt(settings, "setForceDark", enabled ? on : off);
    }

    private static void setAlgorithmicDarkeningAllowed(WebSettings settings, boolean enabled) {
        invokeBoolean(settings, "setAlgorithmicDarkeningAllowed", enabled);
    }

    private static void invokeBoolean(WebSettings settings, String methodName, boolean value) {
        try {
            Method method = WebSettings.class.getMethod(methodName, boolean.class);
            method.invoke(settings, value);
        } catch (Throwable ignored) {
        }
    }

    private static void invokeLong(WebSettings settings, String methodName, long value) {
        try {
            Method method = WebSettings.class.getMethod(methodName, long.class);
            method.invoke(settings, value);
        } catch (Throwable ignored) {
        }
    }

    private static boolean invokeInt(WebSettings settings, String methodName, int value) {
        try {
            Method method = WebSettings.class.getMethod(methodName, int.class);
            method.invoke(settings, value);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void invokeString(WebSettings settings, String methodName, String value) {
        try {
            Method method = WebSettings.class.getMethod(methodName, String.class);
            method.invoke(settings, value);
        } catch (Throwable ignored) {
        }
    }

    private static Integer getStaticInt(Class<?> klass, String fieldName) {
        try {
            return klass.getField(fieldName).getInt(null);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
