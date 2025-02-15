package com.mycatan;

public class TypeCommand implements ICommand
{
    /**
     * <p>Calls {@code getTypeChar()} on {@code tile}.</p>
     * <p>Note: return value of this call must be cast to {@code char}.</p>
     * @param tile the object that data gets extracted from
     * @return the char representation of the type
     */
    @Override
    public int get(Tile tile)
    {
        if(tile instanceof Hex) {
            return ((Hex)tile).getTypeChar();
        }
        return 'ø';
    }


    /**
     * <p>Calls {@code setType()} on {@code tile}.</p>
     * @param tile the object that gets modified
     * @param data the new {@code type} value
     */
    @Override
    public void set(Tile tile , int data)
    {
        if(tile instanceof Hex) {
            ((Hex)tile).setType(data);
        }
    }
}
