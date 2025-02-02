import java.awt.*;
import java.util.Random;

public abstract class Board {
    //STATIC FIELDS
    //Flags
    public static final int DEBUG = 1;
    public static final int RANDOM = 2;

    //Encodings
    public static final int SMALL_BOARD = 0;
    public static final int LARGE_BOARD = 1;

    public static final int CLOCKWISE = 1;
    public static final int COUNTER_CLOCKWISE = -1;

    //FIELDS
    private final Dimension hexGridDim; //HexGrid Rows/Cols
    private final int boardSize;
    private int flags;// 0010->random | 0001->debug | ...
    private int shuffledHexCount, fixedHexCount, unflippedHexCount, portCount;
    //How many of each resource/port in the game
    private int[] resourceCounts;
    private int[] portCounts;
    private TokenCollection tc;

    private HexCollection hc;//interface to access both grid and spiral representations
    private PortCollection pc;

    private int startPosition = 0;
    private int orientation = 1;

    //CONSTRUCTORS

    public Board(int boardSize, int flags) {
        this.boardSize = boardSize;
        this.hexGridDim = new Dimension();
        setFlags(flags);
        initHexGridDim();
        initTileCounts();
        initResourceCounts();
        initPortCounts();
        initTokens();

        initBoard();
    }

    //INITIALIZERS
    /**
     * <p>Initializes the hex grid and spiral, places fixed/unflipped tiles, shuffles hexes and ports.</p>
     */
    private void initBoard(){
        initHexGrid();
        if(getRandomFlag()) {
            poolHexCounts();
        }

        placeFixedTiles();
        placeUnflippedTiles();

        initHexSpiral(true);
        shuffleHexes();

        findAllPorts();
        findValidPorts();
        shufflePorts();
    }


    /**
     *  <p>Initialize the {@code HexCollection} object and its aggregates.</p>
     *  <p>Note: Number of rows in grid must be set to an odd number
     *  or this method will fail.</p>
     */


    private void initHexGrid() {
        hc = new HexCollection(this);
        int medianRow = hexGridDim.height/2;
        boolean debug = getDebugFlag();
        //TODO: Error Handle boards with even number of rows.
        //Initialize the middle row of hexes first
        for (int c = 0; c < hexGridDim.width; c++) {
            hc.add(new Hex(medianRow, c, debug));
        }

        /*
        Initialize hexes symmetrically around the median row
        each row loses one hex for each step away from the median
        and alternates from first to last
        */
        int startOffset = 0;
        int endOffset = 0;
        for(int r = 1; r <= medianRow; r++) {
            if((medianRow-r)%2 == getColumnOffset()){
                startOffset++;
            }
            else{
                endOffset++;
            }
            for (int c = startOffset; c < hexGridDim.width-endOffset; c++) {
                hc.add(new Hex(medianRow - r, c, debug));
                hc.add(new Hex(medianRow + r, c, debug));
            }
        }
    }
    /**
     * <p>Wrapper function for initHexSpiral(int startPosition, int orientation).</p>
     * @param randomStart <p>True -> Initializes hexSpiral(int, int) randomly.</p>
     *                    <p>False -> Initializes hexSpiral(int, int) with
     *                    default value (0,1) or previous value.</p>
     */


    public void initHexSpiral(boolean randomStart) {
        if(randomStart) {
            Random rand = new Random();
            int[] orientation = {-1, 1};
            initHexSpiral(rand.nextInt(Hex.SIDES),
                    orientation[rand.nextInt(2)]);
        }
        else{
            initHexSpiral(startPosition, orientation);
        }
    }
    /**
     * <p>Initializes the hex spiral in HexCollection based of the starting hex and orientation.</p>
     * @param startPosition <p>0->Top Left</p>
     *                      <p>1->Top Right</p>
     *                      <p>2->Right</p>
     *                      <p>3->Bottom Right</p>
     *                      <p>4->Bottom Left</p>
     *                      <p>5->Left</p>
     * @param orientation <p>1->ClockWise</p>
     *                    <p>-1->CounterClockWise</p>
     */


