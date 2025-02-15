package com.mycatan;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Hex extends ResourceTile {
    //STATIC FIELDS
    public static final int SIDES = 6;
    public static final int LENGTH = 50;
    private static final Color TOKEN_COLOR = new Color(200, 200, 180);//86

    //Type Encodings
    public static final int SHUFFLED = 0;
    public static final int FIXED = 1;
    public static final int UNFLIPPED_TYPE = 2;
    private static final char[] typeChars = {'S','F','U'};

    //Hex Strokes
    private static final BasicStroke basicStroke
            = new BasicStroke(8f,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,10f);
    private static final BasicStroke selectStroke
            = new BasicStroke(8f,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,10f, new float[]{8f},2f);


    //FIELDS
    private int type;
    private Token token;

    //grid
    private final Point gridLocation;
    private final Point worldLocation;

    //local coordinates
    private final int[] xPoints;
    private final int[] yPoints;
    private final Polygon polygon;

    //CONSTRUCTORS
    public Hex() {
        this(0,0,false);
    }
    public Hex(int row, int col) {
        this(row, col, false);
    }
    public Hex(int row, int col, boolean debug) {
        super();
        this.gridLocation = new Point(0,0);
        this.worldLocation = new Point(0,0);
        xPoints = new int[SIDES];
        yPoints = new int[SIDES];
        initPoints();
        polygon = new Polygon(xPoints, yPoints, SIDES);
        setGridLocation(row, col);
        token = new Token(0);
        setType(SHUFFLED);
        setDebug(debug);

        setOpaque(false);
    }

    /**
     * <p>Set up the points necessary to draw the {@code Hex}</p>
     */
    //@Override
    protected void initPoints() {
        int HORIZONTAL_OFFSET = -3;
        int VERTICAL_OFFSET = 4;
        for (int i = 0; i < SIDES; i++) {
            xPoints[i] = (int)Math.round(LENGTH + LENGTH * Math.sin(i * 2 * Math.PI / SIDES));
            yPoints[i] = (int)Math.round(LENGTH + LENGTH * Math.cos(i * 2 * Math.PI / SIDES));
            xPoints[i] += HORIZONTAL_OFFSET;
            yPoints[i] += VERTICAL_OFFSET;
            //System.out.println(xPoints[i] + "," + yPoints[i]);
        }
        int width = getPreScaleWidth()/2;
        int height = getPreScaleHeight()/2;
        for (int i = 0; i < SIDES; i++) {
            xPoints[i] -= width;
            yPoints[i] -= height;
            //System.out.println(xPoints[i] + "," + yPoints[i]);
        }
        //this.setBounds(-getWidth()/2,-getHeight()/2,getWidth(),getHeight());
    }

    /**
     * <p>Draws the hex and sets its color to match the value of {@code biome}.
     * If the hex has a valid {@code token} value, draw it </p>
     * <p>If the hex is unflipped, the hex's true biome
     * will not be revelled until it is flipped</p>
     * @param g2d the {@code Graphics2D} object
     */
    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(polygon);

        g2d.setColor(getBiomeColor());
        g2d.fill(polygon);

        if(isDebug()) {
            displayDebugInfo(g2d, this);
        }
    }


    //SETTERS
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

    public void setToken(Token token){
        this.token.setParentHex(null);
        this.token = token;
        if(token != null){
            token.setParentHex(this);
        }
    }

    /**
     * <p>Set the value of the number {@code token} to any {2,3,4,5,6,8,9,10,11,12}.
     * Any other values will not be set and an error message will be thrown.</p>
     * @param token the new token value
     */
    public void setTokenValue(int token){
        if(this.token == null) {
            return;
        }
        if(token >= 0 && token <= 12 && token != 7) {
            this.token.set(token);
        }
        else{
            System.err.println("Invalid token value: " + token);
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
        if(type >= Hex.SHUFFLED && type <= Hex.UNFLIPPED_TYPE) {
            this.type = type;
        }
        else{
            System.err.println("Invalid type value: " + type);
        }
    }

    public void select(){
        setStroke(selectStroke);
    }
    public void deselect(){
        setStroke(basicStroke);
    }



    //GETTERS
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
     * @return the number {@code token} value
     */
    public Token getToken(){
        return token;
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
     * @return the original width of the hex before any resizing
     */
    @Override
    public int getPreScaleWidth() {
        return xPoints[1]- xPoints[4] + (int)getStroke().getLineWidth();
    }

    /**
     * @return the original height of the hex before any resizing
     */
    @Override
    public int getPreScaleHeight() {
        return yPoints[0]- yPoints[3] + (int)(getStroke().getLineWidth());
    }

    //STRING METHODS
    public String toString(){
        return super.toString() +
                "GridLoc: (" + getGridRow() + "," + getGridColumn() + "), " +
                ", Token: " + getToken();
    }

    public boolean contains(int ptrX, int ptrY){
        if(!super.contains(ptrX,ptrY)){
            return false;
        }
        int y1 = 0;
        int y2 = getHeight();

        //small height
        int heightOffset = y2 / 4;
        boolean insideSmallY = (ptrY >= y1 + heightOffset && ptrY <= y2 - heightOffset);
        if(insideSmallY){
            //System.out.println("ptrX: " + ptrX + " ptrY: " + ptrY);
            //System.out.println("insideSmallY (" + ptrX + "," + ptrY + ")");
            return true;
        }

        //diagonal
        int width = getWidth()/2;
        int dx = Math.abs(width - ptrX);
        int w2 = width - dx;
        int dy = (int) Math.round(w2 * Math.tan(Math.toRadians(30)));

        return ptrY >= y1 + heightOffset - dy && ptrY <= y2 - heightOffset + dy;
    }
}
