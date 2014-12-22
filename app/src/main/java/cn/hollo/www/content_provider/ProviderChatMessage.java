package cn.hollo.www.content_provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by orson on 14-12-22.
 */
public class ProviderChatMessage extends ContentProvider {
    public static final Uri     CONTENT_URI = Uri.parse("content://cn.hollo.www.content_provider.chat_message");
    private static final String DB_NAME = "ChatMessage.db";
    private static final int    DB_VERSION = 1;

    private OpenHelperChatMessage dbHelper;
    private ContentResolver resolver = null;

    @Override
    public boolean onCreate() {
        resolver = getContext().getContentResolver();
        dbHelper = new OpenHelperChatMessage(getContext(), DB_NAME, DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
