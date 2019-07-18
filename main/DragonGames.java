package main;

import enums.GameState;
import events.ConnectionStateHandler;
import events.DamageHandler;
import events.InteractionHandler;
import events.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    public static final String prefix = Messages.getString("DragonGames.Prefix"); //$NON-NLS-1$
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
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, String label, String[] args) {
        PluginDescriptionFile info = getDescription();
        sender.sendMessage(
                prefix + String.format(Messages.getString("DragonGames.InfoCommand.Version"), info.getVersion())); //$NON-NLS-1$
        sender.sendMessage(prefix + String.format(Messages.getString("DragonGames.InfoCommand.Authors"), //$NON-NLS-1$
                String.join(Messages.getString("DragonGames.InfoCommand.AuthorsJoiner"), info.getAuthors()))); //$NON-NLS-1$
        sender.sendMessage(
                prefix + String.format(Messages.getString("DragonGames.InfoCommand.Website"), info.getWebsite())); //$NON-NLS-1$

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

        Schedulers.runLobbyCountdown();
    }

}
