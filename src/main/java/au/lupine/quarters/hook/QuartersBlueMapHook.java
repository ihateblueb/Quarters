package au.lupine.quarters.hook;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.MapManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class QuartersBlueMapHook implements QuartersMapHook {

    private static QuartersBlueMapHook instance;

    public static QuartersBlueMapHook getInstance() {
        if (instance == null) instance = new QuartersBlueMapHook();
        return instance;
    }


    public void initialize() {
        BlueMapAPI.onEnable(this::enable);
    }


    public void enable(@NotNull BlueMapAPI api) {
        QuarterManager qm = QuarterManager.getInstance();

        MarkerSet emptyMarkerSet = MarkerSet.builder()
                .label("quarters")
                .build();

        api.getMaps().forEach(m -> m.getMarkerSets().put("quarters", emptyMarkerSet));

        qm.getAllQuarters().forEach(this::addQuarterMarkers);
    }

    public void addQuarterMarkers(@NotNull Quarter q) {
        BlueMapAPI.onEnable(api -> {
            MapManager mm = MapManager.getInstance();
            MarkerSet ms = getMarkerSet(api, getQuarterWorld(q));

            ArrayList<Marker> markers = new ArrayList<>();

            q.getCuboids().forEach(c -> {
                java.awt.Color quarterColor = q.getColour();

                Color color = new Color(
                        quarterColor.getRed(),
                        quarterColor.getGreen(),
                        quarterColor.getBlue(),
                        0.25f
                );
                Color colorOpaque = new Color(
                        quarterColor.getRed(),
                        quarterColor.getGreen(),
                        quarterColor.getBlue(),
                        1
                );

                ExtrudeMarker marker = ExtrudeMarker.builder()
                        .label(q.getName())
                        .detail(mm.getQuarterMarkerLabel(q))
                        .shape(
                                Shape.createRect(c.getMinX(), c.getMinZ(), c.getMaxX() + 1, c.getMaxZ() + 1),
                                c.getMinY(),
                                c.getMaxY()
                        )
                        .fillColor(color)
                        .lineColor(colorOpaque)
                        .build();

                markers.add(marker);
            });

            for (int i = 0; i < markers.size(); i++) {
                String key = mm.getQuarterMarkerIdentifier(q) + "_cuboid_" + i;
                Marker marker = markers.get(i);

                ms.put(key, marker);
            }

            updateMarkerSet(api, getQuarterWorld(q), ms);
        });
    }

    public void removeQuarterMarkers(@NotNull Quarter q) {
        BlueMapAPI.onEnable(api -> {
            MapManager mm = MapManager.getInstance();
            MarkerSet ms = getMarkerSet(api, getQuarterWorld(q));

            for (int i = 0; i < q.getCuboids().size(); i++) {
                String key = mm.getQuarterMarkerIdentifier(q) + "_cuboid_" + i;

                ms.remove(key);
            }

            updateMarkerSet(api, getQuarterWorld(q), ms);
        });
    }

    private World getQuarterWorld(@NotNull Quarter q) {
        return q.getCuboids().get(0).getWorld();
    }

    private MarkerSet getMarkerSet(
            @NotNull BlueMapAPI api,
            @NotNull World w
    ) {
        AtomicReference<MarkerSet> ms = new AtomicReference<>(null);

        api.getWorld(w.getUID()).ifPresent(world -> {
            for (BlueMapMap map : world.getMaps()) {
                ms.set(map.getMarkerSets().get("quarters"));
            }
        });

        return ms.get();
    }

    private void updateMarkerSet(
            @NotNull BlueMapAPI api,
            @NotNull World w,
            @NotNull MarkerSet ms
    ) {
        api.getWorld(w.getUID()).ifPresent(world -> {
            for (BlueMapMap map : world.getMaps()) {
                map.getMarkerSets().put("quarters", ms);
            }
        });
    }

}
