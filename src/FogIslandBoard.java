public class FogIslandBoard extends Board {
    public FogIslandBoard(int boardSize, int flags) {
        super(boardSize, flags);
    }
    @Override
    public void handleFlags(){
        super.handleFlags();
        if(getRandomFlag()){
            addToResourceCount(
                    FogIslandBoardBuilder.getUnflippedResources(getBoardSize()));
            TokenCollection tc = new TokenCollection(
                    FogIslandBoardBuilder.getUnflippedTokens(getBoardSize()));
            tc.shuffle();
            addToTokenCollection(tc);
        }
    }
    @Override
    public void initHexGridDim() {
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                super.setHexGridDim(5,5);
                break;
            case Board.LARGE_BOARD:
                super.setHexGridDim(7,11);
                break;
        }
    }
    @Override
    public void initTileCounts() {
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                super.setTileCounts(19,0,0,9);
                break;
            case Board.LARGE_BOARD:
                super.setTileCounts( 21,19,25,9);
                break;
        }
    }
    @Override
    public void initResourceCounts() {
        super.setResourceCounts(FogIslandBoardBuilder.getResources(getBoardSize()));
    }
    @Override
    public void initPortCounts() {
        super.setPortCounts(FogIslandBoardBuilder.getPorts(getBoardSize()));
    }
    @Override
    public void initTokens() {
        super.setTokens(FogIslandBoardBuilder.getTokens(getBoardSize()));
    }
    @Override
    public void placeFixedTilesSmall(){

    }
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
    @Override
    public void placeUnflippedTilesSmall() {}
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
        TokenCollection tokenCollection = new TokenCollection(
                FogIslandBoardBuilder.getUnflippedTokens(getBoardSize()));
        tokenCollection.shuffle();
        shuffleHexes(FogIslandBoardBuilder.getUnflippedResources(getBoardSize()),
                tokenCollection, s, s+i);
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

    public static class FogIslandBoardBuilder{
        //RESOURCES
        private static final int[] resourcesSmall =
                {0,0,0,0,0,0,0,0};
        private static final int[] resourcesLarge =
                {4,4,5,5,5,2,0,15};
        public static int[] getResources(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return resourcesSmall;
            } else {
                return resourcesLarge;
            }
        }
        //TOKENS
        private static final int[] tokensSmall =
                {0,0,0,0,0,0,0, 0,0,0,0,0,0,0};
        private static final int[] tokensLarge =
                {3,11,4,10,5,6,8,11,6,4,3,10,12,3,9,8,2,4,6,10,9,12,5,11,8};
        public static int[] getTokens(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return tokensSmall;
            } else {
                return tokensLarge;
            }
        }
        //PORTS
        private static final int[] portsSmall =
                {0,0,0,0,0,0};
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
                {3,3,2,2,2, 1,0,12};
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
                {2,1,4,2,1, 1,2,1,1,1};
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
