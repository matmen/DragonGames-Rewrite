package events;

import enums.GameState;
import main.DragonGames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionStateHandler implements Listener {

    private final DragonGames INSTANCE = DragonGames.INSTANCE;

    @EventHandler
    public void onEvent(@NotNull PlayerJoinEvent joinEvent) {
        Player p = joinEvent.getPlayer();

        if (INSTANCE.getGameState().kickMessage != null) {
            joinEvent.setJoinMessage("");
            p.kickPlayer(INSTANCE.getGameState().kickMessage);
        }

        joinEvent.setJoinMessage(String.format(Messages.getString("ConnectionStateHandler.LobbyJoined"), p.getName()));
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20.0D);
        p.setFoodLevel(20);
        p.setExp(0.0f);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.updateInventory();

        AttributeInstance attackSpeed = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attackSpeed != null) attackSpeed.setBaseValue(16);

        p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        for (PotionEffect potionEffect : p.getActivePotionEffects())
            p.removePotionEffect(potionEffect.getType());
    }

    @EventHandler
    public void onEvent(@NotNull PlayerKickEvent kickEvent) {
        Player p = kickEvent.getPlayer();

        if (INSTANCE.getGameState() == GameState.WAITING_FOR_PLAYERS) {
            kickEvent.setLeaveMessage(
                    String.format(Messages.getString("ConnectionStateHandler.LobbyLeft"), p.getName()));
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
            loginEvent.disallow(Result.KICK_FULL, Messages.getString("ConnectionStateHandler.LobbyFull"));
    }

    @EventHandler
    public void onEvent(@NotNull PlayerQuitEvent leaveEvent) {
        Player p = leaveEvent.getPlayer();

        if (INSTANCE.getGameState() == GameState.WAITING_FOR_PLAYERS) {
            leaveEvent
                    .setQuitMessage(String.format(Messages.getString("ConnectionStateHandler.LobbyLeft"), p.getName()));
            return;
        }

        if (p.getGameMode() == GameMode.SPECTATOR || INSTANCE.getGameState() == GameState.WAITING_FOR_RESTART)
            return;

        leaveEvent.setQuitMessage("");
        Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(p,
                new ArrayList<>(Arrays.asList(p.getInventory().getContents())), (int) p.getExp(), null));
    }

    @EventHandler
    public void onEvent(@NotNull ServerListPingEvent e) {
        e.setMotd(INSTANCE.getGameState().motd);
    }

}
