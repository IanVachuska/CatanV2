public class IdCommand implements ICommand{
    /**
     * <p>Calls {@code getId()} on {@code tile}.</p>
     * @param tile the object that data gets extracted from
     * @return the {@code id} value
     */
    @Override
    public int get(Tile tile) {
        return tile.getId();
    }


    /**
     * <p>Calls {@code setID()} on {@code tile}.</p>
     * @param tile the object that gets modified
     * @param data the new {@code id} value
     */
    @Override
    public void set(Tile tile, int data) {
        tile.setId(data);
    }
}