    public void initHexSpiral(int startPosition, int orientation){
        this.startPosition = startPosition;
        this.orientation = orientation;

        /*
        sets head of token collection built in iterator to its current value.
        subsequent calls to tc.reset() will return iterator to head.
        */
        tc.setHead();

        IIterator<Hex> i = hc.getGridIterator();
        Hex hex;

        //Init valid matrix, a cell is valid if a hex at the same row and column is of type "Shuffled"
        boolean[][] valid = new boolean[getHexGridDim().height][getHexGridDim().width];
        while(i.hasNext()){
            hex = i.getNext();
            if(hex != null && hex.getType() == Tile.SHUFFLED) {
                valid[hex.getGridRow()][hex.getGridColumn()] = true;
            }
        }
        //One of Six/Four starting positions for the hex spiral
        hex = getStartingHex(startPosition%Hex.SIDES);

        //The direction of motion is 2 less or greater
        // than the starting position depending on orientation
        int direction = Math.floorMod(startPosition+(2*orientation),Hex.SIDES);
        Hex prev = hex;
        int index = 0;
        while(index < getShuffledHexCount()){
            while (hex != null && valid[hex.getGridRow()][hex.getGridColumn()]){
                hc.addToSpiral(hex, index);
                hex.setId(index);
                valid[hex.getGridRow()][hex.getGridColumn()] = false;
                prev = hex;
                hex = hc.get(hex, direction);
                index++;
            }
            hex = prev;
            direction = Math.floorMod(direction+orientation, Hex.SIDES);
            hex = hc.get(hex, direction);
        }
    }
    /**
     * <p>Helper function for initHexSpiral(int, int)</p>
     * @param startPosition <p>0->Top Left</p>
     *                      <p>1->Top Right</p>
     *                      <p>2->Right</p>
     *                      <p>3->Bottom Right</p>
     *                      <p>4->Bottom Left</p>
     *                      <p>5->Left</p>
     * @return The starting hex, one of 6 hexagonal corners
     */


    public Hex getStartingHex(int startPosition){
        int rows = getHexGridDim().height;
        int cols = getHexGridDim().width;
        int o = getColumnOffset();
        return switch (Math.floorMod(startPosition,Hex.SIDES)) {
            case HexCollection.TOP_LEFT -> hc.get(0, 1);
            case HexCollection.TOP_RIGHT -> hc.get(0, cols - (2+o));
            case HexCollection.RIGHT -> hc.get(rows/2, cols - 1);
            case HexCollection.BOTTOM_RIGHT -> hc.get(rows-1, cols - (2+o));
            case HexCollection.BOTTOM_LEFT -> hc.get(rows-1, 1);
            case HexCollection.LEFT -> hc.get(rows/2, 0);
            default -> null;
        };
    }

    //BOARD CONTROL

