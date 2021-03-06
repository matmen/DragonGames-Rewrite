package me.matmen.DragonGames.schedulers;

import me.matmen.DragonGames.enums.GameState;
import me.matmen.DragonGames.main.DragonGames;
import me.matmen.DragonGames.utils.PlayerTeleporter;
import net.minecraft.server.v1_14_R1.EntityCreature;
import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NavigationAbstract;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.Objects;

public class Schedulers {

    private static final int[] announceAt = {20, 10, 5, 3, 2, 1};
    private static final DragonGames INSTANCE = DragonGames.INSTANCE;
    public static int activeSchedulerID;

    private static void runGameCountdown() {
        INSTANCE.setGameState(GameState.IN_PROGRESS_PVP);
        World activeMap = Objects.requireNonNull(Bukkit.getWorld("active_map"));
        WorldBorder border = activeMap.getWorldBorder();
        border.setSize(100, (long) (GameState.IN_PROGRESS_PVP.delay * 0.9));

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, () -> {
            INSTANCE.remainingGameTime -= 1;

            int[] announceAt = {120, 60, 30, 15, 10, 5, 3, 2, 1};

            if (ArrayUtils.contains(announceAt, INSTANCE.remainingGameTime))
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.GameEndsIn"), INSTANCE.remainingGameTime));

            if (INSTANCE.remainingGameTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);
                runRestartCountdown();
            }
        }, 0, 20);
    }

    private static void runGraceCountdown() {
        INSTANCE.setGameState(GameState.IN_PROGRESS_GRACE_PERIOD);

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, () -> {
            INSTANCE.remainingGraceTime -= 1;

            if (ArrayUtils.contains(announceAt, INSTANCE.remainingGraceTime)) {
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.GraceEndsIn"), INSTANCE.remainingGraceTime));
            }

            if (INSTANCE.remainingGraceTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.ENTITY_CREEPER_HURT, 50, 100);
                runGameCountdown();
            }
        }, 0, 20);
    }

    public static void runLobbyCountdown() {
        INSTANCE.setGameState(GameState.WAITING_FOR_PLAYERS);
        WorldBorder border = Objects.requireNonNull(Bukkit.getWorld("active_map")).getWorldBorder();
        border.reset();

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, () -> {
            INSTANCE.remainingLobbyTime -= 1;

            if (INSTANCE.remainingLobbyTime > 10 && Bukkit.getOnlinePlayers().size() == Bukkit.getMaxPlayers())
                INSTANCE.remainingLobbyTime = 10;

            for (Player p : Bukkit.getOnlinePlayers())
                p.setLevel(INSTANCE.remainingLobbyTime);

            if (ArrayUtils.contains(announceAt, INSTANCE.remainingLobbyTime))
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.TeleportedIn"), INSTANCE.remainingLobbyTime));

            if (INSTANCE.remainingLobbyTime == 0) {
                if (Bukkit.getOnlinePlayers().size() < DragonGames.MIN_PLAYERS) {
                    INSTANCE.remainingLobbyTime = GameState.WAITING_FOR_PLAYERS.delay;
                    Bukkit.broadcastMessage(DragonGames.prefix + Messages.getString("Schedulers.NotEnoughPlayers"));
                    return;
                }

                Bukkit.getScheduler().cancelTask(activeSchedulerID);

                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 50, 100);

                runSpawnCountdown();
            }
        }, 0, 20);
    }

    public static void runRestartCountdown() {
        INSTANCE.setGameState(GameState.WAITING_FOR_RESTART);

        Bukkit.broadcastMessage(DragonGames.prefix
                + String.format(Messages.getString("Schedulers.RestartIn"), GameState.WAITING_FOR_RESTART.delay));

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, () -> {
            INSTANCE.remainingAftergameTime -= 1;

            if (INSTANCE.remainingAftergameTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);

                for (Player p : Bukkit.getOnlinePlayers())
                    p.kickPlayer(INSTANCE.getGameState().kickMessage);

                Bukkit.getServer().shutdown();
            }
        }, 0, 20);
    }

    private static void runSpawnCountdown() {
        INSTANCE.setGameState(GameState.IN_PROGRESS_SPAWNED);

        World w = Objects.requireNonNull(Bukkit.getWorld("active_map"));
        int borderSize = (int) PlayerTeleporter.getBorderSize(Bukkit.getOnlinePlayers().size());
        PlayerTeleporter.teleportPlayers(w, borderSize);

        WorldBorder border = w.getWorldBorder();
        border.reset();
        border.setCenter(0, 0);
        border.setSize(borderSize);

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, () -> {
            INSTANCE.remainingSpawnTime -= 1;

            if (ArrayUtils.contains(announceAt, INSTANCE.remainingSpawnTime)) {
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.GameStartsIn"), INSTANCE.remainingSpawnTime));
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 50, 100);
            }

            for (Player p : Bukkit.getOnlinePlayers())
                p.setLevel(INSTANCE.remainingSpawnTime);

            if (INSTANCE.remainingSpawnTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 37.5f);
                runGraceCountdown();
            }
        }, 0, 20);
    }

}
