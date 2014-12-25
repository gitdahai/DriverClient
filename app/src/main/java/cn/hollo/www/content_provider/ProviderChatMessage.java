package cn.hollo.www.content_provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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

    /*************************************************
     *
     * @return
     */
    public boolean onCreate() {
        resolver = getContext().getContentResolver();
        dbHelper = new OpenHelperChatMessage(getContext(), DB_NAME, DB_VERSION);
        return true;
    }

    /*************************************************
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(OpenHelperChatMessage.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(resolver, uri);
        return cursor;
    }

    /*************************************************
     *
     * @param uri
     * @return
     */
    public String getType(Uri uri) {
        return uri.toString();
    }

    /*************************************************
     *
     * @param uri
     * @param values
     * @return
     */
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(OpenHelperChatMessage.TABLE_NAME, null, values);

        if(id < 0)
            throw new SQLiteException("Unable to insert " + values + " for " + uri);

        Uri newUri = ContentUris.withAppendedId(uri, id);
        resolver.notifyChange(uri, null);
        return newUri;
    }

    /*************************************************
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(OpenHelperChatMessage.TABLE_NAME, selection, selectionArgs);
        resolver.notifyChange(uri, null);
        return count;
    }

    /*************************************************
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(OpenHelperChatMessage.TABLE_NAME, values, selection, selectionArgs);
        resolver.notifyChange(uri, null);
        return count;
    }
}
