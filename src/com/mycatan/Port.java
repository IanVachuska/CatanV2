package com.mycatan;

import javax.xml.crypto.dsig.Transform;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

public class Port extends ResourceTile {
    //STATIC FIELDS
    public static final int LENGTH = 36;

    //Port Strokes
    private static final BasicStroke basicStroke =
            new BasicStroke(6f,BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND,10f);
    private static final BasicStroke selectStroke
            = new BasicStroke(6f,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND,10f, new float[]{8f},4f);


    //FIELDS
    private final Hex hex;
    private boolean flipped;

    private double angle;
    private double angleOffset;

    private final Arc2D.Double arc;
    //local coordinates
    private int width;
    private int height;
    private int offsetX;
    private int offsetY;


    //CONSTRUCTORS
    public Port(Hex hex, int dir) {
        super();
        setOpaque(false);
        this.hex = hex;
        setBiome(ResourceTile.OCEAN);
        int angle = dir+4;
        setAngle((angle*60)%360);
        initPoints();
        arc = new Arc2D.Double((double) -width / 2, (double) -height / 2,
                width, height, 90, -180, Arc2D.PIE);
    }

    /**
     * <p>Set up the points necessary to draw the {@code Port}</p>
     */
    //@Override
    protected void initPoints(){
        width = LENGTH;
        height = LENGTH;

        Rectangle bounds = new Rectangle();
        bounds.width = getPreScaleWidth();
        bounds.height = getPreScaleHeight();
        bounds.x = -bounds.width/2;
        bounds.y = -bounds.height/2;


        initOffset();
        bounds.translate(offsetX, offsetY);
        setBounds(bounds);
    }

    public AffineTransform initTransform(Graphics2D g2d){
        AffineTransform transform = super.initTransform(g2d);
        transform.rotate(angle, 0,0);
        return transform;
    }

    /**
     * <p>Displays extra information on the port to debug errors and aid in development.</p>
     * @param g2d the {@code Graphics2D} object
     */
    //@Override
    protected void displayDebugInfo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(getDebugFont());
        int textHeight = g2d.getFontMetrics().getHeight() - 1;
        String s = Integer.toString(getId());
        //String s = Integer.toString(getAngle());
        int textWidth = g2d.getFontMetrics().stringWidth(s);


        AffineTransform transform = g2d.getTransform();
        transform.translate((float)textHeight,(float)-textHeight/2);
        transform.rotate(-angle, (float)-textHeight/3,(float)textHeight/2);
        transform.rotate(Math.toRadians(angleOffset), (float)-textHeight/3,(float)textHeight/2);
        g2d.setTransform(transform);

