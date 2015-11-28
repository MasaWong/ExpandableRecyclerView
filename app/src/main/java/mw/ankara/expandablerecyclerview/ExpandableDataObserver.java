package mw.ankara.expandablerecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * @author masa
 * @since 11/17/15
 */
public interface ExpandableDataObserver {

    /**
     * Notify any registered observers that the data set has changed.
     * <p>
     * <p>There are two different classes of data change events, item changes and structural
     * changes. Item changes are when a single item has its data updated but no positional
     * changes have occurred. Structural changes are when items are inserted, removed or moved
     * within the data set.</p>
     * <p>
     * <p>This event does not specify what about the data set has changed, forcing
     * any observers to assume that all existing items and structure may no longer be valid.
     * LayoutManagers will be forced to fully rebind and relayout all visible views.</p>
     * <p>
     * <p><code>RecyclerView</code> will attempt to synthesize visible structural change events
     * for adapters that report that they have {@link RecyclerView.Adapter#hasStableIds() stable IDs} when
     * this method is used. This can help for the purposes of animation and visual
     * object persistence but individual item views will still need to be rebound
     * and relaid out.</p>
     * <p>
     * <p>If you are writing an adapter it will always be more efficient to use the more
     * specific change events if you can. Rely on <code>notifyDataSetChanged()</code>
     * as a last resort.</p>
     *
     * @see #notifyExpandableItemChanged
     * @see #notifyExpandableItemInserted
     * @see #notifyExpandableItemRemoved
     */
    void notifyExpandableDataChanged();

    /**
     * Notify any registered observers that the item at <code>position</code> has changed.
     * <p>
     * <p>This is an item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>position</code> is out of date and should be updated.
     * The item at <code>position</code> retains the same identity.</p>
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was changed
     */
    void notifyExpandableItemChanged(int group, int child, int position);

    /**
     * Notify any registered observers that the currently reflected <code>itemCount</code>
     * items starting at <code>positionStart</code> have been newly inserted. The items
     * previously located at <code>positionStart</code> and beyond can now be found starting
     * at position <code>positionStart + itemCount</code>.
     * <p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was inserted
     */
    void notifyExpandableItemInserted(int group, int child, int position);

    /**
     * Notify any registered observers that the item previously located at <code>position</code>
     * has been removed from the data set. The items previously located at and after
     * <code>position</code> may now be found at <code>oldPosition - 1</code>.
     * <p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     * <p></p>if this group has only one child, remove this group at the meantime. </p>
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was removed
     */
    void notifyExpandableRemoved(int group, int child, int position);

    /**
     * Notify any registered observers that the item previously located at <code>position</code>
     * has been removed from the data set. The items previously located at and after
     * <code>position</code> may now be found at <code>oldPosition - 1</code>.
     * <p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param group    Position of the group for which has this child
     * @param child    Position of the child in the group
     * @param position Position of the child that was removed
     */
    void notifyExpandableItemRemoved(int group, int child, int position);

    /**
     * Notify any registered observers that the group previously located at <code>position</code>
     * has been removed from the data set. The group previously located at and after
     * <code>position</code> may now be found at <code>oldPosition - 1</code>.
     * <p>
     * <p>This is a structural change event. Representations of other existing groups in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param group    Position of the group that was removed
     * @param position Position of the group that was removed
     */
    void notifyExpandableGroupRemoved(int group, int position);
}
