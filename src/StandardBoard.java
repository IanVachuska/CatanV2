import java.util.Random;

public class StandardBoard extends Board {
    //CONSTRUCTORS
    public StandardBoard(int boardSize) {
        super(boardSize);
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
        super.setResourceCounts(BoardBuilder.getStandardResources(getBoardSize()));
    }
    /* Sets the amount of each Biome for the Port tiles */
    public void initPortCounts(){
        super.setPortCounts(BoardBuilder.getStandardPorts(getBoardSize()));
    }
    /* Sets the token array with each number in order */
    public void initTokens(){
        super.setTokens(BoardBuilder.getStandardTokens(getBoardSize()));
    }
    /* Wrapper to randomize start position and orientation */
    public void initHexSpiral() {
        Random rand = new Random();
        int[] orientation = {-1,1};
        initHexSpiral(rand.nextInt(Hex.SIDES),
                orientation[rand.nextInt(2)]);
    }
    /* Initializes the hex spiral in HexCollection based of the starting hex and orientation */
    public void initHexSpiral(int startPosition, int orientation){
        int index = 0;//Perhaps use a proxy?
        IIterator i = getHexCollection().getGridIterator();
        Hex hex;
        boolean[][] valid = new boolean[getHexGridDim().height][getHexGridDim().width];

        //init valid matrix
        while(i.hasNext()){
            hex = i.getNext();
            if(hex != null) {
                valid[hex.getRow()][hex.getColumn()] = true;
            }
        }
        HexCollection hc = getHexCollection();
        hex = getStartingHex(startPosition%Hex.SIDES);
        int direction = Math.floorMod(startPosition+(2*orientation),Hex.SIDES);
        Hex prev = hex;
        while(index < getShuffledHexCount()){
            while (hex != null && valid[hex.getRow()][hex.getColumn()]){
                hc.add(hex, index);
                valid[hex.getRow()][hex.getColumn()] = false;
                prev = hex;
                hex = hc.get(hex, direction);
                index++;
            }
            hex = prev;
            direction = Math.floorMod(direction+orientation, Hex.SIDES);
            hex = hc.get(hex, direction);
        }
    }

    /* Helper for initHexSpiral(int, int) */
    private Hex getStartingHex(int startPosition){
        HexCollection hc = getHexCollection();
        int rows = getHexGridDim().height;
        int cols = getHexGridDim().width;
        //int offset = (cols/2)%2;//0 for small board, 1 for large
        int offset = getColumnOffset();//verify this works
        return switch (startPosition) {
            case HexCollection.TOP_LEFT -> hc.get(0, 1);
            case HexCollection.TOP_RIGHT -> hc.get(0, 3);
            case HexCollection.RIGHT -> hc.get(rows/2, cols - 1);
            case HexCollection.BOTTOM_RIGHT -> hc.get(rows-1, 3);
            case HexCollection.BOTTOM_LEFT -> hc.get(rows-1, 1);
            case HexCollection.LEFT -> hc.get(rows/2, 0);
            default -> null;
        };
    }

    /* Test initHexSpiral at every start/orientation */
    public void testHexSpiral() {
        System.out.println("--------------------");
        for (int strp = 0; strp < Hex.SIDES; strp++) {
            initHexSpiral(strp, CLOCKWISE);
            System.out.println(getHexGridString(new BiomeCommand()));
            System.out.println("--------------------");
        }
        System.out.println("--------------------");
        System.out.println("--------------------");
        for (int strp = 0; strp < Hex.SIDES; strp++) {
            initHexSpiral(strp, COUNTER_CLOCKWISE);
            System.out.println(getHexGridString(new BiomeCommand()));
            System.out.println("--------------------");
        }
    }


    //EMPTY BODY METHODS
    @Override
    public void setHexGridDim(int rows, int cols) {}
    @Override
    public void setTileCounts(int randomHexCount, int fixedHexCount, int unflippedHexCount, int portCount) {}
    @Override
    public void placeFixedTiles() {}
}
