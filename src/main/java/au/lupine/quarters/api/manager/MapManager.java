package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.hook.QuartersDynmapHook;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Town;

public final class MapManager {

    private static MapManager instance;

    private MapManager() {}

    public static MapManager getInstance() {
        if (instance == null) instance = new MapManager();
        return instance;
    }

    public String getQuarterMarkerIdentifier(Quarter q) {
        Town town = q.getTown();
        return "quarters_" + town.getName() + "_" + q.getUUID();
    }

    public void addQuarterMarker(Quarter q) {
        if (Quarters.getInstance().getServer().getPluginManager().getPlugin("dynmap").isEnabled()) {
            QuartersDynmapHook.getInstance().addQuarterMarker(q);
        }
    }

    public void removeQuarterMarker(Quarter q) {
        if (Quarters.getInstance().getServer().getPluginManager().getPlugin("dynmap").isEnabled()) {
            QuartersDynmapHook.getInstance().removeQuarterMarker(q);
        }
    }

    public void refreshQuarterMarker(Quarter q) {
        removeQuarterMarker(q);
        addQuarterMarker(q);
    }

}
