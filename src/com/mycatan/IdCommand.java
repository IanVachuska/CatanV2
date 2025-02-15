package com.mycatan;

public class IdCommand implements ICommand{
    /**
     * <p>Calls {@code getId()} on {@code tile}.</p>
     * @param tile the object that data gets extracted from
     * @return the {@code id} value
     */
    @Override
    public int get(Tile tile) {
        int id = 0;
        if(tile instanceof ResourceTile resourceTile) {
            id = resourceTile.getId();
        }
        return id;
    }


    /**
     * <p>Calls {@code setID()} on {@code tile}.</p>
     * @param tile the object that gets modified
     * @param data the new {@code id} value
     */
    @Override
    public void set(Tile tile, int data) {
        if(tile instanceof ResourceTile resourceTile) {
            resourceTile.setId(data);
        }
    }
}
