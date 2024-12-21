public class BiomeCommand implements ICommand
{
    @Override
    public int get(Tile tile)
    {
        return tile.getBiomeChar();
    }
    @Override
    public void set(Tile tile, int data)
    {
        tile.setBiome(data);
    }
}