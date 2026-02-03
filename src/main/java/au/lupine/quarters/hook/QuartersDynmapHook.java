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
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class QuartersDynmapHook implements QuartersMapHook {

    private static QuartersDynmapHook instance;

    public static QuartersDynmapHook getInstance() {
        if (instance == null) instance = new QuartersDynmapHook();
        return instance;
    }


    public void initialize() {
        DynmapCommonAPIListener.register(new QuartersDynmapListener());
    }


    MarkerSet markerSet;

    public void enable(@NotNull DynmapCommonAPI api) {
        QuarterManager qm = QuarterManager.getInstance();

        markerSet = api.getMarkerAPI().createMarkerSet(
                "quarters",
                "Quarters",
                null,
                false
        );
        markerSet.setLayerPriority(2);

        qm.getAllQuarters().forEach(this::addQuarterMarkers);
    }

    public void addQuarterMarkers(@NotNull Quarter q) {
        MapManager mm = MapManager.getInstance();

        String areaName = mm.getQuarterMarkerIdentifier(q);

        ArrayList<Pair<double[], double[]>> traced = traceQuarter(q);

        World qWorld = q.getCuboids().get(0).getWorld();

        String label =  mm.getQuarterMarkerLabel(q);

        for (int i = 0; i < traced.size(); i++) {
            Pair<double[], double[]> t = traced.get(i);

            double[] tracedX = t.getFirst();
            double[] tracedZ = t.getSecond();

            Color color = q.getColour();

            AreaMarker am = markerSet.createAreaMarker(
                    areaName + "_cuboid_" + i,
                    label,
                    true,
                    qWorld.getName(),
                    tracedX,
                    tracedZ,
                    false
            );

            am.setFillStyle(0.5, color.getRGB() & 0x00FFFFFF);
            am.setLineStyle(2, 1, color.getRGB() & 0x00FFFFFF);
        }
    }

    public void removeQuarterMarkers(@NotNull Quarter q) {
        MapManager mm = MapManager.getInstance();

        String areaName = mm.getQuarterMarkerIdentifier(q);

        for (int i = 0; i < q.getCuboids().size(); i++) {
            AreaMarker foundMarker = markerSet.findAreaMarker(areaName + "_cuboid_" + i);
            if (foundMarker != null) foundMarker.deleteMarker();
        }
    }

    private ArrayList<Pair<double[], double[]>> traceQuarter(@NotNull Quarter q) {
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
