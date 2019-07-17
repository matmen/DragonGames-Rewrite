package enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ItemChances {
	WOOD_SWORD(new ItemStack(Material.WOOD_SWORD), 1 * ItemWeights.WEAPONS_WEIGHT),
	STONE_SWORD(new ItemStack(Material.STONE_SWORD), 1 / 2 * ItemWeights.WEAPONS_WEIGHT),
	IRON_SWORD(new ItemStack(Material.IRON_SWORD), 1 / 4 * ItemWeights.WEAPONS_WEIGHT),
	DIAMOND_AXE(new ItemStack(Material.DIAMOND_AXE), 1 / 4 * ItemWeights.WEAPONS_WEIGHT),
	DIAMOND_SWORD(new ItemStack(Material.DIAMOND_SWORD), 1 / 8 * ItemWeights.WEAPONS_WEIGHT),

	BOW(new ItemStack(Material.BOW), 1 * ItemWeights.WEAPONS_WEIGHT),
	ARROW_SINGLE(new ItemStack(Material.ARROW, 1), 1 * ItemWeights.WEAPONS_WEIGHT),
	ARROW_DOUBLE(new ItemStack(Material.ARROW, 2), 1 / 2 * ItemWeights.WEAPONS_WEIGHT),
	ARROW_TRIPLE(new ItemStack(Material.ARROW, 3), 1 / 4 * ItemWeights.WEAPONS_WEIGHT),
	ARROW_QUADRUPLE(new ItemStack(Material.ARROW, 4), 1 / 8 * ItemWeights.WEAPONS_WEIGHT),

	DIAMOND_HELMET(new ItemStack(Material.DIAMOND_HELMET), 1 / 8 * ItemWeights.ARMOR_WEIGHT),
	DIAMOND_CHESTPLATE(new ItemStack(Material.DIAMOND_CHESTPLATE), 1 / 8 * ItemWeights.ARMOR_WEIGHT),
	DIAMOND_LEGGINGS(new ItemStack(Material.DIAMOND_LEGGINGS), 1 / 8 * ItemWeights.ARMOR_WEIGHT),
	DIAMOND_BOOTS(new ItemStack(Material.DIAMOND_BOOTS), 1 / 8 * ItemWeights.ARMOR_WEIGHT),

	IRON_HELMET(new ItemStack(Material.IRON_HELMET), 1 / 4 * ItemWeights.ARMOR_WEIGHT),
	IRON_CHESTPLATE(new ItemStack(Material.IRON_CHESTPLATE), 1 / 4 * ItemWeights.ARMOR_WEIGHT),
	IRON_LEGGINGS(new ItemStack(Material.IRON_LEGGINGS), 1 / 4 * ItemWeights.ARMOR_WEIGHT),
	IRON_BOOTS(new ItemStack(Material.IRON_BOOTS), 1 / 4 * ItemWeights.ARMOR_WEIGHT),

	GOLD_HELMET(new ItemStack(Material.GOLD_HELMET), 1 / 2 * ItemWeights.ARMOR_WEIGHT),
	GOLD_CHESTPLATE(new ItemStack(Material.GOLD_CHESTPLATE), 1 / 2 * ItemWeights.ARMOR_WEIGHT),
	GOLD_LEGGINGS(new ItemStack(Material.GOLD_LEGGINGS), 1 / 2 * ItemWeights.ARMOR_WEIGHT),
	GOLD_BOOTS(new ItemStack(Material.GOLD_BOOTS), 1 / 2 * ItemWeights.ARMOR_WEIGHT),

	LEATHER_HELMET(new ItemStack(Material.LEATHER_HELMET), 1 * ItemWeights.ARMOR_WEIGHT),
	LEATHER_CHESTPLATE(new ItemStack(Material.LEATHER_CHESTPLATE), 1 * ItemWeights.ARMOR_WEIGHT),
	LEATHER_LEGGINGS(new ItemStack(Material.LEATHER_LEGGINGS), 1 * ItemWeights.ARMOR_WEIGHT),
	LEATHER_BOOTS(new ItemStack(Material.LEATHER_BOOTS), 1 * ItemWeights.ARMOR_WEIGHT),

	BREAD_SINGLE(new ItemStack(Material.BREAD), 1 / 2 * ItemWeights.FOOD_WEIGHT),
	BREAD_DOUBLE(new ItemStack(Material.BREAD, 2), 1 / 3 * ItemWeights.FOOD_WEIGHT),
	BREAD_TRIPLE(new ItemStack(Material.BREAD, 3), 1 / 4 * ItemWeights.FOOD_WEIGHT),
	BREAD_QUADRUPLE(new ItemStack(Material.BREAD, 4), 1 / 5 * ItemWeights.FOOD_WEIGHT),
	COOKED_BEEF_SINGLE(new ItemStack(Material.COOKED_BEEF), 1 / 3 * ItemWeights.FOOD_WEIGHT),
	COOKED_BEEF_DOUBLE(new ItemStack(Material.COOKED_BEEF, 2), 1 / 5 * ItemWeights.FOOD_WEIGHT),
	COOKED_BEEF_TRIPLE(new ItemStack(Material.COOKED_BEEF, 3), 1 / 8 * ItemWeights.FOOD_WEIGHT),
	MUSHROOM_SOUP(new ItemStack(Material.MUSHROOM_SOUP), 1 * ItemWeights.FOOD_WEIGHT),
	MELON(new ItemStack(Material.MELON), 1 * ItemWeights.FOOD_WEIGHT),
	GRILLED_PORK_SINGLE(new ItemStack(Material.GRILLED_PORK), 1 / 2 * ItemWeights.FOOD_WEIGHT),
	GRILLED_PORK_DOUBLE(new ItemStack(Material.GRILLED_PORK, 2), 1 / 3 * ItemWeights.FOOD_WEIGHT),
	GRILLED_PORK_TRIPLE(new ItemStack(Material.GRILLED_PORK, 3), 1 / 6 * ItemWeights.FOOD_WEIGHT),
	GOLDEN_CARROT(new ItemStack(Material.GOLDEN_CARROT), 1 / 3 * ItemWeights.FOOD_WEIGHT),

	GOLDEN_APPLE(new ItemStack(Material.GOLDEN_APPLE), 1 / 4 * ItemWeights.SPECIAL_WEIGHT),
	ENCHANTED_GOLDEN_APPLE(new ItemStack(Material.GOLDEN_APPLE), 1 / 16 * ItemWeights.SPECIAL_WEIGHT, (short) 1),
	TRACKER(new ItemStack(Material.COMPASS), 1 * ItemWeights.SPECIAL_WEIGHT,
			Messages.getString("ItemChances.TrackerName")), //$NON-NLS-1$
	FORCEFIELD(new ItemStack(Material.NETHER_STAR), 1 / 2 * ItemWeights.SPECIAL_WEIGHT,
			Messages.getString("ItemChances.ForcefieldName")); //$NON-NLS-1$

	public ItemStack item;
	public double probability;

	private ItemChances(ItemStack item, double probability) {
		this.item = item;
		this.probability = probability;
	}

	private ItemChances(ItemStack item, double probability, short durability) {
		item.setDurability(durability);
		this.item = item;
		this.probability = probability;
	}

	private ItemChances(ItemStack item, double probability, String customName) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(customName);
		item.setItemMeta(meta);

		this.item = item;
		this.probability = probability;
	}

}

class ItemWeights {
	static int WEAPONS_WEIGHT = 1 / 2;
	static int ARMOR_WEIGHT = 1 / 2;
	static int FOOD_WEIGHT = 1;
	static int SPECIAL_WEIGHT = 1 / 3;
}