package dvinc.yatranslatetest2017.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Space 5 on 06.04.2017.
 */

// TODO: Добавить описания классов

public final class HistoryContract {

    // Чтобы случайно не создать экземпляр класса, даем ему пустой конструктор.
    private HistoryContract() {
    }

    /**
     * Имя для всего контент-провайдера.
     */
    static final String CONTENT_AUTHORITY = "dvinc.yatranslatetest2017";

    /**
     * Переменная для доступа к контент-провайдеру.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Путь к провайдеру (добавляется к URI базового содержимого для возможных URI).
     */
    static final String PATH_HISTORY = "history";

    public static final class HistoryEntry implements BaseColumns {

        /**
         * URI содержимого для доступа к данным в провайдере.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HISTORY);

        /**
         * Данные по ссылке {@link #CONTENT_URI} для всего списка истории.
         */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;

        /**
         * Данные по ссылке {@link #CONTENT_URI} для отдельной записи в истории.
         */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;

        /* Переменные для создания базы данных.*/
        final static String TABLE_NAME = "translateHistory";
        final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_TEXT_INPUT = "textInput";
        public final static String COLUMN_TEXT_TRANSLATED = "textTranslated";
        public final static String COLUMN_LANGUAGES_FROM_TO = "languages_from_to";
        public final static String COLUMN_BOOKMARK = "bookmark";
    }
}
