package au.lupine.quarters.hook;

import au.lupine.quarters.api.manager.MapManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.AreaMarker;
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
                mm.getQuarterMarkerLabel(q),
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
        AreaMarker foundMarker = quarterMarkerSet.findAreaMarker(areaName);

        if (foundMarker != null) foundMarker.deleteMarker();
    }

    public void refreshQuarterMarker(Quarter q) {
        removeQuarterMarker(q);
        addQuarterMarker(q);
    }

    public Pair<double[], double[]> traceQuarter(Quarter q) {
        ArrayList<Double> tracedX = new ArrayList<>();
        ArrayList<Double> tracedZ = new ArrayList<>();

        q.getCuboids().forEach((Cuboid c) -> {
            Location c1 = c.getCornerBlockOne().getLocation();
            Location c2 = c.getCornerBlockTwo().getLocation();

            tracedX.add(c1.x() + 1);
            tracedZ.add(c1.z() + 1);
            tracedX.add(c1.x() + 1);
            tracedZ.add(c2.z() + 1);
            tracedX.add(c2.x() + 1);
            tracedZ.add(c2.z());
            tracedX.add(c2.x());
            tracedZ.add(c1.z());
            tracedX.add(c1.x());
            tracedZ.add(c1.z() + 1);
        });

        return Pair.of(
                tracedX.stream().mapToDouble(d -> d).toArray(),
                tracedZ.stream().mapToDouble(d -> d).toArray()
        );
    }

}
