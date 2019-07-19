package events;

import enums.GameState;
import enums.ItemChances;
import main.DragonGames;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class InteractionHandler implements Listener {

    private final DragonGames INSTANCE = DragonGames.INSTANCE;

    private void handleItemRightClick(@NotNull PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getItem() == null)
            return;

        Material itemType = e.getItem().getType();

        if (itemType == ItemChances.TRACKER.item.getType()) {
            Player nearest = null;

            for (Player check : Bukkit.getOnlinePlayers())
                if (check != p && check.getGameMode() == GameMode.SURVIVAL && !check.isSneaking()
                        && (nearest == null || check.getLocation().distance(p.getLocation()) < nearest.getLocation()
                        .distance(p.getLocation())))
                    nearest = check;

            if (nearest == null)
                p.sendMessage(DragonGames.prefix + Messages.getString("InteractionHandler.NoNearbyPlayers"));
            else {
                p.sendMessage(
                        DragonGames.prefix + String.format(Messages.getString("InteractionHandler.TrackerMessage"),
                                nearest.getName(), nearest.getLocation().distance(p.getLocation())));
                p.setCompassTarget(nearest.getLocation());
            }
        } else if (itemType == ItemChances.FORCEFIELD.item.getType()) {
            for (Player toApply : Bukkit.getOnlinePlayers()) {
                double distance = toApply.getLocation().distance(p.getLocation());
                if (distance > 20)
                    continue;
                if (distance < 1)
                    distance = 1;

                toApply.playSound(p.getLocation(), Sound.WITHER_SHOOT, (float) (50 / distance), 1);
                if (toApply == p || toApply.getGameMode() == GameMode.SPECTATOR)
                    continue;

                toApply.setVelocity(toApply.getVelocity().multiply(-20 / distance).setY(1 / distance));
            }

            int newAmount = p.getItemInHand().getAmount() - 1;
            if (newAmount == 0)
                p.setItemInHand(null);
            else
                p.getItemInHand().setAmount(newAmount);

            p.updateInventory();
        } else if (itemType == ItemChances.FIRE_GRENADE.item.getType()) {
            e.setCancelled(true);
            p.getWorld().createExplosion(p.getLocation(), 0);

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target == p || target.getGameMode() != GameMode.SURVIVAL)
                    continue;
                double distance = target.getLocation().distance(p.getLocation());
                if (distance > 10)
                    continue;
                if (distance < 1)
                    distance = 1;

                target.setFireTicks((int) (80 / distance));
            }

            int newAmount = p.getItemInHand().getAmount() - 1;
            if (newAmount == 0)
                p.setItemInHand(null);
            else
                p.getItemInHand().setAmount(newAmount);

            p.updateInventory();
        } else if (itemType == ItemChances.SWITCHER.item.getType()) {
            e.setCancelled(true);

            Player nearest = null;

            for (Player check : Bukkit.getOnlinePlayers())
                if (check != p && check.getGameMode() == GameMode.SURVIVAL && (nearest == null || check.getLocation()
                        .distance(p.getLocation()) < nearest.getLocation().distance(p.getLocation())))
                    nearest = check;

            if (nearest == null || nearest.getLocation().distance(p.getLocation()) > 10)
                p.sendMessage(DragonGames.prefix + Messages.getString("InteractionHandler.NoNearbyPlayers"));
            else {
                Location newPlayerLoc = nearest.getLocation().clone();
                Location newTargetLoc = p.getLocation().clone();

                p.teleport(newPlayerLoc);
                nearest.teleport(newTargetLoc);

                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 50, 0);
                nearest.playSound(nearest.getLocation(), Sound.ENDERMAN_TELEPORT, 50, 0);

                int newAmount = p.getItemInHand().getAmount() - 1;
                if (newAmount == 0)
                    p.setItemInHand(null);
                else
                    p.getItemInHand().setAmount(newAmount);

                p.updateInventory();
            }
        } else if (itemType == ItemChances.BANDAGE.item.getType()) {
            e.setCancelled(true);

            p.setHealth(Math.max(Math.min(p.getHealth() + 8, p.getMaxHealth() - 4), p.getHealth()));
            p.playSound(p.getLocation(), Sound.DIG_WOOL, 50, 0);

            int newAmount = p.getItemInHand().getAmount() - 1;
            if (newAmount == 0)
                p.setItemInHand(null);
            else
                p.getItemInHand().setAmount(newAmount);
        }
    }

    @EventHandler
    public void onEvent(@NotNull BlockBreakEvent blockBreakEvent) {
        blockBreakEvent.setCancelled(!INSTANCE.getGameState().canBuild);
    }

    @EventHandler
    public void onEvent(@NotNull BlockPlaceEvent blockPlaceEvent) {
        blockPlaceEvent.setCancelled(!INSTANCE.getGameState().canBuild);
    }

    @EventHandler
    public void onEvent(@NotNull PlayerInteractEvent playerInteractEvent) {
        Player p = playerInteractEvent.getPlayer();

        if (INSTANCE.getGameState().joinable || !INSTANCE.getGameState().canMove || p.getGameMode() == GameMode.SPECTATOR) {
            playerInteractEvent.setCancelled(true);
            return;
        }

        Action action = playerInteractEvent.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
            handleItemRightClick(playerInteractEvent);

        if (action != Action.RIGHT_CLICK_BLOCK)
            return;

        Block b = playerInteractEvent.getClickedBlock();
        Location loc = b.getLocation();
        String location = String.format(Messages.getString("InteractionHandler.CrateLocationKey"), loc.getBlockX(),
                loc.getBlockZ());
        if (b.getType() == Material.DRAGON_EGG) {
            playerInteractEvent.setCancelled(true);

            if (INSTANCE.crates.containsKey(location)) {
                p.openInventory(INSTANCE.crates.get(location));
                return;
            }

            Inventory inv = Bukkit.createInventory(p, InventoryType.CHEST);

            Random r = new Random();
            for (int i = 0; i <= r.nextInt(5) + 1; i++)
                inv.setItem(r.nextInt(InventoryType.CHEST.getDefaultSize()), INSTANCE.crateFiller.getRandomItem());

            INSTANCE.crates.put(location, inv);

            p.openInventory(inv);

            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> {
                b.setType(Material.AIR);
                loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 2.5f, true, true);
                loc.getWorld().getHighestBlockAt(loc).setType(Material.OBSIDIAN);
                for (HumanEntity viewer : inv.getViewers())
                    viewer.closeInventory();
            }, 200);
        } else if (b.getType() == Material.OBSIDIAN && INSTANCE.crates.containsKey(location)) {
            b.setType(Material.BEDROCK);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2), true);
            p.playSound(loc, Sound.SHOOT_ARROW, 50, 25);
        }
    }

    @EventHandler
    public void onEvent(PlayerMoveEvent playerMoveEvent) {
        Player p = playerMoveEvent.getPlayer();
        Location from = playerMoveEvent.getFrom().clone();
        Location to = playerMoveEvent.getTo().clone();

        if (INSTANCE.getGameState() == GameState.WAITING_FOR_PLAYERS) {
            if (to.getY() < 64) {
                Location spawnLocation = p.getWorld().getSpawnLocation();
                Vector off = spawnLocation.toVector().subtract(to.toVector()).normalize();

                p.setVelocity(new Vector(off.getX(), 0.75, off.getZ()));
                p.setFallDistance(0);
                p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 25, 50);
            }

            return;
        }

        to.setY(from.getY());

        if (!INSTANCE.getGameState().canMove && from.distance(to) > 0) {
            p.teleport(from.setDirection(to.getDirection()));
            return;
        }

        if (p.getGameMode() != GameMode.SPECTATOR)
            return;

        double borderSize = p.getWorld().getWorldBorder().getSize();
        double maxLimit = borderSize / 2 + 25;

        if (to.getX() > maxLimit)
            outOfMap(p, new Vector(-1.0D, 0.2D, 0.0D));

        if (to.getX() < -maxLimit)
            outOfMap(p, new Vector(1.0D, 0.2D, 0.0D));

        if (to.getZ() > maxLimit)
            outOfMap(p, new Vector(0.0D, 0.2D, -1.0D));

        if (to.getZ() < -maxLimit)
            outOfMap(p, new Vector(0.0D, 0.2D, 1.0D));

    }

    private void outOfMap(@NotNull Player p, Vector v) {
        p.setVelocity(v);
        p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 30.0F, 50.0F);
        p.sendMessage(Messages.getString("InteractionHandler.EndOfMap"));
    }

}
