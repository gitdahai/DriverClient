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
 * Created by orson on 14-12-2.
 * 工作任务单数据提供
 */
public class ProviderWorkTask extends ContentProvider {
    public static final Uri     CONTENT_URI = Uri.parse("content://cn.hollo.www.content_provider.WorkTask");
    private static final String DB_NAME = "WorkTask.db";
    private static final int    DB_VERSION = 1;

    private OpenHelperWorkTask dbHelper;
    private ContentResolver     resolver = null;

    @Override
    public boolean onCreate() {
        resolver = getContext().getContentResolver();
        dbHelper = new OpenHelperWorkTask(getContext(), DB_NAME, DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(OpenHelperWorkTask.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return uri.toString();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(OpenHelperWorkTask.TABLE_NAME, null, values);

        if(id < 0)
            throw new SQLiteException("Unable to insert " + values + " for " + uri);

        Uri newUri = ContentUris.withAppendedId(uri, id);
        resolver.notifyChange(uri, null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(OpenHelperWorkTask.TABLE_NAME, selection, selectionArgs);
        resolver.notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        count = db.update(OpenHelperWorkTask.TABLE_NAME, values, selection, selectionArgs);
        resolver.notifyChange(uri, null);
        return count;
    }
}
