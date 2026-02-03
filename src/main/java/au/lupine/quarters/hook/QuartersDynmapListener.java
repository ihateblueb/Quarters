package au.lupine.quarters.hook;

import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.jetbrains.annotations.NotNull;

public class QuartersDynmapListener extends DynmapCommonAPIListener {

    @Override
    public void apiEnabled(@NotNull DynmapCommonAPI api) {
        QuartersDynmapHook.getInstance().enable(api);
    }

}
