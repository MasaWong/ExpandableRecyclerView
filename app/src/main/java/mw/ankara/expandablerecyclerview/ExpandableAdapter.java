package mw.ankara.expandablerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @author masa
 * @since 11/16/15
 */
class ExpandableAdapter extends RecyclerView.Adapter<ViewHolder>
        implements ExpandableDataObserver {

    /**
     * The mask (in packed position representation) for the header
     */
    private static final int PACKED_TYPE_MASK_HEADER = 0x00008000;

    /**
     * The mask (in packed position representation) for the footer
     */
    private static final int PACKED_TYPE_MASK_FOOTER = 0x80000000;

    /**
     * The shift amount (in packed position representation) for the group
     */
    private static final int PACKED_POSITION_SHIFT_FOOTER = 16;

    /**
     * The mask (in packed position representation) for the child
     */
    private static final int PACKED_TYPE_MASK_CHILD = 0x00004000;

    /**
     * The mask (in packed position representation) for the group
     */
    private static final int PACKED_TYPE_MASK_GROUP = 0x40000000;

    /**
     * The shift amount (in packed position representation) for the group
     */
    private static final int PACKED_POSITION_SHIFT_GROUP = 16;

    private PositionInfo mPositionTmp;

    private int mItemCount;

    private ArrayList<GroupInfo> mExpGroupInfos;

    private ExpandableListAdapter mExpandableListAdapter;

    public ExpandableAdapter(ExpandableListAdapter expandableListAdapter) {
        setExpandableListAdapter(expandableListAdapter);
    }

    /**
     * empty view
     */
    private View mEmpty;

    public void setEmpty(View empty) {
        mEmpty = empty;
    }

    /**
     * header part
     */
    private ArrayList<ViewHolder> mHeaders = new ArrayList<>();

    public void addHeader(ViewHolder header) {
        mHeaders.add(header);
        mPositionTmp = null;
        notifyItemInserted(mHeaders.size() - 1);
    }

    public void removeHeader(ViewHolder header) {
        int index = mHeaders.indexOf(header);
        if (index != -1) {
            mHeaders.remove(header);
            mPositionTmp = null;
            notifyItemRemoved(index);
        }
    }

    public int getHeaderCount() {
        return mHeaders.size();
    }

    /**
     * footer part
     */
    private ArrayList<ViewHolder> mFooters = new ArrayList<>();

    public void addFooter(ViewHolder footer) {
        mFooters.add(footer);
        mPositionTmp = null;
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeFooter(ViewHolder footer) {
        int index = mFooters.indexOf(footer);
        if (index != -1) {
            mFooters.remove(footer);
            mPositionTmp = null;
            notifyItemRemoved(mItemCount + getHeaderCount() + index);
        }
    }

    public int getFooterCount() {
        return mFooters.size();
    }

    /**
     * Point to the {@link android.widget.ExpandableListAdapter} that will give us data/Views
     *
     * @param expandableListAdapter the adapter that supplies us with data/Views
     */
    public void setExpandableListAdapter(ExpandableListAdapter expandableListAdapter) {
        if (mExpandableListAdapter != null) {
            mExpandableListAdapter.unregisterObserver(this);
        }

        expandableListAdapter.registerObserver(this);

        recreateExpGroupInfos(expandableListAdapter);
        mExpandableListAdapter = expandableListAdapter;
    }

    /**
     * recalculate {@link #mExpGroupInfos}
     */
    private void recreateExpGroupInfos(ExpandableListAdapter expandableListAdapter) {
        int itemCount = expandableListAdapter.getGroupCount();
        ArrayList<GroupInfo> expGroupInfos = new ArrayList<>(itemCount);
        for (int i = 0, count = itemCount, fpos = 0; i < count; ++i) {
            int childCountAtPosition = expandableListAdapter.getChildCount(i);
            expGroupInfos.add(new GroupInfo(fpos, fpos + childCountAtPosition));

            itemCount += childCountAtPosition;
            fpos += childCountAtPosition + 1;
        }

        mItemCount = itemCount;
        mExpGroupInfos = expGroupInfos;
    }

    void showEmptyViewIfNeeded() {
        if (mEmpty != null) {
            mEmpty.setVisibility(mItemCount == 0 ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * @see {@link ExpandableDataObserver#notifyExpandableDataChanged}
     */
    @Override
    public void notifyExpandableDataChanged() {
        recreateExpGroupInfos(mExpandableListAdapter);

        mPositionTmp = null;
        notifyDataSetChanged();

        showEmptyViewIfNeeded();
    }

    /**
     * @see {@link ExpandableDataObserver#notifyExpandableItemChanged}
     */
    @Override
    public void notifyExpandableItemChanged(int group, int child, int position) {
        // mExpGroupInfos won't change
        notifyItemChanged(position);
        // item change won't make adapter empty
        showEmptyViewIfNeeded();
    }

    /**
     * @see {@link ExpandableDataObserver#notifyExpandableItemInserted}
     */
    @Override
    public void notifyExpandableItemInserted(int group, int child, int position) {
        GroupInfo info = mExpGroupInfos.get(group);
        ++info.lpos;
        ++mItemCount;

        for (int i = group + 1; i < mExpGroupInfos.size(); ++i) {
            info = mExpGroupInfos.get(i);
            ++info.fpos;
            ++info.lpos;
        }

        mPositionTmp = null;
        notifyItemInserted(position);

        showEmptyViewIfNeeded();
    }

    /**
     * @see {@link ExpandableDataObserver#notifyExpandableRemoved}
     */
    @Override
    public void notifyExpandableRemoved(int group, int child, int position) {
        GroupInfo info = mExpGroupInfos.get(group);
        // if the group has only one child, remove it
        if (info.lpos - info.fpos == 1) {
            notifyExpandableGroupRemoved(group, position);
        } else {
            notifyExpandableItemRemoved(group, child, position);
        }
    }

    /**
     * @see {@link ExpandableDataObserver#notifyExpandableItemRemoved}
     */
    @Override
    public void notifyExpandableItemRemoved(int group, int child, int position) {
        GroupInfo info = mExpGroupInfos.get(group);
        --info.lpos; // move last position forward a step, first position not changed

        for (int i = group + 1; i < mExpGroupInfos.size(); ++i) {
            info = mExpGroupInfos.get(i);
            --info.fpos;
            --info.lpos;
        }

        --mItemCount;
        mPositionTmp = null;
        notifyItemRemoved(position);

        showEmptyViewIfNeeded();
    }

    /**
     * @see {@link ExpandableDataObserver#notifyExpandableGroupRemoved}
     */
    @Override
    public void notifyExpandableGroupRemoved(int group, int position) {
        GroupInfo info = mExpGroupInfos.get(group);
        int deleteStart = info.fpos;
        int deleteCount = info.lpos - info.fpos + 1;

        for (int i = group + 1; i < mExpGroupInfos.size(); ++i) {
            info = mExpGroupInfos.get(i);
            info.fpos -= deleteCount;
            info.lpos -= deleteCount;
        }

        mExpGroupInfos.remove(group);
        mItemCount -= deleteCount;
        mPositionTmp = null;
        notifyItemRangeRemoved(deleteStart, deleteCount);

        showEmptyViewIfNeeded();
    }

    @Override
    public int getItemCount() {
        return mItemCount + getHeaderCount() + getFooterCount();
    }

    /**
     * hide item type in view type
     *
     * @param position item position in adapter
     * @return view type with item type and view type
     */
    @Override
    public int getItemViewType(int position) {
        PositionInfo positionInfo = getUnflattenedPos(position);
        if (positionInfo.type == PositionInfo.PACKED_POSITION_TYPE_HEADER) {
            return position | PACKED_TYPE_MASK_HEADER;
        } else if (positionInfo.type == PositionInfo.PACKED_POSITION_TYPE_FOOTER) {
            return (position << PACKED_POSITION_SHIFT_FOOTER) | PACKED_TYPE_MASK_FOOTER;
        } else if (positionInfo.type == PositionInfo.PACKED_POSITION_TYPE_GROUP) {
            return (mExpandableListAdapter.getGroupItemViewType(positionInfo.group)
                    << PACKED_POSITION_SHIFT_GROUP) | PACKED_TYPE_MASK_GROUP;
        } else {
            return mExpandableListAdapter.getChildItemViewType(positionInfo.group,
                    positionInfo.child) | PACKED_TYPE_MASK_CHILD;
        }
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int maskedViewType) {
        if (isHeader(maskedViewType)) {
            return mHeaders.get(getHeaderPosition(maskedViewType));
        } else if (isFooter(maskedViewType)) {
            return mFooters.get(getFooterPosition(maskedViewType));
        } else if (isGroupType(maskedViewType)) {
            return mExpandableListAdapter.onCreateGroupViewHolder(parent,
                    getUnmaskedGroupType(maskedViewType));
        } else {
            return mExpandableListAdapter.onCreateChildViewHolder(parent,
                    getUnmaskedChildType(maskedViewType));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(ViewHolder holder, int position) {
        PositionInfo positionInfo = getUnflattenedPos(position);
        // headers and footers do nothing
        if (positionInfo.type == PositionInfo.PACKED_POSITION_TYPE_GROUP) {
            mExpandableListAdapter.onBindGroupViewHolder(holder, positionInfo.group);
        } else if (positionInfo.type == PositionInfo.PACKED_POSITION_TYPE_CHILD) {
            mExpandableListAdapter.onBindChildViewHolder(holder, positionInfo.group,
                    positionInfo.child);
        }
    }

    boolean isHeader(int maskedViewType) {
        return (maskedViewType & PACKED_TYPE_MASK_HEADER) != 0;
    }

    boolean isFooter(int maskedViewType) {
        return (maskedViewType & PACKED_TYPE_MASK_FOOTER) != 0;
    }

    boolean isGroupType(int maskedViewType) {
        return (maskedViewType & PACKED_TYPE_MASK_GROUP) != 0;
    }

    boolean isChildType(int maskedViewType) {
        return (maskedViewType & PACKED_TYPE_MASK_CHILD) != 0;
    }

    private int getHeaderPosition(int maskedViewType) {
        return maskedViewType ^ PACKED_TYPE_MASK_HEADER;
    }

    private int getFooterPosition(int maskedViewType) {
        return (maskedViewType ^ PACKED_TYPE_MASK_FOOTER) >> PACKED_POSITION_SHIFT_FOOTER;
    }

    private int getUnmaskedGroupType(int maskedViewType) {
        return (maskedViewType ^ PACKED_TYPE_MASK_GROUP) >> PACKED_POSITION_SHIFT_GROUP;
    }

    private int getUnmaskedChildType(int maskedViewType) {
        return maskedViewType ^ PACKED_TYPE_MASK_CHILD;
    }

    /**
     * if this position is a group, return a {@link PositionInfo} with real group position,
     * else this position is a child, return a {@link PositionInfo} with group's position and child's
     */
    PositionInfo getUnflattenedPos(int position) {
        if (mPositionTmp == null || mPositionTmp.position != position) {
            mPositionTmp = binarySearchPosition(position);
        }

        return mPositionTmp;
    }

    /**
     * binary search
     */
    private PositionInfo binarySearchPosition(int position) {
        int realPosition = position;
        if (position < getHeaderCount()) {
            return new PositionInfo(-1, position, position,
                    PositionInfo.PACKED_POSITION_TYPE_HEADER);
        }

        position -= getHeaderCount();
        if (position >= mItemCount) {
            return new PositionInfo(-1, position - mItemCount, position,
                    PositionInfo.PACKED_POSITION_TYPE_FOOTER);
        }

        final ArrayList<GroupInfo> groupInfos = mExpGroupInfos;
        final int groupSize = groupInfos.size();

        int start = 0;
        int end = groupSize - 1;

        int middle = 0;
        GroupInfo groupInfo;

        while (start <= end) {
            middle = start + (end - start) / 2;
            groupInfo = groupInfos.get(middle);

            if (position < groupInfo.fpos) {
                end = middle - 1;
            } else if (position > groupInfo.lpos) {
                start = middle + 1;
            } else if (position == groupInfo.fpos) {
                return new PositionInfo(middle, -1, realPosition,
                        PositionInfo.PACKED_POSITION_TYPE_GROUP);
            } else if (position <= groupInfo.lpos) {
                return new PositionInfo(middle, position - groupInfo.fpos - 1, realPosition,
                        PositionInfo.PACKED_POSITION_TYPE_CHILD);
            } else {
                throw new RuntimeException("mExpGroupInfos must have error");
            }
        }

        return new PositionInfo(middle, -1, -1, PositionInfo.PACKED_POSITION_TYPE_GROUP);
    }

    /**
     * record group info
     */
    private static class GroupInfo {
        /**
         * group name position
         */
        int fpos;
        /**
         * last child position
         */
        int lpos;

        public GroupInfo(int fpos, int lpos) {
            this.fpos = fpos;
            this.lpos = lpos;
        }
    }
}
