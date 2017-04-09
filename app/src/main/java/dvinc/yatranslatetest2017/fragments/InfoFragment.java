package dvinc.yatranslatetest2017.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import dvinc.yatranslatetest2017.R;

/**
 * Created by Space 5 on 30.03.2017.
 */

/**
 * Класс для фрагмента с информацией о приложении.
 */
public class InfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }
}
