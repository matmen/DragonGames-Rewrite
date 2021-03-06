package me.matmen.DragonGames.events;

import me.matmen.DragonGames.enums.GameState;
import me.matmen.DragonGames.main.DragonGames;
import me.matmen.DragonGames.schedulers.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class DamageHandler implements Listener {

    private final DragonGames INSTANCE = DragonGames.INSTANCE;

    @EventHandler
    public void onEvent(@NotNull EntityDamageEvent e) {
        e.setCancelled(!INSTANCE.getGameState().canReceiveDamage);
    }

    @EventHandler
    public void onEvent(@NotNull FoodLevelChangeEvent e) {
        e.setCancelled(!INSTANCE.getGameState().canReceiveDamage);
    }

    @EventHandler
    public void onEvent(@NotNull PlayerDeathEvent e) {
        Player p = e.getEntity();
        Player killer = p.getKiller();

        p.setHealth(20.0D);
        p.setVelocity(new Vector(0, 3, 0));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 50.0f, 50.0f);
        p.setGameMode(GameMode.SPECTATOR);
        e.setDeathMessage(null);
        p.getWorld().strikeLightningEffect(p.getLocation());

        ArrayList<Player> remaining = new ArrayList<>();

        for (Player r : Bukkit.getOnlinePlayers())
            if (r.getGameMode() == GameMode.SURVIVAL)
                remaining.add(r);

        Bukkit.broadcastMessage(String.format(Messages.getString(killer == null ? "DamageHandler.KilledByOther" : "DamageHandler.KilledByHuman"), p.getName(),
                killer == null ? Objects.requireNonNull(p.getLastDamageCause()).getCause().name() : killer.getName(),
                (killer == null ? 0 : killer.getHealth()) / 2));

        if (remaining.size() <= 1) {
            Bukkit.getScheduler().cancelTask(Schedulers.activeSchedulerID);

            Player winner = remaining.get(0);
            if (winner == null)
                winner = p;
            Bukkit.broadcastMessage(String.format(Messages.getString("DamageHandler.GameWon"), winner.getName(),
                    winner.getHealth() / 2));
            Schedulers.runRestartCountdown();
        } else
            Bukkit.broadcastMessage(
                    String.format(Messages.getString("DamageHandler.PlayersRemaining"), remaining.size()));

        Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> p.spigot().respawn(), 1);
    }

}