    /**
     * <p>Wrapper function placeFixedTilesSmall/placeFixedTilesLarge.</p>
     * <p>Number of tiles placed in subclass functions must match
     * fixedHexCount set in initTileCounts().</p>
     * <p>If Random flag is set, do nothing.</p>
     */
    private void placeFixedTiles() {
        if(getRandomFlag()){
            return;
        }
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                placeFixedTilesSmall();
                break;
            case Board.LARGE_BOARD:
                placeFixedTilesLarge();
                break;
        }
    }


    /**
     * <p>Wrapper function placeUnflippedTilesSmall/placeUnflippedTilesLarge.</p>
     * <p>Number of tiles placed in subclass functions must match
     * unflippedHexCount set in initTileCounts().</p>
     * <p>If Random flag is set, do nothing.</p>
     */
    private void placeUnflippedTiles(){
        if(getRandomFlag()){
            return;
        }
        switch (getBoardSize()){
            case Board.SMALL_BOARD:
                placeUnflippedTilesSmall();
                break;
            case Board.LARGE_BOARD:
                placeUnflippedTilesLarge();
                break;
        }
    }


    /**
     * <p>Overloaded wrapper function for shuffleHexes(ICommand, int[], TokenCollection, int, int, int).</p>
     * <p>Call to reshuffle resources and tokens of hexes of type "SHUFFLED"./p>
     */
    public void shuffleHexes(){
        shuffleHexes(new BiomeCommand(), resourceCounts, tc,
                0,getShuffledHexCount(),0);
    }


    /**
     * <p>Overloaded wrapper function for shuffleHexes(ICommand, int[], TokenCollection, int, int, int).</p>
     * <p>Call with new BiomeCommand to reshuffle resources and tokens of hexes of type "SHUFFLED"./p>
     * <p>Call with null value to maintain resource layout but reshuffle tokens.</p>
     * @param command Provide a new BiomeCommand object to shuffle biomes. If null, only tokens will reshuffle.
     */
    public void shuffleHexes(ICommand command){
        if(command instanceof BiomeCommand){
            shuffleHexes(new BiomeCommand(), resourceCounts, tc,
                    0,getShuffledHexCount(),0);
        }else{
            shuffleHexes(null, resourceCounts, tc,
                    0,getShuffledHexCount(),0);
        }
    }


    /**
     * <p>Overloaded wrapper function for shuffleHexes(ICommand, int[], TokenCollection, int, int, int).</p>
     * <p>The four parameters in this method are used to differentiate whether the reshuffle occurs on the hexes
     * of type "SHUFFLED" or "UNFLIPPED".</p>
     * <p>Call with null value to maintain resource layout but reshuffle tokens.
     * @param resourcePool An array of representing the amount of each resource to be shuffled.
     * @param tc An ordered collection of tokens to be placed on resource producing biomes.
     * @param start The index (spiral) for biome shuffling to begin at.
     * @param end The index (spiral) for biome shuffling to end at.
     */
    public void shuffleHexes(int[] resourcePool, TokenCollection tc, int start, int end){
        shuffleHexes(new BiomeCommand(), resourcePool, tc,start,end, resourcePool[Tile.OCEAN]/4);
    }


    /**
     * <p>Main hex shuffle function. Holds all shuffle logic for resource or tokens.</p>
     * @param biomeCommand Provide a new BiomeCommand object to shuffle biomes. If null, only tokens will reshuffle.
     * @param resourcePool An array of representing the amount of each resource to be shuffled.
     * @param tc An ordered collection of tokens to be placed on resource producing biomes.
     * @param start The index (spiral) for biome shuffling to begin at.
     * @param end The index (spiral) for biome shuffling to end at.
     * @param oceanOffset Value used to add a bias towards randomly picking an ocean biome.
     */
    public void shuffleHexes(ICommand biomeCommand
            , int[] resourcePool, TokenCollection tc, int start, int end, int oceanOffset) {
        int startOffset = 0, endOffset = 0;
        Random rand = new Random();
        int randomCycle = 0;//incremented every loop iteration
        int index = start;
        int[] resourceCounts = resourcePool.clone();
        while(index < end) {
            oceanOffset = resourcePool[Tile.OCEAN]/4;

            //if the last resource in the resourceCounts array is 0, then trim tail
            if(resourceCounts[(resourceCounts.length - 1) - endOffset] == 0){
                //System.out.println("end"+endOffset);
                endOffset++;
            }
            //if the first resource in the resourceCounts array is 0, then trim head
            if(resourceCounts[startOffset] == 0){
                //System.out.println("start"+startOffset);
                startOffset++;
            }
            int validBiomeRange = Tile.RESOURCE_MAX+1 - endOffset - startOffset;
            if(validBiomeRange <= 0){
                break;
            }
            //get random biome
            int biome = startOffset + rand.nextInt(validBiomeRange + oceanOffset);
            biome = Math.min(biome, Tile.OCEAN);
            //if valid choice, if not choose again
            if(resourceCounts[biome] > 0 || biomeCommand == null) {
                Hex hex = hc.get(index);
                if(biomeCommand != null){
                    biomeCommand.set(hex, biome);
                    resourceCounts[biome]--;
                }
                if(hex.getBiome() < Tile.DESERT ){
                    int token = tc.getNext();
                    hex.setToken(token);
                }
                else{
                    hex.setToken(0);
                }
                hex.revalidate();
                hex.repaint();
                index++;
            }
            randomCycle++;
        }
        resetTokensToHead();
        System.out.println("Random Report\nh:" + randomCycle + "/" + (end-start));
    }


    /**
     * <p>Sets head of token collection built in iterator to its current value.
     * Subsequent calls to tc.reset() will return iterator to head.</p>
     */
    public void resetTokensToHead(){
        tc.reset();
    }


    /**
     * <p>Reshuffles the order of token in the TokenCollection.</p>
     */
    public void shuffleTokens() {
        tc.reshuffle();
    }


    /**
     * <p>Look at all edges on every hex and determine all valid locations for a port.</p>
     */
    public void findAllPorts() {
        pc = new PortCollection(this);
        IIterator<Hex> i = hc.getSpiralIterator();
        Hex hex;
        int index = 0;
        while (i.hasNext()) {
            hex = i.getNext();
            if(hex.getBiome() == Tile.OCEAN ||
                    hex.getType() == Tile.UNFLIPPED_TYPE){
                continue;
            }

            int startAngle = startPosition
                    - (orientation * boardSize + orientation)
                    + (orientation * (index/8));
            for(int dir = 0; dir < Hex.SIDES; dir++) {
                int angle = Math.floorMod(startAngle+(dir*orientation), Hex.SIDES);
                //System.out.println("$");
                Hex nbrHex = hc.get(hex, angle);
                if(nbrHex == null ||
                        (nbrHex.getBiome() == Tile.OCEAN && nbrHex.getType() != Tile.UNFLIPPED_TYPE)){
                    //System.out.println("^");
                               Port port = new Port(hex, angle);
                    port.setId(index);
                    port.setDebug(getDebugFlag());
                    //System.out.println(index);
                    pc.add(port);
                    index++;
                    //break;
                }
                //potentially a way to short circuit
            }
            //System.out.println("∆");
        }
        //System.out.println("Counter: (" + index + ")");
    }


    /**
     * <p>Look at all valid location of ports and evenly distribute them along the border of the main island.</p>
     */
    public void findValidPorts(){
        IIterator<Port> i = pc.getAllPortIterator();
        Port port;
        int end = 0;
        do{
            //System.out.println(end);
            port = i.getNext();
            if(port.getHex().getType() != Tile.SHUFFLED){
                 break;
            }
            end++;
        }while(i.hasNext());
        end -= 2;
        //System.out.println("Found valid ports: " + end);

        int minHop = end/getPortCount();
        int maxHop = minHop+2;

        int indexV = 0;
        int indexA = -minHop;
        Random rand = new Random();
        while(indexV < getPortCount()){// [minHop ,maxHop-1]
            indexA += minHop + rand.nextInt(maxHop - minHop);
            //indexA += avgHop;
            indexA = Math.min(indexA, end);
            pc.add(pc.get(indexA,false),indexV++);
        }

        port = pc.get(0,true);
        Port lastPort = pc.get(getPortCount()-1,true);

        int val = end - lastPort.getId() + port.getId();
        //System.out.println("val: " + val);
        if(!getRandomFlag() && (val < minHop-1 || val > maxHop-1)){
            System.out.println("∆");
            findValidPorts();
        }
    }


    /**
     * <p>Shuffle the valid ports biomes.</p>
     */
    public void shufflePorts() {
        int startOffset = 0, endOffset = 1;
        Random rand = new Random();
        int randomCycle = 0;//incremented every loop iteration
        int index = 0;
        int[] portCounts = this.portCounts.clone();

        boolean goldStart = rand.nextBoolean();

        while(index < getPortCount()) {
            //if the last resource in the resourceCounts array is 0, then trim tail
            if(portCounts[(portCounts.length - 1) - endOffset] == 0){
                //System.out.println("end"+endOffset);
                endOffset++;
            }
            //if the first resource in the resourceCounts array is 0, then trim head
            if(portCounts[startOffset] == 0){
                //System.out.println("start"+startOffset);
                startOffset++;
            }
            if(portCounts.length  - endOffset - startOffset <= 0){
                break;
            }
            //get random biome
            int biome;
            if(goldStart && portCounts[Tile.GOLD] > 0){
                biome = Tile.GOLD;
            }
            else{
                biome = startOffset + rand.nextInt(portCounts.length - endOffset - startOffset);
                if(biome == Tile.GOLD){
                    System.out.println("†");
                }
            }
            //biome = Math.min(biome, Tile.GOLD);
            //if valid choice, if not choose again
            //System.out.println(index);
            if(portCounts[biome] > 0) {
                Port port = pc.get(index, true);
                port.setBiome(biome);
                goldStart = !goldStart;
                portCounts[biome]--;
                index++;
            }
            randomCycle++;
        }
        resetTokensToHead();
        System.out.println("Random Report\np:" + randomCycle + "/" + getPortCount());
    }


    /**
     * <p>Pool all hex counts into shuffledHexCount.</p>
     */
    public void poolHexCounts(){
        shuffledHexCount += fixedHexCount + unflippedHexCount;
        fixedHexCount = 0;
        unflippedHexCount = 0;
    }


    /**
     * <p>Update each tile's debug flag to the boards current debug flag value.</p>
     */
    public void updateTileDebugFlags() {
        Tile tile;
        //Hexes
        IIterator<Hex> hexIIterator = hc.getGridIterator();
        while(hexIIterator.hasNext()){
            tile = hexIIterator.getNext();
            tile.setDebug(getDebugFlag());
            tile.revalidate();
            tile.repaint();
        }
        //Ports
        IIterator<Port> portIIterator = pc.getAllPortIterator();
        while(portIIterator.hasNext()){
            tile = portIIterator.getNext();
            tile.setDebug(getDebugFlag());
            tile.revalidate();
            tile.repaint();
        }
    }


    /** <p>Sets the flipped status of all valid ports.</p
     * @param flipped <p>True->Ports Shown</p>
     *                <p>False->Ports Hidden</p>
     */
    public void flipPorts(boolean flipped) {
        IIterator<Port> portIIterator = pc.getAllPortIterator();
        Port port;
        while(portIIterator.hasNext()){
            port = portIIterator.getNext();
            port.setFlipped(flipped);
            port.revalidate();
            port.repaint();
        }
    }


    /**
     * <p>Sets all hex's type to shuffled, biome to Ocean, number token to -1, and clears all ports.</p>
     */
    public void clearBoard(){
        IIterator<Hex> i = hc.getGridIterator();
        Hex hex;
        while(i.hasNext()){
            hex = i.getNext();
            hex.setType(Tile.SHUFFLED);
            hex.setBiome(Tile.OCEAN);
            hex.setToken(-1);
        }
        findAllPorts();
    }


    /**
     * <p>Sets all valid port's biomes to Ocean.</p>
     */
    public void clearPorts(){
        IIterator<Port> i = pc.getValidPortIterator();
        Port port;
        while(i.hasNext()){
            port = i.getNext();
            port.setBiome(Tile.OCEAN);
            port.setFlipped(false);
        }
    }

    //UTILITY

    /**
     * <p>Set the id, biome, and type of the hex at the row,column.</p>
     * @param row The row of the placed hex.
     * @param col The column of the placed hex.
     * @param id The hex's id.
     * @param biome The hex's biome.
     */
    public void placeFixedHex(int row, int col, int id, int biome)   {
        Hex hex = hc.get(row, col);
        hex.setId(id);
        hc.addToSpiral(hex, id);
        if(getRandomFlag()){
            return;
        }
        hex.setType(Tile.FIXED);
        hex.setBiome(biome);
        if(resourceCounts[biome] > 0){
            resourceCounts[biome]--;
        }
        if(biome<Tile.DESERT){
            hex.setToken(tc.getNext());
        }
        else{
            hex.setToken(0);
        }
    }


    /**
     * <p>Set the id and type of the hex at the row,column.</p>
     * @param row The row of the placed hex.
     * @param col The column of the placed hex.
     * @param id The hex's id.
     */
    public void placeUnflippedHex(int row, int col, int id){
        Hex hex = hc.get(row, col);
        hex.setId(id);
        hc.addToSpiral(hex, id);
        hex.setType(Tile.UNFLIPPED_TYPE);
    }


    /**
     * <p>Calculates the column offset for the board to account for the rows hexagons being placed horizontally offset.</p>
     * @return <p>grid.height = 5 -> 0</p>
     *         <p>grid.height = 7 -> 1</p>
     */
    public int getColumnOffset(){
        return (hexGridDim.height/2)%2;
    }


    /**
     * <p>Clears all previous flag values and sets {@code flag} to new value.</p>
     * <p>Does not update aggregates.</p>
     * @param newFlags New flag value.
     */
    public void setFlags(int newFlags){
        this.flags = newFlags;
    }


    /**
     * <p>Adds the flag(s) to the current {@code flag} value.</p>
     * <p>Does not update aggregates.</p>
     * @param flag The integer representation of the flag(s) to be set.
     */
    public void addFlags(int flag){
        flags |= flag;
    }


    /**
     * <p>Adds the flag(s) to the current {@code flag} value.</p>
     * <p>Does not update aggregates.</p>
     * @param flag The integer representation of the flag(s) to be removed.
     */
    public void removeFlags(int flag){
        if((flags & flag) == flag){
            flags -= flag;
        }
    }


    /**
     * <p>Resets {@code flag} to zero.</p>
     * <p>Does not update aggregates.</p>
     */
    public void resetFlags(){ flags = 0; }


    /**
     * <p>Add the unflipped resource counts to the shuffled resource counts.</p>
     * @param unflippedResourceCounts The array representing the number of each resource
     *                                to be added to shuffled resource count array.
     */
    public void combineResourceCount(int[] unflippedResourceCounts){
        for(int i = 0; i < resourceCounts.length; i++){
            resourceCounts[i] += unflippedResourceCounts[i];
        }
    }


    /**
     * <p>Concatenate the unflipped token collection to the end of the shuffled token collection.</p>
     * @param unflippedTokenCollection The unflipped token collection.
     */
    public void combineTokenCollections(TokenCollection unflippedTokenCollection){
        tc.addAll(unflippedTokenCollection);
    }

    //SETTERS

    /**
     * <p>Sets the dimensions of the board.</p>
     * @param rows Height of board in hexes.
     * @param cols Maximum width of board in hexes.
     */
    public void setHexGridDim(int rows, int cols) {
        hexGridDim.setSize(cols, rows);
    }


    /**
     * <p>Sets the count values for each type of hex and port.</p>
     * @param shuffledHexCount Number of shuffled hexes.
     * @param fixedHexCount Number of fixed hexes.
     * @param unflippedHexCount Number of unflipped hexes.
     * @param portCount Number of port.
     */
    public void setTileCounts(int shuffledHexCount, int fixedHexCount, int unflippedHexCount, int portCount) {
        this.shuffledHexCount = shuffledHexCount;
        this.fixedHexCount = fixedHexCount;
        this.unflippedHexCount = unflippedHexCount;
        this.portCount = portCount;
    }


    /**
     * <p>Sets {@code resourceCounts} field which represents the number of each hex biome to pe placed</p>
     * @param resourceCounts Integer array representing the number of each biome to be placed
     */
    public void setResourceCounts(int[] resourceCounts) {
        this.resourceCounts = resourceCounts;
    }


    /**
     * <p>Sets {@code portCounts} field which represents the number of each port biomes to pe placed</p>
     * @param portCounts Integer array representing the number of each biome to be placed
     */
    public void setPortCounts(int[] portCounts) {
        this.portCounts = portCounts;
    }


    /**
     * <p>Sets {@code tc} field which represents the ordered array of number tokens to pe placed</p>
     * @param tokens Integer array representing the ordered number tokens to be placed
     */
    public void setTokens(int[] tokens) {
        tc = new TokenCollection(tokens);
    }

    //GETTERS

    /**
     * @return Dimension object representing number the number of rows and columns
     */
    public Dimension getHexGridDim() {
        return hexGridDim;
    }


    /**
     * @return The current {@code HexCollection} object
     */
    public HexCollection getHexCollection() {
        return hc;
    }


    /**
     * @return The current {@code PortCollection} object
     */
    public PortCollection getPortCollection() {
        return pc;
    }


    /**
     * @return Integer representation of the board size. <p>Small -> 0</p> <p>Large -> 1</p>
     */
    public int getBoardSize() {
        return boardSize;
    }


    /**
     * @return {@code shuffledHexCount}
     */
    public int getShuffledHexCount() {
        return shuffledHexCount;
    }


    /**
     * @return {@code fixedHexCount}
     */
    public int getFixedHexCount() {
        return fixedHexCount;
    }


    /**
     * @return {@code unflippedHexCount}
     */
    public int getUnflippedHexCount() {
        return unflippedHexCount;
    }


    /**
     * @return the sum of all hex counts
     */
    public int getTotalHexCount() {
        return shuffledHexCount + fixedHexCount + unflippedHexCount;
    }


    /**
     * @return {@code portCount}
     */
    public int getPortCount() {
        return portCount;
    }


    /**
     * @return {@code flags}
     */
    public int getFlags(){return flags;}


    /**
     * @return <p>True->Debug</p>
     *         <p>False->No Debug</p>
     */
    public boolean getDebugFlag(){
        return (flags&DEBUG) == DEBUG;
    }


    /**
     * @return <p>True->Random</p>
     *         <p>False->No Random</p>
     */
    public boolean getRandomFlag(){
        return (flags&RANDOM) == RANDOM;
    }

    //STRING METHODS
    public String toString(){
        String str;
        str = getHexGridStringTitle("Board Description") +
              "Size:\t\t(" + ((boardSize == Board.SMALL_BOARD)?"Small":"Large") + ")\n" +
              "Type:\t\t(" + getBoardType() + ")\n" +
              "Flags:\t\t(" + getFlagString() + ")\n" +
              "HexGridDim:\t(" + hexGridDim.height + " rows, "
                               + hexGridDim.width + " cols)\n" +
              "TileCounts:\t(" + shuffledHexCount + "s,"
                               + fixedHexCount + "f,"
                               + unflippedHexCount + "u,"
                               + portCount + "p)\n" +
              "Resources:\t(" + resourceCounts[Tile.WOOD] + "w,"
                              + resourceCounts[Tile.SHEEP] + "s,"
                              + resourceCounts[Tile.HAY] + "h,"
                              + resourceCounts[Tile.BRICK] + "b,"
                              + resourceCounts[Tile.ROCK] + "r,"
                              + resourceCounts[Tile.GOLD] + "g,"
                              + resourceCounts[Tile.DESERT] + "d,"
                              + resourceCounts[Tile.OCEAN] + "o)\n" +
              "Ports:\t\t("   + portCounts[Tile.WOOD] + "w,"
                              + portCounts[Tile.SHEEP] + "s,"
                              + portCounts[Tile.HAY] + "h,"
                              + portCounts[Tile.BRICK] + "b,"
                              + portCounts[Tile.ROCK] + "r,"
                              + portCounts[Tile.GOLD] + "g)\n" +
              "Tokens:\t\t(" + getTokenString() + ")\n"
                + getHexGridStringTitle("");
        return str;
    }
    public String getBoardType(){
        String classname = this.getClass().getSimpleName();
        return classname.substring(0, classname.length() - "Board".length());
    }
    public String getTokenString(){
        StringBuilder sb = new StringBuilder();
        IIterator<Integer> iterator = tc.getIterator();
        while(iterator.hasNext()){
            sb.append(iterator.getNext());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    public String getSpiralString(ICommand command){

        return  getHexGridStringTitle("Board Spiral Array") +
                getShuffledString(command) +
                getFixedString(command) +
                getUnflippedString(command) +
                getHexGridStringTitle("") +
                "\n";
    }
    public String getShuffledString(ICommand command){
        StringBuilder sb = new StringBuilder();
        sb.append("Shuffled:\t");
        int start = 0;
        int end = getShuffledHexCount();
        spiralStringHelper(command, sb, start, end);
        return sb.toString();
    }
    public String getFixedString(ICommand command){
        StringBuilder sb = new StringBuilder();
        sb.append("Fixed:\t\t");
        int start = getShuffledHexCount();
        int end = getShuffledHexCount() + getFixedHexCount();
        spiralStringHelper(command, sb, start, end);
        return sb.toString();
    }
    public String getUnflippedString(ICommand command){
        StringBuilder sb = new StringBuilder();
        sb.append("Unflipped:\t");
        int start = getShuffledHexCount() + getFixedHexCount();
        int end = getShuffledHexCount() + getFixedHexCount() + getUnflippedHexCount();
        spiralStringHelper(command, sb, start, end);
        return sb.toString();
    }
    private void spiralStringHelper(ICommand command, StringBuilder sb, int start, int end){
        for(int i = start; i < end; i++){
            sb.append("[");
            int value = command.get(hc.get(i));
            if(command instanceof BiomeCommand){
                sb.append((char)value);
            } else{
                sb.append(value);
            }
            sb.append("],");
        }
        if(start<end) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("\n");
    }
    public String getHexGridString(ICommand command){
        if(command instanceof BiomeCommand || command instanceof TypeCommand) {
            return getHexGridString(command, true);
        }else{
            return getHexGridString(command, false);
        }
    }
    private String getHexGridStringTitle(String title){
        int i = (hexGridDim.width * 2) - title.length()/2;
        return  "-".repeat(Math.max(0, i)) +
                title +
                "-".repeat(Math.max(0, i)) +
                "\n";
    }
    public String getHexGridString(ICommand command, boolean singleDigit){
        StringBuilder sb = new StringBuilder();
        sb.append(getHexGridStringTitle("Board Grid"));
        for(int r = 0; r < hexGridDim.height; r++){
            //possibly make this into its own function later
            if(r%2 != (hexGridDim.height/2)%2){
                sb.append("  ");
            }
            for(int c = 0; c < hexGridDim.width; c++){
                Hex hex = hc.get(r,c);
                if(hex != null){
                    sb.append("[");
                    int value = command.get(hex);
                    if(!singleDigit &&
                            value >=0 &&
                            value < 10){
                        sb.append("0");
                    }

                    if(command instanceof BiomeCommand || command instanceof TypeCommand){
                        sb.append((char)value);
                    }
                    else if(command instanceof TokenCommand && value == 0){
                        sb.deleteCharAt(sb.length()-1);
                        sb.append((hex.getBiome() == Tile.DESERT?"--":"~~"));
                    }else {
                        sb.append(value);
                    }
                    sb.append("]");
                } else{
                    //sb.append("[~]");
                    sb.append("   ");
                    if(!singleDigit) {sb.append(" ");}
                }
                if(singleDigit) {sb.append(" ");}
            }
            sb.append("\n");
        }
        sb.append(getHexGridStringTitle(""));
        return sb.toString();
    }
    public String getFlagString(){
        StringBuilder sb = new StringBuilder();
        if((flags & DEBUG) != 0){
            sb.append("DEBUG|");
        }
        if((flags & RANDOM) != 0){
            sb.append("RANDOM|");
        }
        if(flags != 0){
            sb.deleteCharAt(sb.length()-1);
        }else {
            sb.append("~");
        }
        return sb.toString();
    }


    //TEST METHODS

    /**
     * <p>Tests the {@code HexCollection} object's accessors.</p>
     */
    public void testHexCollectionAccessors(int row, int col){
        System.out.println(getHexGridStringTitle("HexCollection Tests"));
        Hex in, out;
        in = hc.get(row,col);
        in.setBiome(8);
        System.out.println("TopLeft");
        out = hc.getTopLeft(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getGridRow(), in.getGridColumn());
        }else{
            out.setBiome(1);
            System.out.printf("(%d,%d)->(%d,%d)\n", in.getGridRow(), in.getGridColumn()
                    , out.getGridRow(), out.getGridColumn());
        }
        System.out.println("TopRight");
        out = hc.getTopRight(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getGridRow(), in.getGridColumn());
        }else{
            out.setBiome(2);
            System.out.printf("(%d,%d)->(%d,%d)\n",in.getGridRow(),in.getGridColumn()
                                            ,out.getGridRow(),out.getGridColumn());
        }
        System.out.println("BottomLeft");
        out = hc.getBottomLeft(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getGridRow(), in.getGridColumn());
        }else{
            out.setBiome(3);
            System.out.printf("(%d,%d)->(%d,%d)\n", in.getGridRow(), in.getGridColumn()
                    , out.getGridRow(), out.getGridColumn());
        }
        System.out.println("BottomRight");
        out = hc.getBottomRight(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getGridRow(), in.getGridColumn());
        }else{
            out.setBiome(4);
            System.out.printf("(%d,%d)->(%d,%d)\n",in.getGridRow(),in.getGridColumn()
                    ,out.getGridRow(),out.getGridColumn());
        }
        System.out.println("Left");
        out = hc.getLeft(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getGridRow(), in.getGridColumn());
        }else{
            out.setBiome(5);
            System.out.printf("(%d,%d)->(%d,%d)\n", in.getGridRow(), in.getGridColumn()
                    , out.getGridRow(), out.getGridColumn());
        }
        System.out.println("Right");
        out = hc.getRight(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getGridRow(), in.getGridColumn());
        }else{
            out.setBiome(6);
            System.out.printf("(%d,%d)->(%d,%d)\n",in.getGridRow(),in.getGridColumn()
                    ,out.getGridRow(),out.getGridColumn());
        }
        System.out.println(getHexGridStringTitle(""));

    }


    /**
     * <p>Tests {@code initHexSpiral()} at every {@code startPosition} and {@code orientation}.</p>
     */
    public void testHexSpiral() {
        for (int startPosition = 0; startPosition < Hex.SIDES; startPosition++) {
            initHexSpiral(startPosition, CLOCKWISE);
            System.out.println("start: " + startPosition + ", dir: " + CLOCKWISE);
            System.out.println(getHexGridString(new IdCommand()));
        }
        for (int startPosition = 0; startPosition < Hex.SIDES; startPosition++) {
            initHexSpiral(startPosition, COUNTER_CLOCKWISE);
            System.out.println("start: " + startPosition + ", dir: " + COUNTER_CLOCKWISE);
            System.out.println(getHexGridString(new IdCommand()));
        }
    }


    /**
     * Tests {@code addFlags()}, {@code setFlags()}, {@code removeFlags()}, and {@code resetFlags()}
     */
    public void testFlagMethods() {
        int flags = this.flags;
        System.out.print(getHexGridStringTitle("Flag Tests"));
        //Reset
        resetFlags();
        System.out.println(getFlagString() + "==~");
        //Add
        addFlags(DEBUG);
        addFlags(RANDOM);
        System.out.println(getFlagString() + "==DEBUG|RANDOM");
        //Remove
        removeFlags(DEBUG);
        System.out.println(getFlagString() + "==RANDOM");
        //Set
        setFlags(DEBUG);
        System.out.println(getFlagString() + "==DEBUG");
        //Return board to current flag value.
        setFlags(flags);

        System.out.print(getHexGridStringTitle(""));
    }

    //ABSTRACT
    public abstract void initHexGridDim();
    public abstract void initTileCounts();
    public abstract void initResourceCounts();
    public abstract void initPortCounts();
    public abstract void initTokens();

    public abstract void placeFixedTilesSmall();
    public abstract void placeFixedTilesLarge();

    public abstract void placeUnflippedTilesSmall();
    public abstract void placeUnflippedTilesLarge();
}
