package com.mycatan;

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

    //private int spiralSize;
    private final Hex[] spiral;

    //CONSTRUCTORS
    public HexCollection(Board board) {
        offset = board.getColumnOffset();
        columns = board.getHexGridDim().width;
        //spiralSize = 0;
        gridSize = 0;
        grid = new Hex[board.getHexGridDim().height][columns];
        int totalHexCount = board.getShuffledHexCount()
                + board.getFixedHexCount()
                + board.getUnflippedHexCount();
        spiral = new Hex[totalHexCount];
    }

    //ADD

    /**
     * <p>Add the {@code hex} to the {@code hexGrid} collection.</p>
     * <p>The row and column location that the {@code hex} gets inserted
     * at is determined by the the {@code hex}'s {@code gridLocation}.</p>
     * @param hex the object that gets added
     */
    public void addToGrid(Hex hex){
        grid[hex.getGridRow()][hex.getGridColumn()] = hex;
        gridSize++;
    }


    /**
     * <p>Add the {@code hex} to the {@code hexSpiral} collection.</p>
     * <p>The location that the {@code hex} gets inserted into the array
     * is determined by the {@code hex}'s {@code id} field.</p>
     * <p>You must set the {@code id} field of the {@code hex}
     * you are inserting before calling this method</p>
     * @param hex the object that gets added
     */
    public void addToSpiral(Hex hex){
        int id = hex.getId();
        spiral[id] = hex;
    }


    /**
     * @param row the {@code row} of the {@code hex} you want to access
     * @param col the {@code column} of the {@code hex} you want to access
     * @return the {@code hex} you want to access
     */
    public Hex get(int row, int col){
        return grid[row][col];
    }


    /**
     * @param id the {@code id} of the {@code hex} you want to access
     * @return the {@code hex} you want to access
     */
    //UNIVERSAL GETTERS
    public Hex get(int id){
        return spiral[id];
    }


    /**
     * <p>This function is intended to be used to access a {@code port}'s parent {@code hex}</p>
     * @param tile the {@code column} of the {@code hex} you want to access
     * @return the {@code hex} with the same {@code gridLocation}
     */
    public Hex get(Tile tile){
        return grid[tile.getGridRow()][tile.getGridColumn()];
    }


    //PROXIMITY ACCESSORS

    /**
     *
     * @param hex the object whose neighbor you want to access
     * @param direction the six sided direction you want to access.
     *                 <p>Use constants {@code TOP_LEFT}, {@code TOP_RIGHT}, {@code RIGHT}
     *                  {@code BOTTOM_RIGHT},{@code BOTTOM_LEFT},{@code LEFT},</p>
     *                 <p>or any integer between 0 and {@code Hex.SIDES}</p>
     * @return the {@code hex} object in that direction if it exists, else returns null
     */
    public Hex getNeighbor(Hex hex, int direction){
        return switch (Math.floorMod(direction, Hex.SIDES)) {
            case TOP_LEFT -> getTopLeft(hex);
            case TOP_RIGHT -> getTopRight(hex);
            case RIGHT -> getRight(hex);
            case BOTTOM_RIGHT -> getBottomRight(hex);
            case BOTTOM_LEFT -> getBottomLeft(hex);
            case LEFT -> getLeft(hex);
            default -> null;
        };
    }


    /**
     * @param hex the object whose neighbor you want to access
     * @return the {@code hex} object in the left direction if it exists, else returns null
     */
    public Hex getLeft(Hex hex){
        int row = hex.getGridRow();
        int col = hex.getGridColumn() - 1;
        if(col >= 0){
            return grid[row][col];
        }
        return null;
    }


    /**
     * @param hex the object whose neighbor you want to access
     * @return the {@code hex} object in the right direction if it exists, else returns null
     */
    public Hex getRight(Hex hex){
        int row = hex.getGridRow();
        int col = hex.getGridColumn() + 1;
        if(col <= grid[0].length-1){
            return grid[row][col];
        }
        return null;
    }


    /**
     * @param hex the object whose neighbor you want to access
     * @return the {@code hex} object in the top left direction if it exists, else returns null
     */
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


    /**
     * @param hex the object whose neighbor you want to access
     * @return the {@code hex} object in the top right direction if it exists, else returns null
     */
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


    /**
     * @param hex the object whose neighbor you want to access
     * @return the {@code hex} object in the bottom left direction if it exists, else returns null
     */
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


    /**
     * @param hex the object whose neighbor you want to access
     * @return the {@code hex} object in the bottom right direction if it exists, else returns null
     */
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


    //ITERATOR ACCESSORS

    /**
     * @return an {@code IIterator} object for the {@code hexGrid} collection
     */
    public IIterator<Hex> getGridIterator(){
        return new HexGridIterator();
    }


    /**
     * @return an {@code IIterator} object for the {@code hexSpiral} collection
     */
    public IIterator<Hex> getSpiralIterator(){
        return new HexSpiralIterator();
    }

    /**
     * <p>Helper functions for the above Top/Bottom getters.
     * Used to apply left grid offsets in every other column</p>
     * @param row the row of the {@code hex} to be inserted
     * @return and integer offset value, 0 or 1
     */
    private int applyLeftOffset(int row){
        return (row%2 != offset)?0:1;
    }


    /**
     * <p>Helper functions for the above Top/Bottom getters.
     * Used to apply right grid offsets in every other column</p>
     * @param row the row of the {@code hex} to be inserted
     * @return and integer offset value, 0 or 1
     */
    private int applyRightOffset(int row){
        return (~applyLeftOffset(row))%2;
    }


//------------------------------ITERATOR CLASSES------------------------------//

    /**
     * <p>{@code IIterator} class for the {@code hexGrid} collection.</p>
     */
    private class HexGridIterator implements IIterator<Hex>{
        //FIELDS
        private int index;
        private int nullOffset;

        //CONSTRUCTORS
        public HexGridIterator(){
            this.index = 0;
            this.nullOffset = 0;
        }

        //METHODS
        /**
         * @return {@code true} if there is another {@code hex} in the collection, else {@code false}
         */
        @Override
        public boolean hasNext() {
            return index < gridSize;
        }


        /**
         * @return the next {@code hex} in the {@code grid} collection if it exists, else null
         */
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


        /**
         * <p>Resets the iterator to its initial position.</p>
         */
        @Override
        public void reset() {
            index = 0;
            nullOffset = 0;
        }


        //EMPTY BODY METHODS
        @Override
        public void setHead() {}
    }

    /**
     * <p>{@code IIterator} class for the {@code hexSpiral} collection.</p>
     */
    private class HexSpiralIterator implements IIterator<Hex>{
        //FIELDS
        private int index;

        //CONSTRUCTORS
        public HexSpiralIterator(){
            this.index = 0;
        }

        //METHODS
        /**
         * @return {@code true} if there is another {@code hex} in the collection, else {@code false}
         */
        @Override
        public boolean hasNext() {
            return index < spiral.length;
        }


        /**
         * @return the next {@code hex} in the {@code spiral} collection if it exists, else null
         */
        @Override
        public Hex getNext() {
            if(hasNext()) {
                return spiral[index++];
            }
            else{
                System.err.println("End of spiral collection");
                return null;
            }
        }


        /**
         * <p>Resets the iterator to its initial position.</p>
         */
        @Override
        public void reset() {
            index = 0;
        }


        //EMPTY BODY METHODS
        @Override
        public void setHead() {}
    }
}
