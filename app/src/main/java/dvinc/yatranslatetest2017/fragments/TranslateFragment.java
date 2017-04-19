package dvinc.yatranslatetest2017.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;

import dvinc.yatranslatetest2017.R;
import dvinc.yatranslatetest2017.database.HistoryContentProvider;
import dvinc.yatranslatetest2017.database.HistoryContract.*;
import static dvinc.yatranslatetest2017.YandexApiData.*;

/**
 * Created by Space 5 on 28.03.2017.
 *
 */

// TODO: добавить больше комментариев.

/**
 * Класс для фрагмента с переводчиком текста.
 */
public class TranslateFragment extends Fragment{

    private static final String LOG_TAG = "TranslateFragment";

    private final int TRIGGER_SERACH = 1;
    // Where did 1000 come from? It's arbitrary, since I can't find average android typing speed.
    private final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;

    private EditText translateTextInput;
    private Button buttonTranslate;
    private TextView translatedText;
    private Button buttonDeleteInputText;
    private Spinner spinnerLangFrom;
    private Spinner spinnerLangTo;
    private String stringLangFrom = "ru";
    private String stringLangTo = "en";
    private Button buttonChangeLang;
    private CheckBox bookmarkCheckbox;
    private String chooseBookmark = "0";
    /**
     * TODO
     */
    private static int response_code = 0;

    /**
     * TODO
     */
    private static String response_code_message = "";

    /**
     * TODO 
     */
    public static long current_id = 0;

    /* Стартовые языки Русский - Английский. */
    static final int START_LANG_FROM = 17;
    static final int START_LANG_TO = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        translateTextInput = (EditText) view.findViewById(R.id.translateTextInput);
        buttonTranslate = (Button) view.findViewById(R.id.buttonTranslate);
        translatedText = (TextView) view.findViewById(R.id.translatedText);
        buttonDeleteInputText = (Button) view.findViewById(R.id.buttonDeleteText);
        buttonChangeLang = (Button) view.findViewById(R.id.buttonChangeLang);
        bookmarkCheckbox = (CheckBox) view.findViewById(R.id.bookmarkCheckbox);

