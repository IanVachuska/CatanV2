public interface ICommand
{
    int get(Tile tile);
    void set(Tile tile, int data);
}
