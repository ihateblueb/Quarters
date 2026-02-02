package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.hook.QuartersBlueMapHook;
import au.lupine.quarters.hook.QuartersDynmapHook;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.plugin.Plugin;

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

    public String getQuarterMarkerLabel(Quarter q) {
        String label = "<div>";

        label += "<b>" + q.getName() + "</b>";

        if (q.isForSale()) {
            label += " (For Sale: " + q.getPrice() + ")";
        }

        label += "<br/>";

        label += "<br/><b>Type:</b> " + q.getType().getCommonName();

        label += "<br/><b>Owner:</b> ";
        if (q.hasOwner()) {
            label += Quarters.getInstance().getServer().getOfflinePlayer(q.getOwner()).getName();
        } else {
            label += "Unowned";
        }

        label += "<br/><b>Embassy:</b> ";
        if (q.isEmbassy()) {
            label += "true";
        } else {
            label += "false";
        }

        return label + "</div>";
    }

    public void addQuarterMarker(Quarter q) {
        Plugin dynmap = Quarters.getInstance().getServer().getPluginManager().getPlugin("dynmap");
        Plugin bluemap = Quarters.getInstance().getServer().getPluginManager().getPlugin("BlueMap");

        if (dynmap != null && dynmap.isEnabled()) {
            QuartersDynmapHook.getInstance().removeQuarterMarkers(q);
        }

        if (bluemap != null && bluemap.isEnabled()) {
            QuartersBlueMapHook.getInstance().removeQuarterMarkers(q);
        }
    }

    public void removeQuarterMarker(Quarter q) {
        Plugin dynmap = Quarters.getInstance().getServer().getPluginManager().getPlugin("dynmap");
        Plugin bluemap = Quarters.getInstance().getServer().getPluginManager().getPlugin("BlueMap");

        if (dynmap != null && dynmap.isEnabled()) {
            QuartersDynmapHook.getInstance().removeQuarterMarkers(q);
        }

        if (bluemap != null && bluemap.isEnabled()) {
            QuartersBlueMapHook.getInstance().removeQuarterMarkers(q);
        }
    }

    public void refreshQuarterMarker(Quarter q) {
        removeQuarterMarker(q);
        addQuarterMarker(q);
    }

    public void refreshQuarterMarker(Quarter oldQ, Quarter q) {
        removeQuarterMarker(oldQ);
        addQuarterMarker(q);
    }

}
