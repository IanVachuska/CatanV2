import java.awt.*;
import java.util.Random;

public abstract class Board implements IProxy {
    //STATIC FIELDS
    public static final int SMALL_BOARD = 0;
    public static final int LARGE_BOARD = 1;
    public static final int CLOCKWISE = 1;
    public static final int COUNTER_CLOCKWISE = -1;

    //Fields
    private final int boardSize;
    private int shuffledHexCount, fixedHexCount, unflippedHexCount, portCount;
    //How many of each resource/port in the game
    private int[] resourceCounts;
    private int[] portCounts;

    private final Dimension hexGridDim; //HexGrid Rows/Cols
    private HexCollection hc;//interface to access both grid and spiral collections
    private int[] tokens;
    private Port[] ports;
    private int flags;// 0001->random | 0010->debug | ...

    //CONSTRUCTORS
    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.hexGridDim = new Dimension();
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
        placeFixedTiles();
        initHexSpiral();
        shuffleHexs();
        shufflePorts();
    }
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

    //BOARD CONTROL
    public void shuffleHexs() {
        int startOffset = 0, endOffset = 0;
        int tokenOffset = 0;//doesnt get incremented if resource is desert/ocean
        Random rand = new Random();
        int randomCycle = 0;//incremented every loop iteration
        int index = 0;//incremented every VALID loop iteration
        int[] resourceCounts = this.resourceCounts.clone();
        while(index < shuffledHexCount) {
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
            int biome = startOffset + rand.nextInt(resourceCounts.length - endOffset - startOffset);
            //if valid choice, if not choose again
            if(resourceCounts[biome] > 0) {
                Hex hex = hc.get(index);
                hex.setBiome(biome);
                resourceCounts[biome]--;
                if(biome < Tile.DESERT){
                    hex.setToken(tokens[tokenOffset]);
                    tokenOffset++;
                }
                index++;
            }
            randomCycle++;
        }
        System.out.println("Random Report\nh:" + randomCycle + "/" + index);
    }
    public void shufflePorts() {}
    public void clearBoard(){
        IIterator i = hc.getGridIterator();
        Hex hex;
        while(i.hasNext()){
            hex = i.getNext();
            //hex.setBiome(Tile.OCEAN);
            hex.setBiome(0);
            hex.setToken(0);
        }
    }

    //UTILITY
    public int getColumnOffset(){
        return (hexGridDim.height/2)%2;
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
        this.tokens = tokens;
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

    //STRING METHODS
    public String toString(){
        String str;
        str = "----Board Description----\n" +
              "Size:\t\t(" + ((boardSize == Board.SMALL_BOARD)?"Small":"Large") + ")\n" +
              "Type:\t\t(" + getBoardType() + ")\n" +
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
                              + resourceCounts[Tile.OCEAN] + "o,"
                              + resourceCounts[Tile.UNFLIPPED] + "u)\n" +
              "Ports:\t\t("   + portCounts[Tile.WOOD] + "w,"
                              + portCounts[Tile.SHEEP] + "s,"
                              + portCounts[Tile.HAY] + "h,"
                              + portCounts[Tile.BRICK] + "b,"
                              + portCounts[Tile.ROCK] + "r,"
                              + portCounts[Tile.GOLD] + "g)\n" +
              "Tokens:\t\t(" + getTokenString() + ")";
        return str;
    }
    public String getBoardType(){
        String classname = this.getClass().getSimpleName();
        return classname.substring(0, classname.length() - "Board".length());
    }
    public String getTokenString(){
        StringBuilder sb = new StringBuilder();
        sb.append(tokens[0]);
        for(int i = 1; i < tokens.length; i++){
            sb.append(",");
            sb.append(tokens[i]);
        }
        return sb.toString();
    }
    public String getHexGridString(ICommand command){
        if(command instanceof BiomeCommand) {
            return getHexGridString(command, true);
        }else{
            return getHexGridString(command, false);
        }

    }
    public String getSpiralString(ICommand command){
        StringBuilder sb = new StringBuilder();
        sb.append("---Board Spiral Array---\n");
        for(int i = 0; i < getShuffledHexCount(); i++){
            sb.append("[");
            sb.append(command.get(hc.get(i)));
            sb.append("],");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    public String getHexGridString(ICommand command, boolean singleDigit){
        StringBuilder sb = new StringBuilder();
        sb.append("-".repeat(Math.max(0, hexGridDim.width)));
        sb.append("Board Grid");
        sb.append("-".repeat(Math.max(0, hexGridDim.width)));
        sb.append("\n");
        for(int r = 0; r < hexGridDim.height; r++){
            //possibly make this into its own function later
            if(r%2 != (hexGridDim.height/2)%2){
                sb.append("  ");
            }
            for(int c = 0; c < hexGridDim.width; c++){
                Hex hex = hc.get(r,c);
                if(hex != null){
                    sb.append("[");
                    if(!singleDigit &&
                            command.get(hex) >=0 &&
                            command.get(hex) < 10){
                        sb.append("0");
                    }
                    sb.append((char)command.get(hex));
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
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    //TEST METHODS
    public void testIterator(int row, int col){
        System.out.println("-----Iterator Tests-----");

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
    }

    //ABSTRACT
    public abstract void initHexGridDim();
    public abstract void initTileCounts();
    public abstract void initResourceCounts();
    public abstract void initPortCounts();
    public abstract void initTokens();

    public abstract void placeFixedTiles();
    public abstract void initHexSpiral();
    public abstract void initHexSpiral(int startPosition, int orientation);

    public abstract void testHexSpiral();

    //----------------------------------------
    public static class BoardBuilder{
        private static final int[] numTokensStandardSmall =
                {5,2,6,3,8,10,9,12,11,4,8,10,9,4,5,6,3,11};
        private static final int[] numTokensStandardLarge =
                {2,5,4,6,3,9,8,11,11,10,6,3,8,4,8,10,11,
                        12,10,5,4,9,5,9,12,3,2,6};
        public static int[] getStandardTokens(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return numTokensStandardSmall;
            } else {
                return numTokensStandardLarge;
            }
        }
        private static final int[] resourcesStandardSmall =
                {4,4,4,3,3,0,1,0,0};
        private static final int[] resourcesStandardLarge =
                {6,6,6,5,5,0,2,0,0};
        public static int[] getStandardResources(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return resourcesStandardSmall;
            } else {
                return resourcesStandardLarge;
            }
        }
        private static final int[] portStandardSmall =
                {1,1,1,1,1,4,0,0,0};
        private static final int[] portStandardLarge =
                {1,2,1,1,1,5,0,0,0};
        public static int[] getStandardPorts(int boardSize) {
            if(boardSize == SMALL_BOARD){
                return portStandardSmall;
            } else {
                return portStandardLarge;
            }
        }
    }
}
