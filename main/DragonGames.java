package main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import enums.GameState;
import events.ConnectionStateHandler;
import events.DamageHandler;
import events.InteractionHandler;
import events.MessageHandler;
import schedulers.Schedulers;
import utils.CrateFiller;

public class DragonGames extends JavaPlugin {
	public static DragonGames INSTANCE;
	public static String prefix = Messages.getString("DragonGames.Prefix"); //$NON-NLS-1$

	public static int MIN_PLAYERS = 1;
	public int remainingLobbyTime = GameState.WAITING_FOR_PLAYERS.delay;
	public int remainingSpawnTime = GameState.IN_PROGRESS_SPAWNED.delay;
	public int remainingGraceTime = GameState.IN_PROGRESS_GRACE_PERIOD.delay;
	public int remainingGameTime = GameState.IN_PROGRESS_PVP.delay;
	public int remainingAftergameTime = GameState.WAITING_FOR_RESTART.delay;

	public CrateFiller crateFiller = new CrateFiller();
	public HashMap<String, Inventory> crates = new HashMap<String, Inventory>();

	private GameState gameState = GameState.STARTING;

	public GameState getGameState() {
		return gameState;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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

	public void setGameState(GameState newState) {
		gameState = newState;
		if (newState.broadcast != null)
			Bukkit.broadcastMessage(prefix + newState.broadcast);
	}

}
