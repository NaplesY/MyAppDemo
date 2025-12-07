package com.example.myappdemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myappdemo.feed.FeedListManager;
import com.example.myappdemo.feed.cardexposure.CardExposureCallback;
import com.example.myappdemo.feed.cardexposure.CardExposureLogTool;
import com.example.myappdemo.feed.cardexposure.CardExposureManager;
import com.example.myappdemo.feed.adapter.FeedAdapter;
import com.example.myappdemo.R;
import com.example.myappdemo.callback.FeedItemLongClickListener;
import com.example.myappdemo.data.User;
import com.example.myappdemo.data.UserViewModel;
import com.example.myappdemo.feed.card.FeedCardRegistry;
import com.example.myappdemo.feed.FeedDataManager;
import com.example.myappdemo.feed.card.AvatarFeedCard;
import com.example.myappdemo.feed.card.TextFeedCard;
import com.example.myappdemo.feed.card.VideoFeedCard;
import com.example.myappdemo.feed.adapter.viewholder.FeedViewHolder;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private UserViewModel userViewModel;
    private Switch switchGridLayout;
    private Switch switchLog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager gridLayoutManager;
    private CardExposureManager cardExposureManager;
    private FeedListManager feedListManager;
    private TextView textViewLog;
    private ScrollView scrollViewLog;
    private CardExposureLogTool logTool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUiViews();// 初始化UI
        initViewModel();// 初始化ViewModel
        initFeedList();// 初始化Feed流相关
        initExposureManager(); //初始化曝光时间相关
        bindUiEvents();// UI交互事件

        userViewModel.initUsers();// 初始化数据

        // 在此注册卡片（先注册优先）
        FeedCardRegistry registry = FeedCardRegistry.getInstance();

        registry.registerCard(new VideoFeedCard());
        registry.registerCard(new AvatarFeedCard());
        registry.registerCard(new TextFeedCard());

    }


    // 初始化UI
    private void initUiViews() {
        switchGridLayout = findViewById(R.id.switchGridLayout);
        switchLog = findViewById(R.id.switchLog);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView1);
        scrollViewLog = findViewById(R.id.scrollViewLog);
        textViewLog = findViewById(R.id.textViewLog);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        logTool = new CardExposureLogTool(scrollViewLog, textViewLog);
    }

    // 初始化VM
    private void initViewModel() {
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);

        // 观察LiveData变化
        userViewModel.getAllUsersLive().observe(this, users -> {
            feedAdapter.setAllUsers(users);
            feedAdapter.notifyDataSetChanged();
        });

        userViewModel.isLoadingMore().observe(this, isLoading -> {
            feedListManager.setLoadingMore(isLoading);
            feedAdapter.setLoading(isLoading);
        });

        userViewModel.isRefreshing().observe(this, isRefreshing ->
                swipeRefreshLayout.setRefreshing(isRefreshing));
    }

    // 调用Feed流逻辑
    private void initFeedList() {
        FeedDataManager dataManager = new FeedDataManager();
        feedAdapter = new FeedAdapter(dataManager);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(feedAdapter);
        // 卡片列数控制，加载更多
        feedListManager = new FeedListManager(recyclerView, gridLayoutManager, new FeedListManager.LoadMoreCallback() {
            @Override
            public void onLoadMore() {
                userViewModel.loadMoreUsers();
            }
        });
        // 长按删卡
        feedAdapter.onFeedItemLongClickListener(new FeedItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, User user) {
                new AlertDialog.Builder(FeedActivity.this)
                        .setTitle("删除信息")
                        .setMessage("要删除「" + user.getName() + "」吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            feedAdapter.removeUser(position);
                            dialog.dismiss();

                        })
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }

    // 调用曝光事件逻辑
    private void initExposureManager() {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        cardExposureManager = new CardExposureManager(recyclerView, layoutManager, new CardExposureCallback() {
            @Override
            public void onCardStartExpose(int position, float visibleRatio) {
                int cardId = feedAdapter.getCardId(position);
                String log = "开始露出 | cardId: " + cardId + " | 曝光比例: " + String.format("%.2f", visibleRatio);
                Log.d("CardExposure", log);
                logTool.addLog(log);
            }

            @Override
            public void onCardHalfExpose(int position, float visibleRatio) {
                int cardId = feedAdapter.getCardId(position);
                String log = "露出50% | cardId: " + cardId + " | 曝光比例: " + String.format("%.2f", visibleRatio);
                Log.d("CardExposure", log);
                logTool.addLog(log);
            }

            @Override
            public void onCardFullyExpose(int position, float visibleRatio) {
                int cardId = feedAdapter.getCardId(position);
                String log = "完整露出 | cardId: " + cardId + " | 曝光比例: " + String.format("%.2f", visibleRatio);
                Log.d("CardExposure", log);
                logTool.addLog(log);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder instanceof FeedViewHolder) {
                    feedAdapter.playVideo((FeedViewHolder) viewHolder, position);
                }
            }

            @Override
            public void onCardDisappear(RecyclerView.ViewHolder holder, int position) {
                int cardId = feedAdapter.getCardId(position);
                String log = "卡片消失 | cardId: " + cardId;
                Log.d("CardExposure", log);
                logTool.addLog(log);
                if (holder instanceof FeedViewHolder) {
                    feedAdapter.stopVideo((FeedViewHolder) holder, position);
                }
            }
        });

    }

    // 控制UI交互
    private void bindUiEvents() {
        // 开关--切换单双列
        switchGridLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                feedListManager.switchSpanCount(isChecked);
                if (isChecked) {
                    Toast.makeText(FeedActivity.this, "切换为双列", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedActivity.this, "切换为单列", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 开关--显示曝光时间日志
        switchLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    scrollViewLog.setVisibility(View.VISIBLE);
                    Toast.makeText(FeedActivity.this, "显示日志", Toast.LENGTH_SHORT).show();
                } else {
                    scrollViewLog.setVisibility(View.INVISIBLE);
                    Toast.makeText(FeedActivity.this, "关闭日志", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userViewModel.refreshUsers();
            }
        });
    }

}

  



