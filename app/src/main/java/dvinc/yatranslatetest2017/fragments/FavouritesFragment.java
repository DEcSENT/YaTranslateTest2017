package dvinc.yatranslatetest2017.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ListView;

import dvinc.yatranslatetest2017.R;
import dvinc.yatranslatetest2017.database.HistoryContract;
import dvinc.yatranslatetest2017.database.HistoryContract.*;
import dvinc.yatranslatetest2017.database.HistoryCursorAdapter;

/**
 * Created by Space 5 on 11.04.2017.
 */

/**
 * Класс для фрагмента с избранными переводами.
 */

public class FavouritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int HISTORY_LOADER = 0;
    HistoryCursorAdapter favouritesCursorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        ListView listViewFavourites = (ListView) view.findViewById(R.id.listFavourites);
        listViewFavourites.setEmptyView(view.findViewById(R.id.emptyFav));
        favouritesCursorAdapter = new HistoryCursorAdapter(getContext(), null);
        listViewFavourites.setAdapter(favouritesCursorAdapter);

        /* По клику в listview ставим\убираем метку "Избранного". */
        listViewFavourites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        currentUri,   // URI контент-провайдера для запроса конкретной записи
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
                HistoryContract.HistoryEntry.COLUMN_TEXT_INPUT,
                HistoryContract.HistoryEntry.COLUMN_TEXT_TRANSLATED,
                HistoryContract.HistoryEntry.COLUMN_LANGUAGES_FROM_TO,
                HistoryContract.HistoryEntry.COLUMN_BOOKMARK};

        // Загрузчик запускает запрос ContentProvider в фоновом потоке
        return new CursorLoader(getActivity(),
                HistoryContract.HistoryEntry.CONTENT_URI,   // URI контент-провайдера для запроса
                projection,             // колонки, которые попадут в результирующий курсор
                "bookmark="+"1",                   // условие для отображения избранных записей
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Обновляем CursorAdapter новым курсором, которые содержит обновленные данные
        favouritesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Освобождаем ресурсы
        favouritesCursorAdapter.swapCursor(null);
    }
}
