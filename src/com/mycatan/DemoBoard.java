package com.mycatan;

import javax.swing.*;

public class DemoBoard extends Board {
    public DemoBoard() {
        super(0, Board.DEBUG);
    }

    @Override
    public void initHexGridDim() {
        super.setHexGridDim(3, 3);
    }

    @Override
    public void initTileCounts() {
        super.setTileCounts(0,7,0,4);
    }

    @Override
    public void initResourceCounts() {
        int[] r = {1,1,1,1,1,0,0,2};
        super.setResourceCounts(r.clone());
    }

    @Override
    public void initPortCounts() {
        int[] p = {1,1,1,0,0,1};
        super.setPortCounts(p.clone());
    }

    @Override
    public void initTokens() {
        int[] t = {9, 11, 4, 8, 2};
        super.setTokens(t);
    }

    @Override
    public void placeFixedTilesSmall() {
        int r = 0, c = 0, id = getShuffledHexCount();
        placeFixedHex(r, c++, id++, ResourceTile.WOOD);
        placeFixedHex(r, c++, id++, ResourceTile.OCEAN);

        r = 1; c = 0;
        placeFixedHex(r, c++, id++, ResourceTile.HAY);
        placeFixedHex(r, c++, id++, ResourceTile.OCEAN);
        placeFixedHex(r, c  , id++, ResourceTile.BRICK);

        r = 2; c = 0;
        placeFixedHex(r, c++, id++, ResourceTile.ROCK);
        placeFixedHex(r, c  , id++, ResourceTile.SHEEP);

    }
    @Override
    public void initHexSpiral(boolean randomStart){
        super.initHexSpiral(false);
    }
    @Override
    public void findValidPorts(){
        PortCollection pc = getPortCollection();
        int indexV = 0;
        pc.add(pc.get(4,false),indexV++);
        pc.add(pc.get(6,false),indexV++);
        pc.add(pc.get(9,false),indexV++);
        pc.add(pc.get(12,false),indexV++);
    }

    @Override
    public void placeFixedTilesLarge() {
        placeFixedTilesSmall();
    }

    @Override
    public void placeUnflippedTilesSmall() {

    }

    @Override
    public void placeUnflippedTilesLarge() {

    }
}
