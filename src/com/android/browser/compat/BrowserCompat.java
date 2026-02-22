/*
 * Minimal compatibility layer for legacy android.provider.Browser APIs that
 * are hidden/removed in modern public SDKs.
 */
package com.android.browser.compat;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BrowserContract.History;
import android.provider.BrowserContract.Searches;
import android.webkit.WebIconDatabase;

public final class BrowserCompat {
    private BrowserCompat() {}

    public static final boolean LOGV_ENABLED = false;
    public static final boolean LOGD_ENABLED = true;
    public static final String AUTHORITY = "com.android.browser.classic";
    public static final String PARAM_LIMIT = "limit";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri BOOKMARKS_URI = Uri.parse("content://" + AUTHORITY + "/bookmarks");
    public static final Uri BOOKMARKS_DEFAULT_FOLDER_URI =
            Uri.parse("content://" + AUTHORITY + "/bookmarks/folder");
    public static final Uri BOOKMARKS_DEFAULT_FOLDER_ID_URI =
            Uri.parse("content://" + AUTHORITY + "/bookmarks/folder/id");
    public static final Uri HISTORY_URI = Uri.parse("content://" + AUTHORITY + "/history");
    public static final Uri SEARCHES_URI = Uri.parse("content://" + AUTHORITY + "/searches");
    public static final Uri COMBINED_URI = Uri.parse("content://" + AUTHORITY + "/combined");
    public static final Uri IMAGES_URI = Uri.parse("content://" + AUTHORITY + "/images");

    public static final String INITIAL_ZOOM_LEVEL = "browser.initialZoomLevel";
    public static final String EXTRA_APPLICATION_ID = "com.android.browser.application_id";
    public static final String EXTRA_HEADERS = "com.android.browser.headers";
    public static final String EXTRA_CREATE_NEW_TAB = "create_new_tab";
    public static final String EXTRA_SHARE_SCREENSHOT = "share_screenshot";
    public static final String EXTRA_SHARE_FAVICON = "share_favicon";

    public static final int TRUNCATE_N_OLDEST = 5;
    private static final int MAX_HISTORY_COUNT = 250;

    public static final class BookmarkColumns {
        public static final String _ID = "_id";
        public static final String URL = "url";
        public static final String VISITS = "visits";
        public static final String DATE = "date";
        public static final String BOOKMARK = "bookmark";
        public static final String TITLE = "title";
        public static final String FAVICON = "favicon";
        public static final String THUMBNAIL = "thumbnail";
        public static final String TOUCH_ICON = "touch_icon";
        public static final String USER_ENTERED = "user_entered";

        private BookmarkColumns() {}
    }

    public static Uri buildBookmarksFolderUri(long folderId) {
        return Uri.parse("content://" + AUTHORITY + "/bookmarks/folder/" + folderId);
    }

    public static long getDefaultBookmarksRootId(ContentResolver resolver) {
        Cursor cursor = null;
        try {
            cursor = resolver.query(BOOKMARKS_DEFAULT_FOLDER_ID_URI,
                    new String[] { BookmarkColumns._ID }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } catch (RuntimeException ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 1L;
    }

    public static final String[] HISTORY_PROJECTION = new String[] {
            BookmarkColumns._ID, // 0
            BookmarkColumns.URL, // 1
            BookmarkColumns.VISITS, // 2
            BookmarkColumns.DATE, // 3
            BookmarkColumns.BOOKMARK, // 4
            BookmarkColumns.TITLE, // 5
            BookmarkColumns.FAVICON, // 6
            BookmarkColumns.THUMBNAIL, // 7
            BookmarkColumns.TOUCH_ICON, // 8
            BookmarkColumns.USER_ENTERED, // 9
    };

    public static void saveBookmark(Context context, String title, String url) {
        Intent intent = new Intent(Intent.ACTION_INSERT, BOOKMARKS_URI);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static void sendString(Context context, String text) {
        sendString(context, text, "Share link");
    }

    public static void sendString(Context context, String text, String chooserTitle) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, text);
        try {
            Intent chooser = Intent.createChooser(send, chooserTitle);
            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static String[] getVisitedHistory(ContentResolver resolver) {
        Cursor cursor = null;
        try {
            cursor = resolver.query(
                    HISTORY_URI,
                    new String[] { History.URL },
                    History.VISITS + " > 0",
                    null,
                    null);
            if (cursor == null) {
                return new String[0];
            }
            String[] urls = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                urls[i++] = cursor.getString(0);
            }
            return urls;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void addSearchUrl(ContentResolver resolver, String search) {
        ContentValues values = new ContentValues();
        values.put(Searches.SEARCH, search);
        values.put(Searches.DATE, System.currentTimeMillis());
        try {
            resolver.insert(SEARCHES_URI, values);
        } catch (RuntimeException ignored) {
        }
    }

    public static void clearSearches(ContentResolver resolver) {
        resolver.delete(SEARCHES_URI, null, null);
    }

    public static void deleteFromHistory(ContentResolver resolver, String url) {
        resolver.delete(HISTORY_URI, History.URL + "=?", new String[] { url });
    }

    public static void clearHistory(ContentResolver resolver) {
        Cursor cursor = null;
        try {
            cursor = resolver.query(HISTORY_URI, new String[] { History.URL },
                    null, null, null);
            if (cursor != null) {
                WebIconDatabase iconDb = WebIconDatabase.getInstance();
                while (cursor.moveToNext()) {
                    iconDb.releaseIconForPageUrl(cursor.getString(0));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        resolver.delete(HISTORY_URI, null, null);
    }

    public static void truncateHistory(ContentResolver resolver) {
        Cursor cursor = null;
        try {
            cursor = resolver.query(
                    HISTORY_URI,
                    new String[] { History._ID, History.URL, History.DATE_LAST_VISITED },
                    null,
                    null,
                    History.DATE_LAST_VISITED + " ASC");
            if (cursor == null || cursor.getCount() < MAX_HISTORY_COUNT) {
                return;
            }
            WebIconDatabase iconDb = WebIconDatabase.getInstance();
            if (cursor.moveToFirst()) {
                for (int i = 0; i < TRUNCATE_N_OLDEST; i++) {
                    resolver.delete(
                            ContentUris.withAppendedId(HISTORY_URI, cursor.getLong(0)),
                            null,
                            null);
                    iconDb.releaseIconForPageUrl(cursor.getString(1));
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
