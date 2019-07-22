package world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChunkGenerator extends org.bukkit.generator.ChunkGenerator {

    private static final int scale = 15;
    private static final int defaultHeight = 32 + scale;
    static final int seaLevel = defaultHeight - scale + 8;
    private static final double frequency = 0.5;
    private static final double amplitude = 0.5;

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        SimplexOctaveGenerator heightmapGenerator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        heightmapGenerator.setScale(0.01D);

        SimplexOctaveGenerator dirtThicknessGenerator = new SimplexOctaveGenerator(new Random(world.getSeed() + 1), 6);
        dirtThicknessGenerator.setScale(0.01D);

        SimplexOctaveGenerator caveLevelGenerator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        caveLevelGenerator.setScale(0.008D);

        SimplexOctaveGenerator caveHeightGenerator = new SimplexOctaveGenerator(new Random(world.getSeed() + 2), 4);
        caveHeightGenerator.setScale(0.1D);

        ChunkData chunk = createChunkData(world);

        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++) {
                world.setBiome(x, z, Biome.PLAINS);

                int currentHeight = (int) (heightmapGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) * scale) + defaultHeight;
                int maxHeight = currentHeight;

                chunk.setBlock(x, currentHeight, z, Material.GRASS);
                currentHeight--;

                int dirtThickness = (int) Math.abs(dirtThicknessGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude) * 3) + 1;
                int dirtTo = currentHeight - dirtThickness;

                while (currentHeight >= dirtTo) {
                    chunk.setBlock(x, currentHeight, z, Material.DIRT);
                    currentHeight--;
                }

                while (currentHeight > 0) {
                    chunk.setBlock(x, currentHeight, z, Material.STONE);
                    currentHeight--;
                }

                chunk.setBlock(x, 0, z, Material.BEDROCK);

                for (currentHeight = seaLevel; currentHeight <= seaLevel; currentHeight--) {
                    if (chunk.getType(x, currentHeight, z) == Material.AIR) {
                        chunk.setBlock(x, currentHeight, z, Material.STATIONARY_WATER);
                        world.setBiome(x, z, Biome.RIVER);
                    } else {
                        if (currentHeight == seaLevel) break;
                        if (chunk.getType(x, currentHeight + random.nextInt(3), z) == Material.GRAVEL) break;

                        if (seaLevel - currentHeight < 2)
                            chunk.setBlock(x, currentHeight, z, Material.SAND);
                        else if (seaLevel - currentHeight < random.nextInt(2) + 2)
                            chunk.setBlock(x, currentHeight, z, Material.SAND);
                        else
                            chunk.setBlock(x, currentHeight, z, Material.GRAVEL);
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

    @Override
    public List<org.bukkit.generator.BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(new BlockPopulator());
    }

}
