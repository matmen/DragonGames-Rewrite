package me.matmen.DragonGames.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ItemChances {
    WOODEN_SWORD(new ItemStack(Material.WOODEN_SWORD), ItemWeights.WEAPONS_WEIGHT),
    STONE_SWORD(new ItemStack(Material.STONE_SWORD), ItemWeights.WEAPONS_WEIGHT / 2),
    IRON_SWORD(new ItemStack(Material.IRON_SWORD), ItemWeights.WEAPONS_WEIGHT / 4),
    DIAMOND_AXE(new ItemStack(Material.DIAMOND_AXE), ItemWeights.WEAPONS_WEIGHT / 4),
    DIAMOND_SWORD(new ItemStack(Material.DIAMOND_SWORD), ItemWeights.WEAPONS_WEIGHT / 8),

    LIGHTER(new ItemStack(Material.FLINT_AND_STEEL), ItemWeights.WEAPONS_WEIGHT / 4),
    FISHING_ROD(new ItemStack(Material.FISHING_ROD), ItemWeights.WEAPONS_WEIGHT / 6),

    BOW(new ItemStack(Material.BOW), ItemWeights.WEAPONS_WEIGHT / 2),
    CROSSBOW(new ItemStack(Material.CROSSBOW), ItemWeights.WEAPONS_WEIGHT / 16),
    TRIDENT(new ItemStack(Material.TRIDENT), ItemWeights.WEAPONS_WEIGHT / 16),

    ARROW_SINGLE(new ItemStack(Material.ARROW, 1), ItemWeights.PROJECTILES_WEIGHT),
    ARROW_DOUBLE(new ItemStack(Material.ARROW, 2), ItemWeights.PROJECTILES_WEIGHT / 2),
    ARROW_TRIPLE(new ItemStack(Material.ARROW, 3), ItemWeights.PROJECTILES_WEIGHT / 4),
    ARROW_QUADRUPLE(new ItemStack(Material.ARROW, 4), ItemWeights.PROJECTILES_WEIGHT / 8),

    SHARP_ARROW_SINGLE(getTippedArrow(PotionType.INSTANT_DAMAGE, 1), ItemWeights.PROJECTILES_WEIGHT / 6),
    SHARP_ARROW_DOUBLE(getTippedArrow(PotionType.INSTANT_DAMAGE, 2), ItemWeights.PROJECTILES_WEIGHT / 12),
    SHARP_ARROW_TRIPLE(getTippedArrow(PotionType.INSTANT_DAMAGE, 3), ItemWeights.PROJECTILES_WEIGHT / 18),
    SHARP_ARROW_QUADRUPLE(getTippedArrow(PotionType.INSTANT_DAMAGE, 4), ItemWeights.PROJECTILES_WEIGHT / 24),

    STICKY_ARROW_SINGLE(getTippedArrow(PotionType.SLOWNESS, 1), ItemWeights.PROJECTILES_WEIGHT / 4),
    STICKY_ARROW_DOUBLE(getTippedArrow(PotionType.SLOWNESS, 2), ItemWeights.PROJECTILES_WEIGHT / 8),
    STICKY_ARROW_TRIPLE(getTippedArrow(PotionType.SLOWNESS, 3), ItemWeights.PROJECTILES_WEIGHT / 16),
    STICKY_ARROW_QUADRUPLE(getTippedArrow(PotionType.SLOWNESS, 4), ItemWeights.PROJECTILES_WEIGHT / 32),

    POISONED_ARROW_SINGLE(getTippedArrow(PotionType.POISON, 1), ItemWeights.PROJECTILES_WEIGHT / 8),
    POISONED_ARROW_DOUBLE(getTippedArrow(PotionType.POISON, 2), ItemWeights.PROJECTILES_WEIGHT / 16),
    POISONED_ARROW_TRIPLE(getTippedArrow(PotionType.POISON, 3), ItemWeights.PROJECTILES_WEIGHT / 32),
    POISONED_ARROW_QUADRUPLE(getTippedArrow(PotionType.POISON, 4), ItemWeights.PROJECTILES_WEIGHT / 64),

    WEAKENING_ARROW_SINGLE(getTippedArrow(PotionType.WEAKNESS, 1), ItemWeights.PROJECTILES_WEIGHT / 8),
    WEAKENING_ARROW_DOUBLE(getTippedArrow(PotionType.WEAKNESS, 2), ItemWeights.PROJECTILES_WEIGHT / 16),
    WEAKENING_ARROW_TRIPLE(getTippedArrow(PotionType.WEAKNESS, 3), ItemWeights.PROJECTILES_WEIGHT / 32),
    WEAKENING_ARROW_QUADRUPLE(getTippedArrow(PotionType.WEAKNESS, 4), ItemWeights.PROJECTILES_WEIGHT / 64),

    DIAMOND_HELMET(new ItemStack(Material.DIAMOND_HELMET), ItemWeights.ARMOR_WEIGHT / 8),
    DIAMOND_CHESTPLATE(new ItemStack(Material.DIAMOND_CHESTPLATE), ItemWeights.ARMOR_WEIGHT / 8),
    DIAMOND_LEGGINGS(new ItemStack(Material.DIAMOND_LEGGINGS), ItemWeights.ARMOR_WEIGHT / 8),
    DIAMOND_BOOTS(new ItemStack(Material.DIAMOND_BOOTS), ItemWeights.ARMOR_WEIGHT / 8),

    IRON_HELMET(new ItemStack(Material.IRON_HELMET), ItemWeights.ARMOR_WEIGHT / 4),
    IRON_CHESTPLATE(new ItemStack(Material.IRON_CHESTPLATE), ItemWeights.ARMOR_WEIGHT / 4),
    IRON_LEGGINGS(new ItemStack(Material.IRON_LEGGINGS), ItemWeights.ARMOR_WEIGHT / 4),
    IRON_BOOTS(new ItemStack(Material.IRON_BOOTS), ItemWeights.ARMOR_WEIGHT / 4),

    GOLDEN_HELMET(new ItemStack(Material.GOLDEN_HELMET), ItemWeights.ARMOR_WEIGHT / 2),
    GOLDEN_CHESTPLATE(new ItemStack(Material.GOLDEN_CHESTPLATE), ItemWeights.ARMOR_WEIGHT / 2),
    GOLDEN_LEGGINGS(new ItemStack(Material.GOLDEN_LEGGINGS), ItemWeights.ARMOR_WEIGHT / 2),
    GOLDEN_BOOTS(new ItemStack(Material.GOLDEN_BOOTS), ItemWeights.ARMOR_WEIGHT / 2),

    LEATHER_HELMET(new ItemStack(Material.LEATHER_HELMET), ItemWeights.ARMOR_WEIGHT),
    LEATHER_CHESTPLATE(new ItemStack(Material.LEATHER_CHESTPLATE), ItemWeights.ARMOR_WEIGHT),
    LEATHER_LEGGINGS(new ItemStack(Material.LEATHER_LEGGINGS), ItemWeights.ARMOR_WEIGHT),
    LEATHER_BOOTS(new ItemStack(Material.LEATHER_BOOTS), ItemWeights.ARMOR_WEIGHT),

    BREAD_SINGLE(new ItemStack(Material.BREAD), ItemWeights.FOOD_WEIGHT / 2),
    BREAD_DOUBLE(new ItemStack(Material.BREAD, 2), ItemWeights.FOOD_WEIGHT / 3),
    BREAD_TRIPLE(new ItemStack(Material.BREAD, 3), ItemWeights.FOOD_WEIGHT / 4),
    BREAD_QUADRUPLE(new ItemStack(Material.BREAD, 4), ItemWeights.FOOD_WEIGHT / 5),
    COOKED_BEEF_SINGLE(new ItemStack(Material.COOKED_BEEF), ItemWeights.FOOD_WEIGHT / 3),
    COOKED_BEEF_DOUBLE(new ItemStack(Material.COOKED_BEEF, 2), ItemWeights.FOOD_WEIGHT / 5),
    COOKED_BEEF_TRIPLE(new ItemStack(Material.COOKED_BEEF, 3), ItemWeights.FOOD_WEIGHT / 8),
    MUSHROOM_STEW(new ItemStack(Material.MUSHROOM_STEW), 1 * ItemWeights.FOOD_WEIGHT),
    MELON_SLICE(new ItemStack(Material.MELON_SLICE), 1 * ItemWeights.FOOD_WEIGHT),
    COOKED_PORKCHOP_SINGLE(new ItemStack(Material.COOKED_PORKCHOP), ItemWeights.FOOD_WEIGHT / 2),
    COOKED_PORKCHOP_DOUBLE(new ItemStack(Material.COOKED_PORKCHOP, 2), ItemWeights.FOOD_WEIGHT / 3),
    COOKED_PORKCHOP_TRIPLE(new ItemStack(Material.COOKED_PORKCHOP, 3), ItemWeights.FOOD_WEIGHT / 6),
    GOLDEN_CARROT(new ItemStack(Material.GOLDEN_CARROT), ItemWeights.FOOD_WEIGHT / 3),

    GOLDEN_APPLE(new ItemStack(Material.GOLDEN_APPLE), ItemWeights.SPECIAL_WEIGHT / 4),
    ENCHANTED_GOLDEN_APPLE(new ItemStack(Material.GOLDEN_APPLE), ItemWeights.SPECIAL_WEIGHT / 16, (short) 1),
    LAVA_BUCKET(new ItemStack(Material.LAVA_BUCKET), ItemWeights.SPECIAL_WEIGHT / 16),
    WATER_BUCKET(new ItemStack(Material.WATER_BUCKET), ItemWeights.SPECIAL_WEIGHT / 8),
    TRACKER(new ItemStack(Material.COMPASS), ItemWeights.SPECIAL_WEIGHT,
            Messages.getString("ItemChances.TrackerName")),
    FORCEFIELD(new ItemStack(Material.NETHER_STAR), ItemWeights.SPECIAL_WEIGHT / 4,
            Messages.getString("ItemChances.ForcefieldName")),
    FIRE_GRENADE(new ItemStack(Material.FIRE_CHARGE), ItemWeights.SPECIAL_WEIGHT / 8,
            Messages.getString("ItemChances.FireGrenadeName")),
    SWITCHER(new ItemStack(Material.NAME_TAG), ItemWeights.SPECIAL_WEIGHT / 4,
            Messages.getString("ItemChances.SwitcherName")),
    BANDAGE(new ItemStack(Material.PAPER), ItemWeights.SPECIAL_WEIGHT / 2,
            Messages.getString("ItemChances.BandageName")),
    TNT(new ItemStack(Material.TNT), ItemWeights.SPECIAL_WEIGHT / 4),
    TNT_INCENDIARY(new ItemStack(Material.TNT), ItemWeights.SPECIAL_WEIGHT / 8,
            Messages.getString("ItemChances.TntIncendiaryName")),
    TOTEM_OF_UNDYING(new ItemStack(Material.TOTEM_OF_UNDYING), ItemWeights.SPECIAL_WEIGHT / 64),

    GOLD(new ItemStack(Material.GOLD_INGOT), ItemWeights.RESOURCE_WEIGHT),
    IRON(new ItemStack(Material.IRON_INGOT), ItemWeights.RESOURCE_WEIGHT / 2),
    DIAMOND(new ItemStack(Material.DIAMOND), ItemWeights.RESOURCE_WEIGHT / 8),
    STICK(new ItemStack(Material.STICK), ItemWeights.RESOURCE_WEIGHT / 4),
    STICK_DOUBLE(new ItemStack(Material.STICK, 2), ItemWeights.RESOURCE_WEIGHT / 4),

    WOODEN_PICKAXE(new ItemStack(Material.WOODEN_PICKAXE), ItemWeights.RESOURCE_WEIGHT),
    GOLDEN_PICKAXE(new ItemStack(Material.GOLDEN_PICKAXE), ItemWeights.RESOURCE_WEIGHT / 4),
    IRON_PICKAXE(new ItemStack(Material.IRON_PICKAXE), ItemWeights.RESOURCE_WEIGHT / 4),
    DIAMOND_PICKAXE(new ItemStack(Material.DIAMOND_PICKAXE), ItemWeights.RESOURCE_WEIGHT / 8),
    WOODEN_SHOVEL(new ItemStack(Material.WOODEN_SHOVEL), ItemWeights.RESOURCE_WEIGHT / 2),
    GOLDEN_SHOVEL(new ItemStack(Material.GOLDEN_SHOVEL), ItemWeights.RESOURCE_WEIGHT / 8),
    IRON_SHOVEL(new ItemStack(Material.IRON_SHOVEL), ItemWeights.RESOURCE_WEIGHT / 8),
    DIAMOND_SHOVEL(new ItemStack(Material.DIAMOND_SHOVEL), ItemWeights.RESOURCE_WEIGHT / 16),
    ;

    public final ItemStack item;
    public final double probability;

    @Contract(pure = true)
    ItemChances(ItemStack item, double probability) {
        this.item = item;
        this.probability = probability;
    }

    @SuppressWarnings("SameParameterValue")
    ItemChances(@NotNull ItemStack item, double probability, short damage) {
        ItemMeta meta = item.getItemMeta();
        ((Damageable) meta).setDamage(damage);
        item.setItemMeta(meta);

        this.item = item;
        this.probability = probability;
    }

    ItemChances(@NotNull ItemStack item, double probability, String customName) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(customName);
        item.setItemMeta(meta);

        this.item = item;
        this.probability = probability;
    }

    private static ItemStack getTippedArrow(PotionType type, int count) {
        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, count);
        PotionMeta meta = (PotionMeta) arrow.getItemMeta();
        meta.setBasePotionData(new PotionData(type, false, false));
        arrow.setItemMeta(meta);
        return arrow;
    }

}

class ItemWeights {
    static final double WEAPONS_WEIGHT = 1 / 2;
    static final double PROJECTILES_WEIGHT = 1 / 4;
    static final double ARMOR_WEIGHT = 1 / 3;
    static final double FOOD_WEIGHT = 1;
    static final double SPECIAL_WEIGHT = 1 / 4;
    static final double RESOURCE_WEIGHT = 1 / 5;
}