
public class HexCollection {
    //STATIC FIELDS
    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM_RIGHT = 3;
    public static final int BOTTOM_LEFT = 4;
    public static final int LEFT = 5;

    //FIELDS
    private final int columns;
    private final int offset;
    private int gridSize;

    private final Hex[][] grid;

    private int spiralSize;
    private final Hex[] spiral;

    //CONSTRUCTOR
    public HexCollection(Board board) {
        offset = board.getColumnOffset();
        columns = board.getHexGridDim().width;
        spiralSize = 0;
        gridSize = 0;
        //collectionSize = board.getShuffledHexCount() + board.getFixedHexCount() + board.getUnflippedHexCount();
        grid = new Hex[board.getHexGridDim().height][columns];
        spiral = new Hex[board.getShuffledHexCount()];
    }
    //ADD
    public void add(Hex hex){
        grid[hex.getRow()][hex.getColumn()] = hex;
        gridSize++;
    }
    public void add(Hex hex, int spiralIndex){
        spiral[spiralIndex] = hex;
        spiralSize++;
    }

    //UNIVERSAL GETTERS
    public Hex get(int spiralIndex){
        return spiral[spiralIndex];
    }
    public Hex get(int row, int col){
        return grid[row][col];
    }
    //PROXIMITY ACCESSORS
    public Hex get(Hex hex, int direction){
        return switch (Math.floorMod(direction, Hex.SIDES)) {
            case TOP_LEFT ->
                    getTopLeft(hex);
            case TOP_RIGHT ->
                    getTopRight(hex);
            case RIGHT ->
                    getRight(hex);
            case BOTTOM_RIGHT ->
                    getBottomRight(hex);
            case BOTTOM_LEFT ->
                    getBottomLeft(hex);
            case LEFT ->
                    getLeft(hex);
            default -> null;
        };
    }
    public Hex getLeft(Hex hex){
        int row = hex.getRow();
        int col = hex.getColumn() - 1;
        if(col >= 0){
            return grid[row][col];
        }
        return null;
    }
    public Hex getRight(Hex hex){
        int row = hex.getRow();
        int col = hex.getColumn() + 1;
        if(col <= grid[0].length-2){
            return grid[row][col];
        }
        return null;
    }
    public Hex getTopLeft(Hex hex){
        int offset = applyLeftOffset(hex.getRow());
        int row = hex.getRow() - 1;
        int col = hex.getColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + ")");
        if(row >= 0 && col >= 0){
            return grid[row][col];
        }
        return null;
    }
    public Hex getTopRight(Hex hex){
        int offset = applyRightOffset(hex.getRow());
        int row = hex.getRow() - 1;
        int col = hex.getColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + ")");
        if(row >= 0 && col <= grid[0].length-1){
            return grid[row][col];
        }
        return null;
    }
    public Hex getBottomLeft(Hex hex){
        int offset = applyLeftOffset(hex.getRow());
        int row = hex.getRow() + 1;
        int col = hex.getColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + ")");
        if(row <= grid.length-1 && col >= 0){
            return grid[row][col];
        }
        return null;
    }
    public Hex getBottomRight(Hex hex){
        int offset = applyRightOffset(hex.getRow());
        int row = hex.getRow() + 1;
        int col = hex.getColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + ")");
        if(row <= grid.length-1 && col <= grid[0].length-1){
            return grid[row][col];
        }
        return null;
    }

    public IIterator getGridIterator(){
        return new HexGridIterator();
    }
    public IIterator getSpiralIterator(){
        return new HexGridIterator();
    }

    /* Helper functions for the above Top/Bottom getters
     * Used to apply grid offsets in every other column*/
    private int applyLeftOffset(int row){
        return (row%2 != offset)?0:1;
    }
    private int applyRightOffset(int row){
        return (~applyLeftOffset(row))%2;
    }

    //ITERATORS---------------------------------------------//
    private class HexGridIterator implements IIterator{
        private int index;
        private int nullOffset;

        public HexGridIterator(){
            this.index = 0;
            this.nullOffset = 0;
        }
        @Override
        public boolean hasNext() {
            return index < gridSize;
        }
        @Override
        public Hex getNext() {
            Hex hex;
            while((hex = grid[(index+nullOffset) / columns][(index+nullOffset) % columns]) == null
                    && hasNext()) {
                nullOffset++;
            }
            index++;
            return hex;
        }
    }
    private class HexSpiralIterator implements IIterator{
        private int index;

        public HexSpiralIterator(){
            this.index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < spiralSize;
        }
        @Override
        public Hex getNext() {
            return spiral[index++];
        }
    }
}
