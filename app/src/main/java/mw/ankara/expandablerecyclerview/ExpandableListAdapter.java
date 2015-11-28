package mw.ankara.expandablerecyclerview;

import android.database.Observable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @author masa
 * @since 11/16/15
 */
public abstract class ExpandableListAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder>
        extends Observable<ExpandableDataObserver> {

    // part of Observer Pattern * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * notify all notifications
     */
    public void notifyDataSetChanged() {
        for (ExpandableDataObserver observer : mObservers) {
            observer.notifyExpandableDataChanged();
        }
    }

    /**
     * notify an item changed
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was inserted
     */
    public void notifyItemChanged(int group, int child, int position) {
        for (ExpandableDataObserver observer : mObservers) {
            observer.notifyExpandableItemChanged(group, child, position);
        }
    }

    /**
     * notify an item removed, if the group which has this child has only one child, remove it at the
     * meantime
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was inserted
     */
    public void notifyRemoved(int group, int child, int position) {
        for (ExpandableDataObserver observer : mObservers) {
            observer.notifyExpandableRemoved(group, child, position);
        }
    }

    /**
     * notify an item removed
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was inserted
     */
    public void notifyItemRemoved(int group, int child, int position) {
        for (ExpandableDataObserver observer : mObservers) {
            observer.notifyExpandableItemRemoved(group, child, position);
        }
    }

    /**
     * notify an item removed
     *
     * @param group    Position of the group for which has this child
     * @param position Position of the child that was inserted
     */
    public void notifyGroupRemoved(int group, int position) {
        for (ExpandableDataObserver observer : mObservers) {
            observer.notifyExpandableGroupRemoved(group, position);
        }
    }

    /**
     * override super method to support param type ExpandableDataObserver
     */
    @Override
    public void registerObserver(ExpandableDataObserver observer) {
        super.registerObserver(observer);
    }

    /**
     * override super method to support param type ExpandableDataObserver
     */
    @Override
    public void unregisterObserver(ExpandableDataObserver observer) {
        super.unregisterObserver(observer);
    }

    //part of Adapter Pattern * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    public abstract int getGroupCount();

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children count should be returned
     * @return the number of children
     */
    public abstract int getChildCount(int groupPosition);

    /**
     * Gets the view type of the specified group.
     *
     * @param groupPosition the position of the group for which the view type is wanted
     * @return integer value identifying the type of the view needed to represent the group item at position. Type codes need positive number but not be contiguous.
     */
    public abstract int getGroupItemViewType(int groupPosition);

    /**
     * Gets the view type of the specified child.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which the view type is wanted
     * @return integer value identifying the type of the view needed to represent the group item at position. Type codes need positive number but not be contiguous.
     */
    public abstract int getChildItemViewType(int groupPosition, int childPosition);

    /**
     * Called when RecyclerView needs a new {@link GVH} of the given type to represent a group item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View
     * @return A new group ViewHolder that holds a View of the given view type
     */
    public abstract GVH onCreateGroupViewHolder(ViewGroup parent, int viewType);

    /**
     * Called when RecyclerView needs a new {@link CVH} of the given type to represent a child item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View
     * @return A new child ViewHolder that holds a View of the given view type
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup parent, int viewType);

    /**
     * Called by RecyclerView to display the group data at the specified position.
     * This method should update the contents of the {@link android.support.v7.widget.RecyclerView.ViewHolder#itemView}
     * to reflect the item at the given position.
     *
     * @param holder        The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * @param groupPosition The position of the group item within the adapter's data set
     */
    public abstract void onBindGroupViewHolder(GVH holder, int groupPosition);

    /**
     * Called by RecyclerView to display the child data at the specified position.
     * This method should update the contents of the {@link android.support.v7.widget.RecyclerView.ViewHolder#itemView}
     * to reflect the item at the given position.
     *
     * @param holder        The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * @param groupPosition The position of the group item within the adapter's data set
     * @param childPosition The position of the child item within the group
     */
    public abstract void onBindChildViewHolder(CVH holder, int groupPosition, int childPosition);

    /**
     * <p>Called when a user attempt to expand/collapse a group item by tapping.</p>
     * <p>Tips: If you want to set your own click event listener to group items, make this method always return false.
     * It will disable auto expanding/collapsing when a group item is clicked.</p>
     *
     * @param holder        The ViewHolder which is associated to group item user is attempt to expand/collapse
     * @param groupPosition Group position
     * @param x             Touched X position. Relative from the itemView's top-left
     * @param y             Touched Y position. Relative from the itemView's top-left
     * @param expand        true: expand, false: collapse
     * @return Whether to perform expand/collapse operation.
     */
    public abstract boolean onCheckCanExpandOrCollapseGroup(GVH holder, int groupPosition, int x,
            int y, boolean expand);

    /**
     * Called when a group attempt to expand by user operation.
     *
     * @param groupPosition The position of the group item within the adapter's data set
     * @param fromUser      Whether the expand request is issued by a user operation
     * @return Whether the group can be expanded. If returns false, the group keeps collapsed.
     */
    public abstract boolean onHookGroupExpand(int groupPosition, boolean fromUser);

    /**
     * Called when a group attempt to expand by user operationø.
     *
     * @param groupPosition The position of the group item within the adapter's data set
     * @param fromUser      Whether the collapse request is issued by a user operation
     * @return Whether the group can be collapsed. If returns false, the group keeps expanded.
     */
    public abstract boolean onHookGroupCollapse(int groupPosition, boolean fromUser);

    //part of Callback Pattern * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Called when a child view swiped to delete
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was inserted
     */
    public abstract void onChildSwiped(int group, int child, int position);
}
