import java.awt.*;
import java.util.Random;

public abstract class Board implements IProxy {
    //STATIC FIELDS
    //Flags
    public static final int DEBUG = 1;
    public static final int RANDOM = 2;

    //Encodings
    public static final int STANDARD_BOARD = 0;
    public static final int SEAFARERS_BOARD = 1;
    public static final int FOGISLAND_BOARD = 2;
    public static final int SMALL_BOARD = 0;
    public static final int LARGE_BOARD = 1;
    public static final int CLOCKWISE = 1;
    public static final int COUNTER_CLOCKWISE = -1;

    //FIELDS
    private final int boardSize;
    private int shuffledHexCount, fixedHexCount, unflippedHexCount, portCount;
    //How many of each resource/port in the game
    private int[] resourceCounts;
    private int[] portCounts;
    private TokenCollection tc;

    private final Dimension hexGridDim; //HexGrid Rows/Cols
    private HexCollection hc;//interface to access both grid and spiral collections

    private Port[] ports;
    private int flags;// 0001->random | 0010->debug | ...

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
    public void initBoard(){
        initHexGrid();
        handleFlags();
        placeFixedTiles();
        placeUnflippedTiles();
        initHexSpiral();
        shuffleHexes();
        shufflePorts();
    }
    /* Pool all hex counts into shuffled*/
    public void handleFlags(){
        if(getRandomFlag()){
            shuffledHexCount += fixedHexCount + unflippedHexCount;
            fixedHexCount = 0;
            unflippedHexCount = 0;
        }
    }
    /* Initialize HexCollection and its aggregates */
    public void initHexGrid() {
        hc = new HexCollection(this);
        int medianRow = hexGridDim.height/2;
        //middle row first
        for (int c = 0; c < hexGridDim.width; c++) {
            Hex hex = new Hex();
            hex.setLoc(medianRow,c);
            hc.add(hex);
        }
        int startOffset = 0;
        int endOffset = 1;
        for(int r = 1; r <= medianRow; r++) {
            for (int c = startOffset; c < hexGridDim.width-endOffset; c++) {
                Hex hex1 = new Hex();
                Hex hex2 = new Hex();

                hex1.setLoc(medianRow - r,c);
                hex2.setLoc(medianRow + r,c);

                hc.add(hex1);
                hc.add(hex2);
            }
            if((medianRow-r)%2 == getColumnOffset()){
                endOffset++;
            }
            else{
                startOffset++;
            }
        }
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
        IIterator<Hex> i = getHexCollection().getGridIterator();
        Hex hex;
        boolean[][] valid = new boolean[getHexGridDim().height][getHexGridDim().width];

        //init valid matrix
        while(i.hasNext()){
            hex = i.getNext();
            if(hex != null && hex.getType() == Hex.SHUFFLED) {
                valid[hex.getRow()][hex.getColumn()] = true;
            }
        }
        HexCollection hc = getHexCollection();
        hex = getStartingHex(startPosition%Hex.SIDES);
        int direction = Math.floorMod(startPosition+(2*orientation),Hex.SIDES);
        Hex prev = hex;
        int index = 0;
        while(index < getShuffledHexCount()){
            while (hex != null && valid[hex.getRow()][hex.getColumn()]){
                hc.addToSpiral(hex, index);
                hex.setId(index);
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
    public Hex getStartingHex(int startPosition){
        int rows = getHexGridDim().height;
        int cols = getHexGridDim().width;
        int o = getColumnOffset();
        return switch (startPosition%Hex.SIDES) {
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
    public void placeFixedTiles() {
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
    public void placeUnflippedTiles(){
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

    public void shuffleHexes(){
        shuffleHexes(new BiomeCommand(), new TokenCommand(), resourceCounts, tc,
                0,getShuffledHexCount(),0);
    }
    public void shuffleHexes(int[] resourcePool, TokenCollection tc, int start, int end){
        shuffleHexes(new BiomeCommand(), new TokenCommand(), resourcePool, tc,start,end,0);
    }
    public void shuffleHexes(ICommand command){
        if(command instanceof BiomeCommand){
            shuffleHexes(new BiomeCommand(), null, resourceCounts, tc,
                    0,getShuffledHexCount(),0);
        }else{
            shuffleHexes(null, new TokenCommand(), resourceCounts, tc,
                    0,getShuffledHexCount(),0);
        }
    }
    public void shuffleHexes(ICommand biomeCommand, ICommand tokenCommand
            , int[] resourcePool, TokenCollection tc, int start, int end, int oceanOffset) {
        int startOffset = 0, endOffset = 0;
        Random rand = new Random();
        int randomCycle = 0;//incremented every loop iteration
        int index = start;
        int[] resourceCounts = resourcePool.clone();
        while(index < end) {
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
            if(resourceCounts.length  - endOffset - startOffset <= 0){
                break;
            }
            //get random biome
            int biome = startOffset + rand.nextInt(resourceCounts.length
                    - endOffset - startOffset + oceanOffset);
            biome = Math.min(biome, Tile.OCEAN);
            //if valid choice, if not choose again
            if(resourceCounts[biome] > 0) {
                Hex hex = hc.get(index);
                if(biomeCommand != null){
                    biomeCommand.set(hex, biome);
                }
                resourceCounts[biome]--;
                if(hex.getBiome() < Tile.DESERT && tokenCommand != null){
                    int token = tc.getNext();
                    tokenCommand.set(hex, token);
                }
                index++;
            }
            randomCycle++;
        }
        System.out.println("Random Report\nh:" + randomCycle + "/" + (end-start));
    }
    public void shufflePorts() {

    }
    public void clearBoard(){
        IIterator<Hex> i = hc.getGridIterator();
        Hex hex;
        while(i.hasNext()){
            hex = i.getNext();
            //hex.setBiome(Tile.OCEAN);
            hex.setBiome(0);
            hex.setToken(0);
        }
    }

    //UTILITY
    public void placeFixedHex(int row, int col, int id, int biome){
        Hex hex = hc.get(row, col);
        hex.setId(id);
        hc.addToSpiral(hex, id);
        if(getRandomFlag()){
            return;
        }
        hex.setType(Hex.FIXED);
        hex.setBiome(biome);
        if(resourceCounts[biome] > 0){
            resourceCounts[biome]--;
        }
        if(biome<Tile.DESERT){
            hex.setToken(tc.getNext());
        }
    }
    public void placeUnflippedHex(int row, int col, int id){
        Hex hex = hc.get(row, col);
        hex.setId(id);
        hc.addToSpiral(hex, id);
        hex.setType(Hex.UNFLIPPED);
    }
    public int getColumnOffset(){
        return (hexGridDim.height/2)%2;
    }

    public void addFlag(int flag){
        flags |= flag;
    }
    public void removeFlag(int flag){
        if((flags & DEBUG) == DEBUG){
            flags -= DEBUG;
        }
    }
    public void resetFlags(){ flags = 0; }
    public void addToResourceCount(int[] old){
        for(int i = 0; i < resourceCounts.length; i++){
            resourceCounts[i] += old[i];
        }
    }
    public void addToTokenCollection(TokenCollection newTc){
        tc.addAll(newTc);
    }
    //SETTERS
    public void setHexGridDim(int rows, int cols) {
        hexGridDim.setSize(cols, rows);
    }
    public void setTileCounts(int shuffledHexCount, int fixedHexCount, int unflippedHexCount, int portCount) {
        this.shuffledHexCount = shuffledHexCount;
        this.fixedHexCount = fixedHexCount;
        this.unflippedHexCount = unflippedHexCount;
        this.portCount = portCount;
    }
    public void setResourceCounts(int[] resourceCounts) {
        this.resourceCounts = resourceCounts;
    }
    public void setPortCounts(int[] portCounts) {
        this.portCounts = portCounts;
    }
    public void setTokens(int[] tokens) {
        tc = new TokenCollection(tokens);
    }
    public void setFlags(int newFlags){
        this.flags = newFlags;
    }

    //GETTERS
    public Dimension getHexGridDim() {
        return hexGridDim;
    }
    public HexCollection getHexCollection() {
        return hc;
    }
    public int getBoardSize() {
        return boardSize;
    }
    public int getShuffledHexCount() {
        return shuffledHexCount;
    }
    public int getFixedHexCount() {
        return fixedHexCount;
    }
    public int getUnflippedHexCount() {
        return unflippedHexCount;
    }
    public int getFlags(){return flags;}
    public boolean getDebugFlag(){
        return (flags&DEBUG) == DEBUG;
    }
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
        if(command instanceof BiomeCommand) {
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

                    if(command instanceof BiomeCommand){
                        sb.append((char)value);
                    }
                    else if(command instanceof TokenCommand && value == 0){
                        sb.deleteCharAt(sb.length()-1);
                        sb.append("~~");
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
    public void testIterator(int row, int col){
        System.out.println(getHexGridStringTitle("Iterator Tests"));
        Hex in, out;
        in = hc.get(row,col);
        in.setBiome(8);
        System.out.println("TopLeft");
        out = hc.getTopLeft(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getRow(), in.getColumn());
        }else{
            out.setBiome(1);
            System.out.printf("(%d,%d)->(%d,%d)\n", in.getRow(), in.getColumn()
                    , out.getRow(), out.getColumn());
        }
        System.out.println("TopRight");
        out = hc.getTopRight(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getRow(), in.getColumn());
        }else{
            out.setBiome(2);
            System.out.printf("(%d,%d)->(%d,%d)\n",in.getRow(),in.getColumn()
                                            ,out.getRow(),out.getColumn());
        }
        System.out.println("BottomLeft");
        out = hc.getBottomLeft(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getRow(), in.getColumn());
        }else{
            out.setBiome(3);
            System.out.printf("(%d,%d)->(%d,%d)\n", in.getRow(), in.getColumn()
                    , out.getRow(), out.getColumn());
        }
        System.out.println("BottomRight");
        out = hc.getBottomRight(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getRow(), in.getColumn());
        }else{
            out.setBiome(4);
            System.out.printf("(%d,%d)->(%d,%d)\n",in.getRow(),in.getColumn()
                    ,out.getRow(),out.getColumn());
        }
        System.out.println("Left");
        out = hc.getLeft(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getRow(), in.getColumn());
        }else{
            out.setBiome(5);
            System.out.printf("(%d,%d)->(%d,%d)\n", in.getRow(), in.getColumn()
                    , out.getRow(), out.getColumn());
        }
        System.out.println("Right");
        out = hc.getRight(in);
        if(out == null) {
            System.out.printf("(%d,%d)->(null)\n", in.getRow(), in.getColumn());
        }else{
            out.setBiome(6);
            System.out.printf("(%d,%d)->(%d,%d)\n",in.getRow(),in.getColumn()
                    ,out.getRow(),out.getColumn());
        }
        System.out.println(getHexGridStringTitle(""));

    }
    /* Test initHexSpiral at every start/orientation */
    public void testHexSpiral() {
        for (int strp = 0; strp < Hex.SIDES; strp++) {
            initHexSpiral(strp, CLOCKWISE);
            System.out.println("start: " + strp + ", dir: " + CLOCKWISE);
            System.out.println(getHexGridString(new IdCommand()));
        }
        for (int strp = 0; strp < Hex.SIDES; strp++) {
            initHexSpiral(strp, COUNTER_CLOCKWISE);
            System.out.println("start: " + strp + ", dir: " + COUNTER_CLOCKWISE);
            System.out.println(getHexGridString(new IdCommand()));
        }
    }
    public void testFlags() {
        int flags = this.flags;
        resetFlags();
        System.out.print(getHexGridStringTitle("Flag Tests"));
        System.out.println(getFlagString() + "==~");
        addFlag(DEBUG);
        addFlag(RANDOM);
        System.out.println(getFlagString() + "==DEBUG|RANDOM");
        removeFlag(DEBUG);
        System.out.println(getFlagString() + "==RANDOM");
        setFlags(DEBUG);
        System.out.println(getFlagString() + "==DEBUG");
        System.out.print(getHexGridStringTitle(""));
        setFlags(flags);
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
