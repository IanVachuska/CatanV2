package com.mycatan;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public abstract class Tile extends JButton implements IDrawable{
    //STATIC FIELDS
    private static final Color[] biomeColors = {
            new Color(0, 128, 0),//wood
            new Color(128, 255, 0),//sheep
            new Color(255, 255, 0,255),//hay
            new Color(255, 153, 0),//brick
            new Color(153, 153, 153),//rock
            new Color(215, 195, 20),//gold
            new Color(153, 102, 0),//desert
            new Color(0, 120, 255),//ocean
            new Color(255, 255, 255)//unflipped
    };
    //Hex Strokes
    private static final BasicStroke basicHexStroke
            = new BasicStroke(8f,BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,10f);
    private static final BasicStroke selectHexStroke
            = new BasicStroke(8f,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,10f, new float[]{8f},2f);

    //Port Strokes
    private static final BasicStroke basicPortStroke =
            new BasicStroke(6f,BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND,10f);
    private static final BasicStroke selectPortStroke
            = new BasicStroke(6f,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND,10f, new float[]{8f},4f);

    //Resource Encodings
    public static final int WOOD = 0;
    public static final int SHEEP = 1;
    public static final int HAY = 2;
    public static final int BRICK = 3;
    public static final int ROCK = 4;
    public static final int GOLD = 5;
    public static final int DESERT = 6;
    public static final int OCEAN = 7;
    public static final int UNFLIPPED_RESOURCE = 8;
    public static final int RESOURCE_MIN = WOOD;
    public static final int RESOURCE_MAX = OCEAN;
    private static final char[] biomeChars = {'W','S','H','B','R','G','D','O','U'};

    //Type Encodings
    public static final int SHUFFLED = 0;
    public static final int FIXED = 1;
    public static final int UNFLIPPED_TYPE = 2;
    private static final char[] typeChars = {'S','F','U'};




    //FIELDS
    private int biome;
    private int type;
    private int id;
    private boolean debug;

    private final Point gridLocation;
    private final Point worldLocation;
    private final AffineTransform size;

    private BasicStroke stroke;
    private final Font debugFont;


    //CONSTRUCTORS
    public Tile() {
        this.gridLocation = new Point();
        this.worldLocation = new Point();
        this.size = new AffineTransform();

        this.biome = UNFLIPPED_RESOURCE;
        this.id = 0;

        setStrokeSelected(false);
        debugFont = new Font("Arial", Font.BOLD, 10);
    }

    //SETTERS

     /**
     * <p>Sets the {@code biome} field using integer encoding to represent each resource.
     * Invalid biome values will not be set and an error message will be thrown.</p>
     * <p>Setting the biome manually should be done using:</p>
     * <p>Random int values ranging from {@code RESOURCE_MIN} to {@code RESOURCE_MAX}, or</p>
     * <p>Constants {@code WOOD}, {@code SHEEP},{@code HAY}, {@code BRICK},
     * {@code ROCK}, {@code GOLD}, {@code DESERT}, {@code OCEAN}.</p>
     * @param biome the integer encoded resource
     */
    public void setBiome(int biome) {
        if(biome >= Tile.RESOURCE_MIN && biome <= Tile.RESOURCE_MAX) {
            this.biome = biome;
        }
        else{
            System.err.println("Invalid biome value: " + biome);
        }
    }


    /**
     * <p>Sets the {@code type} field using integer encoding to represent each type.
     * Invalid type values will not be set and an error message will be thrown.</p>
     * <p>Setting the type manually should be done using:</p>
     * <p>Constants {@code SHUFFLED}, {@code FIXED},{@code UNFLIPPED_TYPE}.</p>
     * @param type the integer encoded type
     */
    public void setType(int type){
        if(type >= Tile.SHUFFLED && type <= Tile.UNFLIPPED_TYPE) {
            this.type = type;
        }
        else{
            System.err.println("Invalid type value: " + type);
        }
    }


    /**
     * <p>Sets the {@code id} field. A collection of tiles must have id values ranging from 0 to
     * {@code getTotalHexCount()}. Negative id values will not be set and an error message will be thrown.</p>
     * @param id a positive integer representing a tile's unique position in its collection
     */
    public void setId(int id) {
        if(id >= 0) {
            this.id = id;
        }
        else{
            System.err.println("Invalid id value: " + id);
        }
    }


    /**
     * <p>Sets the {@code debug} flag.</p>
     * @param debug true enables the tile's debug display
     */
    public void setDebug(boolean debug){
        this.debug = debug;
    }


     /**
     * <p>Sets the tile's {@code row} and {@code column} location on the HexGrid.
     * A Negative row or column value will not update either value and an error message will be thrown.</p>
     * @param row the tile's row location
     * @param col the tile's column location
     */
    public void setGridLocation(int row, int col){
        if(row >= 0 && col >= 0){
            this.gridLocation.setLocation(col,row);
        }
        else{
            System.err.println("Invalid row/column value: " + row + "," + col);
        }
    }


    /**
     * <p>Sets the tile's {@code x} and {@code y} location in the game world.</p>
     * @param x the tile's x location
     * @param y the tile's y location
     */
    public void setWorldLocation(int x, int y){
        this.worldLocation.setLocation(x,y);
    }


    /**
     * <p>Sets the scale value for the tile's size.</p>
     * @param size the value to be scaled by
     */
    public void setScale(double size){
        this.size.setToIdentity();
        this.size.scale(size,size);
    }


    /**
     * <p>Sets a tile's stroke to one of two values.</p>
     * @param selected true sets stroke to selected, false sets stroke to basic
     */
    public void setStrokeSelected(boolean selected) {
        if (selected) {
            stroke = selectHexStroke;
        }else{
            stroke = basicHexStroke;
        }
        revalidate();
        repaint();
    }


    //GETTERS

    /**
     * @return the integer encoded biome
     */
    public int getBiome() {
        return biome;
    }


    /**
     * @return the char encoded biome
     */
    public char getBiomeChar(){
        return biomeChars[biome];
    }


    /**
     * <p>If the tile is type {@code UNFLIPPED_TYPE}, return unflipped color instead.</p>
     * @return the {@code Color} object representing the biome or unflipped color
     */
    public Color getBiomeColor(){
        if(type==UNFLIPPED_TYPE&&!debug){
            return biomeColors[UNFLIPPED_RESOURCE];
        }
        return biomeColors[biome];
    }


    /**
     * <p>Returns the biome color using the integer encoding.</p>
     * @param biome use constants {@code WOOD}, {@code SHEEP}, {@code HAY},
     * {@code BRICK}, {@code ROCK}, {@code GOLD}, {@code DESERT}, {@code OCEAN},
     *             or {@code UNFLIPPED_RESOURCE} to get the color of an unflipped hex
     * @return the biome color
     */
    public static Color getBiomeColor(int biome){
        if(biome >= Tile.RESOURCE_MIN) {
            return biomeColors[Math.min(biome, UNFLIPPED_RESOURCE)];
        }
        else{
            System.err.println("Invalid biome value: " + biome);
            return biomeColors[UNFLIPPED_RESOURCE];
        }
    }


    /**
     * @return the integer encoded type
     */
    public int getType(){
        return type;
    }


    /**
     * @return the char encoded type
     */
    public char getTypeChar(){
        return typeChars[type];
    }


    /**
     * @return the tile's id.
     */
    public int getId(){
        return id;
    }


    /**
     * @return the current state of the {@code debug} flag
     */
    public boolean isDebug(){
        return debug;
    }


    /**
     * @return the tile's {@code row} location on the HexGrid
     */
    public int getGridRow(){
        return gridLocation.y;
    }


    /**
     *
     * @return the tile's {@code column} location on the HexGrid
     */
    public int getGridColumn(){
        return gridLocation.x;
    }


    /**
     * @return tile's {@code x} location in the game world
     */
    public int getWorldX(){
        return worldLocation.x;
    }


    /**
     * @return tile's {@code y} location in the game world
     */
    public int getWorldY(){
        return worldLocation.y;
    }


    /**
     * @return the {@code AffineTransform} object representing the size change from world to window view
     */
    public AffineTransform getScale(){
        return size;
    }


    /**
     * @return the currently selected stroke value
     */
    public BasicStroke getStroke(){
        return stroke;
    }


    /**
     * @return the currently selected font value
     */
    public Font getDebugFont(){
        return debugFont;
    }


    /**
     * <p>Draw the currently set bounding box around the {@code Tile} object.</p>
     * @param g2d the tile's {@code Graphics2D} object
     */
    protected void drawBoundingBox(Graphics2D g2d){
        Color oldColor = g2d.getColor();
        g2d.setColor(Color.red);
        g2d.draw(getBounds());
        g2d.setColor(oldColor);
    }

    //STRING METHODS
    @Override
    public String toString(){
        return  "---" + getClass().getSimpleName()+ "String---\n" +
                "ID: " + id + ", " +
                "Location: (" + getGridRow() + "," + getGridColumn() + "), " +
                "Biome: " + getBiome();
    }

    //ABSTRACT METHODS
    /**
     * <p>Set up the points necessary to draw the {@code Tile}</p>
     */
    abstract protected void initPoints();
    /**
     * <p>Paints the Tile. Do not call this function directly.</p>
     * @param g the {@code Graphics} object
     */
    @Override
    abstract protected void paintComponent(Graphics g);

    /**
     * <p>Displays extra information on the tile to debug errors and aid in development.</p>
     * @param g2d the {@code Graphics2D} object
     */
    abstract protected void displayDebugInfo(Graphics2D g2d);

    /**
     * @return the original width of the tile before any resizing
     */
    abstract int getTileWidth();

    /**
     * @return the original height of the tile before any resizing
     */
    abstract int getTileHeight();
}
