package com.mycatan;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

public class Token extends Tile {
    //STATIC FIELDS
    private static final int LENGTH = 20;
    private static final Color TOKEN_COLOR = new Color(200, 200, 180);
    //Hex Strokes
    private static final BasicStroke basicStroke
            = new BasicStroke(8f,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,10f);
    private static final BasicStroke selectStroke
            = new BasicStroke(8f,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,10f, new float[]{8f},2f);

    //FIELDS
    private int value;
    private Hex parentHex;
    private final Font tokenFont;

    private final Arc2D.Double arc;

    //CONSTRUCTOR
    public Token(int value){
        this(value, false, null);
    }
    public Token(int value,  boolean debug) {
        this(value, false, null);
    }
    public Token(int value, boolean debug,  Hex parentHex) {
        this.value = value;
        this.parentHex = parentHex;
        setDebug(debug);


        arc = new Arc2D.Double((double) -LENGTH, (double)-LENGTH,
                (double)LENGTH*2, (double)LENGTH*2, 0, 360, Arc2D.PIE);
        tokenFont = new Font("Arial", Font.BOLD, 18);
        setOpaque(false);
    }


    /*
    * WorldLocation
    */

    //METHODS
    public int valueOf() {
        return value;
    }
    /**
     * <p>Set the value of the number {@code token} to any {2,3,4,5,6,8,9,10,11,12}.
     * Any other values will not be set and an error message will be thrown.</p>
     * @param token the new token value
     */
    public void set(int token){
        if(token >= 0 && token <= 12 && token != 7) {
            this.value = token;
        }
        else{
            System.err.println("Invalid token value: " + token);
        }
    }

    /**
     * @return the token's parent {@code hex}
     */
    public Hex getParentHex() {
        return parentHex;
    }

    /**
     * set the token's parent {@code hex}
     */
    public void setParentHex(Hex parentHex) {
        this.parentHex = parentHex;
    }

    /**
     * <p>Draws the token and its value onto the hex</p>
     * @param g2d the {@code Graphics2d} object
     */
    @Override
    public void draw(Graphics2D g2d) {
        if(value != 0) {
            //Draw and fill the token
            g2d.draw(arc);
            g2d.setColor(TOKEN_COLOR);
            g2d.fill(arc);

            //Get font data
            g2d.setFont(tokenFont);
            String tokenString = Integer.toString(valueOf());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(tokenString);
            int textHeight = fm.getHeight() - 8;

            //Draw the tokens number value
            g2d.setColor(Color.BLACK);
            g2d.drawString(tokenString,
                    -textWidth / 2,
                    textHeight / 2);
        }
        if(isDebug()){
            displayDebugInfo(g2d, parentHex);
        }
    }

    @Override
    public int getPreScaleWidth() {
        return LENGTH*2 + (int)getStroke().getLineWidth();
    }

    @Override
    public int getPreScaleHeight() {
        return getPreScaleWidth();
    }
    @Override
    public boolean isDebug() {
        if(parentHex != null){
            return parentHex.isDebug();
        }
        return false;
    }

    /**
     * @return the tile's id.
     */
    public int getId(){
        return parentHex.getId();
    }

    @Override
    public int getGridRow() {
        return parentHex.getGridRow();
    }

    @Override
    public int getGridColumn() {
        return parentHex.getGridColumn();
    }

    @Override
    public int getWorldX() {
        if(parentHex != null) {
            return parentHex.getWorldX() + (parentHex.getPreScaleWidth() - getPreScaleWidth()) / 2;
        }
        return 0;
    }

    @Override
    public int getWorldY() {
        if(parentHex != null) {
            return parentHex.getWorldY() + (parentHex.getPreScaleHeight() - getPreScaleHeight()) / 2;
        }
        return 0;
    }


    @Override
    public void select() {
        setStroke(selectStroke);
    }

    @Override
    public void deselect() {
        setStroke(basicStroke);
    }

    @Override
    public void swap(ISelectable other) {
        Token otherToken = (Token) other;

        int tempTokenValue = otherToken.value;
        otherToken.value = this.value;
        this.value = tempTokenValue;
        System.out.println("kkkkkkk");
    }
    @Override
    public  boolean contains(int ptrX, int ptrY) {
        if(!super.contains(ptrX,ptrY)){
            return false;
        }
        int width = getWidth()/2;
        int height = getHeight()/2;
        int distance = (int)Math.round(Math.sqrt(Math.pow(ptrX-width,2) + Math.pow(ptrY-height,2)));
        return distance <= width;
    }
}
