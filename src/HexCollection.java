
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
        grid = new Hex[board.getHexGridDim().height][columns];
        int totalHexCount = board.getShuffledHexCount()
                + board.getFixedHexCount()
                + board.getUnflippedHexCount();
        spiral = new Hex[totalHexCount];
    }
    //ADD
    public void add(Hex hex){
        grid[hex.getGridRow()][hex.getGridColumn()] = hex;
        gridSize++;
    }
    public void addToSpiral(Hex hex, int spiralIndex){
        spiral[spiralIndex] = hex;
        spiralSize = Math.max(spiralSize, spiralIndex);
    }

    //UNIVERSAL GETTERS
    public Hex get(int spiralIndex){
        return spiral[spiralIndex];
    }
    public Hex get(int row, int col){
        return grid[row][col];
    }
    public Hex get(Tile tile){
        return grid[tile.getGridRow()][tile.getGridColumn()];
    }
    //PROXIMITY ACCESSORS
    public Hex get(Hex hex, int direction){
        Hex hexr = null;
        switch (Math.floorMod(direction, Hex.SIDES)) {
            case TOP_LEFT:
                //System.out.println("TOP_LEFT");
                hexr = getTopLeft(hex);
                break;
            case TOP_RIGHT:
                //System.out.println("TOP_RIGHT");
                hexr = getTopRight(hex);
                break;
            case RIGHT:
                //System.out.println("RIGHT");
                hexr = getRight(hex);
                break;
            case BOTTOM_RIGHT:
                //System.out.println("BOTTOM_RIGHT");
                hexr = getBottomRight(hex);
                break;
            case BOTTOM_LEFT:
                // System.out.println("BOTTOM_LEFT");
                hexr = getBottomLeft(hex);
                break;
            case LEFT:
                //System.out.println("LEFT");
                hexr = getLeft(hex);
                break;
        }
        return hexr;
    }
    public Hex getLeft(Hex hex){
        int row = hex.getGridRow();
        int col = hex.getGridColumn() - 1;
        if(col >= 0){
            return grid[row][col];
        }
        return null;
    }
    public Hex getRight(Hex hex){
        int row = hex.getGridRow();
        int col = hex.getGridColumn() + 1;
        if(col <= grid[0].length-1){
            return grid[row][col];
        }
        return null;
    }
    public Hex getTopLeft(Hex hex){
        int offset = applyLeftOffset(hex.getGridRow());
        int row = hex.getGridRow() - 1;
        int col = hex.getGridColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + ")");
        if(row >= 0 && col >= 0){
            return grid[row][col];
        }
        return null;
    }
    public Hex getTopRight(Hex hex){
        int offset = applyRightOffset(hex.getGridRow());
        int row = hex.getGridRow() - 1;
        int col = hex.getGridColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + "), " + (grid[0].length-1));
        if(row >= 0 && col <= grid[0].length-1){
            return grid[row][col];
        }
        return null;
    }
    public Hex getBottomLeft(Hex hex){
        int offset = applyLeftOffset(hex.getGridRow());
        int row = hex.getGridRow() + 1;
        int col = hex.getGridColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + ")");
        if(row <= grid.length-1 && col >= 0){
            return grid[row][col];
        }
        return null;
    }
    public Hex getBottomRight(Hex hex){
        int offset = applyRightOffset(hex.getGridRow());
        int row = hex.getGridRow() + 1;
        int col = hex.getGridColumn() - offset;

        //System.out.println(offset);
        //System.out.println("(" + (row) + "," + (col) + ")");
        if(row <= grid.length-1 && col <= grid[0].length-1){
            return grid[row][col];
        }
        return null;
    }

    public IIterator<Hex> getGridIterator(){
        return new HexGridIterator();
    }
    public IIterator<Hex> getSpiralIterator(){
        return new HexSpiralIterator();
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
    private class HexGridIterator implements IIterator<Hex>{
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

        @Override
        public void reset() {
            index = 0;
            nullOffset = 0;
        }
        @Override
        public void setHead() {}
    }
    private class HexSpiralIterator implements IIterator<Hex>{
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
        @Override
        public void reset() {
            index = 0;
        }
        @Override
        public void setHead() {}
    }
}
