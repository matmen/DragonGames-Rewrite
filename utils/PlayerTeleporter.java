package utils;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import main.DragonGames;

public class PlayerTeleporter {

	static EntityType[] foodEntities = { EntityType.SHEEP, EntityType.PIG, EntityType.COW, EntityType.CHICKEN };

	public static void teleportPlayers() {
		World w = Bukkit.getWorlds().get(0);
		int borderSize = (int) Math.floor(w.getWorldBorder().getSize());
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

			p.teleport(loc);
			p.setCompassTarget(loc);

			for (int i = 0; i <= r.nextInt(50) + 25; i++) {
				Location mobSpawnLoc = loc.add(r.nextInt(50), 0, r.nextInt(50));
				mobSpawnLoc.setY(w.getHighestBlockYAt(mobSpawnLoc));
				w.spawnEntity(mobSpawnLoc, foodEntities[r.nextInt(foodEntities.length)]);
			}
		}
	}

	@SuppressWarnings("unused")
	private DragonGames INSTANCE = DragonGames.INSTANCE;

}
