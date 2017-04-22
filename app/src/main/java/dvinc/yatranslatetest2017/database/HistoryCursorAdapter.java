package dvinc.yatranslatetest2017.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dvinc.yatranslatetest2017.R;

import static dvinc.yatranslatetest2017.database.HistoryContract.HistoryEntry.*;

/**
 * Created by Space 5 on 01.04.2017.
 *
 */

/**
 * Класс для создания адаптера данных. Заполняет список из курсора.
 */
public class HistoryCursorAdapter extends CursorAdapter implements Filterable{

    public HistoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * Метод для привязки всех данных к заданному виду, например, для установки текста в TextView.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Находим поля, в которые будем подставлять свои данные в списке
        TextView textInputTextView = (TextView) view.findViewById(R.id.textInput);
        TextView translatedTextTextView = (TextView) view.findViewById(R.id.textTranslated);
        TextView languagesTextView = (TextView) view.findViewById(R.id.languages);
        //TextView bookmarkTextView = (TextView) view.findViewById(R.id.bookmark);
        ImageView bookmarkColor = (ImageView) view.findViewById(R.id.bookmarkPic);

        if (cursor != null) {
            // Находим индексы столбцов в курсоре
            int textInputColumnIndex = cursor.getColumnIndex(COLUMN_TEXT_INPUT);
            int translatedTextColumnIndex = cursor.getColumnIndex(COLUMN_TEXT_TRANSLATED);
            int languagesColumnIndex = cursor.getColumnIndex(COLUMN_LANGUAGES_FROM_TO);
            int bookmarkColumnIndex = cursor.getColumnIndex(COLUMN_BOOKMARK);

            // Читаем данные из курсора для текущей записи
            String textInput = cursor.getString(textInputColumnIndex);
            String translatedText = cursor.getString(translatedTextColumnIndex);
            String languages = cursor.getString(languagesColumnIndex);
            String bookmark = cursor.getString(bookmarkColumnIndex);

            // Обновляем текстовые поля, подставляя в них данные для текущей записи
            textInputTextView.setText(textInput);
            translatedTextTextView.setText(translatedText.replace("\\n", "\n"));
            languagesTextView.setText(languages.toUpperCase());
//            bookmarkTextView.setText(bookmark);

            // Окрашиваем заметку в зависимости от того, избранная ли эта запись (зеленый цвет), или нет (красный цвет)
            if (bookmark.equals("1")){
                bookmarkColor.setColorFilter(ContextCompat.getColor(context,R.color.bookmarkYes));
            } else {
                bookmarkColor.setColorFilter(ContextCompat.getColor(context,R.color.bookmarkNo));
            }
        }
    }

}
