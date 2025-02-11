package com.mycatan;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Hex extends Tile {
    //STATIC FIELDS
    public static final int SIDES = 6;
    public static final int LENGTH = 50;
    private static final Color TOKEN_COLOR = new Color(200, 200, 180);

    //FIELDS
    private int token;
    //local coordinates
    private final int[] xPoints;
    private final int[] yPoints;
    private final Polygon polygon;

    private final Font tokenFont;

    //CONSTRUCTORS
    public Hex() {
        this(0,0,false);
    }
    public Hex(int row, int col) {
        this(row, col, false);
    }
    public Hex(int row, int col, boolean debug) {
        super();
        xPoints = new int[SIDES];
        yPoints = new int[SIDES];
        initPoints();
        polygon = new Polygon(xPoints, yPoints, SIDES);
        setGridLocation(row, col);
        token = -1;
        setType(SHUFFLED);
        setDebug(debug);

        tokenFont = new Font("Arial", Font.BOLD, 18);
        setOpaque(false);
    }

    /**
     * <p>Set up the points necessary to draw the {@code Hex}</p>
     */
    @Override
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
        //this.setBounds(-getWidth()/2,-getHeight()/2,getWidth(),getHeight());
    }

    /**
     * <p>Paints the hex. Do not call this function directly.</p>
     * @param g the {@code Graphics} object
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(getStroke());
        if(isDebug()) {
            //drawBoundingBox(g2d);
        }

        AffineTransform transform = g2d.getTransform();
        transform.concatenate(getScale());
        g2d.setTransform(transform);

        draw(g2d);

        if(isDebug()) {
            displayDebugInfo(g2d);
        }
    }

    /**
     * <p>Displays extra information on each hex to debug errors and aid in development.</p>
     * @param g2d the {@code Graphics2D} object
     */
    @Override
    protected void displayDebugInfo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(getDebugFont());
        FontMetrics fm = g2d.getFontMetrics();
        int textHeight = fm.getHeight()-4;
        int textWidth;
        int centerOffset = 28;

        //Rows/Columns (Grid)
        String s = getGridRow() + "," + getGridColumn();
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                (getTileWidth() - textWidth)/2,
                (getTileHeight() + textHeight + centerOffset)/2);

        //ID (spiral)
        s = Integer.toString(getId());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                (getTileWidth() - textWidth)/2,
                (getTileHeight() + textHeight - centerOffset)/2);

        //Type
        s = String.valueOf(getTypeChar());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                (getTileWidth() - textWidth - centerOffset)/2,
                (getTileHeight() + textHeight)/2);

        //Biome
        s = String.valueOf(getBiomeChar());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                (getTileWidth() - textWidth + centerOffset)/2,
                (getTileHeight() + textHeight)/2);
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

        g2d.setColor(Color.BLACK);
        if(token > 0 && (getType() != UNFLIPPED_TYPE || isDebug())) {
            drawToken(g2d);
        }
    }

    /**
     * <p>Draws the token and its value onto the hex</p>
     * @param g2d the {@code Graphics2d} object
     */
    private void drawToken(Graphics2D g2d){
        //Draw and fill the token
        int arcRadius = 20;
        int x = getTileWidth() / 2 - arcRadius;
        int y = getTileHeight() / 2 - arcRadius;
        int arcDiameter = arcRadius * 2;
        g2d.drawArc(x,y,arcDiameter,arcDiameter, 0,360);
        g2d.setColor(TOKEN_COLOR);
        g2d.fillArc(x,y,arcDiameter,arcDiameter, 0,360);

        //Get font data
        g2d.setFont(tokenFont);
        String tokenString = Integer.toString(getToken());
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(tokenString);
        int textHeight = fm.getHeight() - 8;

        //Draw the tokens number value
        g2d.setColor(Color.BLACK);
        g2d.drawString(tokenString,
                (getTileWidth() - textWidth) / 2,
                (getTileHeight() + textHeight) / 2);
    }

    //SETTERS

    /**
     * <p>Set the value of the number {@code token} to any {2,3,4,5,6,8,9,10,11,12}.
     * Any other values will not be set and an error message will be thrown.</p>
     * @param token the new token value
     */
    public void setToken(int token){
        if(token >= 0 && token <= 12 && token != 7) {
            this.token = token;
        }
        else{
            System.err.println("Invalid token value: " + token);
        }
    }

    //GETTERS

    /**
     * @return the number {@code token} value
     */
    public int getToken(){
        return token;
    }

    /**
     * @return the original width of the hex before any resizing
     */
    @Override
    public int getTileWidth() {
        return xPoints[1]-xPoints[4] + (int)getStroke().getLineWidth();
    }

    /**
     * @return the original height of the hex before any resizing
     */
    @Override
    public int getTileHeight() {
        return yPoints[0]-yPoints[3] + (int)(getStroke().getLineWidth());
    }

    //STRING METHODS
    public String toString(){
        return super.toString() +
                ", Token: " + getToken();
    }
}
