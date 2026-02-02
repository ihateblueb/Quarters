package au.lupine.quarters.hook;

import au.lupine.quarters.api.manager.MapManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Objects;

public class QuartersBlueMapHook implements QuartersMapHook {

    private static QuartersBlueMapHook instance;

    public static QuartersBlueMapHook getInstance() {
        if (instance == null) instance = new QuartersBlueMapHook();
        return instance;
    }


    public void initialize() {
        BlueMapAPI.onEnable(this::enable);
    }


    BlueMapAPI api;
    MarkerSet markerSet;

    public void enable(BlueMapAPI api) {
        QuarterManager qm = QuarterManager.getInstance();

        if (Objects.isNull(api)) return;

        this.api = api;
        this.markerSet = MarkerSet.builder()
                .label("Quarters")
                .build();

        qm.getAllQuarters().forEach(this::addQuarterMarkers);
    }

    public void addQuarterMarkers(Quarter q) {
        MapManager mm = MapManager.getInstance();

        ArrayList<Marker> markers = new ArrayList<>();

        q.getCuboids().forEach((Cuboid c) -> {
            Location midPoint = c.getMidPoint();

            POIMarker marker = POIMarker.builder()
                    .label(q.getName())
                    .detail(mm.getQuarterMarkerLabel(q))
                    .position(Vector3d.from(midPoint.x(), midPoint.y(), midPoint.z()))
                    .build();

            markers.add(marker);
        });

        for (int i = 0; i < markers.size(); i++) {
            markerSet.put(mm.getQuarterMarkerIdentifier(q) + "_cuboid_" + i, markers.get(i));
        }

        updateMarkerSet(q.getCuboids().get(0).getWorld());
    }

    public void removeQuarterMarkers(Quarter q) {
        MapManager mm = MapManager.getInstance();

        q.getCuboids().forEach((Cuboid c) -> {
            markerSet.remove(mm.getQuarterMarkerIdentifier(q));
        });

        updateMarkerSet(q.getCuboids().get(0).getWorld());
    }

    private void updateMarkerSet(World w) {
        api.getWorld(w.getUID()).ifPresent((BlueMapWorld world) -> {
            for (BlueMapMap map : world.getMaps()) {
                map.getMarkerSets().put("quarters", markerSet);
            }
        });
    }

}
