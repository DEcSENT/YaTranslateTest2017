package dvinc.yatranslatetest2017.fragments;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import dvinc.yatranslatetest2017.R;
import dvinc.yatranslatetest2017.database.*;
import dvinc.yatranslatetest2017.database.HistoryContract.*;

/**
 * Created by Space 5 on 30.03.2017.
 *
 */

/**
 * Класс для фрагмента с исторей переводов.
 */
public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    /* Идентификатор для загрузчика */
    private static final int HISTORY_LOADER = 0;

    HistoryCursorAdapter historyCursorAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translated_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty));
        historyCursorAdapter = new HistoryCursorAdapter(getContext(), null);
        listView.setAdapter(historyCursorAdapter);

        /* По клику в listview ставим\убираем метку "Избранного". */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int current = 0;
                Uri currentUri = ContentUris.withAppendedId(HistoryEntry.CONTENT_URI, id);
                String[] projection = {
                        HistoryContract.HistoryEntry._ID,
                        HistoryEntry.COLUMN_TEXT_INPUT,
                        HistoryEntry.COLUMN_TEXT_TRANSLATED,
                        HistoryEntry.COLUMN_LANGUAGES_FROM_TO,
                        HistoryEntry.COLUMN_BOOKMARK};
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                        currentUri,
                        projection,
                        null,
                        null,
                        null);
                if (cursor != null){
                    cursor.moveToFirst();
                    int bookmarkColumnIndex = cursor.getColumnIndex(HistoryEntry.COLUMN_BOOKMARK);
                    String bookmark = cursor.getString(bookmarkColumnIndex);
                    if (bookmark.equals("1")){
                        ContentValues values = new ContentValues();
                        values.put(HistoryEntry.COLUMN_BOOKMARK, "0");
                        current = getActivity().getApplicationContext().getContentResolver().update(currentUri, values, null, null);
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(HistoryEntry.COLUMN_BOOKMARK, "1");
                        current = getActivity().getApplicationContext().getContentResolver().update(currentUri, values, null, null);
                    }
                    cursor.close();
                }
            }
        });

        getLoaderManager().initLoader(HISTORY_LOADER, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Зададим нужные колонки
        String[] projection = {
                HistoryContract.HistoryEntry._ID,
                HistoryEntry.COLUMN_TEXT_INPUT,
                HistoryEntry.COLUMN_TEXT_TRANSLATED,
                HistoryEntry.COLUMN_LANGUAGES_FROM_TO,
                HistoryEntry.COLUMN_BOOKMARK};

        // Загрузчик запускает запрос ContentProvider в фоновом потоке
        return new CursorLoader(getActivity(),
                HistoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Обновляем CursorAdapter новым курсором, которые содержит обновленные данные
        historyCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Освобождаем ресурсы
        historyCursorAdapter.swapCursor(null);
    }
}
