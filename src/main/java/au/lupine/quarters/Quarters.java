package au.lupine.quarters;

import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.command.quarters.QuartersCommand;
import au.lupine.quarters.command.quartersadmin.QuartersAdminCommand;
import au.lupine.quarters.hook.QuartersMapHook;
import au.lupine.quarters.hook.QuartersPlaceholderExpansion;
import au.lupine.quarters.listener.*;
import au.lupine.quarters.object.metadata.QuarterListDataField;
import au.lupine.quarters.object.metadata.QuarterListDataFieldDeserialiser;
import au.lupine.quarters.object.wrapper.Pair;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import com.palmergames.util.JavaUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Quarters extends JavaPlugin {

    private static Quarters instance;

    private static Logger logger;

    @Override
    public void onEnable() {
        registerCommands(
                Pair.of("quarters", new QuartersCommand()),
                Pair.of("quartersadmin", new QuartersAdminCommand())
        );

        registerHooks();

        registerListeners(
                new QuarterEntryListener(),
                new QuarterIntegrityListener(),
                new QuarterParticleListener(),
                new QuartersWandListener(),
                new StatusScreenListener(),
                new TownyActionListener()
        );

        // Don't register this listener if a towny version pre 0.101.2.5 is being used, it breaks otherwise
        if (JavaUtil.classExists("com.palmergames.adventure.text.Component")) {
            logWarning("You seem to be using an older version of Towny, version 0.101.2.5 or above is required for quarters statistics to show in resident/town status screens.");
        } else {
            registerListeners(new StatusScreenListener());
        }

        logInfo("Quarters enabled :3");
    }

    @Override
    public void onDisable() {
        logInfo("Quarters disabled :v");
    }

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();

        ConfigManager.getInstance().setup();

        MetadataLoader.getInstance().registerDeserializer(QuarterListDataField.typeID(), new QuarterListDataFieldDeserialiser());
    }

    @SafeVarargs
    private void registerCommands(Pair<String, CommandExecutor>... commandPair) {
        for (Pair<String, CommandExecutor> pair : commandPair) {
            String name = pair.getFirst();

            PluginCommand command = getCommand(name);
            if (command == null) {
                logSevere("Command " + name + " was null, failed to set a command executor");
                continue;
            }

            command.setExecutor(pair.getSecond());
        }
    }

    private void registerHooks() {
        PluginManager pm = getServer().getPluginManager();

        Plugin placeholderApi = pm.getPlugin("PlaceholderAPI");
        if (placeholderApi != null && placeholderApi.isEnabled()) {
            new QuartersPlaceholderExpansion().register();
        }

        // Loading the hook classes like this prevents missing dependencies being noticed
        Plugin dynmap = pm.getPlugin("dynmap");
        if (dynmap != null && dynmap.isEnabled()) {
            this.initializeMapHook("QuartersDynmapHook");
            logInfo("Enabled Dynmap integration");
        }

        Plugin bluemap = pm.getPlugin("BlueMap");
        if (bluemap != null && bluemap.isEnabled()) {
            this.initializeMapHook("QuartersBlueMapHook");
            logInfo("Enabled BlueMap integration");
        }
    }

    private void initializeMapHook(String name) {
        try {
            String prefix = "au.lupine.quarters.hook";
            Class<?> clazz = Class.forName(prefix + "." + name);
            QuartersMapHook mapHook = (QuartersMapHook) clazz.getConstructor().newInstance();
            mapHook.initialize();
        } catch (Exception e) {
            logSevere("Failed to initialize map hook. " + e.getMessage());
        }
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pm = getServer().getPluginManager();

        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    public static Quarters getInstance() {
        return instance;
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }

    public static void logWarning(String msg) {
        logger.warning(msg);
    }

    public static void logSevere(String msg) {
        logger.severe(msg);
    }
}
