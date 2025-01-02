
public class SeafarersBoard extends Board{
    public SeafarersBoard(int boardSize, int flags) {
        super(boardSize, flags);
    }
    @Override
    public void initHexGridDim() {
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                super.setHexGridDim(7,7);
                break;
            case Board.LARGE_BOARD:
                super.setHexGridDim(7,10);
                break;
        }
    }
    @Override
    public void initTileCounts() {
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                super.setTileCounts(14,23,0,8);
                break;
            case Board.LARGE_BOARD:
                super.setTileCounts( 30,28,0,11);
                break;
        }
    }
    /* Sets the amount of each Biome for the Hex tiles */
    @Override
    public void initResourceCounts(){
        super.setResourceCounts(SeafarersBoardBuilder.getResources(getBoardSize()));
    }
    /* Sets the amount of each Biome for the Port tiles */
    @Override
    public void initPortCounts(){
        super.setPortCounts(SeafarersBoardBuilder.getPorts(getBoardSize()));
    }
    /* Sets the token array with each number in order */
    @Override
    public void initTokens(){
        super.setTokens(SeafarersBoardBuilder.getTokens(getBoardSize()));
    }
    @Override
    public Hex getStartingHex(int startPosition) {
        if(getRandomFlag()){
            return super.getStartingHex(startPosition);
        }
        return switch (getBoardSize()) {
            case Board.SMALL_BOARD -> getStartingHexSmall(startPosition);
            case Board.LARGE_BOARD -> getStartingHexLarge(startPosition);
            default -> null;
        };
    }
    private Hex getStartingHexSmall(int startPosition) {
        HexCollection hc = getHexCollection();
        int rows = getHexGridDim().height;
        int cols = getHexGridDim().width;
        return switch (startPosition) {
            case HexCollection.TOP_LEFT -> hc.get(1, 1);
            case HexCollection.TOP_RIGHT -> hc.get(1, 3);
            case HexCollection.RIGHT -> hc.get(2, 3);
            case HexCollection.BOTTOM_RIGHT -> hc.get(4, 2);
            case HexCollection.BOTTOM_LEFT -> hc.get(4, 0);
            case HexCollection.LEFT -> hc.get(3, 0);
            default -> null;
        };
    }
    private Hex getStartingHexLarge(int startPosition) {
        HexCollection hc = getHexCollection();
        int rows = getHexGridDim().height;
        int cols = getHexGridDim().width;
        int o = getColumnOffset();
        int border = 2;
        return switch (startPosition) {
            case HexCollection.TOP_LEFT -> hc.get(0, 1+border);
            case HexCollection.TOP_RIGHT -> hc.get(0, cols - (2+border+o));
            case HexCollection.RIGHT -> hc.get(rows/2, cols - (1+border));
            case HexCollection.BOTTOM_RIGHT -> hc.get(rows-1, cols - (2+border+o));
            case HexCollection.BOTTOM_LEFT -> hc.get(rows-1, 1+border);
            case HexCollection.LEFT -> hc.get(rows/2, border);
            default -> null;
        };
    }
    @Override
    public void placeFixedTilesSmall(){
        int r = 0, c = 1, id = getShuffledHexCount();
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.BRICK);

        r = 1; c = 4;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.GOLD);

        r = 2; c = 4;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.OCEAN);

        r = 3; c = 4;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c++, id++, Tile.SHEEP);
        placeFixedHex(r, c  , id++, Tile.OCEAN);

        r = 4; c = 3;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c++, id++, Tile.HAY);
        placeFixedHex(r, c  , id++, Tile.ROCK);

        r = 5; c = 1;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c++, id++, Tile.GOLD);
        placeFixedHex(r, c  , id++, Tile.OCEAN);

        r = 6; c = 1;
        placeFixedHex(r, c++, id++, Tile.ROCK);
        placeFixedHex(r, c++, id++, Tile.BRICK);
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
    }
    @Override
    public void placeFixedTilesLarge(){
        int r = 0, c = 1, id = getShuffledHexCount();
        placeFixedHex(r, c++, id++, Tile.GOLD);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
        c = 6;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.ROCK);

        r = 1; c = 1;
        placeFixedHex(r, c++, id++, Tile.ROCK);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
        c = 7;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.OCEAN);

        r = 2; c = 0;
        placeFixedHex(r, c++, id++, Tile.SHEEP);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
        c = 7;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.BRICK);

        r = 3; c = 0;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
        c = 8;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.OCEAN);

        r = 4; c = 0;
        placeFixedHex(r, c++, id++, Tile.BRICK);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
        c = 7;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.HAY);

        r = 5; c = 1;
        placeFixedHex(r, c++, id++, Tile.WOOD);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
        c = 7;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.OCEAN);

        r = 6; c = 1;
        placeFixedHex(r, c++, id++, Tile.GOLD);
        placeFixedHex(r, c  , id++, Tile.OCEAN);
        c = 6;
        placeFixedHex(r, c++, id++, Tile.OCEAN);
        placeFixedHex(r, c  , id++, Tile.GOLD);
    }

    //EMPTY BODY METHODS
    @Override
    public void setHexGridDim(int rows, int cols) {}
    @Override
    public void setTileCounts(int randomHexCount, int fixedHexCount, int unflippedHexCount, int portCount) {}
    @Override
    public void placeUnflippedTilesSmall() {}
    @Override
    public void placeUnflippedTilesLarge() {}

    public static class SeafarersBoardBuilder{
        //RESOURCES
        private static final int[] resourcesSmall =
                {3,5,4,4,4, 2,0,15};
        private static final int[] resourcesLarge =
                {7,7,7,7,7, 3,2,18};
        public static int[] getResources(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return resourcesSmall;
            } else {
                return resourcesLarge;
            }
        }

        //TOKENS
        private static final int[] tokensSmall =
                {10,8,4,9,3,4,5,12,8,2,4,6,10,8,11,5,6,11,10,5,9,3};
        private static final int[] tokensLarge =
                {9,6,11,8,12,4,3,2,5,10,2,5,4,6,3,9,8,11,11,10,6,
                        3,8,4,8,10,11,12,10,5,4,9,5,9,12,3,2,6};
        public static int[] getTokens(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return tokensSmall;
            } else {
                return tokensLarge;
            }
        }

        //PORTS
        private static final int[] portsSmall =
                {1,1,1,1,1,3};
        private static final int[] portsLarge =
                {1,1,1,1,1,4};
        public static int[] getPorts(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return portsSmall;
            } else {
                return portsLarge;
            }
        }

        //UNFLIPPED RESOURCES
        private static final int[] unflippedResourcesSmall =
                {0,0,0,0,0, 0,0,0};
        private static final int[] unflippedResourcesLarge =
                {0,0,0,0,0, 0,0,0};
        public static int[] getUnflippedResources(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return unflippedResourcesSmall;
            } else {
                return unflippedResourcesLarge;
            }
        }
        //UNFLIPPED TOKENS
        private static final int[] unflippedTokensSmall =
                {0,0,0,0,0, 0,0,0,0,0};
        private static final int[] unflippedTokensLarge =
                {0,0,0,0,0, 0,0,0,0,0};
        //       2,3,4,5,6,8,9,10,11,12
        public static int[] getUnflippedTokens(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return unflippedTokensSmall;
            } else {
                return unflippedTokensLarge;
            }
        }
    }
}
