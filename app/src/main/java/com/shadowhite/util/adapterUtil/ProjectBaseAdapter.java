package com.shadowhite.util.adapterUtil;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.w3engineers.ext.strom.application.ui.base.BaseViewHolder;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.ext.strom.application.ui.base.ItemLongClickListener;
import com.w3engineers.ext.strom.application.ui.widget.BaseRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract class. All common recycler view adapter related task happens here.
 */
public abstract class ProjectBaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {

    private final List<T> mItemList;
    protected ItemClickListener<T> mItemClickListener;
    protected ItemLongClickListener<T> mItemLongClickListener;
    private final int NO_ITEM = 0;
    private final int HAS_ITEM = 1;

    private View mEmptyLayoutView, mProgressView;
    private BaseRecyclerView mRecyclerView;


    public ProjectBaseAdapter() {
        mItemList = new ArrayList<>();
        this.registerAdapterDataObserver(adapterDataObserver);
    }


    public abstract boolean isEqual(T left, T right);

    /**
     * @deprecated Would replace this method with {@link BaseAdapterViewHolder}
     * @param parent
     * @param viewType
     * @return
     */
    @Deprecated
    public abstract BaseViewHolder<T> newViewHolder(ViewGroup parent, int viewType);

    /**
     * Commit child fragment of BaseFragment on a frameLayout
     *
     * @param itemLongClickListener ItemLongClickListener object
     */
    public void setItemLongClickListener(ItemLongClickListener<T> itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    /**
     * Commit child fragment of BaseFragment on a frameLayout
     *
     * @param itemClickListener ItemClickListener object
     */
    public void setItemClickListener(ItemClickListener<T> itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = (BaseRecyclerView) recyclerView;
        mEmptyLayoutView = mRecyclerView.getRootView().findViewById(mRecyclerView.getEmptyLayoutId());

        if (mRecyclerView.getProgressViewId() != 0) {
            mProgressView = mRecyclerView.getRootView().findViewById(mRecyclerView.getProgressViewId());
        }
    }

    @Override
    public BaseViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return newViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
        T itemData = getItem(position);

        holder.bind(itemData);
    }

    /**
     * Attached recycler view
     *
     * @return: RecyclerView
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * Clear current item list and update UI
     */
    public void clear() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    /**
     * @return current item list
     */
    public List<T> getItems() {
        return mItemList;
    }


    /**
     * Remove a Item from list and update UI
     *
     * @param t T type object
     */
    public void removeItem(T t) {
        int toIndex = mItemList.indexOf(t);
        if (toIndex < 0 || toIndex >= mItemList.size()) return;
        mItemList.remove(toIndex);
        notifyDataSetChanged();
    }

    /**
     * @param position int value
     * @return get current Item based on Position
     */
    public T getItem(int position) {
        if (position < 0 || position >= mItemList.size()) return null;
        return mItemList.get(position);
    }

   /* public void addItem(T item) {

        int position = 0;
        addItem(item, position);

    }*/

    /**
     * @param item T type object
     * @return int value: current item inserted position in list
     */
    public int addItem(T item) {
        T tItem = findItem(item);

        if (tItem == null) {
            mItemList.add(item);
            notifyItemInserted(mItemList.size() - 1);
            return mItemList.size() - 1;
        }
        return updateItem(item, tItem);
    }

    /**
     * @param items adapter item list
     */
    public void addItem(List<T> items) {
        for (T item : items) {
            addItem(item);
        }
    }

    /**
     * @param item     T type object
     * @param position int value of position where value will be inserted
     */
    public void addItemToPosition(T item, int position) {
        mItemList.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * @param item     T type object
     * @param position int value of position where value will be inserted
     */
    public void addAllItemToPosition(List<T> item, int position) {
        mItemList.addAll(position, item);
        notifyItemInserted(position);
    }




    /**
     * @param item T type object
     * @return if found then item from list otherwise null
     */
    public T findItem(T item) {
        for (T tItem : mItemList) {
            if (isEqual(item, tItem)) {
                return tItem;
            }
        }
        return null;
    }

    /**
     * @param items List type object list
     */
    public void addItems(List<T> items) {
        for (T item : items) {
            addItem(item);
        }
    }

    /**
     * @param oldItem T type object
     * @param newItem T type object
     * @return int value: newItem position in list
     */
    public int updateItem(T oldItem, T newItem) {
        int toIndex = mItemList.indexOf(newItem);
        mItemList.set(toIndex, oldItem);
        notifyItemChanged(toIndex);
        return toIndex;
    }

    @Override
    public int getItemCount() {
        int count = mItemList.size();
        if (count == NO_ITEM) {
            showView(count);
        }
        return count;
    }

    private void showView(int itemCount) {

        if (mProgressView == null) {
            showHideEmptyView(itemCount);
        } else {
            if (mProgressView.getVisibility() == View.VISIBLE) {
                mEmptyLayoutView.setVisibility(View.GONE);
            } else {
                showHideEmptyView(itemCount);
            }
        }
    }


    private void showHideEmptyView(int itemCount) {
        if (itemCount == NO_ITEM) {
            mEmptyLayoutView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyLayoutView.setVisibility(View.GONE);

        }
    }

    public ViewDataBinding inflate(ViewGroup viewGroup, int item_layout) {
        return DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), item_layout, viewGroup, false);
    }

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            int itemCount = getItemCount();
            if (itemCount == NO_ITEM) {
                showView(itemCount);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            if (itemCount == HAS_ITEM) {
                showView(itemCount);
            }
        }

    };

    /**
     * Abstract class. All common ViewHolder related task happens here. Works coupled with {@link BaseAdapter}
     * @param <T>
     */
    public abstract class BaseAdapterViewHolder<T> extends BaseViewHolder<T> {

        /**
         * This class automatically set's itself the root views click listener. Most of the time this is beneficial.
         * If you face any problem with this default behavior you should call the overriden constructor
         * @param viewDataBinding
         */
        public BaseAdapterViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
        }

        /**
         * Extended from {@link BaseViewHolder#BaseViewHolder(ViewDataBinding, boolean)}
         * @param viewDataBinding
         * @param isResetDefaultListener
         */
        public BaseAdapterViewHolder(ViewDataBinding viewDataBinding, boolean isResetDefaultListener) {
            super(viewDataBinding, isResetDefaultListener);
        }

        @Override
        public void onClick(View view) {

            if(mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getItem(getAdapterPosition()));
                mItemClickListener.onItemClick(view, getItem(getAdapterPosition()), getAdapterPosition());
            }
        }
    }


}