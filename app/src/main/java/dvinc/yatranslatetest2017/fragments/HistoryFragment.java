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
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

        /* По клику на отдельный перевод в listview ставим\убираем метку "Избранного". */
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

        /* По долгому нажатию на выбранный перевод запускаем диалог, для подтверждения\отмены удаления выбранного элемента. */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showConfirmatonDeleteOneItemDialog(id);
                return true;
            }
        });

        getLoaderManager().initLoader(HISTORY_LOADER, null, this);
        return view;
    }

    /**
     * Предупреждающий диалог для удаления одной записи из истории.
     */
    private void showConfirmatonDeleteOneItemDialog(final long item_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity().getWindow().getContext());
        builder.setMessage(R.string.delete_dialog_msg2);
        builder.setPositiveButton(R.string.delete_history_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteOneItemFromHistory(item_id);
            }
        });
        builder.setNegativeButton(R.string.delete_history_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Метод для удаления одной записи из истории.
     */
    private void deleteOneItemFromHistory(long id){
        Uri currentUri = ContentUris.withAppendedId(HistoryEntry.CONTENT_URI, id);
        getActivity().getApplicationContext().getContentResolver().delete(currentUri, null, null);
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
