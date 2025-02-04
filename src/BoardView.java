import javax.swing.*;
import java.awt.*;

public class BoardView extends JLayeredPane {
    private final Dimension ws;
    private final Dimension origin;
    private final Board board;

    public BoardView(Board board) {
        super();
        this.board = board;
        this.ws = new Dimension();
        this.origin = new Dimension(30,15);

        this.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
        //this.setBackground(Color.BLACK);
        this.setBackground(Tile.getBiomeColor(Tile.OCEAN));
        this.setOpaque(true);
    }


    /**
     * <p>Add {@code Hex} and {@code Port} tiles to the {@code ContentPane}
     * using the world coordinates and default size scaling.</p>
     * <p>Then use {@code resize(double)} to transform world coordinates to window coordinates. </p>
     */
    public void displayBoard(){
        placeHexes();
        placePorts();
        this.setVisible(true);
    }


    /**
     * <p>Iterates over each {@code Hex} in the {@code HexCollection}
     * and adds it to the {@code ContentPane}.</p>
     * <p>Initialize the bounds and location of each hex.</p>
     * <p>Initializes the board's world size</p>
     */
    private void placeHexes(){
        IIterator<Hex> i = board.getHexCollection().getGridIterator();

        Hex hex = i.getNext();
        int height = hex.getTileHeight() - (hex.getTileHeight() - Hex.LENGTH)/2;
        int width = hex.getTileWidth();
        //SET WORLDSIZE
        ws.height = height * board.getHexGridDim().height + hex.getTileHeight()/2 + 2*origin.height + 4;
        ws.width = width * board.getHexGridDim().width + 2*origin.width;
        i.reset();
        while(i.hasNext()){
            hex = i.getNext();
            int o = hex.getGridRow()%2;
            int x = hex.getGridColumn()*width
                    + (o != board.getColumnOffset()?width/2:0);
            int y = hex.getGridRow()*height;

            hex.setWorldLocation(x  + origin.width,y  + origin.height);
            hex.setBounds(hex.getWorldX(), hex.getWorldY(), width, height);
            add(hex);
        }
    }


    /**
     * <p>Iterates over each {@code Port} in the {@code PortCollection}
     * and adds it to the {@code ContentPane}.</p>
     * <p>Initialize the bounds and location of each port.</p>
     */
    public void placePorts(){
        HexCollection hc = board.getHexCollection();
        IIterator<Port> i = board.getPortCollection().getAllPortIterator();
        Port port;
        while(i.hasNext()){
            port = i.getNext();
            Hex hex = hc.get(port);
            Point hexPoint = hex.getLocation();

            hexPoint.x += hex.getTileWidth()/2 - port.getTileWidth()/2 + port.getXOffset();
            hexPoint.y += hex.getTileHeight()/2 - port.getTileHeight()/2 + port.getYOffset();

            Rectangle bounds = port.getBounds();
            bounds.translate(hexPoint.x, hexPoint.y);

            port.setWorldLocation(hexPoint.x,hexPoint.y);
            port.setBounds(bounds);

            add(port, JLayeredPane.POPUP_LAYER);
        }
    }


    /**
     * <p>Remove all ports from the {@code ContentPane}.</p>
     */
    public void removePorts() {
        IIterator<Port> i = board.getPortCollection().getAllPortIterator();
        Port port;
        while (i.hasNext()) {
            port = i.getNext();
            port.setVisible(false);
            this.remove(port);
        }
    }


    /**
     * @return the {@code Dimension} object representing the world size
     */
    public Dimension getWorldSize(){
        return ws;
    }

    //RESIZE

    /**
     * <p>Resize and relocate each tile on the board to match the window/world size ratio.</p>
     * @param size the new scaling factor for the tiles
     */
    public void resize(double size){
        resizeHexes(size);
        resizePorts(size);
    }


    /**
     * <p>Resize and relocate each hex on the board to match the window/world size ratio.</p>
     * @param size the new scaling factor for the hexes
     */
    private void resizeHexes(double size){
        IIterator<Hex> i = board.getHexCollection().getGridIterator();
        while(i.hasNext()) {
           resizeTile(i.getNext(), size);
        }
    }


    /**
     * <p>Resize and relocate each port on the board to match the window/world size ratio</p>
     * @param size the new scaling factor for the ports
     */
    private void resizePorts(double size){
        IIterator<Port> i = board.getPortCollection().getAllPortIterator();
        while(i.hasNext()) {
            resizeTile(i.getNext(), size);
        }
    }


    /**
     * <p>Resize and relocate the {@code tile}.</p>
     * @param tile the tile to be resized
     * @param size the new scaling factor for the tile
     */
    private void resizeTile(Tile tile, double size){
        tile.setScale(size);
        int x = (int)(size*tile.getWorldX());
        int y = (int)(size*tile.getWorldY());
        int width = (int)(size*tile.getTileWidth());
        int height = (int)(size*tile.getTileHeight());
        tile.setBounds(x,y,width,height);
        tile.setLocation(x,y);
    }
}
