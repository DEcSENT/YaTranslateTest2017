package dvinc.yatranslatetest2017;

/**
 * Created by Space 5 on 13.04.2017.
 *
 */

/**
 * Класс для хранения переменных, используемых в API Яндекс.Переводчика.
 */
public class YandexApiData {
    public static final String API_KEY = "trnsl.1.1.20170328T143200Z.d9c846c67aa9b21b.509d1e3c9fab6c4803e6c85b6ff90749f02484e2";
    public static final String YA_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=";

    // TODO: добавить больше языков

    public static final String[] LANG_SHORT_ARRAY = {"en","ar","ba","be","el","da","he","it","es","zh","ko","la","de","no","fa",
            "pl","pt","ru","uk","fr","sv","eo","ja"};
    public static final String[] LANG_SHORT_ARRAY_FULL = {"Английский","Арабский","Башкирский","Белорусский","Греческий","Датский","Иврит","Итальянский","Испанский",
            "Китайский","Корейский","Латынь","Немецкий","Норвежский","Персидский",
            "Польский","Португальский","Русский","Украинский","Французский",
            "Шведский","Эсперанто","Японский"};
}
