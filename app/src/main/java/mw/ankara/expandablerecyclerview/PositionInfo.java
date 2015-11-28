package mw.ankara.expandablerecyclerview;

/**
 * 用于记录{@link ExpandableAdapter}中，子View的位置信息
 * @author masa
 * @since 11/17/15
 */
public class PositionInfo {

    /**
     * The packed position represents a header.
     */
    public static final int PACKED_POSITION_TYPE_HEADER = -2;

    /**
     * The packed position represents a footer.
     */
    public static final int PACKED_POSITION_TYPE_FOOTER = -1;

    /**
     * The packed position represents a group.
     */
    public static final int PACKED_POSITION_TYPE_GROUP = 0;

    /**
     * The packed position represents a child.
     */
    public static final int PACKED_POSITION_TYPE_CHILD = 1;

    int group;
    int child;
    int position;
    @PositionType
    int type;

    public PositionInfo(int group, int child, int position, @PositionType int type) {
        this.group = group;
        this.child = child;
        this.position = position;
        this.type = type;
    }
}
