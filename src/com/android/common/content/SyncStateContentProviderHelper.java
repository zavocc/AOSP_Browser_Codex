/*
 * Copyright (C) 2026
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.android.common.content;

import android.accounts.Account;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BrowserContract.SyncState;

/**
 * Minimal local helper used by BrowserProvider2 when platform sync helper is unavailable.
 */
public class SyncStateContentProviderHelper {

    private static final String TABLE_SYNCSTATE = "syncstate";

    public void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SYNCSTATE + " ("
                + SyncState._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SyncState.ACCOUNT_NAME + " TEXT,"
                + SyncState.ACCOUNT_TYPE + " TEXT,"
                + SyncState.DATA + " BLOB"
                + ");");
    }

    public void onAccountsChanged(SQLiteDatabase db, Account[] accounts) {
        // No-op in modern standalone builds; sync adapters are intentionally disabled.
    }

    public void onDatabaseOpened(SQLiteDatabase db) {
        // No-op.
    }

    public Cursor query(SQLiteDatabase db, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return db.query(TABLE_SYNCSTATE, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public long insert(SQLiteDatabase db, ContentValues values) {
        return db.insert(TABLE_SYNCSTATE, SyncState.DATA, values);
    }

    public int delete(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_SYNCSTATE, selection, selectionArgs);
    }

    public int update(SQLiteDatabase db, ContentValues values, String selection,
            String[] selectionArgs) {
        return db.update(TABLE_SYNCSTATE, values, selection, selectionArgs);
    }
}
