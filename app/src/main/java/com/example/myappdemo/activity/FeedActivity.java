package com.example.myappdemo.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myappdemo.feed.FeedAdapter;
import com.example.myappdemo.R;
import com.example.myappdemo.callback.FeedItemLongClickListener;
import com.example.myappdemo.callback.GetUsersCallback;
import com.example.myappdemo.database.User;
import com.example.myappdemo.database.UserViewModel;
import com.example.myappdemo.feed.FeedDataManager;

import java.util.List;

public class FeedActivity extends AppCompatActivity {

    RecyclerView recyclerView1;
    FeedAdapter feedAdapter1;
    UserViewModel userViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoadingMore = false; // 避免重复加载的标志

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

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright
        );
        recyclerView1 = findViewById(R.id.recyclerView1);
        feedAdapter1 = new FeedAdapter();
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(feedAdapter1);
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);
        FeedDataManager dataManager = new FeedDataManager();

        //数据刷新
        userViewModel.getAllUsers(new GetUsersCallback() {
            @Override
            public void onGetUsersResult(List<User> users) {
                feedAdapter1.setAllUsers(users);
                feedAdapter1.notifyDataSetChanged();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //展示FEED流
                userViewModel.getAllUsers(new GetUsersCallback() {
                    @Override
                    public void onGetUsersResult(List<User> users) {
                        feedAdapter1.setAllUsers(users);
                        feedAdapter1.notifyDataSetChanged();
                        //刷新后取消下拉动画
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        });

        //滚动加载更多
        recyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        //长按删卡
        feedAdapter1.FeedAdapterBuild(dataManager, new FeedItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, User user) {
                new AlertDialog.Builder(FeedActivity.this)
                        .setTitle("删除信息")
                        .setMessage("要删除「" + user.getName() + "」吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            feedAdapter1.removeUser(position);
                            dialog.dismiss();
                        })
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }

    // 加载更多（追加数据）
    private void loadMoreUser() {
        isLoadingMore = true;
        feedAdapter1.setLoading(true); // 显示加载中
        userViewModel.getAllUsers(users -> {
            isLoadingMore = false;
            feedAdapter1.setLoading(false); // 隐藏加载中
            feedAdapter1.addUsers(users); // 追加数据
        });
    }
}
