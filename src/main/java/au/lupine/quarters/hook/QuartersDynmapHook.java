package au.lupine.quarters.hook;

import au.lupine.quarters.api.manager.MapManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.Pair;
import org.bukkit.World;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.MarkerSet;

import java.util.ArrayList;
import java.util.Objects;

public class QuartersDynmapHook extends DynmapCommonAPIListener implements QuartersMapHook {

    private static QuartersDynmapHook instance;

    public static QuartersDynmapHook getInstance() {
        if (instance == null) instance = new QuartersDynmapHook();
        return instance;
    }


    MarkerSet quarterMarkerSet;

    @Override
    public void apiEnabled(DynmapCommonAPI api) {
        QuarterManager qm = QuarterManager.getInstance();

        if (Objects.isNull(api)) return;

        quarterMarkerSet = api.getMarkerAPI().createMarkerSet(
                "quarters",
                "Quarters",
                null,
                false
        );

        qm.getAllQuarters().forEach(this::addQuarterMarker);
    }

    public void addQuarterMarker(Quarter q) {
        MapManager mm = MapManager.getInstance();

        String areaName = mm.getQuarterMarkerIdentifier(q);

        Pair<double[], double[]> traced = traceQuarter(q);
        double[] tracedX = traced.getFirst();
        double[] tracedZ = traced.getSecond();

        // getFirst() errors on compile for some reason
        World qWorld = q.getCuboids().get(0).getWorld();

        quarterMarkerSet.createAreaMarker(
                areaName,
                q.getName(),
                true,
                qWorld.getName(),
                tracedX,
                tracedZ,
                false
        );
    }

    public void removeQuarterMarker(Quarter q) {
        MapManager mm = MapManager.getInstance();

        String areaName = mm.getQuarterMarkerIdentifier(q);
        quarterMarkerSet.findAreaMarker(areaName).deleteMarker();
    }

    public void refreshQuarterMarker(Quarter q) {
        removeQuarterMarker(q);
        addQuarterMarker(q);
    }

    public Pair<double[], double[]> traceQuarter(Quarter q) {
        ArrayList<Double> tracedX = new ArrayList<>();
        ArrayList<Double> tracedZ = new ArrayList<>();

        q.getCuboids().forEach((Cuboid c) -> {
            tracedX.add(c.getCornerOne().x());
            tracedX.add(c.getCornerTwo().x());

            tracedZ.add(c.getCornerOne().z());
            tracedZ.add(c.getCornerTwo().z());
        });

        return Pair.of(
                tracedX.stream().mapToDouble(d -> d).toArray(),
                tracedZ.stream().mapToDouble(d -> d).toArray()
        );
    }
    
}
