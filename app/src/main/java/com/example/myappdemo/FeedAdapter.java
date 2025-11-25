package com.example.myappdemo;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOADING = 1;
    List<User> allUsers = new ArrayList<>();
    private boolean isLoading = false; //加载状态标志

    // 设置加载状态
    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }
    // 判断是否正在加载
    public boolean isLoading() {
        return isLoading;
    }

    // 刷新的时候重排数据
    public void setAllUsers(List<User> allUsers) {
        List<User> shuffledUsers = new ArrayList<>(allUsers);
        Collections.shuffle(shuffledUsers);
        this.allUsers.clear();
        this.allUsers.addAll(shuffledUsers);
        notifyDataSetChanged();
    }

    // 加载更多（追加数据）
    public void addUsers(List<User> newUsers) {
        List<User> shuffledUsers = new ArrayList<>(newUsers);
        Collections.shuffle(shuffledUsers);
        int startPos = this.allUsers.size();
        this.allUsers.addAll(shuffledUsers);
        //局部刷新，提升性能
        notifyItemRangeInserted(startPos, shuffledUsers.size());
    }

    //长按删卡功能
    //回调接口
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, User user);
    }

    private OnItemLongClickListener longClickListener;

    // 设置长按监听
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    //删除指定位置数据
    public void removeUser(int position) {
        if (position >= 0 && position < allUsers.size()) {
            allUsers.remove(position);
            notifyItemRemoved(position); //局部刷新，带动画
            // 修复删除后位置错乱问题
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //显示加载中
        if (isLoading && position == getItemCount() - 1) {
            return TYPE_LOADING;
        }
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.cell_card1, parent, false);
            return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = allUsers.get(position);
        holder.textViewName.setText(user.getName());
        holder.textViewSign.setText(user.getAccount());
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
        return allUsers.size() ;
    }

    //ViewHolder
    static class FeedViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewSign;
        ProgressBar progressBar;
        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewSign = itemView.findViewById(R.id.textViewSign);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }


}

