package com.mycatan;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public abstract class Tile extends JButton implements IDrawable, ISelectable{
    //STATIC FIELDS


    //FIELDS
    private final AffineTransform size;

    private BasicStroke stroke;
    private final Font debugFont;


    //CONSTRUCTORS
    public Tile() {
        super();
        this.size = new AffineTransform();
        debugFont = new Font("Arial", Font.BOLD, 10);
        deselect();
    }

    /**
     * <p>Displays extra information on each hex to debug errors and aid in development.</p>
     * @param g2d the {@code Graphics2D} object
     */
    protected void displayDebugInfo(Graphics2D g2d, Hex hex) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(getDebugFont());
        FontMetrics fm = g2d.getFontMetrics();
        int textHeight = fm.getHeight()-5;
        int textWidth;
        int centerOffset = 29;

        //Rows/Columns (Grid)
        String s = hex.getGridRow() + "," + hex.getGridColumn();
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                -textWidth/2,
                (textHeight + centerOffset)/2);

        //ID (spiral)
        s = Integer.toString(hex.getId());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                -textWidth/2,
                (textHeight - centerOffset)/2);

        //Type
        s = String.valueOf(hex.getTypeChar());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                -(textWidth - centerOffset)/2,
                textHeight/2);

        //Biome
        s = String.valueOf(hex.getBiomeChar());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s,
                -(textWidth + centerOffset)/2,
                textHeight/2);
    }


    /**
     * <p>Paints the hex. Do not call this function directly.</p>
     * @param g the {@code Graphics} object
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(getStroke());
        g2d.setTransform(initTransform(g2d));
        draw(g2d);
    }

    public AffineTransform initTransform(Graphics2D g2d){
        AffineTransform transform = g2d.getTransform();
        transform.concatenate(getScale());
        transform.translate((float) getPreScaleWidth()/2, (float) getPreScaleHeight()/2);
        return transform;
    }

    //SETTERS
    /**
     * <p>Sets the {@code debug} flag.</p>
     * @param debug true enables the tile's debug display
     */
    public void setDebug(boolean debug){
        //this.debug = debug;
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
     * @param stroke the new stroke
     */
    public void setStroke(BasicStroke stroke) {
        this.stroke = stroke;
        revalidate();
        repaint();
    }

    //GETTERS


    /**
     * @return the current state of the {@code debug} flag
     */
    public boolean isDebug(){
        //return debug;
        return Board.isDebug();
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
    protected void drawBoundingBox(Graphics2D g2d) {
        Color oldColor = g2d.getColor();
        g2d.setColor(Color.red);
        Rectangle b = getBounds();
        b.x = 0;
        b.y = 0;
        g2d.draw(b);
        g2d.setColor(oldColor);
    }


    //STRING METHODS
    @Override
    public String toString(){
        return  "---" + getClass().getSimpleName()+ "String---\n" +
                "ID: " + getId() + ", ";
    }

    //ABSTRACT METHODS
    /*
     * <p>Set up the points necessary to draw the {@code Tile}</p>
     */
    //abstract protected void initPoints();
    /*
     * <p>Paints the Tile. Do not call this function directly.</p>
     * @param g the {@code Graphics} object
     */
    //abstract protected void paintComponent(Graphics g);
    /*
     * <p>Displays extra information on the tile to debug errors and aid in development.</p>
     * @param g2d the {@code Graphics2D} object
     */
    //abstract protected void displayDebugInfo(Graphics2D g2d);

    /**
     * @return the original width of the tile before any resizing
     */
    abstract int getPreScaleWidth();

    /**
     * @return the original height of the tile before any resizing
     */
    abstract int getPreScaleHeight();

    abstract int getId();

    abstract int getGridRow();

    abstract int getGridColumn();

    abstract int getWorldX();

    abstract int getWorldY();
}
