package events;

import enums.GameState;
import main.DragonGames;
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
import schedulers.Schedulers;

import java.util.ArrayList;

public class DamageHandler implements Listener {

    private final DragonGames INSTANCE = DragonGames.INSTANCE;

    @EventHandler
    public void onEvent(@NotNull EntityDamageEvent e) {
        e.setCancelled(e.getEntityType() == EntityType.PLAYER && !INSTANCE.getGameState().canReceiveDamage);
    }

    @EventHandler
    public void onEvent(@NotNull FoodLevelChangeEvent e) {
        e.setCancelled(INSTANCE.getGameState() != GameState.IN_PROGRESS_PVP);
    }

    @EventHandler
    public void onEvent(@NotNull PlayerDeathEvent e) {
        Player p = e.getEntity();
        Player killer = p.getKiller();

        ArrayList<Player> remaining = new ArrayList<>();

        for (Player r : Bukkit.getOnlinePlayers())
            if (r.getGameMode() == GameMode.SURVIVAL)
                remaining.add(r);

        Bukkit.broadcastMessage(String.format(Messages.getString(killer == null ? "DamageHandler.KilledByOther" : "DamageHandler.KilledByHuman"), p.getName(),
                killer == null ? p.getLastDamageCause().getCause().name() : killer.getName(),
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

        p.setHealth(20.0D);
        p.setVelocity(new Vector(0, 3, 0));
        p.getWorld().playSound(p.getLocation(), Sound.WITHER_SHOOT, 50.0f, 50.0f);
        p.setGameMode(GameMode.SPECTATOR);
        e.setDeathMessage(null);
        p.getLocation().getWorld().strikeLightningEffect(p.getLocation());
    }

}