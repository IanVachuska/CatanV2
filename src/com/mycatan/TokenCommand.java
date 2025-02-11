package com.mycatan;

public class TokenCommand implements ICommand
{
    /**
     * <p>Calls {@code getToken()} on {@code hex}.</p>
     * @param hex the object that data gets extracted from
     * @return the {@code token} value
     */
    @Override
    public int get(Tile hex)
    {
        if(hex instanceof Hex){
            return ((Hex)hex).getToken();
        }
        return 0;
    }


    /**
     * <p>Calls {@code setToken()} on {@code hex}.</p>
     * @param hex the object that gets modified
     * @param data the new {@code token} value
     */
    @Override
    public void set(Tile hex , int data)
    {
        if(hex instanceof Hex){
            ((Hex)hex).setToken(data);
        }
    }
}