package events;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import enums.GameState;
import main.DragonGames;

public class ConnectionStateHandler implements Listener {

	private DragonGames INSTANCE = DragonGames.INSTANCE;

	@EventHandler
	public void onEvent(PlayerJoinEvent joinEvent) {
		Player p = joinEvent.getPlayer();

		if (INSTANCE.getGameState().kickMessage != null) {
			joinEvent.setJoinMessage(null);
			p.kickPlayer(INSTANCE.getGameState().kickMessage);
		}

		joinEvent.setJoinMessage(String.format(Messages.getString("ConnectionStateHandler.LobbyJoined"), p.getName())); //$NON-NLS-1$
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setExp(0.0f);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.updateInventory();
		p.teleport(p.getWorld().getSpawnLocation());

		for (PotionEffect potionEffect : p.getActivePotionEffects())
			p.removePotionEffect(potionEffect.getType());
	}

	@EventHandler
	public void onEvent(PlayerKickEvent kickEvent) {
		Player p = kickEvent.getPlayer();

		if (INSTANCE.getGameState() == GameState.WAITING_FOR_PLAYERS) {
			kickEvent.setLeaveMessage(
					String.format(Messages.getString("ConnectionStateHandler.LobbyLeft"), p.getName())); //$NON-NLS-1$
			return;
		}

		if (p.getGameMode() == GameMode.SPECTATOR || INSTANCE.getGameState() == GameState.WAITING_FOR_RESTART)
			return;

		p.damage(p.getHealth(), p);
	}

	@EventHandler
	public void onEvent(PlayerLoginEvent loginEvent) {
		if (INSTANCE.getGameState().kickMessage != null)
			loginEvent.disallow(Result.KICK_OTHER, INSTANCE.getGameState().kickMessage);

		if (Bukkit.getOnlinePlayers().size() >= Bukkit.getServer().getMaxPlayers())
			loginEvent.disallow(Result.KICK_FULL, Messages.getString("ConnectionStateHandler.LobbyFull")); //$NON-NLS-1$
	}

	@EventHandler
	public void onEvent(PlayerQuitEvent leaveEvent) {
		Player p = leaveEvent.getPlayer();

		if (INSTANCE.getGameState() == GameState.WAITING_FOR_PLAYERS) {
			leaveEvent
					.setQuitMessage(String.format(Messages.getString("ConnectionStateHandler.LobbyLeft"), p.getName())); //$NON-NLS-1$
			return;
		}

		if (p.getGameMode() == GameMode.SPECTATOR || INSTANCE.getGameState() == GameState.WAITING_FOR_RESTART)
			return;

		leaveEvent.setQuitMessage(null);
		Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(p,
				new ArrayList<ItemStack>(Arrays.asList(p.getInventory().getContents())), (int) p.getExp(), null));
	}

	@EventHandler
	public void onEvent(ServerListPingEvent e) {
		e.setMotd(INSTANCE.getGameState().motd);
	}

}
