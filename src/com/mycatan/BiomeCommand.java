package com.mycatan;

public class BiomeCommand implements ICommand
{
    /**
     * <p>Calls {@code getBiomeChar()} on {@code tile}.</p>
     * <p>Note: return value of this call must be cast to {@code char}.</p>
     * @param tile the object that data gets extracted from
     * @return the char representation of the biome
     */
    @Override
    public int get(Tile tile)
    {
        return tile.getBiomeChar();
    }


    /**
     * <p>Calls {@code setBiome()} on {@code tile}.</p>
     * @param tile the object that gets modified
     * @param data the new {@code biome} value
     */
    @Override
    public void set(Tile tile, int data)
    {
        tile.setBiome(data);
    }
}