package dvinc.yatranslatetest2017;

/**
 * Created by Space 5 on 30.03.2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dvinc.yatranslatetest2017.fragments.*;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        //TODO: пофиксить предупреждение

        switch (position) {
            case 0:
                TranslateFragment tab1 = new TranslateFragment();
                return tab1;
            case 1:
                HistoryFragment tab2 = new HistoryFragment();
                return tab2;
            case 2:
                FavouritesFragment tab3 = new FavouritesFragment();
                return tab3;
            case 3:
                InfoFragment tab4 = new InfoFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}