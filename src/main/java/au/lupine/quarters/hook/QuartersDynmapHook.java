package au.lupine.quarters.hook;

import au.lupine.quarters.api.manager.MapManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.Pair;
import org.bukkit.World;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class QuartersDynmapHook implements QuartersMapHook {

    private static QuartersDynmapHook instance;

    public static QuartersDynmapHook getInstance() {
        if (instance == null) instance = new QuartersDynmapHook();
        return instance;
    }


    public void initialize() {
        DynmapCommonAPIListener.register(new QuartersDynmapListener());
    }


    MarkerSet quarterMarkerSet;

    public void enable(DynmapCommonAPI api) {
        QuarterManager qm = QuarterManager.getInstance();

        if (Objects.isNull(api)) return;

        quarterMarkerSet = api.getMarkerAPI().createMarkerSet(
                "quarters",
                "Quarters",
                null,
                false
        );

        qm.getAllQuarters().forEach(this::addQuarterMarkers);
    }

    public void addQuarterMarkers(Quarter q) {
        MapManager mm = MapManager.getInstance();

        String areaName = mm.getQuarterMarkerIdentifier(q);

        ArrayList<Pair<double[], double[]>> traced = traceQuarter(q);

        // getFirst() errors on compile for some reason
        World qWorld = q.getCuboids().get(0).getWorld();

        String label =  mm.getQuarterMarkerLabel(q);

        AtomicInteger i = new AtomicInteger();
        traced.forEach((Pair<double[], double[]> t) -> {
            double[] tracedX = t.getFirst();
            double[] tracedZ = t.getSecond();

            quarterMarkerSet.createAreaMarker(
                    areaName + "_cuboid_" + i,
                    label,
                    true,
                    qWorld.getName(),
                    tracedX,
                    tracedZ,
                    false
            );

            i.getAndIncrement();
        });
    }

    public void removeQuarterMarkers(Quarter q) {
        MapManager mm = MapManager.getInstance();

        String areaName = mm.getQuarterMarkerIdentifier(q);

        for (int i = 0; i < q.getCuboids().size(); i++) {
            AreaMarker foundMarker = quarterMarkerSet.findAreaMarker(areaName + "_cuboid_" + i);
            if (foundMarker != null) foundMarker.deleteMarker();
        }
    }

    public ArrayList<Pair<double[], double[]>> traceQuarter(Quarter q) {
        ArrayList<Pair<double[], double[]>> tracedCuboids = new ArrayList<>();

        q.getCuboids().forEach((Cuboid c) -> {
            double[] cuboidX = new double[4];
            double[] cuboidZ = new double[4];

            cuboidX[0] = c.getMinX();
            cuboidZ[0] = c.getMinZ();
            cuboidX[1] = c.getMinX();
            cuboidZ[1] = c.getMaxZ() + 1;
            cuboidX[2] = c.getMaxX() + 1;
            cuboidZ[2] = c.getMaxZ() + 1;
            cuboidX[3] = c.getMaxX() + 1;
            cuboidZ[3] = c.getMinZ();

            tracedCuboids.add(new Pair<>(cuboidX, cuboidZ));
        });

        return tracedCuboids;
    }

}
