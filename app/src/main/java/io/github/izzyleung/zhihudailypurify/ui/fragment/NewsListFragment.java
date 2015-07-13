package io.github.izzyleung.zhihudailypurify.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.izzyleung.zhihudailypurify.R;
import io.github.izzyleung.zhihudailypurify.ZhihuDailyPurifyApplication;
import io.github.izzyleung.zhihudailypurify.adapter.NewsAdapter;
import io.github.izzyleung.zhihudailypurify.bean.DailyNews;
import io.github.izzyleung.zhihudailypurify.task.AccelerateGetNewsTask;
import io.github.izzyleung.zhihudailypurify.task.BaseGetNewsTask;
import io.github.izzyleung.zhihudailypurify.task.OriginalGetNewsTask;

public class NewsListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, BaseGetNewsTask.UpdateUIListener {
    private List<DailyNews> newsList = new ArrayList<>();

    private NewsAdapter mAdapter;
    private String date;
    private boolean isAutoRefresh;
    private boolean isToday;
    // Fragment is single in PortalActivity
    private boolean isSingle;
    private boolean isRefreshed = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        new RecoverNewsListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            date = bundle.getString("date");
            isToday = bundle.getBoolean("first_page?");
            isSingle = bundle.getBoolean("single?");

            setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        assert view != null;
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.news_list);
        mRecyclerView.setHasFixedSize(!isToday);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mAdapter = new NewsAdapter(newsList,getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_primary);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isAutoRefresh = pref.getBoolean("auto_refresh?", true);

        refreshIf((isToday || isSingle) && isAutoRefresh && !isRefreshed);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        refreshIf(isVisibleToUser && isAutoRefresh && !isRefreshed);
    }

    private void refreshIf(boolean prerequisite) {
        if (prerequisite) {
            doRefresh();
        }
    }

    private void doRefresh() {
        if (isToday) {
            new OriginalGetNewsTask(date, this).execute();
        } else {
            SharedPreferences sharedPreferences
                    = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (sharedPreferences.getBoolean("using_accelerate_server?", false)) {
                new AccelerateGetNewsTask(date, this).execute();
            } else {
                new OriginalGetNewsTask(date, this).execute();
            }
        }
    }

    @Override
    public void onRefresh() {
        doRefresh();
    }

    @Override
    public void beforeTaskStart() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void afterTaskFinished(List<DailyNews> resultList, boolean isRefreshSuccess, boolean isContentSame) {
        mSwipeRefreshLayout.setRefreshing(false);
        isRefreshed = true;

        if (isRefreshSuccess) {
            if (!isContentSame) {
                newsList = resultList;

                mAdapter.updateNewsList(newsList);
            }
        } else if (isAdded()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private class RecoverNewsListTask extends AsyncTask<Void, Void, List<DailyNews>> {

        @Override
        protected List<DailyNews> doInBackground(Void... params) {
            return ZhihuDailyPurifyApplication.getDataSource().newsOfTheDay(date);
        }

        @Override
        protected void onPostExecute(List<DailyNews> newsListRecovered) {
            if (newsListRecovered != null) {
                newsList = newsListRecovered;

                for (DailyNews news : newsList) {
                    news.setDate(date);
                }

                mAdapter.updateNewsList(newsList);
            }
        }
    }
}