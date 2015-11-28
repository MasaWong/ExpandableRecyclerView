package mw.ankara.expandablerecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author masa
 * @since 11/16/15
 */
public class ExpandableRecyclerView extends RecyclerView {

    private ExpandableListAdapter mAdapter;

    private ExpandableAdapter mConnector;

    public ExpandableRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public ExpandableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(VERTICAL);
        setLayoutManager(layoutManager);

        ItemSwipeCallback itemSwipeCallback = new ItemSwipeCallback();
        ItemTouchHelper helper = new ItemTouchHelper(itemSwipeCallback);
        helper.attachToRecyclerView(this);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        throw new RuntimeException(
                "For ExpandableRecyclerView, use setAdapter(ExpandableListAdapter) instead of " +
                        "setAdapter(Adapter)");
    }

    public void setAdapter(ExpandableListAdapter adapter) {
        // Set member variable
        mAdapter = adapter;

        if (adapter != null) {
            // Create the connector
            if (mConnector == null) {
                mConnector = new ExpandableAdapter(adapter);
            } else {
                mConnector.setExpandableListAdapter(adapter);
            }
        } else {
            mConnector = null;
        }

        // Link the RecyclerView (superclass) to the expandable list data through the connector
        super.setAdapter(mConnector);
    }

    /**
     * TODO : warning
     * empty part, set adapter first
     */
    public void setEmptyView(View empty) {
        if (mConnector == null) {
            throw new RuntimeException(
                    "For ExpandableRecyclerView, call setAdapter(ExpandableListAdapter) first");
        } else {
            mConnector.setEmpty(empty);
        }
    }

    /**
     * header part
     */
    public void addHeader(ViewHolder header) {
        mConnector.addHeader(header);
    }

    public void removeHeader(ViewHolder header) {
        mConnector.removeHeader(header);
    }

    public int getHeaderCount() {
        return mConnector.getHeaderCount();
    }

    /**
     * footer part
     */
    public void addFooter(ViewHolder footer) {
        mConnector.addFooter(footer);
    }

    public void removeFooter(ViewHolder footer) {
        mConnector.removeFooter(footer);
    }

    public int getFooterCount() {
        return mConnector.getFooterCount();
    }

    private class ItemSwipeCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            if (mConnector.isChildType(viewHolder.getItemViewType())) {
                return makeMovementFlags(0, ItemTouchHelper.END | ItemTouchHelper.START);
            } else {
                return 0;
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder,
                ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(ViewHolder viewHolder, int direction) {
            PositionInfo info = mConnector.getUnflattenedPos(viewHolder.getAdapterPosition());
            mAdapter.onChildSwiped(info.group, info.child, info.position);
        }
    }
}
