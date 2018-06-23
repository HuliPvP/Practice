package net.practice.practice;

import lombok.Getter;
import net.practice.practice.board.BoardManager;
import net.practice.practice.board.provider.ProviderResolver;
import net.practice.practice.command.CommandHandler;
import net.practice.practice.game.arenatest.ArenaManager;
import net.practice.practice.game.arenatest.map.MapLoc;
import net.practice.practice.game.ladder.LadderManager;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.queue.QueueRunnable;
import net.practice.practice.listener.ListenerHandler;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.storage.MongoBackend;
import net.practice.practice.task.CleanerTask;
import net.practice.practice.task.UpdateInventoryTask;
import net.practice.practice.util.LocUtils;
import net.practice.practice.util.command.CommandFramework;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Practice extends JavaPlugin {

    @Getter private static Practice instance;

    @Getter private MongoBackend backend;

    @Getter private BoardManager boardManager;
    @Getter private ArenaManager arenaManager;
    @Getter private net.practice.practice.game.arenatest.ArenaManager testArenaManager;
    @Getter private LadderManager ladderManager;

    @Getter private CommandFramework commandFramework;

    @Getter private Location spawn, editor;

    @Override
    public void onEnable() {
        instance = this;

        /* Initialize storage */
        saveDefaultConfig();

        backend = new MongoBackend(this);

        MapLoc.createWorld();

        /* Initialize managers */
        boardManager = new BoardManager(new ProviderResolver());
        boardManager.setupAll();
        //getServer().getPluginManager().registerEvents(boardManager, this);
        arenaManager = new ArenaManager();
        testArenaManager = new net.practice.practice.game.arenatest.ArenaManager();
        ladderManager = new LadderManager();

        /* Commands and Listeners */
        commandFramework = new CommandFramework(this);

        CommandHandler.registerCommands();
        ListenerHandler.registerListeners();

        getServer().getOnlinePlayers().forEach(SpawnHandler::spawn);

        /* Tasks */
        new QueueRunnable().runTaskTimer(this, 20L, 40L);
        new CleanerTask().runTaskTimerAsynchronously(this, 20L, 15L * 20L);
        new UpdateInventoryTask().runTaskTimerAsynchronously(this, 20L, 20L * 2);
    }

    @Override
    public void onDisable() {
        CommandHandler.unregisterCommands();

        boardManager.onDisable();

        arenaManager.saveArenas();
        ladderManager.saveLadders();

        backend.saveProfiles();
        backend.close();

        Profile.getProfiles().clear();

        getServer().unloadWorld(MapLoc.getArenaWorld(), false);
        File region = new File("arenas1");
        if (region.exists()) {
            region.delete();
        }
    }

    public Location getSpawn() {
        if (spawn == null) {
            String configLoc = getConfig().getString("locations.spawn");
            if (configLoc != null) {
                return spawn = LocUtils.deserializeLocation(getConfig().getString("locations.spawn"));
            }
        } else {
            return spawn;
        }
        return null;
    }

    public Location getEditor() {
        if (editor == null) {
            String configLoc = getConfig().getString("locations.editor");
            if (configLoc != null) {
                return editor = LocUtils.deserializeLocation(getConfig().getString("locations.editor"));
            }
        } else {
            return editor;
        }
        return null;
    }

    public void setSpawn(Location location) {
        spawn = location;

        getConfig().set("locations.spawn", LocUtils.serializeLocation(location));
        saveConfig();
    }

    public void setEditor(Location location) {
        editor = location;

        getConfig().set("locations.editor", LocUtils.serializeLocation(location));
        saveConfig();
    }
}
