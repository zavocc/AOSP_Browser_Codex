/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.browser;

import android.net.WebAddress;
import android.os.Debug;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

/**
 * Performance analysis
 */
public class Performance {

    private static final String LOGTAG = "browser";

    private final static boolean LOGD_ENABLED =
            com.android.browser.Browser.LOGD_ENABLED;

    private static boolean mInTrace;

    private static long mStart;
    private static long mProcessStart;

    private static long mUiStart;

    static void tracePageStart(String url) {
        if (BrowserSettings.getInstance().isTracing()) {
            String host;
            try {
                WebAddress uri = new WebAddress(url);
                host = uri.getHost();
            } catch (android.net.ParseException ex) {
                host = "browser";
            }
            host = host.replace('.', '_');
            host += ".trace";
            mInTrace = true;
            Debug.startMethodTracing(host, 20 * 1024 * 1024);
        }
    }

    static void tracePageFinished() {
        if (mInTrace) {
            mInTrace = false;
            Debug.stopMethodTracing();
        }
    }

    static void onPageStarted() {
        mStart = SystemClock.uptimeMillis();
        mProcessStart = Process.getElapsedCpuTime();
        mUiStart = SystemClock.currentThreadTimeMillis();
    }

    static void onPageFinished(String url) {
        String uiInfo =
                "UI thread used " + (SystemClock.currentThreadTimeMillis() - mUiStart) + " ms";
        if (LOGD_ENABLED) {
            Log.d(LOGTAG, uiInfo);
        }
        String performanceString =
                "It took total " + (SystemClock.uptimeMillis() - mStart)
                        + " ms clock time to load the page.\nbrowser process used "
                        + (Process.getElapsedCpuTime() - mProcessStart)
                        + " ms, " + uiInfo;
        if (LOGD_ENABLED) {
            Log.d(LOGTAG, performanceString + "\nWebpage: " + url);
        }
        if (url != null) {
            String newUrl = new String(url);
            if (newUrl.startsWith("http://www.")) {
                newUrl = newUrl.substring(11);
            } else if (newUrl.startsWith("http://")) {
                newUrl = newUrl.substring(7);
            } else if (newUrl.startsWith("https://www.")) {
                newUrl = newUrl.substring(12);
            } else if (newUrl.startsWith("https://")) {
                newUrl = newUrl.substring(8);
            }
            if (LOGD_ENABLED) {
                Log.d(LOGTAG, newUrl + " loaded");
            }
        }
    }
}
