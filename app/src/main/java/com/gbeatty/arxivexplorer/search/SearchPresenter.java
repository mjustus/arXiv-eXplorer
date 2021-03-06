package com.gbeatty.arxivexplorer.search;

import com.gbeatty.arxivexplorer.models.Paper;
import com.gbeatty.arxivexplorer.network.ArxivAPI;
import com.gbeatty.arxivexplorer.network.Parser;
import com.gbeatty.arxivexplorer.paper.list.PapersPresenter;
import com.gbeatty.arxivexplorer.paper.list.PapersView;
import com.gbeatty.arxivexplorer.settings.SharedPreferencesView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

class SearchPresenter extends PapersPresenter {

    private final String searchQuery;

    public SearchPresenter(PapersView view, SharedPreferencesView sharedPreferencesView, String searchQuery) {
        super(view, sharedPreferencesView);
        this.searchQuery = searchQuery;
    }

    @Override
    public void getPapers() {
        try {
//            getView().setRefreshing(true);
            ArxivAPI.searchAll(searchQuery,
                    getSharedPreferenceView().getSortOrder(),
                    getSharedPreferenceView().getSortBy(),
                    getSharedPreferenceView().getMaxResult(),
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            errorLoading();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                List<Paper> papers = Parser.parse(responseBody.byteStream());
                                responseBody.close();

                                setQuery(response.request().url().toString());
                                updatePapers(papers);

                            } catch (XmlPullParserException | ParseException e) {
                                errorLoading();
                            }
                        }
                    });
        } catch (Exception e) {
            errorLoading();
        }
    }

}
