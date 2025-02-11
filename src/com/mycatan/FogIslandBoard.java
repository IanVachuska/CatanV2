package com.mycatan;

public class FogIslandBoard extends Board implements IFlippable {

    //CONSTRUCTORS
    public FogIslandBoard(int boardSize, int flags) {
        super(boardSize, flags);
    }


    //INITIALIZERS

    /**
     * <p>Initializes the {@code rows} and {@code columns} of the hexGrid</p>
     */
    @Override
    public void initHexGridDim() {
        super.setHexGridDim(7,11);
        /*
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                super.setHexGridDim(5,5);
                break;
            case Board.LARGE_BOARD:
                super.setHexGridDim(7,11);
                break;
        }

        */
    }


    /**
     * <p>Sets the {@code shuffledHexCount}, {@code fixedHexCount},
     * {@code unflippedHexCount}, {@code portCount} values</p>
     */
    @Override
    public void initTileCounts() {

        super.setTileCounts( 21,19,25,9);
        /*
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                //super.setTileCounts(19,0,0,9);
                //break;
            case Board.LARGE_BOARD:
                super.setTileCounts( 21,19,25,9);
                break;
        }

        */



    }


    /**
     * Initializes the {@code resourceCount} array. This value is used to determine
     * the number of occurrences of each {@code biome}. This value is used for fixed and shuffled
     * hex types.
     * <p>Unflipped hex types use an independent count</p>
     */
    @Override
    public void initResourceCounts() {
        super.setResourceCounts(FogIslandBoardBuilder.getResources(getBoardSize()));
    }


    /**
     *  Initializes the {@code portCount} array. This value is used to determine
     *  the number of occurrences of each port {@code biome}
     */
    @Override
    public void initPortCounts() {
        super.setPortCounts(FogIslandBoardBuilder.getPorts(getBoardSize()));
    }


    /**
     * <p>Initializes the {@code TokenCollection} with an array of ordered integer token values.</p>
     */
    @Override
    public void initTokens() {
        super.setTokens(FogIslandBoardBuilder.getTokens(getBoardSize()));
    }


    /**
     * <p>Calculates and returns the hex located at one of the 4 corners in the group of shuffled hexes</p>
     * <p>Values of Right and Left are equivalent to Top Right and Top Left respectively.</p>
     * @param startPosition Use random values between 0 and {@code Hex.SIDES} or constants
     *      {@code HexCollection.TOP_LEFT}, {@code HexCollection.TOP_RIGHT},
     *      {@code HexCollection.RIGHT}, {@code HexCollection.BOTTOM_RIGHT},
     *      {@code HexCollection.BOTTOM_LEFT}, {@code HexCollection.LEFT}
     * @return the starting {@code hex} in the {@code hexSpiral}
     */
    @Override
    public Hex getStartingHex(int startPosition) {
        if(getRandomFlag()){
            return super.getStartingHex(startPosition);
        }
        return switch (getBoardSize()) {
            case SMALL_BOARD -> getStartingHexSmall(startPosition);
            case LARGE_BOARD -> getStartingHexLarge(startPosition);
            default -> null;
        };
    }


    /**
     * <p>Helper function for {@code getStartingHex(int)}, this function is called
     * when the board {@code size} is equal to {@code Board.SMALL_BOARD}</p>
     * @param startPosition Use random values between 0 and {@code Hex.SIDES} or constants
     *      {@code HexCollection.TOP_LEFT}, {@code HexCollection.TOP_RIGHT},
     *      {@code HexCollection.RIGHT}, {@code HexCollection.BOTTOM_RIGHT},
     *      {@code HexCollection.BOTTOM_LEFT}, {@code HexCollection.LEFT}
     * @return the starting {@code hex} in the {@code hexSpiral}
     */
    private Hex getStartingHexSmall(int startPosition) {
        return getStartingHexLarge(startPosition);
    }


