package io.github.izzyleung.zhihudailypurify.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.github.izzyleung.zhihudailypurify.R;
import io.github.izzyleung.zhihudailypurify.adapter.DateHeaderAdapter;
import io.github.izzyleung.zhihudailypurify.adapter.NewsAdapter;
import io.github.izzyleung.zhihudailypurify.bean.DailyNews;

public class SearchNewsFragment extends Fragment {
    private List<DailyNews> newsList = new ArrayList<>();

    private NewsAdapter mAdapter;
    private DateHeaderAdapter mHeaderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        assert view != null;

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.search_result_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new NewsAdapter(newsList,getActivity());
        mHeaderAdapter = new DateHeaderAdapter(newsList);

        StickyHeadersItemDecoration header = new StickyHeadersBuilder()
                .setAdapter(mAdapter)
                .setRecyclerView(mRecyclerView)
                .setStickyHeadersAdapter(mHeaderAdapter)
                .build();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(header);

        return view;
    }

    public void updateContent(List<DailyNews> newsList) {
        mHeaderAdapter.setNewsList(newsList);
        mAdapter.updateNewsList(newsList);
    }
}
