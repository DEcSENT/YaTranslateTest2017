package dvinc.yatranslatetest2017.fragments;

import android.content.ContentValues;
import android.content.Context;
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;

import dvinc.yatranslatetest2017.R;
import dvinc.yatranslatetest2017.database.HistoryContract.*;

/**
 * Created by Space 5 on 28.03.2017.
 */

// TODO: добавить больше комментариев.

/**
 * Класс для фрагмента с переводчиком текста.
 */
public class TranslateFragment extends Fragment{

    private static final String API_KEY = "trnsl.1.1.20170328T143200Z.d9c846c67aa9b21b.509d1e3c9fab6c4803e6c85b6ff90749f02484e2";
    private static final String YA_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=";
    private static final String COPYRIGHT_COMMENT = "Переведено сервисом «Яндекс.Переводчик» http://translate.yandex.ru/";

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

    static final String[] LANG_SHORT_ARRAY = {"en","ar","el","it","es","zh","ko","de","no","fa",
            "pl","pt","uk","ru","fr","sv","ja"};

    static final String[] LANG_SHORT_ARRAY_FULL = {"english","arabian","hellenic","italian","spanish",
            "chinese","korean","german","norwegian","persian",
            "polish","portuguese","ukrainian","russian","french",
            "swedish","japanese"};

    static final int START_LANG_FROM = 13;
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


        getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

        buttonDeleteInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateTextInput.setText("");
                translatedText.setText("");
            }
        });



        buttonChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = spinnerLangFrom.getSelectedItemPosition();
                spinnerLangFrom.setSelection(spinnerLangTo.getSelectedItemPosition());
                spinnerLangTo.setSelection(current);
            }
        });

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

        setupLanguageFromSpiner(view);
        setupLanguageToSpiner(view);

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
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




    // TODO: проверить возможную утечку памяти?

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TRIGGER_SERACH) {
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

        private String getOutputFromUrl(String text) throws IOException {
            String translated;

            URL urlObj = new URL(YA_URL + API_KEY);
            HttpsURLConnection connection = (HttpsURLConnection)urlObj.openConnection();
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

            return translated;
        }

        @Override
        protected void onPostExecute(String output) {
            translatedText.setText(output);

            /* Передаем данные в контент провайдер */

            ContentValues values = new ContentValues();
            values.put(HistoryEntry.COLUMN_TEXT_INPUT, translateTextInput.getText().toString());
            values.put(HistoryEntry.COLUMN_TEXT_TRANSLATED, output);
            values.put(HistoryEntry.COLUMN_LANGUAGES_FROM_TO, stringLangFrom + '-' + stringLangTo);
            values.put(HistoryEntry.COLUMN_BOOKMARK, chooseBookmark);

            /* Внимание, здесь может выпасть NPE
            * TODO: пофиксить это дело
            */
            Context context = getActivity().getApplicationContext();
            if(context != null) {
                try {
                    context.getContentResolver().insert(HistoryEntry.CONTENT_URI, values);
                } catch (Exception e) {
                    Log.v("TranslateFragment", "ALARM IN onPostExecute method" + e);
                }
            }

        }
        @Override
        protected void onPreExecute() {
            translatedText.setText(R.string.connection);
        }
    }

    /**
     * Метод для спиннера с выбором языка с которого нужно перевести.
     */
    private void setupLanguageFromSpiner(View view){
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
    private void setupLanguageToSpiner(View view){
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
