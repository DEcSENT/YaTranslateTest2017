package dvinc.yatranslatetest2017.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import dvinc.yatranslatetest2017.database.HistoryContract;
import dvinc.yatranslatetest2017.pageradapters.HistoryPagerAdapter;
import dvinc.yatranslatetest2017.R;
/**
 * Created by Space 5 on 13.04.2017.
 *
 */

/**
 * Класс для фрагмента, содержащего в себе два других фрагмента История и Избранное.
 */
public class MainHistoryFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_history, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layoutTest);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.history_fragment_head));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.favourites_fragment_head));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pagerTest);
        final HistoryPagerAdapter adapter = new HistoryPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Метод для удаления всех записей из истории.
     */
    private void deleteAllHistory() {
        int rowsDeleted = getActivity().getApplicationContext().getContentResolver().delete(HistoryContract.HistoryEntry.CONTENT_URI, null, null);
        Log.v("HistoryFragment", "Удалено записей из истории: " + rowsDeleted );
        Toast.makeText(this.getActivity().getWindow().getContext(), "Удалено записей из истории: " +
                rowsDeleted, Toast.LENGTH_LONG)
                .show();
    }

    /* Создаем меню в фрагменте. */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* По клику на иконку в меню вызываем предупреждающий диалог для удаления всех записей из истории. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_history:
                showDeleteConfirmationDialog();
                break;
        }
        return true;
    }

    /**
     * Предупреждающий диалог для удаления всех записей из истории.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity().getWindow().getContext());
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_history_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllHistory();
            }
        });
        builder.setNegativeButton(R.string.delete_history_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
