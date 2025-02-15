package com.mycatan;

import java.awt.*;

public class FlippableHex extends Hex implements IFlippableTile{
    //FIELDS
    private boolean flipped;

    //CONSTRUCTORS
    public FlippableHex() {
        this(0,0,false);
    }
    public FlippableHex(int row, int col) {
        this(row, col, false);
    }
    public FlippableHex(int row, int col, boolean debug) {
        super(row, col, debug);
        flipped = false;
    }



    //METHODS
    public void flip(boolean show) {

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
        if(flipped || isDebug()){
            return ResourceTile.getBiomeColor(ResourceTile.UNFLIPPED_RESOURCE);
        }
        return super.getBiomeColor();
    }

}