        g2d.drawString(s, (getPreScaleWidth()-textWidth-60)/2, (getPreScaleHeight()-30)/2);
    }

    /**
     * <p>Draws the port and sets its color to match the value of {@code biome}.
     * <p>If the port is unflipped, the port's true biome
     * will not be revelled until it is flipped</p>
     * @param g2d the {@code Graphics2D} object
     */
    @Override
    public void draw(Graphics2D g2d) {
        int biome = getBiome();
        if(biome == ResourceTile.OCEAN && !isDebug()){
            return;
        }
        g2d.setColor(Color.BLACK);
        g2d.draw(arc);

        Color portColor = getBiomeColor();
        if(biome != ResourceTile.OCEAN && !isFlipped() && !isDebug()){
            portColor = getBiomeColor(ResourceTile.DESERT);
        }
        g2d.setColor(portColor);
        g2d.fill(arc);

        if(isDebug()){
            displayDebugInfo(g2d);
        }
    }

    /**
     * <p>Sets the horizontal and vertical offsets based on the current angle</p>
     */
    private void initOffset(){
        offsetX = (int)(Math.round((float)hex.getPreScaleWidth()/2) * Math.cos(angle));
        offsetY = (int)(Math.round((float)hex.getPreScaleHeight()/2) * Math.sin(angle));
        int xo = 2;
        int yo = 4;
        int ao = 30;
        switch(getAngle()){
            case 0:
                offsetX += xo;
                break;
            case 60:
                offsetX += xo;
                offsetY -= yo;
                angleOffset = -ao;
                break;
            case 120:
                offsetX -= xo;
                offsetY -= yo;
                angleOffset = ao;
                break;
            case 180:
                offsetX -= xo;
                break;
            case 240:
                offsetX -= xo;
                offsetY += yo;
                angleOffset = -ao;
                break;
            case 300:
                offsetX += xo;
                offsetY += yo;
                angleOffset = ao;
                break;
        }
    }

    //SETTERS

    /**
     * <p>Sets the {@code angle} field which represents how much the port will be rotated by in radians</p>
     * <p>It is important to note that the parameter argument is in degrees,
     * however the value gets converted to radians before {@code angle} field is assigned</p>
     * @param degrees the amount of rotation in degrees.
     */
    public void setAngle(int degrees){
        angle = Math.toRadians(degrees%360);
    }


    /**
     * Sets the {@code flipped} field to the argument value
     * @param flipped the new flipped status of the port
     */
    public void flip(boolean flipped){
        this.flipped = flipped;
    }

    //GETTERS

    /**
     * @return the {@code angle} field converted to degrees
     */
    public int getAngle(){
        return (int)Math.round(Math.toDegrees(angle))%360;
    }

    /**
     * @return the port's parent {@code hex}
     */
    public Hex getHex(){
        return hex;
    }

    /**
     * @return the current flipped status of the port
     */
    public boolean isFlipped(){
        return flipped;
    }

    /**
     * @return the original width of the port before any resizing
     */
    @Override
    public int getPreScaleWidth(){
        return width + 2 * (int)getStroke().getLineWidth();
    }

    /**
     * @return the original height of the port before any resizing
     */
    @Override
    public int getPreScaleHeight(){
        return height + 2 * (int)getStroke().getLineWidth();
    }

    @Override
    int getGridRow() {
        return hex.getGridRow();
    }

    @Override
    int getGridColumn() {
        return hex.getGridColumn();
    }

    @Override
    int getWorldX() {
        return hex.getWorldX() + (hex.getPreScaleWidth() - getPreScaleWidth())/2 + getXOffset();
    }

    @Override
    int getWorldY() {
        return hex.getWorldY() + (hex.getPreScaleHeight() - getPreScaleHeight())/2 + getYOffset();
    }

    /**
     * @return the horizontal offset of the port compared to its parent hex
     */
    public int getXOffset() {
        return offsetX;
    }

    /**
     * @return the vertical offset of the port compared to its parent hex
     */
    public int getYOffset() {
        return offsetY;
    }

    @Override
    public void select(){
        setStroke(selectStroke);
        revalidate();
        repaint();
    }

    @Override
    public void deselect() {
        setStroke(basicStroke);
        revalidate();
        repaint();
    }

    public boolean contains(int ptrX, int ptrY){
        if(!super.contains(ptrX,ptrY)){
            return false;
        }
        int radius = getWidth()/2;
        int distance = (int)Math.round(Math.sqrt(Math.pow(ptrX-radius,2) + Math.pow(ptrY-radius,2)));
        boolean inCircle = distance <= radius - 6;
        if(!inCircle){
            return false;
        }
        //System.out.println("ptrX: "+ptrX+" ptrY: "+ptrY);
        //System.out.println("radius: "+radius +" distance: "+distance);

        int w2 = radius + (int)getStroke().getLineWidth() - ptrX;
        int dy = (int) Math.round(w2 * Math.sin(Math.toRadians(angle)));
        return switch (getAngle()) {
            case 0 -> ptrX >= radius;
            case 60, 120 -> ptrY >= radius + dy;
            case 180 -> ptrX <= radius;
            case 240, 300 -> ptrY <= radius + dy;
            default -> false;
        };
    }
}
