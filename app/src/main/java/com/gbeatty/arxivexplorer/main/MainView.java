package com.gbeatty.arxivexplorer.main;


import android.support.v4.app.Fragment;

import com.gbeatty.arxivexplorer.models.Category;

interface MainView {

    void switchToCategoriesFragment(Category[] categories, String tag);

    void switchToFavoritesFragment(String tag);

    void switchToDashboardFragment(String tag);

    void switchToSearchFragment(String searchQuery, String tag);

    Fragment getCurrentFragment();

    void goToSettings();

    void goToRating();

    void goToDonate();

    void switchToDownloadedFragment(String downloadedFragmentTag);

    void refreshPaperList();
}
