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
 * TODO: описание класса
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
                // Здесь и ниже переменная зануляется TODO: больше написать
                TranslateFragment.current_id = 0;
                return new MainHistoryFragment();
            case 2:
                TranslateFragment.current_id = 0;
                return new InfoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}