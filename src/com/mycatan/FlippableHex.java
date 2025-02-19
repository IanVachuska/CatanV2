package com.mycatan;

import java.awt.*;

public class FlippableHex extends Hex implements IFlippableTile{
    //FIELDS
    private boolean flipped;

    //CONSTRUCTORS
    public FlippableHex() {
        this(0,0);
    }
    public FlippableHex(int row, int col) {
        super(row, col);
        flip(false);
    }



    //METHODS
    public void flip(boolean show) {
        flipped = show;
        if(getToken() != null){
            getToken().setVisible(show);
        }
    }

    public boolean isFlipped() {
        return flipped;
    }

    /**
     * <p>If the tile is type {@code UNFLIPPED_TYPE}, return unflipped color instead.</p>
     * @return the {@code Color} object representing the biome or unflipped color
     */
    @Override
    public Color getBiomeColor(){
        if(flipped){
            return super.getBiomeColor();

        }
        return ResourceTile.getBiomeColor(ResourceTile.UNFLIPPED_RESOURCE);

    }
}
