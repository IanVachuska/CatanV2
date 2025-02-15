package com.mycatan;

public class TokenCommand implements ICommand
{
    /**
     * <p>Calls {@code getToken()} on {@code hex}.</p>
     * @param tile the object that data gets extracted from
     * @return the {@code token} value
     */
    @Override
    public int get(Tile tile)
    {
        if(tile instanceof Hex hex){
            Token token = hex.getToken();
            if(token != null) {
                return hex.getToken().valueOf();
            }
        }
        return 0;
    }


    /**
     * <p>Calls {@code setToken()} on {@code hex}.</p>
     * @param tile the object that gets modified
     * @param data the new {@code token} value
     */
    @Override
    public void set(Tile tile , int data)
    {
        if(tile instanceof Hex hex){
            //hex.setTokenValue(data);
        }
    }
}