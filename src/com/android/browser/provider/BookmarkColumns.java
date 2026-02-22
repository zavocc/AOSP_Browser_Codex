/*
 * Compatibility copy of legacy android.provider.Browser.BookmarkColumns.
 * Newer public SDKs no longer expose this nested interface.
 */
package com.android.browser.provider;

interface BookmarkColumns {
    String _ID = "_id";
    String URL = "url";
    String VISITS = "visits";
    String DATE = "date";
    String BOOKMARK = "bookmark";
    String TITLE = "title";
    String CREATED = "created";
    String USER_ENTERED = "user_entered";
    String FAVICON = "favicon";
    String THUMBNAIL = "thumbnail";
    String TOUCH_ICON = "touch_icon";
}
