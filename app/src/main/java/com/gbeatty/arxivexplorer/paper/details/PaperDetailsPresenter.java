package com.gbeatty.arxivexplorer.paper.details;

import com.gbeatty.arxivexplorer.R;
import com.gbeatty.arxivexplorer.models.Paper;
import com.gbeatty.arxivexplorer.network.ArxivAPI;
import com.gbeatty.arxivexplorer.paper.base.PapersPresenterBase;
import com.gbeatty.arxivexplorer.settings.SharedPreferencesView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class PaperDetailsPresenter extends PapersPresenterBase {

    private final PaperDetailsView view;
    private final Paper paper;

    PaperDetailsPresenter(PaperDetailsView view, SharedPreferencesView sharedPreferencesView, Paper paper) {
        super(sharedPreferencesView);
        this.view = view;
        this.paper = paper;
    }

    void initializeMainContent() {
        view.setTitle(paper.getTitle());
        view.setAuthors(paper.getAuthor());
        view.setSummary(paper.getSummary());
        view.setLastUpdatedDate("Updated: " + paper.getUpdatedDate());
        view.setPublishedDate("Submitted: " + paper.getPublishedDate());
        view.setPaperCategories(paper.getCategories());
    }

    void updateFavoritedMenuItem() {
        if (isPaperFavorited(paper.getPaperID())) {
            view.setFavoritedIcon();
        } else {
            view.setNotFavoritedIcon();
        }
    }

    void updateDownloadedMenuItem(){
        if(isPaperDownloaded(paper.getPaperID())){
            view.setDownloadedIcon();
        }else{
            view.setNotDownloadedIcon();
        }
    }

    private boolean isPaperDownloaded(String paperID) {
        return Paper.count(Paper.class, "paper_id = ? and downloaded = 1", new String[]{paperID}) > 0;
    }

    private void navigationFavoritePaperClicked() {
        toggleFavoritePaper(paper);
        updateFavoritedMenuItem();
    }

    private void navigationDownloadPaperClicked() {

        File papersPath = new File(view.getFilesDir(), "papers");
        File file = new File(papersPath, paper.getPaperID());

        if (file.exists()) {
            savePaperIfDoesntExist(paper);
            setPaperDownloaded(paper.getPaperID(), true);
            view.viewDownloadedPaper(file);
            return;
        }

        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        downloadPDFandView(file);
    }

    public static void setPaperDownloaded(String paperID, boolean downloaded){
        List<Paper> ps = Paper.find(Paper.class, "paper_id = ?", paperID);
        if(ps == null || ps.isEmpty()) return;
        Paper p = ps.get(0);
        p.setDownloaded(downloaded);
        p.save();
    }

    private void downloadPDFandView(File file) {
        view.showLoading();

        ArxivAPI.downloadFileFromURL(paper.getPDFURL(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                view.errorLoading();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(response.body().source());
                sink.close();

                view.dismissLoading();
                savePaperIfDoesntExist(paper);
                setPaperDownloaded(paper.getPaperID(), true);
                updateDownloadedMenuItem();
                view.viewDownloadedPaper(file);
            }
        });
    }

    boolean onNavigationItemSelected(int id) {
        switch (id) {
            case R.id.menu_favorite_paper:
                navigationFavoritePaperClicked();
                return true;
            case R.id.menu_download_paper:
                navigationDownloadPaperClicked();
                return true;
        }
        return false;
    }
}