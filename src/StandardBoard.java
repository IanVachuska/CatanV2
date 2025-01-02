
public class StandardBoard extends Board {
    //CONSTRUCTORS
    public StandardBoard(int boardSize, int flags) {
        super(boardSize, flags);
    }
    //INITIALIZERS
    /* Initializes the rows and columns of the hexGrid */
    public void initHexGridDim() {
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                super.setHexGridDim(5,5);
                break;
            case Board.LARGE_BOARD:
                super.setHexGridDim(7,6);
                break;
        }
    }
    /* Sets the total amounts of Hex and Port tiles in the game */
    public void initTileCounts() {
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                super.setTileCounts(19,0,0,9);
                break;
            case Board.LARGE_BOARD:
                super.setTileCounts( 30,0,0,11);
                break;
        }
    }
    /* Sets the amount of each Biome for the Hex tiles */
    public void initResourceCounts(){
        super.setResourceCounts(StandardBoardBuilder.getResources(getBoardSize()));
    }
    /* Sets the amount of each Biome for the Port tiles */
    public void initPortCounts(){
        super.setPortCounts(StandardBoardBuilder.getPorts(getBoardSize()));
    }
    /* Sets the token array with each number in order */
    public void initTokens(){
        super.setTokens(StandardBoardBuilder.getTokens(getBoardSize()));
    }


    //EMPTY BODY METHODS
    @Override
    public void placeFixedTilesSmall() {}
    @Override
    public void placeFixedTilesLarge() {}
    @Override
    public void placeUnflippedTilesSmall() {}
    @Override
    public void placeUnflippedTilesLarge() {}

    @Override
    public void setHexGridDim(int rows, int cols) {}
    @Override
    public void setTileCounts(int randomHexCount, int fixedHexCount, int unflippedHexCount, int portCount) {}

    //--------------------------------------------------------------------------------------

    public static class StandardBoardBuilder {
        //RESOURCES
        private static final int[] resourcesSmall =
                {4,4,4,3,3, 0,1,0};
        private static final int[] resourcesLarge =
                {6,6,6,5,5, 0,2,0};
        public static int[] getResources(int boardSize) {
            if (boardSize == SMALL_BOARD) {
                return resourcesSmall;
            } else {
                return resourcesLarge;
            }
        }

        //TOKENS
        private static final int[] tokensSmall =
                {5,2,6,3,8,10,9,12,11,4,8,10,9,4,5,6,3,11};
        private static final int[] tokensLarge =
                {2,5,4,6,3,9,8,11,11,10,6,3,8,4,8,10,11,12,10,5,4,9,5,9,12,3,2,6};
        public static int[] getTokens(int boardSize) {
            if (boardSize == SMALL_BOARD) {
                return tokensSmall;
            } else {
                return tokensLarge;
            }
        }

        //PORTS
        private static final int[] portsSmall =
                {1,1,1,1,1,4};
        private static final int[] portsLarge =
                {1,2,1,1,1,5};
        public static int[] getPorts(int boardSize) {
            if (boardSize == SMALL_BOARD) {
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
