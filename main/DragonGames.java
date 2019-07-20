package main;

import enums.GameState;
import events.ConnectionStateHandler;
import events.DamageHandler;
import events.InteractionHandler;
import events.MessageHandler;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import schedulers.Schedulers;
import utils.CrateFiller;

import java.util.HashMap;

public class DragonGames extends JavaPlugin {
    public static final String prefix = Messages.getString("DragonGames.Prefix");
    public static final int MIN_PLAYERS = 2;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
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

        Bukkit.getScheduler().cancelAllTasks();
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
        Bukkit.getServer().createWorld(wc);

        for (World w : Bukkit.getWorlds()) {
            w.setSpawnFlags(false, false);
            w.setAutoSave(false);
            w.setKeepSpawnInMemory(true);

            for (Entity e : w.getEntities())
                if (e.getType().isAlive())
                    e.remove();
        }

        Schedulers.runLobbyCountdown();
    }

}
