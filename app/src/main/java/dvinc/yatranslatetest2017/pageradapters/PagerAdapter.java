package dvinc.yatranslatetest2017.pageradapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dvinc.yatranslatetest2017.fragments.*;

/**
 * Created by Space 5 on 30.03.2017.
 *
 */

/**
 * Класс адаптера для заполнения страниц.
 * Содержит в себе фрагменты: Перевод, История, Инфо.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TranslateFragment();
            case 1:
                setDefaultValue();
                return new MainHistoryFragment();
            case 2:
                setDefaultValue();
                return new InfoFragment();
            default:
                return null;
        }
    }

    /* При переходе на новый фрагмент устанавливаем значения по умолчанию для нового перевода в фрагменте Перевод. */
    private void setDefaultValue(){
        TranslateFragment.current_id = 0;
        TranslateFragment.chooseBookmark = "0";
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}