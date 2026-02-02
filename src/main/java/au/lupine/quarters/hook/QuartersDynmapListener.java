package au.lupine.quarters.hook;

import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;

public class QuartersDynmapListener extends DynmapCommonAPIListener {

    @Override
    public void apiEnabled(DynmapCommonAPI api) {
        QuartersDynmapHook.getInstance().enable(api);
    }

}
