package dvinc.yatranslatetest2017.database;

/**
 * Created by Space 5 on 06.04.2017.
 *
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import dvinc.yatranslatetest2017.database.HistoryContract.HistoryEntry;


// TODO: проверить предупреждение о NPE. Добавить больше комментариев

/**
 * Класс контент-провайдера.
 */
public class HistoryContentProvider extends ContentProvider {

    public static final String TAG = HistoryContentProvider.class.getSimpleName();

    private static final int ALL_HISTORY = 100;

    private static final int HISTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static long updatedItemId = 0;

    static {
        sUriMatcher.addURI(HistoryContract.CONTENT_AUTHORITY, HistoryContract.PATH_HISTORY, ALL_HISTORY);
        sUriMatcher.addURI(HistoryContract.CONTENT_AUTHORITY, HistoryContract.PATH_HISTORY + "/#", HISTORY_ID);
    }

    public DBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_HISTORY:
                /* Примечение: здесь курсор для всей истории сортируется в обратном порядке, для отображения последних записей вверху истории. */
                cursor = database.query(HistoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null /*HistoryEntry._ID + " DESC"*/);
                break;
            case HISTORY_ID:
                selection = HistoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(HistoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        if(getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_HISTORY:
                return HistoryEntry.CONTENT_LIST_TYPE;
            case HISTORY_ID:
                return HistoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_HISTORY:
                return insertHistory(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        updatedItemId = 0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_HISTORY:
                rowsDeleted = database.delete(HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HISTORY_ID:
                selection = HistoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0 & getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Возвращает количество удаленных строк
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_HISTORY:
                return updateHistory(uri, values, selection, selectionArgs);
            case HISTORY_ID:
                selection = HistoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHistory(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertHistory(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(HistoryEntry.TABLE_NAME, null, values);
        updatedItemId = id;//DatabaseUtils.queryNumEntries(database, HistoryEntry.TABLE_NAME);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        if(getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateHistory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Если нет значений для обновления, возвращаем 0
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Обновляем запись в базе данных и возвращаем количество обновленных строк
        int rowsUpdated = database.update(HistoryEntry.TABLE_NAME, values, selection, selectionArgs);

        Log.v("updateHistory", "Rows updated: " + rowsUpdated);

        // Если хотя бы одна строка была обновлена, оповещаем об изменении
        if (rowsUpdated != 0 & getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Возвращает количество обновленных строк
        return rowsUpdated;
    }
}
