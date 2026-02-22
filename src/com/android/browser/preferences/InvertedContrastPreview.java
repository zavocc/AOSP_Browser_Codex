/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.browser.preferences;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.android.browser.BrowserSettings;

public class InvertedContrastPreview extends WebViewPreview {

    static final String IMG_ROOT = "content://com.android.browser.home/res/raw/";
    static final String[] THUMBS = new String[] {
        "thumb_google",
        "thumb_amazon",
        "thumb_cnn",
        "thumb_espn",
        "", // break
        "thumb_bbc",
        "thumb_nytimes",
        "thumb_weatherchannel",
        "thumb_picasa",
    };

    String mHtml;
    static final String HTML_PREFIX = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><style>body{width:1000px;}%s</style></head><body>";
    static final String HTML_SUFFIX = "</body></html>";

    public InvertedContrastPreview(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public InvertedContrastPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InvertedContrastPreview(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        StringBuilder builder = new StringBuilder();
        for (String thumb : THUMBS) {
            if (TextUtils.isEmpty(thumb)) {
                builder.append("<br />");
                continue;
            }
            builder.append("<img src=\"");
            builder.append(IMG_ROOT);
            builder.append(thumb);
            builder.append("\" />&nbsp;");
        }
        mHtml = builder.toString();
    }

    @Override
    protected void updatePreview(boolean forceReload) {
        if (mWebView == null) return;

        BrowserSettings settings = BrowserSettings.getInstance();
        String css = "";
        if (settings.useInvertedRendering()) {
            css = "html{-webkit-filter:invert(1) hue-rotate(180deg);"
                    + "filter:invert(1) hue-rotate(180deg);"
                    + "background:#fff;}img,video,picture,canvas,svg,image,iframe{-webkit-filter:invert(1) hue-rotate(180deg);"
                    + "filter:invert(1) hue-rotate(180deg);}";
        }
        String html = HTML_PREFIX.replace("%s", css) + mHtml + HTML_SUFFIX;
        mWebView.loadDataWithBaseURL(IMG_ROOT, html, "text/html", "utf-8", null);
    }

}
