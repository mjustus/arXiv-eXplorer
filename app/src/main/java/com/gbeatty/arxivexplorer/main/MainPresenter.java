package com.gbeatty.arxivexplorer.main;

import com.gbeatty.arxivexplorer.arxivdata.Categories;
import com.gbeatty.arxivexplorer.base.BasePresenter;
import com.gbeatty.arxivexplorer.helpers.Tags;
import com.gbeatty.arxivexplorer.network.ArxivAPI;
import com.gbeatty.arxivexplorer.settings.SharedPreferencesView;

import okhttp3.Call;

class MainPresenter extends BasePresenter {

    private final MainView view;

    MainPresenter(MainView view, SharedPreferencesView sharedPreferencesView) {
        super(sharedPreferencesView);
        this.view = view;
    }

    boolean onNavigationItemSelected(int id) {

        if (view.getCurrentFragment().getTag() == null) return false;

        switch (id) {

            case 0:
                if (view.getCurrentFragment().getTag().equals(Tags.DASHBOARD_FRAGMENT_TAG)){
                    view.refreshPaperList();
                    return false;
                }
                switchToDashboardFragment();
                return true;

            case 1:
                if (view.getCurrentFragment().getTag().equals(Tags.FAVORITES_FRAGMENT_TAG)){
                    view.refreshPaperList();
                    return false;
                }
                switchToFavoritesFragment();
                return true;

            case 2:
                if (view.getCurrentFragment().getTag().equals(Tags.DOWNLOADED_FRAGMENT_TAG)){
                    view.refreshPaperList();
                    return false;
                }
                switchToDownloadedFragment();
                return true;

            case 3:
                if (view.getCurrentFragment().getTag().equals(Tags.MAIN_CATEGORIES_TAG)){
                    return false;
                }
                switchToCategoriesFragment();
                return true;
        }
        return false;
    }

    private void switchToDownloadedFragment() {
        view.switchToDownloadedFragment(Tags.DOWNLOADED_FRAGMENT_TAG);
    }

    public void switchToCategoriesFragment() {
        view.switchToCategoriesFragment(Categories.CATEGORIES, Tags.MAIN_CATEGORIES_TAG);
    }

    private void switchToFavoritesFragment() {
        view.switchToFavoritesFragment(Tags.FAVORITES_FRAGMENT_TAG);
    }

    public void switchToDashboardFragment() {
        view.switchToDashboardFragment(Tags.DASHBOARD_FRAGMENT_TAG);
    }

    public void cancelHttpCalls() {
        for (Call call : ArxivAPI.getClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : ArxivAPI.getClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    public void onQueryTextSubmit(String searchQuery) {
        view.switchToSearchFragment(searchQuery, Tags.SEARCH_RESULTS_TAG);
    }

    public void navigationSettingsClicked() {
        view.goToSettings();
    }

    public void navigationRatingClicked() {
        view.goToRating();
    }

    public void navigationDonateClicked() {
        view.goToDonate();
    }
}
