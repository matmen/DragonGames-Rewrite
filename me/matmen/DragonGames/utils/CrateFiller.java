package me.matmen.DragonGames.utils;

import me.matmen.DragonGames.enums.Enchantments;
import me.matmen.DragonGames.enums.ItemChances;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class CrateFiller {
    private final ArrayList<ItemStack> items = new ArrayList<>();
    private final Random random = new Random();

    public CrateFiller() {
        double lowestProbability = 1;

        for (ItemChances i : ItemChances.values())
            if (i.probability < lowestProbability && i.probability > 0)
                lowestProbability = i.probability;

        for (ItemChances i : ItemChances.values())
            for (int c = 0; c <= (lowestProbability * i.probability); c++)
                items.add(i.item);
    }

    public ItemStack getRandomItem() {
        ItemStack item = items.get(random.nextInt(items.size()));

        for (Enchantments e : Enchantments.values())
            if (e.type == item.getType()) {
                int enchantmentLevel = (int) Math.round(Math.pow(random.nextDouble(), 14) * e.maxLevel);

                if(enchantmentLevel > 0)
                    item.addEnchantment(e.enchantment, enchantmentLevel);
            }

        return item;
    }

}
