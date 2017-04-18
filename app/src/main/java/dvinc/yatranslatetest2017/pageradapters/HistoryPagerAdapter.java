package dvinc.yatranslatetest2017.pageradapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dvinc.yatranslatetest2017.fragments.*;

/**
 * Created by Space 5 on 13.04.2017.
 *
 */

/**
 * TODO: описание класса
 */
public class HistoryPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public HistoryPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HistoryFragment();
            case 1:
                return new FavouritesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}