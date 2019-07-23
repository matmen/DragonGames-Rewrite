package world;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

class BlockPopulator extends org.bukkit.generator.BlockPopulator {

    private final Material[] veinMaterials = {Material.STONE, Material.GOLD_ORE, Material.IRON_ORE, Material.COAL_ORE, Material.DIAMOND_ORE};
    private final TreeType[] treeTypes = {TreeType.TREE, TreeType.BIRCH, TreeType.BIG_TREE};

    private static int getHighestBlockAt(@NotNull Chunk chunk, int x, int z) {
        int y;
        //noinspection StatementWithEmptyBody
        for (y = chunk.getWorld().getMaxHeight() - 1; chunk.getBlock(x, y, z).getType() == Material.AIR; y--) ;
        return y;
    }

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        generateVeins(random, chunk);

        generateEggs(random, chunk);

        generateTrees(random, chunk);
        generateGrass(random, chunk);
    }

    private void generateVeins(Random random, Chunk chunk) {
        for (int i = 0; i < 24; i++) {
            Material veinType = veinMaterials[(int) Math.floor(Math.pow(random.nextDouble(), 2) * veinMaterials.length)];
            if (veinType == Material.STONE) continue;

            int x = random.nextInt(15);
            int z = random.nextInt(15);
            int y = random.nextInt(getHighestBlockAt(chunk, x, z));

            if (chunk.getBlock(x, y, z).getType() == Material.STONE) {
                boolean isStone = true;

                int placed = 0;
                int tries = 0;

                while (isStone || (placed < 2 && tries < 6)) {
                    if (isStone) {
                        chunk.getBlock(x, y, z).setType(veinType, false);
                        placed++;
                    } else tries++;

                    if (random.nextDouble() < 0.4) {
                        switch (random.nextInt(5)) {
                            case 0:
                                x++;
                                break;
                            case 1:
                                y++;
                                break;
                            case 2:
                                z++;
                                break;
                            case 3:
                                x--;
                                break;
                            case 4:
                                y--;
                                break;
                            case 5:
                                z--;
                                break;
                        }

                        isStone = chunk.getBlock(x, y, z).getType() == Material.STONE;
                    } else isStone = false;
                }
            }
        }
    }

    private void generateGrass(Random random, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (random.nextDouble() < 0.3) {
                    int y = getHighestBlockAt(chunk, x, z);
                    if (y < ChunkGenerator.seaLevel + 5 &&
                            random.nextDouble() / Math.max(y - ChunkGenerator.seaLevel, 1) < 0.1) continue;

                    Block g = chunk.getBlock(x, y, z);
                    if (g.getType() != Material.GRASS_BLOCK) continue;

                    Block b = chunk.getBlock(x, y + 1, z);
                    b.setType(Material.GRASS, false);
                }
            }
        }
    }

    private void generateEggs(Random random, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (random.nextDouble() < 0.001) {
                    int y = getHighestBlockAt(chunk, x, z);

                    Block g = chunk.getBlock(x, y, z);
                    if (g.getType() != Material.GRASS_BLOCK) continue;

                    chunk.getBlock(x, y + 1, z).setType(Material.DRAGON_EGG, false);
                }
            }
        }
    }

    private void generateTrees(@NotNull Random random, Chunk chunk) {
        if (random.nextDouble() < 0.8) {
            int amount = random.nextInt(3) + 1;

            for (int i = 1; i < amount; i++) {
                int x = random.nextInt(15);
                int z = random.nextInt(15);
                int y = getHighestBlockAt(chunk, x, z);

                Block b = chunk.getBlock(x, y, z);
                if (b.getType() != Material.GRASS_BLOCK) continue;

                TreeType treeType = treeTypes[(int) Math.floor(Math.pow(random.nextDouble(), 2) * treeTypes.length)];
                chunk.getWorld().generateTree(chunk.getBlock(x, y, z).getLocation(), treeType);
            }
        }
    }

}
