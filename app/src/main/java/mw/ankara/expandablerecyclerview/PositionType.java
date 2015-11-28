package mw.ankara.expandablerecyclerview;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author masa
 * @since 11/17/15
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@IntDef({PositionInfo.PACKED_POSITION_TYPE_CHILD, PositionInfo.PACKED_POSITION_TYPE_GROUP,
        PositionInfo.PACKED_POSITION_TYPE_HEADER, PositionInfo.PACKED_POSITION_TYPE_FOOTER})
@interface PositionType {
}