        /* Скрываем клавиатуру при старте приложения. */
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable textObjectFromEdit = translateTextInput.getText();
                String newString = textObjectFromEdit.toString();
                if(!newString.equals("")) {
                    BGTask task = new BGTask();
                    task.execute(String.valueOf(translateTextInput.getText()));
                }
            }
        });

        /** Метод для очистки поля ввода текста на перевод и самого поля для перевода. */
        buttonDeleteInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateTextInput.setText("");
                translatedText.setText("");
                current_id = 0;
            }
        });

        /** Метод для зеркальной смены языков в спиннерах. */
        buttonChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = spinnerLangFrom.getSelectedItemPosition();
                spinnerLangFrom.setSelection(spinnerLangTo.getSelectedItemPosition());
                spinnerLangTo.setSelection(current);
            }
        });

        /** Метод для чекбокса избранного. */
        bookmarkCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarkCheckbox.isChecked()){
                    chooseBookmark = "1";
                }else{
                    chooseBookmark = "0";
                }
            }
        });

        setupLanguageFromSpinner(view);
        setupLanguageToSpinner(view);

        return view;
    }

    //TODO: здесь что-то не так с методом afterTextChanged, он отправляет пустой текст после быстрого полного побуквенного удаления текста. Нужно пофиксить, иначе выпадает "Код 0"
    @Override
    public void onResume() {
        super.onResume();
        /* Этот метод был вынесен сюда, чтобы избежать произвольной отправки текста на перевод при пересоздании фрагмента. */
        translateTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                /* Перед отправкой текста на перевод нужно проверить не пустой ли он. */
                String textInput = translateTextInput.getText().toString();
                if(!textInput.trim().isEmpty()) {
                    handler.removeMessages(TRIGGER_SERACH);
                    handler.sendEmptyMessageDelayed(TRIGGER_SERACH, SEARCH_TRIGGER_DELAY_IN_MS);
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        current_id = 0;
        Log.v("onPause", "current_id set to "+ current_id);
    }

    // TODO: проверить возможную утечку памяти?

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String textInput = translateTextInput.getText().toString();
            if (msg.what == TRIGGER_SERACH & !textInput.trim().isEmpty()) {
                BGTask task = new BGTask();
                task.execute(String.valueOf(translateTextInput.getText()));
            }
        }
    };

    /**
     * Класс для работы с переводом в фоне.
     */
    private class BGTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... translatingTextArray) {
            String output = null;
            for (String text : translatingTextArray) {
                try {
                    output = getOutputFromUrl(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return output;
        }

        /**
         *  Метод для создания подключения и отправки запроса в интернет.
         *  В ответе получаем строку в формате json, парсим её, получаем перевод, и код ответа.
         */
        private String getOutputFromUrl(String text) throws IOException {
            String translated = "";

            URL urlObj = new URL(YA_URL + API_KEY);
            try {
                HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes("&text=" + URLEncoder.encode(text, "UTF-8") + "&lang=" + stringLangFrom + '-' + stringLangTo);

                InputStream response = connection.getInputStream();
                String jsonString = new Scanner(response).nextLine();

                int start = jsonString.indexOf("[");
                int end = jsonString.indexOf("]");
                translated = jsonString.substring(start + 2, end - 1);
                connection.disconnect();

                response_code = Integer.parseInt(jsonString.substring(8, 11));
            } catch (Exception e){
                Log.v(LOG_TAG, "ALARM in getOutputFromUrl method"+e);
            }

            return translated;
        }

        @Override
        protected void onPostExecute(String output) {

            /* Если код полученного ответа равен 200, значит перевод выполнен успешно, и данные можно записать в историю. */
            if (response_code == 200) {
                translatedText.setText(output);

                if(current_id == 0) {
                /* Передаем данные в контент-провайдер */
                    ContentValues values = new ContentValues();
                    values.put(HistoryEntry.COLUMN_TEXT_INPUT, translateTextInput.getText().toString());
                    values.put(HistoryEntry.COLUMN_TEXT_TRANSLATED, output + current_id);
                    values.put(HistoryEntry.COLUMN_LANGUAGES_FROM_TO, stringLangFrom + " - " + stringLangTo);
                    values.put(HistoryEntry.COLUMN_BOOKMARK, chooseBookmark);

                    Context context = getActivity().getApplicationContext();
                    if (context != null) {
                        try {
                            context.getContentResolver().insert(HistoryEntry.CONTENT_URI, values);
                        } catch (Exception e) {
                            Log.v(LOG_TAG, "ALARM IN onPostExecute method" + e);
                        }
                    }
                    current_id = (HistoryContentProvider.updatedItemId);
                }
                else {
                    ContentValues values = new ContentValues();
                    values.put(HistoryEntry.COLUMN_TEXT_INPUT, translateTextInput.getText().toString());
                    values.put(HistoryEntry.COLUMN_TEXT_TRANSLATED, output + current_id);
                    values.put(HistoryEntry.COLUMN_LANGUAGES_FROM_TO, stringLangFrom + " - " + stringLangTo);
                    values.put(HistoryEntry.COLUMN_BOOKMARK, chooseBookmark);
                    Context context = getActivity().getApplicationContext();
                    Uri currentHistoryUri = ContentUris.withAppendedId(HistoryEntry.CONTENT_URI, current_id);
                    if (context != null) {
                        try {
                            Log.v(LOG_TAG, "Before insert "+current_id);
                            context.getContentResolver().update(currentHistoryUri, values, null, null);
                            Log.v(LOG_TAG, "After insert "+current_id);
                        } catch (Exception e) {
                            Log.v(LOG_TAG, "ALARM IN onPostExecute method" + e);
                        }
                    }
                }

                /* Если код полученного ответа равен чему-то другому, значит что-то пошло не так. В зависимости от кода показывается всплывающее сообщение, соответствующее этому коду. */
            } else if (response_code == 401){
                response_code_message = getResources().getString(R.string.code_401);
            } else if (response_code == 402){
                response_code_message = getResources().getString(R.string.code_402);
            } else if (response_code == 404){
                response_code_message = getResources().getString(R.string.code_404);
            } else if (response_code == 413){
                response_code_message = getResources().getString(R.string.code_413);
            } else if (response_code == 422){
                response_code_message = getResources().getString(R.string.code_422);
            } else if (response_code == 501){
                response_code_message = getResources().getString(R.string.code_501);
            } else {

                /* Если ответное сообщение не было получено, значит нет подключения к интернету. */
                response_code_message = getResources().getString(R.string.code_something_wrong);
            }
            if(response_code != 200) {
                showCodeMessage(response_code, response_code_message);
            }
            response_code = 0;
        }
        @Override
        protected void onPreExecute() {
            translatedText.setText(R.string.connection);
        }
    }

    /**
     * Метод для отображения всплывающего сообщения в случае, если что-то пойдет неправильно и перевода не будет, либо возникнет ошибка на стороне Яндекса.
     * @param code - Код ошибки.
     * @param code_message - Текст сообщения.
     */
    private void showCodeMessage(int code, String code_message){
        Toast.makeText(this.getActivity().getWindow().getContext(), code_message + " Код: " +
                code, Toast.LENGTH_LONG)
                .show();
    }

    /**
     * Метод для спиннера с выбором языка с которого нужно перевести.
     */
    private void setupLanguageFromSpinner(View view){
        ArrayAdapter<String> adapterSpinnerFrom = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, LANG_SHORT_ARRAY_FULL);
        adapterSpinnerFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLangFrom = (Spinner) view.findViewById(R.id.spinnerLangFrom);
        spinnerLangFrom.setAdapter(adapterSpinnerFrom);
        spinnerLangFrom.setSelection(START_LANG_FROM);
        spinnerLangFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stringLangFrom = LANG_SHORT_ARRAY[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Метод для спиннера с выбором языка на который нужно перевести.
     */
    private void setupLanguageToSpinner(View view){
        ArrayAdapter<String> adapterSpinnerTo = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, LANG_SHORT_ARRAY_FULL);
        adapterSpinnerTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLangTo = (Spinner)view.findViewById(R.id.spinnerLangTo);
        spinnerLangTo.setAdapter(adapterSpinnerTo);
        spinnerLangTo.setSelection(START_LANG_TO);
        spinnerLangTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stringLangTo = LANG_SHORT_ARRAY[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
