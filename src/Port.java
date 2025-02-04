import java.awt.*;
import java.awt.geom.AffineTransform;

public class Port extends Tile {
    //STATIC FIELDS
    public static final int LENGTH = 36;

    //FIELDS
    private final Hex hex;
    private boolean flipped;

    private double angle;
    private double angleOffset;

    //local coordinates
    private int width;
    private int height;
    private int shiftX;
    private int shiftY;


    //CONSTRUCTORS
    public Port(Hex hex, int dir) {
        super();
        setOpaque(false);
        this.hex = hex;
        setBiome(OCEAN);
        int angle = dir+4;
        setAngle((angle*60)%360);
        initPoints();
        setGridLocation(hex.getGridRow(), hex.getGridColumn());//Not Location
    }

    /**
     * <p>Set up the points necessary to draw the {@code Port}</p>
     */
    @Override
    protected void initPoints(){
        width = LENGTH;
        height = LENGTH;

        Rectangle bounds = new Rectangle();
        bounds.width = getTileWidth();
        bounds.height = getTileHeight();
        bounds.x = -bounds.width/2;
        bounds.y = -bounds.height/2;


        initHexShift();
        bounds.translate(shiftX,shiftY);
        setBounds(bounds);
    }

    /**
     * <p>Paints the port. Do not call this function directly.</p>
     * @param g the {@code Graphics} object
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(getStroke());
        if(isDebug()){
            drawBoundingBox(g2d);
        }

        AffineTransform transform = g2d.getTransform();
        transform.concatenate(getScale());
        transform.translate((float) getTileWidth()/2, (float) getTileHeight()/2);
        transform.rotate(angle, 0,0);
        g2d.setTransform(transform);


        draw(g2d);
        if(isDebug()) {
            displayDebugInfo(g2d);
        }


    }

    /**
     * <p>Displays extra information on the port to debug errors and aid in development.</p>
     * @param g2d the {@code Graphics2D} object
     */
    @Override
    protected void displayDebugInfo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(getDebugFont());

        String s = Integer.toString(getId());
        //String s = Integer.toString(getAngle());
        int textWidth = g2d.getFontMetrics().stringWidth(s);

        AffineTransform transform = g2d.getTransform();
        transform.translate(12,-6);
        transform.rotate(-angle, -4,6);
        transform.rotate(Math.toRadians(angleOffset), -4,6);
        g2d.setTransform(transform);

        g2d.drawString(s, (getTileWidth()-textWidth-60)/2, (getTileHeight()-30)/2);
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
        if(biome == Tile.OCEAN && !isDebug()){
            return;
        }
        int strokeOffset = (int)getStroke().getLineWidth();
        int y = (height+strokeOffset)/2;

        g2d.setColor(Color.BLACK);
        g2d.drawArc(-width/2,-height/2,width,height, 90, -180);
        g2d.drawLine(0,-y,0,y);

        Color portColor = getBiomeColor();
        if(biome != Tile.OCEAN && !isFlipped() && !isDebug()){
            portColor = Tile.getBiomeColor(Tile.DESERT);
        }
        g2d.setColor(portColor);
        g2d.fillArc(-width/2,-height/2,width,height, 90, -180);
    }

    /**
     * <p>Sets the horizontal and vertical shifts based on the current angle</p>
     */
    private void initHexShift(){
        shiftX = (int)(Math.round((float)hex.getTileWidth()/2) * Math.cos(angle));
        shiftY = (int)(Math.round((float)hex.getTileHeight()/2) * Math.sin(angle));
        int xo = 2;
        int yo = 4;
        int ao = 30;
        switch(getAngle()){
            case 0:
                shiftX += xo;
                break;
            case 60:
                shiftX += xo;
                shiftY -= yo;
                angleOffset = -ao;
                break;
            case 120:
                shiftX -= xo;
                shiftY -= yo;
                angleOffset = ao;
                break;
            case 180:
                shiftX -= xo;
                break;
            case 240:
                shiftX -= xo;
                shiftY += yo;
                angleOffset = -ao;
                break;
            case 300:
                shiftX += xo;
                shiftY += yo;
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
    public void setFlipped(boolean flipped){
        this.flipped = flipped;
    }

    //GETTERS

    /**
     * @return the {@code angle} field converted to degrees
     */
    public int getAngle(){
        return (int)Math.round(Math.toDegrees(angle));
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
    public int getTileWidth(){
        return width + 2 * (int)getStroke().getLineWidth();
    }

    /**
     * @return the original height of the port before any resizing
     */
    @Override
    public int getTileHeight(){
        return height + 2 * (int)getStroke().getLineWidth();
    }

    /**
     * @return the horizontal offset of the port compared to its parent hex
     */
    public int getXOffset() {
        return shiftX;
    }

    /**
     * @return the vertical offset of the port compared to its parent hex
     */
    public int getYOffset() {
        return shiftY;
    }
}
