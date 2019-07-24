package me.matmen.DragonGames.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Random;

public class PlayerTeleporter {

    private static final EntityType[] foodEntities = {EntityType.SHEEP, EntityType.PIG, EntityType.COW, EntityType.CHICKEN};

    public static void teleportPlayers(World w, int borderSize) {
        Random r = new Random();

        for (Entity e : w.getEntities())
            if (e.getType().isAlive() && e.getType() != EntityType.PLAYER)
                e.remove();

        for (Player p : Bukkit.getOnlinePlayers()) {
            Location loc = null;

            while (loc == null) {
                int x = r.nextInt(borderSize) - borderSize / 2;
                int z = r.nextInt(borderSize) - borderSize / 2;
                loc = new Location(w, x + .5, 0, z + .5);

                int y = w.getHighestBlockYAt(loc);
                if (!w.getBlockAt(x, y - 1, z).getType().isSolid()) {
                    loc = null;
                    continue;
                }

                loc.setY(y);
            }

            p.teleportAsync(loc);
            p.setCompassTarget(loc);

            for (int i = 0; i <= r.nextInt(50) + 25; i++) {
                Location mobSpawnLoc = loc.add(r.nextInt(50), 0, r.nextInt(50));
                mobSpawnLoc.setY(w.getHighestBlockYAt(mobSpawnLoc));
                w.spawnEntity(mobSpawnLoc, foodEntities[r.nextInt(foodEntities.length)]);
            }
        }
    }

    public static double getBorderSize(int playerCount) {
        return Math.max(Math.pow(playerCount, 1.25) * 10, 200);
    }

}
