package au.lupine.quarters.hook;

import au.lupine.quarters.object.entity.Quarter;

public interface QuartersMapHook {

    /**
     * Initialize method to be executed on plugin startup if the map plugin is found.
     * */
    void initialize();

    /**
     * Adds markers required for a quarter
     *
     * @param q Quarter to add
     * */
    void addQuarterMarkers(Quarter q);

    /**
     * Removes markers associated with a quarter
     *
     * @param q Quarter to remove
     * */
    void removeQuarterMarkers(Quarter q);

    /**
     * Remove and then add markers associated with a quarter
     *
     * @param q Quarter to refresh
     * */
    default void refreshQuarterMarkers(Quarter q) {
        removeQuarterMarkers(q);
        addQuarterMarkers(q);
    }

    /**
     * Remove and then add markers associated with a quarter
     *
     * @param oldQ Quarter to remove
     * @param q Quarter to add
     * */
    default void refreshQuarterMarkers(Quarter oldQ, Quarter q) {
        removeQuarterMarkers(q);
        addQuarterMarkers(oldQ);
    }

}