    /**
     * <p>Helper function for {@code getStartingHex(int)}, this function is called
     * when the board {@code size} is equal to {@code Board.LARGE_BOARD}</p>
     * @param startPosition Use random values between 0 and {@code Hex.SIDES} or constants
     *      {@code HexCollection.TOP_LEFT}, {@code HexCollection.TOP_RIGHT},
     *      {@code HexCollection.RIGHT}, {@code HexCollection.BOTTOM_RIGHT},
     *      {@code HexCollection.BOTTOM_LEFT}, {@code HexCollection.LEFT}
     * @return the starting {@code hex} in the {@code hexSpiral}
     */
    private Hex getStartingHexLarge(int startPosition) {
        HexCollection hc = getHexCollection();
        int rows = getHexGridDim().height;
        int cols = getHexGridDim().width;
        int o = getColumnOffset();
        return switch (startPosition%Hex.SIDES) {
            case HexCollection.TOP_LEFT -> hc.get(0, 1);
            case HexCollection.TOP_RIGHT -> hc.get(0, 8);
            case HexCollection.RIGHT -> hc.get(0, 8);
            case HexCollection.BOTTOM_RIGHT -> hc.get(2, 7);
            case HexCollection.BOTTOM_LEFT -> hc.get(2, 2);
            case HexCollection.LEFT -> hc.get(0, 1);
            default -> null;
        };
    }


    /**
     * <p>Pool all hex counts into shuffledHexCount.
     * Combine {@code shuffled} and {@code unflipped} resource counts and token array</p>
     */
    @Override
    public void poolHexCounts(){
        super.poolHexCounts();
        combineResourceCount(
                FogIslandBoardBuilder.getUnflippedResources(getBoardSize()));
        TokenCollection tc = new TokenCollection(
                FogIslandBoardBuilder.getUnflippedTokens(getBoardSize()));
        tc.shuffle();
        combineTokenCollections(tc);

    }


    /**
     * <p>Helper function for {@code placeFixedTiles()}, this function is called
     * when the board {@code size} is equal to {@code Board.SMALL_BOARD}.</p>
     */
    @Override
    public void placeFixedTilesSmall(){
        placeFixedTilesLarge();
    }


    /**
     * <p>Helper function for {@code placeFixedTiles()}, this function is called
     * when the board {@code size} is equal to {@code Board.LARGE_BOARD}.</p>
     */
    @Override
    public void placeFixedTilesLarge(){
        int id = getShuffledHexCount();
        placeFixedHex(1,1, id++, Tile.OCEAN);
        placeFixedHex(1,9, id++, Tile.OCEAN);

        placeFixedHex(2,1, id++, Tile.OCEAN);
        placeFixedHex(2,8, id++, Tile.OCEAN);

        placeFixedHex(3,0, id++, Tile.OCEAN);
        placeFixedHex(3,2, id++, Tile.OCEAN);
        placeFixedHex(3,3, id++, Tile.OCEAN);
        placeFixedHex(3,4, id++, Tile.WOOD);
        placeFixedHex(3,5, id++, Tile.OCEAN);
        placeFixedHex(3,6, id++, Tile.SHEEP);
        placeFixedHex(3,7, id++, Tile.OCEAN);
        placeFixedHex(3,8, id++, Tile.OCEAN);
        placeFixedHex(3,10,id++, Tile.OCEAN);

        placeFixedHex(4,3, id++, Tile.OCEAN);
        placeFixedHex(4,4, id++, Tile.OCEAN);
        placeFixedHex(4,5, id++, Tile.OCEAN);
        placeFixedHex(4,6, id++, Tile.OCEAN);

        placeFixedHex(6,1, id++, Tile.GOLD);
        placeFixedHex(6,8, id++, Tile.GOLD);
    }


    /**
     * <p>Helper function for {@code placeUnflippedTiles()}, this function is called
     * when the board {@code size} is equal to {@code Board.SMALL_BOARD}.</p>
     */
    @Override
    public void placeUnflippedTilesSmall() {
        placeUnflippedTilesLarge();
    }


