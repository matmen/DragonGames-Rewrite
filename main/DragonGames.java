package main;

import enums.GameState;
import events.ConnectionStateHandler;
import events.DamageHandler;
import events.InteractionHandler;
import events.MessageHandler;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import schedulers.Schedulers;
import utils.CrateFiller;
import utils.PlayerTeleporter;
import world.ChunkGenerator;

import java.util.HashMap;
import java.util.logging.Level;

public class DragonGames extends JavaPlugin {
    public static final String prefix = Messages.getString("DragonGames.Prefix");
    public static final int MIN_PLAYERS = 1;
    public static DragonGames INSTANCE;
    public final CrateFiller crateFiller = new CrateFiller();
    public final HashMap<String, Inventory> crates = new HashMap<>();
    public int remainingLobbyTime = GameState.WAITING_FOR_PLAYERS.delay;
    public int remainingSpawnTime = GameState.IN_PROGRESS_SPAWNED.delay;
    public int remainingGraceTime = GameState.IN_PROGRESS_GRACE_PERIOD.delay;
    public int remainingGameTime = GameState.IN_PROGRESS_PVP.delay;
    public int remainingAftergameTime = GameState.WAITING_FOR_RESTART.delay;
    private GameState gameState = GameState.STARTING;

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(@NotNull GameState newState) {
        gameState = newState;
        if (newState.broadcast != null)
            Bukkit.broadcastMessage(prefix + newState.broadcast);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equals("player")) {
            if (args.length < 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(prefix + Messages.getString("DragonGames.InvalidPlayer"));
            } else {
                EntityPlayer playerHandle = ((CraftPlayer) target).getHandle();
                sender.sendMessage(prefix + String.format(Messages.getString("DragonGames.PlayerInfo.Header"), target.getName()));
                sender.sendMessage(prefix + String.format(Messages.getString("DragonGames.PlayerInfo.Ping"), playerHandle.ping));
            }
        } else {
            PluginDescriptionFile info = getDescription();
            sender.sendMessage(
                    prefix + String.format(Messages.getString("DragonGames.InfoCommand.Version"), info.getVersion()));
            sender.sendMessage(prefix + String.format(Messages.getString("DragonGames.InfoCommand.Authors"),
                    String.join(Messages.getString("DragonGames.InfoCommand.AuthorsJoiner"), info.getAuthors())));
            sender.sendMessage(
                    prefix + String.format(Messages.getString("DragonGames.InfoCommand.Website"), info.getWebsite()));
        }

        return true;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            player.kickPlayer(GameState.WAITING_FOR_RESTART.kickMessage);

        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionStateHandler(), this);
        pluginManager.registerEvents(new DamageHandler(), this);
        pluginManager.registerEvents(new InteractionHandler(), this);
        pluginManager.registerEvents(new MessageHandler(), this);

        WorldCreator wc = new WorldCreator("active_map");
        wc.generator(new ChunkGenerator());
        World activeMap = Bukkit.getServer().createWorld(wc);

        if (activeMap == null) {
            getLogger().log(Level.SEVERE, "Active map could not be created!");
            Bukkit.shutdown();
            return;
        }

        int chunkCount = (int) Math.ceil(PlayerTeleporter.getBorderSize(Bukkit.getServer().getMaxPlayers()) / 16 / 2);
        int chunksLoaded = 0;
        double lastRatio = 0;
        long lastMessage = System.currentTimeMillis();
        for (int spawnOffsetX = -chunkCount; spawnOffsetX <= chunkCount; spawnOffsetX++) {
            for (int spawnOffsetZ = -chunkCount; spawnOffsetZ <= chunkCount; spawnOffsetZ++) {
                activeMap.loadChunk(spawnOffsetX, spawnOffsetZ, true);
                double ratio = chunksLoaded++ / Math.pow(chunkCount * 2, 2);

                if (ratio - lastRatio > 0.1 && System.currentTimeMillis() - lastMessage > 1000) {
                    getLogger().log(Level.INFO, String.format("Preparing play area for %1$s, %2$d%%", activeMap.getName(), (int) (ratio * 100)));
                    lastRatio = ratio;
                    lastMessage = System.currentTimeMillis();
                }
            }
        }

        for (World w : Bukkit.getWorlds()) {
            w.setSpawnFlags(false, false);
            w.setAutoSave(false);
            w.setKeepSpawnInMemory(true);

            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            w.setTime(6000);

            w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            w.setStorm(false);
            w.setThundering(false);

            for (Entity e : w.getEntities())
                if (e.getType().isAlive())
                    e.remove();
        }

        Schedulers.runLobbyCountdown();
    }

}
