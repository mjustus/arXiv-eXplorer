package com.gbeatty.arxivexplorer.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gbeatty.arxivexplorer.arxivdata.Categories;
import com.gbeatty.arxivexplorer.models.Category;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class DashboardSettingsActivity extends BaseSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new DashboardPrefs()).commit();
        setupActionBar();
    }

    public static class DashboardPrefs extends PreferenceFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            return view;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());

            PreferenceCategory dashboardCategories = new PreferenceCategory(getActivity());
            dashboardCategories.setTitle("Dashboard Categories");
            screen.addPreference(dashboardCategories);

            SwitchPreference toggleAll = new SwitchPreference(getActivity());
            toggleAll.setDefaultValue(true);
            toggleAll.setKey("toggle_all");
            toggleAll.setTitle("Toggle All");
            dashboardCategories.addPreference(toggleAll);

            for (Category category : Categories.CATEGORIES) {
                PreferenceCategory toggleCategory = new PreferenceCategory(getActivity());
                toggleCategory.setTitle(category.getName());
                screen.addPreference(toggleCategory);
                if (category.getSubCategories().length != 0) {
                    for (Category subCategory : category.getSubCategories()) {
                        SwitchPreference sp = new SwitchPreference(getActivity());
                        sp.setDefaultValue(true);
                        sp.setKey(subCategory.getShortName());
                        if (subCategory.getCatKey().equals("all")) {
                            sp.setTitle(category.getName() + " - " + "Toggle All");
                            sp.setOnPreferenceClickListener(preference -> {
                                for (int i = 0; i < toggleCategory.getPreferenceCount(); i++) {
                                    SwitchPreference s = (SwitchPreference) toggleCategory.getPreference(i);
                                    s.setChecked(sp.isChecked());
                                }
                                return false;
                            });
                        } else
                            sp.setTitle(subCategory.getShortName() + " - " + subCategory.getName());
                        toggleCategory.addPreference(sp);
                    }
                } else {
                    SwitchPreference sp = new SwitchPreference(getActivity());
                    sp.setDefaultValue(true);
                    sp.setKey(category.getShortName());
                    sp.setTitle(category.getShortName());
                    toggleCategory.addPreference(sp);
                }

            }
            setPreferenceScreen(screen);

            toggleAll.setOnPreferenceClickListener(preference -> {
                for (int i = 0; i < screen.getPreferenceCount(); i++) {
                    PreferenceCategory preferenceCategory = (PreferenceCategory) screen.getPreference(i);
                    for (int j = 0; j < preferenceCategory.getPreferenceCount(); j++) {
//                        if(screen.getPreference(i) instanceof SwitchPreference){
                        SwitchPreference s = (SwitchPreference) preferenceCategory.getPreference(j);
                        s.setChecked(toggleAll.isChecked());
//                        }
                    }

                }
                return true;
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
