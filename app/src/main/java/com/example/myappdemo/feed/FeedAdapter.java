package com.example.myappdemo.feed;

import static com.example.myappdemo.constant.FeedConstant.TYPE_LOADING;
import static com.example.myappdemo.constant.FeedConstant.TYPE_TEXT;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappdemo.R;
import com.example.myappdemo.callback.FeedItemLongClickListener;
import com.example.myappdemo.database.User;
import com.example.myappdemo.feed.viewholder.FeedViewHolder;
import com.example.myappdemo.feed.viewholder.LoadingFeedViewHolder;
import com.example.myappdemo.feed.viewholder.TextFeedViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private boolean isLoading = false; //加载状态标志
    private FeedItemLongClickListener longClickListener;
    private FeedDataManager dataManager;


    public void FeedAdapterBuild(FeedDataManager dataManager, FeedItemLongClickListener listener) {
        this.dataManager = dataManager;
        this.longClickListener = listener;
    }

    // 设置加载状态
    @SuppressLint("NotifyDataSetChanged")
    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }

    // 设置卡片类型ViewType
    @Override
    public int getItemViewType(int position) {

        if (isLoading && position == getItemCount() - 1) {
            return TYPE_LOADING;
        }

        User user = dataManager.getUser(position);//之后直接根据user判断type
        return TYPE_TEXT;
    }

    // 刷新数据
    @SuppressLint("NotifyDataSetChanged")
    public void setAllUsers(List<User> allUsers) {
        dataManager.setAllUsers(allUsers);
        notifyDataSetChanged();
    }

    // 加载更多数据
    public void addUsers(List<User> newUsers) {
        int startPos = getItemCount();
        dataManager.addUsers(newUsers);
        notifyItemRangeInserted(startPos, getItemCount() - startPos);//局部刷新，提升性能
    }

    //删除指定位置数据
    public void removeUser(int position) {
        dataManager.removeUser(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }


    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_LOADING:
                View loadingView = layoutInflater.inflate(R.layout.cell_loading, parent, false);
                return new LoadingFeedViewHolder(loadingView);
            case TYPE_TEXT:
                View textView = layoutInflater.inflate(R.layout.cell_card1, parent, false);
                return new TextFeedViewHolder(textView);
            default:
                throw new IllegalArgumentException("未知ViewType：" + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder.getItemViewType() == TYPE_LOADING) {
            return;
        }
        User user = dataManager.getUser(position);
        holder.bindData(user);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(position, user);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataManager.getItemCount();
    }

}

