package com.example.myappdemo.feed.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.myappdemo.R;
import com.example.myappdemo.callback.FeedItemLongClickListener;
import com.example.myappdemo.data.User;
import com.example.myappdemo.feed.FeedDataManager;
import com.example.myappdemo.feed.card.FeedCard;
import com.example.myappdemo.feed.card.FeedCardRegistry;
import com.example.myappdemo.feed.card.LoadingFeedCard;
import com.example.myappdemo.feed.adapter.viewholder.FeedViewHolder;
import com.example.myappdemo.feed.adapter.viewholder.VideoFeedViewHolder;
import com.example.myappdemo.utils.FeedViewPrerenderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> implements ListPreloader.PreloadModelProvider<String> {

    public static final int TYPE_LOADING = 0;
    private boolean isLoading = false; //加载状态标志
    private FeedItemLongClickListener longClickListener;
    private final FeedDataManager dataManager;
    private final RequestOptions imageOptions;
    private final Context context;
    private final ViewPreloadSizeProvider<String> preloadSizeProvider;

    public FeedAdapter(Context context, FeedDataManager dataManager, ViewPreloadSizeProvider<String> preloadSizeProvider) {
        this.context = context;
        this.dataManager = dataManager;
        this.preloadSizeProvider = preloadSizeProvider;
        // 通用图片设置
        imageOptions = new RequestOptions()
                .skipMemoryCache(false)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal) // 加载中显示的默认图
                .error(android.R.drawable.ic_menu_report_image) // 加载失败显示的图
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 缓存
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
        FeedCard feedcard = FeedCardRegistry.getInstance().findCardByViewType(viewType);
        if (feedcard == null) {
            feedcard = new LoadingFeedCard();
        }
        // 优先从预渲染池里拿View
        View itemview = FeedViewPrerenderUtils.getInstance().getView(viewType);
        if (itemview == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            itemview = layoutInflater.inflate(feedcard.getLayoutResId(), parent, false);
        }
        return feedcard.createViewHolder(itemview);

    }

    // 给ViewHolder绑定数据
    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder.getItemViewType() == TYPE_LOADING) {
            return;
        }
        User user = dataManager.getUser(position);
        holder.bindData(user, imageOptions);

        // Glide预加载图片用，量一下图片尺寸
        if (holder instanceof VideoFeedViewHolder) {
            View coverImage = holder.itemView.findViewById(R.id.imageViewCover);
            if (coverImage != null) {
                preloadSizeProvider.setView(coverImage);
            }
        }

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


    // Glide 预加载
    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        // 返回需要预加载的图片 URL
        if (isLoading && position == getItemCount() - 1) return Collections.emptyList();
        User user = dataManager.getUser(position);
        List<String> urls = new ArrayList<>();
        String videoCoverPath = user.getVideoCoverPath();
        String avatarPath = user.getAvatarPath();
        if (!TextUtils.isEmpty(videoCoverPath)) {
            urls.add(videoCoverPath);
        }
        return urls; // 把清单交给 Glide
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return Glide.with(context)
                .load(item)
                .apply(imageOptions);
    }
}

