/*
 * Copyright (C) 2026
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.android.browser;

import android.webkit.HttpAuthHandler;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.lang.reflect.Method;

/**
 * Compatibility wrappers for WebView/Cookie APIs that changed across Android releases.
 */
final class WebViewCompatibility {

    private WebViewCompatibility() {
    }

    static boolean isPrivateBrowsingEnabled(WebView view) {
        if (view == null) {
            return false;
        }
        try {
            Method m = WebView.class.getMethod("isPrivateBrowsingEnabled");
            Object result = m.invoke(view);
            return result instanceof Boolean && ((Boolean) result);
        } catch (Throwable ignored) {
            return false;
        }
    }

    static void removeSessionCookies(CookieManager cookieManager) {
        if (cookieManager == null) {
            return;
        }
        if (invokeWithNullCallback(cookieManager, "removeSessionCookies")) {
            flushCookies(cookieManager);
            return;
        }
        invoke(cookieManager, "removeSessionCookie");
        flushCookies(cookieManager);
    }

    static void removeAllCookies(CookieManager cookieManager) {
        if (cookieManager == null) {
            return;
        }
        if (invokeWithNullCallback(cookieManager, "removeAllCookies")) {
            flushCookies(cookieManager);
            return;
        }
        invoke(cookieManager, "removeAllCookie");
        flushCookies(cookieManager);
    }

    static void flushCookies(CookieManager cookieManager) {
        if (cookieManager == null) {
            return;
        }
        invoke(cookieManager, "flush");
    }

    static void debugDump(WebView view) {
        if (view == null) {
            return;
        }
        invoke(view, "debugDump");
    }

    static void enablePlatformNotifications() {
        invokeStatic(WebView.class, "enablePlatformNotifications");
    }

    static void disablePlatformNotifications() {
        invokeStatic(WebView.class, "disablePlatformNotifications");
    }

    static int getContentWidth(WebView view) {
        if (view == null) {
            return 0;
        }
        Integer reflected = invokeInt(view, "getContentWidth");
        if (reflected != null) {
            return reflected;
        }
        return view.getWidth();
    }

    static int getVisibleTitleHeight(WebView view) {
        if (view == null) {
            return 0;
        }
        Integer reflected = invokeInt(view, "getVisibleTitleHeight");
        return reflected != null ? reflected : 0;
    }

    static String getTouchIconUrl(WebView view) {
        if (view == null) {
            return null;
        }
        try {
            Method m = view.getClass().getMethod("getTouchIconUrl");
            Object result = m.invoke(view);
            return result instanceof String ? (String) result : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    static boolean suppressHttpAuthDialog(HttpAuthHandler handler) {
        if (handler == null) {
            return false;
        }
        try {
            Method m = handler.getClass().getMethod("suppressDialog");
            Object result = m.invoke(handler);
            return result instanceof Boolean && (Boolean) result;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean invokeWithNullCallback(Object receiver, String methodName) {
        try {
            Method m = receiver.getClass().getMethod(methodName, ValueCallback.class);
            m.invoke(receiver, new Object[] { null });
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean invoke(Object receiver, String methodName) {
        try {
            Method m = receiver.getClass().getMethod(methodName);
            m.invoke(receiver);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean invokeStatic(Class<?> klass, String methodName) {
        try {
            Method m = klass.getMethod(methodName);
            m.invoke(null);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Integer invokeInt(Object receiver, String methodName) {
        try {
            Method m = receiver.getClass().getMethod(methodName);
            Object result = m.invoke(receiver);
            return result instanceof Integer ? (Integer) result : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
