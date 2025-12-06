package com.example.myappdemo.feed;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappdemo.callback.FeedItemLongClickListener;
import com.example.myappdemo.R;
import com.example.myappdemo.database.User;
import com.example.myappdemo.feed.card.FeedCard;
import com.example.myappdemo.feed.card.LoadingFeedCard;
import com.example.myappdemo.feed.viewholder.FeedViewHolder;
import com.example.myappdemo.feed.viewholder.LoadingFeedViewHolder;
import com.example.myappdemo.feed.viewholder.VideoFeedViewHolder;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    public static final int TYPE_LOADING = 0;
    private boolean isLoading = false; //加载状态标志
    private FeedItemLongClickListener longClickListener;
    private final FeedDataManager dataManager;

    public FeedAdapter(FeedDataManager dataManager) {
        this.dataManager = dataManager;
    }


    public void onFeedItemLongClickListener(FeedItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    // 设置加载状态
    @SuppressLint("NotifyDataSetChanged")
    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
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

    // 设置卡片类型viewType
    @Override
    public int getItemViewType(int position) {
        //加载状态
        if (isLoading && position == getItemCount() - 1) {
            return TYPE_LOADING;
        }
        //根据user判断viewType
        User user = dataManager.getUser(position);
        FeedCard card = FeedCardRegistry.getInstance().chooseCardForUser(user);
        return card.getViewType();
    }

    // 获取用户id
    public int getCardId(int position) {
        User user = dataManager.getUser(position);
        return user.getId();
    }

    public void playVideo(@NonNull FeedViewHolder holder, int position){
        User user = dataManager.getUser(position);
        if (holder instanceof VideoFeedViewHolder) {
            holder.startPlay(user.getVideoDuration());
        }
    }
    public void stopVideo(@NonNull FeedViewHolder holder, int position){
        if (holder instanceof VideoFeedViewHolder) {
            holder.stopPlay();
        }
    }

    // 创建ViewHolder
    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FeedCard feedcard = FeedCardRegistry.getInstance().findCardByViewType(viewType);
        if (feedcard == null) {
            feedcard = new LoadingFeedCard();
        }
        
        View itemview = layoutInflater.inflate(feedcard.getLayoutResId(), parent, false);
        return feedcard.createViewHolder(itemview);

    }

    // 给ViewHolder绑定数据
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

    // getItemCount
    @Override
    public int getItemCount() {
        return dataManager.getItemCount();
    }

}

