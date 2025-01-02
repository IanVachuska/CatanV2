public class IdCommand implements ICommand{
    @Override
    public int get(Tile tile) {
        return tile.getId();
    }

    @Override
    public void set(Tile tile, int data) {
        tile.setId(data);
    }
}
