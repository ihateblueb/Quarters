package au.lupine.quarters.hook;

import au.lupine.quarters.object.entity.Quarter;

public interface QuartersMapHook {

    void addQuarterMarkers(Quarter q);

    void removeQuarterMarkers(Quarter q);

    default void refreshQuarterMarkers(Quarter q) {
        addQuarterMarkers(q);
        removeQuarterMarkers(q);
    }

    default void refreshQuarterMarkers(Quarter oldQ, Quarter q) {
        addQuarterMarkers(oldQ);
        removeQuarterMarkers(q);
    }

}
