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

    private static int getHighestBlockAt(@NotNull Chunk chunk, int x, int z) {
        int y;
        //noinspection StatementWithEmptyBody
        for (y = chunk.getWorld().getMaxHeight() - 1; chunk.getBlock(x, y, z).getType() == Material.AIR; y--) ;
        return y;
    }

    @Override
    public void populate(World world, @NotNull Random random, Chunk chunk) {
        generateVeins(random, chunk);
        //generateLakes(random, chunk);

        generateEggs(random, chunk);

        generateTrees(random, chunk);
        generateGrass(random, chunk);
    }

    private void generateLakes(@NotNull Random random, Chunk chunk) {
        if (random.nextDouble() < 0.01) {
            World world = chunk.getWorld();

            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();

            int x = chunkX * 16 + random.nextInt(15) - 8;
            int z = chunkZ * 16 + random.nextInt(15) - 8;

            int y = getHighestBlockAt(chunk, x, z);
            y -= 7;

            Block block = world.getBlockAt(z + 8, y, z + 8);

            if (random.nextInt(100) < 90) block.setType(Material.WATER);
            else block.setType(Material.LAVA);

            boolean[] boolMatrix = new boolean[2048];
            int i = random.nextInt(4) + 4;

            int j, j1, k1;

            for (j = 0; j < i; ++j) {
                double d0 = random.nextDouble() * 6.0D + 3.0D;
                double d1 = random.nextDouble() * 4.0D + 2.0D;
                double d2 = random.nextDouble() * 6.0D + 3.0D;
                double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                for (int k = 1; k < 15; ++k)
                    for (int l = 1; l < 15; ++l)
                        for (int i1 = 1; i1 < 7; ++i1) {
                            double d6 = ((double) k - d3) / (d0 / 2.0D);
                            double d7 = ((double) i1 - d4) / (d1 / 2.0D);
                            double d8 = ((double) l - d5) / (d2 / 2.0D);
                            double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                            if (d9 < 1.0D)
                                boolMatrix[(k * 16 + l) * 8 + i1] = true;
                        }
            }

            for (j = 0; j < 16; ++j)
                for (k1 = 0; k1 < 16; ++k1)
                    for (j1 = 0; j1 < 8; ++j1)
                        if (j1 > 4 && boolMatrix[(j * 16 + k1) * 8 + j1])
                            world.getBlockAt(x + j, y + j1, z + k1).setType(Material.AIR);

            for (j = 0; j < 16; ++j)
                for (k1 = 0; k1 < 16; ++k1)
                    for (j1 = 4; j1 < 8; ++j1)
                        if (boolMatrix[(j * 16 + k1) * 8 + j1]) {
                            int X1 = x + j;
                            int Y1 = y + j1 - 1;
                            int Z1 = z + k1;
                            if (world.getBlockAt(X1, Y1, Z1).getType() == Material.DIRT)
                                world.getBlockAt(X1, Y1, Z1).setType(Material.GRASS);
                        }
        }
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
                        chunk.getBlock(x, y, z).setType(veinType);
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

    @SuppressWarnings("deprecation")
    private void generateGrass(Random random, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (random.nextDouble() < 0.3) {
                    int y = getHighestBlockAt(chunk, x, z);
                    if (y < ChunkGenerator.seaLevel + 5 &&
                            random.nextDouble() / Math.max(y - ChunkGenerator.seaLevel, 1) < 0.1) continue;

                    Block g = chunk.getBlock(x, y, z);
                    if (g.getType() != Material.GRASS) continue;

                    Block b = chunk.getBlock(x, y + 1, z);
                    b.setType(Material.LONG_GRASS);
                    b.setData((byte) 1);
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
                    if (g.getType() != Material.GRASS) continue;

                    chunk.getBlock(x, y + 1, z).setType(Material.DRAGON_EGG);
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
                if (b.getType() != Material.GRASS) continue;

                chunk.getWorld().generateTree(chunk.getBlock(x, y, z).getLocation(), TreeType.TREE);
            }
        }
    }

}
