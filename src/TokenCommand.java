public class TokenCommand implements ICommand
{
    @Override
    public int get(Tile hex)
    {
        if(hex instanceof Hex){
            return ((Hex)hex).getToken();
        }
        return 0;
    }
    @Override
    public void set(Tile hex , int data)
    {
        if(hex instanceof Hex){
            ((Hex)hex).setToken(data);
        }
    }
}