public class TypeCommand implements ICommand
{
    @Override
    public int get(Tile hex)
    {
        if(hex instanceof Hex){
            return ((Hex)hex).getType();
        }
        return 0;
    }
    @Override
    public void set(Tile hex , int data)
    {
        if(hex instanceof Hex){
            ((Hex)hex).setType(data);
        }
    }
}
