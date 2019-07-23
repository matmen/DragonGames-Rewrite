package world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChunkGenerator extends org.bukkit.generator.ChunkGenerator {

    private static final int scale = 15;
    private static final int defaultHeight = 32 + scale;
    static final int seaLevel = defaultHeight - scale + 8;
    private static final double frequency = 0.5;
    private static final double amplitude = 0.5;

    @NotNull
    @Override
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        SimplexOctaveGenerator heightmapGenerator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        heightmapGenerator.setScale(0.01D);

        SimplexOctaveGenerator caveLevelGenerator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        caveLevelGenerator.setScale(0.008D);

        SimplexOctaveGenerator oceanSandGenerator = new SimplexOctaveGenerator(new Random(world.getSeed() + 1), 8);
        oceanSandGenerator.setScale(0.01D);

        SimplexOctaveGenerator oceanBedThicknessGenerator = new SimplexOctaveGenerator(new Random(world.getSeed() + 2), 8);
        oceanBedThicknessGenerator.setScale(0.01D);

        SimplexOctaveGenerator dirtThicknessGenerator = new SimplexOctaveGenerator(new Random(world.getSeed() + 3), 6);
        dirtThicknessGenerator.setScale(0.01D);

        SimplexOctaveGenerator caveHeightGenerator = new SimplexOctaveGenerator(new Random(world.getSeed() + 4), 4);
        caveHeightGenerator.setScale(0.1D);

        ChunkData chunk = createChunkData(world);

        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++) {
                world.setBiome(x, z, Biome.PLAINS);

                int currentHeight = (int) (heightmapGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) * scale) + defaultHeight;
                int maxHeight = currentHeight;

                chunk.setBlock(x, currentHeight, z, Material.GRASS_BLOCK);
                currentHeight--;

                int dirtTo = currentHeight - ((int) Math.abs(dirtThicknessGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) * 2) + 1);

                while (currentHeight > dirtTo) {
                    chunk.setBlock(x, currentHeight, z, Material.DIRT);
                    currentHeight--;
                }

                while (currentHeight > 0) {
                    chunk.setBlock(x, currentHeight, z, Material.STONE);
                    currentHeight--;
                }

                chunk.setBlock(x, 0, z, Material.BEDROCK);

                if (maxHeight < seaLevel) {
                    int oceanBedThickness = (int) Math.abs(oceanBedThicknessGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) * 3) + 1;

                    for (currentHeight = seaLevel; currentHeight > maxHeight - oceanBedThickness; currentHeight--) {
                        if (currentHeight > maxHeight) {
                            chunk.setBlock(x, currentHeight, z, Material.WATER);
                            world.setBiome(x, z, Biome.RIVER);
                        } else {
                            if (oceanSandGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) > 0.5)
                                chunk.setBlock(x, currentHeight, z, Material.SAND);
                            else
                                chunk.setBlock(x, currentHeight, z, Material.GRAVEL);
                        }
                    }
                }

                int caveLevel = (int) Math.abs(caveLevelGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) * (maxHeight - scale)) + scale;
                if (maxHeight == seaLevel && caveLevel > (defaultHeight - scale - 8)) continue;

                int caveHeight = (int) Math.abs(caveHeightGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) * 12);

                if (caveHeight >= 6) {
                    for (int yOff = 0; yOff < caveHeight - 4; yOff++) {
                        int y = caveLevel - yOff;
                        if (chunk.getType(x, y, z) == Material.STONE)
                            chunk.setBlock(x, y, z, Material.AIR);
                    }
                }
            }
        return chunk;
    }

    @NotNull
    @Override
    public List<org.bukkit.generator.BlockPopulator> getDefaultPopulators(@NotNull World world) {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(new BlockPopulator());
    }

}
