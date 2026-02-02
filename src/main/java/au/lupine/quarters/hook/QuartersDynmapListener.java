package au.lupine.quarters.hook;

import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.MarkerSet;

import java.util.Objects;

public class QuartersDynmapListener extends DynmapCommonAPIListener {
    MarkerSet quarterMarkerSet;

    @Override
    public void apiEnabled(DynmapCommonAPI api) {
        if (Objects.isNull(api)) return;

        quarterMarkerSet = api.getMarkerAPI().createMarkerSet(
                "quarters",
                "Quarters",
                null,
                false
        );

        QuarterManager.getInstance().getAllQuarters().forEach(this::traceQuarter);
    }

    public void traceQuarter(Quarter q) {

    }
}
