import javax.swing.*;
import java.util.StringTokenizer;

public class Catan extends JFrame {//CONTROLLER
    private Board board;//MODEL(gw)
    private BoardView bv;//VIEW(mv)

    //CONSTRUCTORS
    public Catan() {
        this(Board.STANDARD_BOARD, Board.SMALL_BOARD, 0);
    }
    public Catan(String type) {
        System.out.println("CATAN");
        StringTokenizer st = new StringTokenizer(type.toLowerCase());
        int boardType=0, boardSize=0, boardFlags=0;
        if(st.hasMoreTokens()) {
            boardType = parseBoardType(st.nextToken());
        }
        if(st.hasMoreTokens()) {
            boardSize = parseBoardSize(st.nextToken());
        }
        if(st.hasMoreTokens()) {
            boardFlags = parseBoardMods(st.nextToken());
        }

        switch (boardType) {
            case Board.STANDARD_BOARD:
                board = new StandardBoard(boardSize, boardFlags);
                break;
            case Board.SEAFARERS_BOARD:
                board = new SeafarersBoard(boardSize, boardFlags);
                break;
            case Board.FOGISLAND_BOARD:
                board = new FogIslandBoard(boardSize, boardFlags);
        }
        this.runTests();
        bv = new BoardView();
    }
    public Catan(int boardType, int boardSize, int boardMods) {
        switch (boardType) {
            case Board.STANDARD_BOARD:
                board = new StandardBoard(boardSize, boardMods);
                break;
            case Board.SEAFARERS_BOARD:
                board = new SeafarersBoard(boardSize, boardMods);
                break;
            case Board.FOGISLAND_BOARD:
                board = new FogIslandBoard(boardSize, boardMods);
        }
        this.runTests();
        bv = new BoardView();
    }
    private int parseBoardType(String type){
        String[] types = {"standard", "seafarers", "fogisland"};
        int[] counts = new int[types.length];
        int min = Math.min(type.length(), types[0].length());
        //min = Math.min(min, types[1].length());
        //min = Math.min(min, types[2].length());
        for(int i = 0; i < min; i++) {
            char val = type.charAt(i);
            for (int j = 0; j < types.length; j++) {
                if (val == types[j].charAt(i)) {
                    counts[j]++;
                }
            }
        }
        int max = Math.max(counts[0], counts[1]);
        max = Math.max(max, counts[2]);
        for(int i = 0; i < types.length; i++) {
            if (counts[i] == max) {
                return i;
            }
        }

        return switch (type.charAt(0)) {
            case 's' -> Board.SMALL_BOARD;
            case 'l' -> Board.LARGE_BOARD;
            default -> 0;
        };
    }
    private int parseBoardSize(String size){
        return switch (size.charAt(0)) {
            case 's' -> Board.SMALL_BOARD;
            case 'l' -> Board.LARGE_BOARD;
            default -> 0;
        };
    }
    private int parseBoardMods(String mods){
        int flag = 0;
        if(mods.charAt(0) != '-'){
            return flag;
        }
        if(mods.contains("d")){
            flag |= Board.DEBUG;
        }
        if(mods.contains("r")){
            flag |= Board.RANDOM;
        }
        return flag;
    }
    //CONTROL METHODS
    public void reshuffleBoard() {
        board.initHexSpiral();
        board.shuffleHexes();
    }
    public void reshuffleResources(){
        board.initHexSpiral();
        board.shuffleHexes(new BiomeCommand());
    }
    public void reshuffleTokens(){
        board.initHexSpiral();
        board.shuffleHexes(new TokenCommand());
    }
    public void reshufflePorts(){}
    public void newBoard() {}

    //TEST METHOD
    public void runTests(){
        //board.testIterator(3,3);
        //board.testHexSpiral();
        //board.testFlags();
        System.out.print(board.toString());
        System.out.print(board.getSpiralString(new BiomeCommand()));
        System.out.println(board.getHexGridString(new TokenCommand()));
        System.out.println(board.getHexGridString(new IdCommand()));
        System.out.println(board.getHexGridString(new BiomeCommand()));
        System.out.println(board.getHexGridString(new TypeCommand()));

        //board.clearBoard();
        System.out.println();
    }//
}
