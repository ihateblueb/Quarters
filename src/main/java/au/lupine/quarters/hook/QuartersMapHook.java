package au.lupine.quarters.hook;

import au.lupine.quarters.object.entity.Quarter;

public interface QuartersMapHook {

    void addQuarterMarker(Quarter q);

    void removeQuarterMarker(Quarter q);

    default void refreshQuarterMarker(Quarter q) {
        addQuarterMarker(q);
        removeQuarterMarker(q);
    }

}
