public interface ICommand
{
    /**
     * <p>Get the data element from {@code tile}.</p>
     * @param tile the object that data gets extracted from
     * @return the data element
     */
    int get(Tile tile);


    /**
     * <p>Set the data element in {@code tile}.</p>
     * @param tile the object that gets modified
     * @param data the new data value
     */
    void set(Tile tile, int data);
}
