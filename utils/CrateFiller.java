package utils;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

import enums.ItemChances;

public class CrateFiller {
	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	Random random = new Random();

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
		return items.get(random.nextInt(items.size()));
	}

}
