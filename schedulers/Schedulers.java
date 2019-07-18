package schedulers;

import enums.GameState;
import main.DragonGames;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import utils.PlayerTeleporter;

public class Schedulers {

    private static final int[] announceAt = {20, 10, 5, 3, 2, 1};
    public static int activeSchedulerID;

    private static void runGameCountdown() {
        DragonGames.INSTANCE.setGameState(GameState.IN_PROGRESS_PVP);
        Bukkit.getWorlds().get(0).getWorldBorder().setSize(100, (long) (GameState.IN_PROGRESS_PVP.delay * 0.9));

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonGames.INSTANCE, () -> {
            DragonGames.INSTANCE.remainingGameTime -= 1;

            int[] announceAt = {120, 60, 30, 15, 10, 5, 3, 2, 1};

            if (ArrayUtils.contains(announceAt, DragonGames.INSTANCE.remainingGameTime))
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.GameEndsIn"), DragonGames.INSTANCE.remainingGameTime)); //$NON-NLS-1$

            if (DragonGames.INSTANCE.remainingGameTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);
                runRestartCountdown();
            }
        }, 0, 20);
    }

    private static void runGraceCountdown() {
        DragonGames.INSTANCE.setGameState(GameState.IN_PROGRESS_GRACE_PERIOD);

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonGames.INSTANCE, () -> {
            DragonGames.INSTANCE.remainingGraceTime -= 1;

            if (ArrayUtils.contains(announceAt, DragonGames.INSTANCE.remainingGraceTime)) {
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.GraceEndsIn"), DragonGames.INSTANCE.remainingGraceTime)); //$NON-NLS-1$
            }

            if (DragonGames.INSTANCE.remainingGraceTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.CREEPER_HISS, 50, 100);
                runGameCountdown();
            }
        }, 0, 20);
    }

    public static void runLobbyCountdown() {
        DragonGames.INSTANCE.setGameState(GameState.WAITING_FOR_PLAYERS);
        Bukkit.getWorlds().get(0).getWorldBorder().reset();

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonGames.INSTANCE, () -> {
            DragonGames.INSTANCE.remainingLobbyTime -= 1;

            for (Player p : Bukkit.getOnlinePlayers())
                p.setLevel(DragonGames.INSTANCE.remainingLobbyTime);

            if (ArrayUtils.contains(announceAt, DragonGames.INSTANCE.remainingLobbyTime))
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.TeleportedIn"), DragonGames.INSTANCE.remainingLobbyTime)); //$NON-NLS-1$

            if (DragonGames.INSTANCE.remainingLobbyTime == 0) {
                if (Bukkit.getOnlinePlayers().size() < DragonGames.MIN_PLAYERS) {
                    DragonGames.INSTANCE.remainingLobbyTime = GameState.WAITING_FOR_PLAYERS.delay;
                    Bukkit.broadcastMessage(DragonGames.prefix + Messages.getString("Schedulers.NotEnoughPlayers")); //$NON-NLS-1$
                    return;
                }

                Bukkit.getScheduler().cancelTask(activeSchedulerID);

                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 50, 100);

                runSpawnCountdown();
            }
        }, 0, 20);
    }

    public static void runRestartCountdown() {
        DragonGames.INSTANCE.setGameState(GameState.WAITING_FOR_RESTART);

        Bukkit.broadcastMessage(DragonGames.prefix
                + String.format(Messages.getString("Schedulers.RestartIn"), GameState.WAITING_FOR_RESTART.delay)); //$NON-NLS-1$

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonGames.INSTANCE, () -> {
            DragonGames.INSTANCE.remainingAftergameTime -= 1;

            if (DragonGames.INSTANCE.remainingAftergameTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);

                for (Player p : Bukkit.getOnlinePlayers())
                    p.kickPlayer(DragonGames.INSTANCE.getGameState().kickMessage);

                Bukkit.getServer().shutdown();
            }
        }, 0, 20);
    }

    private static void runSpawnCountdown() {
        DragonGames.INSTANCE.setGameState(GameState.IN_PROGRESS_SPAWNED);
        WorldBorder border = Bukkit.getWorlds().get(0).getWorldBorder();
        border.reset();
        border.setCenter(0, 0);
        border.setSize(Math.max(Math.pow(Bukkit.getOnlinePlayers().size(), 1.25) * 10, 200));

        PlayerTeleporter.teleportPlayers();

        activeSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonGames.INSTANCE, () -> {
            DragonGames.INSTANCE.remainingSpawnTime -= 1;

            if (ArrayUtils.contains(announceAt, DragonGames.INSTANCE.remainingSpawnTime)) {
                Bukkit.broadcastMessage(DragonGames.prefix + String.format(
                        Messages.getString("Schedulers.GameStartsIn"), DragonGames.INSTANCE.remainingSpawnTime)); //$NON-NLS-1$
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 50, 100);
            }

            for (Player p : Bukkit.getOnlinePlayers())
                p.setLevel(DragonGames.INSTANCE.remainingSpawnTime);

            if (DragonGames.INSTANCE.remainingSpawnTime == 0) {
                Bukkit.getScheduler().cancelTask(activeSchedulerID);
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.LEVEL_UP, 50, 37.5f);
                runGraceCountdown();
            }
        }, 0, 20);
    }

}
