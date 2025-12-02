package com.example.myappdemo.activity;

import static com.example.myappdemo.feed.feedAdapter.TYPE_LOADING;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myappdemo.callback.CardExposureListener;
import com.example.myappdemo.feed.CardExposureCalculator;
import com.example.myappdemo.feed.CardExposureLogTool;
import com.example.myappdemo.feed.CardExposureState;
import com.example.myappdemo.feed.feedAdapter;
import com.example.myappdemo.R;
import com.example.myappdemo.callback.FeedItemLongClickListener;
import com.example.myappdemo.callback.GetUsersCallback;
import com.example.myappdemo.database.User;
import com.example.myappdemo.database.UserViewModel;
import com.example.myappdemo.feed.FeedCardRegistry;
import com.example.myappdemo.feed.FeedDataManager;
import com.example.myappdemo.feed.card.AvatarFeedCard;
import com.example.myappdemo.feed.card.TextFeedCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity implements CardExposureListener {

    RecyclerView recyclerView;
    feedAdapter feedAdapter;
    UserViewModel userViewModel;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch swGridLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager gridLayoutManager;
    private TextView textViewLog;
    private ScrollView scrollView;
    private CardExposureLogTool logTool;
    private boolean isLoadingMore = false; // 避免重复加载的标志
    private Map<Integer, CardExposureState> exposureStateMap = new HashMap<>();

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

        swGridLayout = findViewById(R.id.switchGridLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        recyclerView = findViewById(R.id.recyclerView1);
        scrollView = findViewById(R.id.scrollView);
        textViewLog = findViewById(R.id.textViewLog);
        FeedDataManager dataManager = new FeedDataManager();
        feedAdapter = new feedAdapter(dataManager);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(feedAdapter);
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);
        logTool = new CardExposureLogTool(scrollView, textViewLog);

        FeedCardRegistry registry = FeedCardRegistry.getInstance();
        cardExposureListeners();

        //在此注册卡片
        registry.registerCard(new AvatarFeedCard());
        registry.registerCard(new TextFeedCard());


        //数据刷新
        userViewModel.getAllUsers(new GetUsersCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onGetUsersResult(List<User> users) {
                feedAdapter.setAllUsers(users);
                feedAdapter.notifyDataSetChanged();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //展示FEED流
                userViewModel.getAllUsers(new GetUsersCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onGetUsersResult(List<User> users) {
                        feedAdapter.setAllUsers(users);
                        feedAdapter.notifyDataSetChanged();
                        //刷新后取消下拉动画
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        });

        //滚动加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) return;
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm == null) return;
                // 最后一个可见项的位置
                int lastVisiblePos = lm.findLastVisibleItemPosition();
                // 总Item数
                int totalItemCount = lm.getItemCount();
                //条件：滑到最后一项 + 不在加载中 + 有数据
                if (lastVisiblePos == totalItemCount - 1
                        && !isLoadingMore
                        && totalItemCount > 0) {
                    loadMoreUser(); // 加载更多
                }
            }
        });
        //确保加载动画只有一列
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemType = feedAdapter.getItemViewType(position);
                if (gridLayoutManager.getSpanCount() == 2) {
                    return itemType == TYPE_LOADING ? 2 : 1;
                } else {
                    return 1; // 单列时所有Item都占1列
                }
            }
        });

        //长按删卡
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

        //切换单双列
        swGridLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                gridLayoutManager.setSpanCount(isChecked ? 2 : 1);
                feedAdapter.notifyDataSetChanged();
                if (isChecked) {
                    Toast.makeText(FeedActivity.this, "切换为双列", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedActivity.this, "切换为单列", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    // 加载更多（追加数据）
    private void loadMoreUser() {
        isLoadingMore = true;
        feedAdapter.setLoading(true); // 显示加载中
        userViewModel.getAllUsers(users -> {
            isLoadingMore = false;
            feedAdapter.setLoading(false); // 隐藏加载中
            feedAdapter.addUsers(users); // 追加数据
        });
    }

    // recyclerview监听器-何时检查卡片曝光状态
    private void cardExposureListeners() {
        //滚动时和滚动停止时
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    checkAllCards();
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkAllCards();
                }
            }
        });
        //布局改变（单双列切换、刷新……）
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.postDelayed(checkAllCards(), 100);//防止View加载慢，延迟100ms
            }
        });
        //初始化时
        recyclerView.postDelayed(checkAllCards(), 100);
    }



    @Override
    public void onCardStartExpose(int cardId, float visibleRatio) {
        String log = "开始露出 | cardId: " + cardId + " | 曝光比例: " + String.format("%.2f", visibleRatio);
        Log.d("CardExposure", log);
        logTool.addLog(log);
    }

    @Override
    public void onCardHalfExpose(int cardId, float visibleRatio) {
        String log = "露出50% | cardId: " + cardId + " | 曝光比例: " + String.format("%.2f", visibleRatio);
        Log.d("CardExposure", log);
        logTool.addLog(log);
    }

    @Override
    public void onCardFullyExpose(int cardId, float visibleRatio) {
        String log = "完整露出 | cardId: " + cardId + " | 曝光比例: " + String.format("%.2f", visibleRatio);
        Log.d("CardExposure", log);
        logTool.addLog(log);
    }

    @Override
    public void onCardDisappear(int cardId) {
        String log = "卡片消失 | cardId: " + cardId;
        Log.d("CardExposure", log);
        logTool.addLog(log);
    }

    private Runnable checkAllCards() {
        if (feedAdapter == null || feedAdapter.getItemCount() == 0){
            return null;
        }
        // 遍历所有卡片
        for (int position = 0; position < feedAdapter.getItemCount(); position++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            View cardView = viewHolder != null ? viewHolder.itemView : null;
            float visibleRatio = 0.0f;
            int cardId = feedAdapter.getCardId(position);
            // 获取曝光状态or新建
            CardExposureState exposureState = exposureStateMap.computeIfAbsent(cardId, i -> new CardExposureState(i, false, false, false, true));
            // 计算曝光比例
            if (cardView != null) {
                visibleRatio = CardExposureCalculator.calculateVisibleRatio(cardView);
            }
            // 判断曝光状态
            updateCardExposureState(cardId, visibleRatio, exposureState);
        }
        return null;

    }
    //根据曝光比例更新曝光状态，触发回调
    private void updateCardExposureState(int cardId, float visibleRatio, CardExposureState exposureState) {
        if (exposureState == null) {
            return;
        }
        boolean currentVisible = visibleRatio > 0.0f;
        boolean lastVisible = !exposureState.isHasDisappear();
        // 卡片露出
        if (currentVisible){
            if (!exposureState.isHasExposed()){
                onCardStartExpose(cardId, visibleRatio);
                exposureState.setHasExposed(true);
            }
            // 卡片露出超过50%
            if (visibleRatio > 0.5f && !exposureState.isHasHalfExposed()){
                onCardHalfExpose(cardId, visibleRatio);
                exposureState.setHasHalfExposed(true);
            }
            // 卡片完整露出
            if (visibleRatio == 1.0f && !exposureState.isHasFullyExposed()){
                onCardFullyExpose(cardId, visibleRatio);
                exposureState.setHasFullyExposed(true);
            }
        }
        // 卡片消失
        if (lastVisible && !currentVisible){
            onCardDisappear(cardId);
            exposureState.reset();
        }
        exposureState.setHasDisappear(!currentVisible);
    }

}
