package dvinc.yatranslatetest2017.fragments;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
 */

/**
 * Класс для фрагмента с исторей переводов.
 */
public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    /* Идентификатор для загрузчика */
    private static final int HISTORY_LOADER = 0;

    HistoryCursorAdapter historyCursorAdapter;
    EditText searchInHistory;

    SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translated_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty));
        historyCursorAdapter = new HistoryCursorAdapter(getContext(), null);
        listView.setAdapter(historyCursorAdapter);
        //listView.setTextFilterEnabled(true);

//        /* Просто фича для searchView, не знаю зачем, но пусть пока будет*/
//        searchView = (SearchView) view.findViewById(R.id.search_history);
//        searchView.setSuggestionsAdapter(historyCursorAdapter);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });


//        searchInHistory = (EditText) view.findViewById(R.id.search_in_history);

//        searchInHistory.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                historyCursorAdapter.getFilter().filter(s.toString());
//                historyCursorAdapter.notifyDataSetChanged();
//            }
//
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        historyCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
//            @Override
//            public Cursor runQuery(CharSequence constraint) {
//
//                /* Так будет работать, если сделать базу статик, но тогда она не будет обновляться*/
//                //SQLiteDatabase db = HistoryContentProvider.mDbHelper.getReadableDatabase();
//
//                if (constraint == null || constraint.length() == 0) {
//
//                    return db.rawQuery("select * from " + HistoryEntry.TABLE_NAME, null);
//                }
//                else {
//                    return db.rawQuery("select * from " + HistoryEntry.TABLE_NAME + " where " +
//                            HistoryEntry.COLUMN_TEXT_INPUT + " like ?", new String[]{"%" + constraint.toString() + "%"});
//                }
//            }
//        });

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
                        currentUri,   // URI контент-провайдера для запроса
                        projection,             // колонки, которые попадут в результирующий курсор
                        null,                   // без условия WHERE
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
                HistoryEntry.CONTENT_URI,   // URI контент-провайдера для запроса
                projection,             // колонки, которые попадут в результирующий курсор
                null,                   // без условия WHERE
                null,                   // без аргументов
                null);                  // сортировка по умолчанию
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

    /**
     * Метод для удаления всех записей из истории.
     */
    private void deleteAllHistory() {
        int rowsDeleted = getActivity().getApplicationContext().getContentResolver().delete(HistoryEntry.CONTENT_URI, null, null);
        Log.v("HistoryFragment", "Удалено записей из истории: " + rowsDeleted );
        Toast.makeText(this.getActivity().getWindow().getContext(), "Удалено записей из истории: " +
                rowsDeleted, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_history:
                showDeleteConfirmationDialog();
                break;
        }
        return true;
    }


    /**
     * Предупреждающий диалог для удаления всех записей из истории.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity().getWindow().getContext());
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_history_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllHistory();
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
}
