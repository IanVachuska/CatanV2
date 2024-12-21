import javax.swing.*;

public class Catan extends JFrame {//CONTROLLER
    private Board board;//MODEL(gw)
    private BoardView bv;//VIEW(mv)

    //CONSTRUCTORS
    public Catan() {
        this("Standard", Board.LARGE_BOARD);
    }
    public Catan(String type, int size) {
        switch (type) {
            case "Standard":
                board = new StandardBoard(size);
                break;
            case "Seafarers":
                board = new SeafarersBoard(size);
                break;
            case "FogIsland":
                board = new FogIslandBoard(size);
        }
        bv = new BoardView();
    }

    //CONTROL METHODS
    public void reshuffleBoard() {}
    public void reshuffleResources(){
        board.initResourceCounts();
        board.placeFixedTiles();
        board.shuffleHexs();
    }
    public void reshuffleTokens(){}
    public void reshufflePorts(){}
    public void newBoard() {}

    //TEST METHOD
    public void runTests(){
        System.out.println(board.toString());
        //board.testIterator(3,3);
        //System.out.println(board.getSpiralString(new BiomeCommand()));
        System.out.println(board.getHexGridString(new BiomeCommand()));
        //board.testBoardSpiral();
        board.clearBoard();
    }//
}