    /**
     * <p>Helper function for {@code placeUnflippedTiles()}, this function is called
     * when the board {@code size} is equal to {@code Board.LARGE_BOARD}.</p>
     */
    @Override
    public void placeUnflippedTilesLarge() {
        int s = getShuffledHexCount() + getFixedHexCount();
        int r=2,c=0,i=0;
        placeUnflippedHex(r,c,s+(i++));
        c=9;
        placeUnflippedHex(r,c,s+(i++));
        r=3;c=1;
        placeUnflippedHex(r,c,s+(i++));
        c=9;
        placeUnflippedHex(r,c,s+(i++));
        r=4;c=0;
        placeUnflippedHex(r,c++,s+(i++));
        placeUnflippedHex(r,c++,s+(i++));
        placeUnflippedHex(r,c,s+(i++));
        c=7;
        placeUnflippedHex(r,c++,s+(i++));
        placeUnflippedHex(r,c++,s+(i++));
        placeUnflippedHex(r,c,s+(i++));
        r=5;
        for(int j=1;j<10;j++){
            placeUnflippedHex(r,j,s+(i++));
        }
        r=6;
        for(int j=2;j<8;j++){
            placeUnflippedHex(r,j,s+(i++));
        }
        System.out.println(s+i);
        shuffleUnflippedHexes();
    }


    /**
     * Shuffles the unflipped hexes' biome and token
     */
    @Override
    public void shuffleUnflippedHexes(){
        TokenCollection tokenCollection = new TokenCollection(
                FogIslandBoardBuilder.getUnflippedTokens(getBoardSize()));
        tokenCollection.shuffle();
        int s = getShuffledHexCount() + getFixedHexCount();
        int u = getUnflippedHexCount();
        shuffleHexes(FogIslandBoardBuilder.getUnflippedResources(getBoardSize()),
                tokenCollection, s, s+u);
    }


    //EMPTY BODY METHODS
    @Override
    public void setHexGridDim(int rows, int cols) {}
    @Override
    public void setTileCounts(int randomHexCount, int fixedHexCount, int unflippedHexCount, int portCount) {}
    @Override
    public void setResourceCounts(int[] resourceCounts) {}
    @Override
    public void setPortCounts(int[] portCounts) {}
    @Override
    public void setTokens(int[] tokenCounts) {}

    //--------------------------------------------------------------------------------------

    public static class FogIslandBoardBuilder{
        //RESOURCES
        private static final int[] resourcesSmall =
                {0,0,0,0,0,0,0,0};
        private static final int[] resourcesLarge =
                {4,4,5,5,5,2,0,15};
        public static int[] getResources(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return resourcesLarge.clone();
            } else {
                return resourcesLarge.clone();
            }
        }
        //TOKENS
        private static final int[] tokensSmall =
                {0,0,0,0,0,0,0, 0,0,0,0,0,0,0};
        private static final int[] tokensLarge =
                {3,11,4,10,5,6,8,11,6,4,3,10,12,3,9,8,2,4,6,10,9,12,5,11,8};
        public static int[] getTokens(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return tokensLarge.clone();
            } else {
                return tokensLarge.clone();
            }
        }
        //PORTS
        private static final int[] portsSmall =
                {0,0,0,0,0,0};
        private static final int[] portsLarge =
                {1,1,1,1,1,4};
        public static int[] getPorts(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return portsLarge.clone();//portsSmall;
            } else {
                return portsLarge.clone();
            }
        }

        //UNFLIPPED RESOURCES
        private static final int[] unflippedResourcesSmall =
                {0,0,0,0,0, 0,0,0};
        private static final int[] unflippedResourcesLarge =
                {3,3,2,2,2, 1,0,12};
        public static int[] getUnflippedResources(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return unflippedResourcesLarge.clone();//unflippedResourcesSmall;
            } else {
                return unflippedResourcesLarge.clone();
            }
        }
        //UNFLIPPED TOKENS
        private static final int[] unflippedTokensSmall =
                {0,0,0,0,0, 0,0,0,0,0};
        private static final int[] unflippedTokensLarge =
                {2,1,4,2,1, 1,2,1,1,1};
        //       2,3,4,5,6,8,9,10,11,12
        public static int[] getUnflippedTokens(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return unflippedTokensLarge.clone();//unflippedTokensSmall;
            } else {
                return unflippedTokensLarge.clone();
            }
        }

    }
}
