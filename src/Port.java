import java.awt.*;
import java.awt.geom.AffineTransform;

public class Port extends Tile {
    //STATIC FIELDS
    public static final int SIDES = 3;
    public static final int LENGTH = 36;

    //FIELDS
    private final Hex hex;
    //local coordinates
    private int width;
    private int height;
    private final Rectangle bounds;
    private int shiftX;
    private int shiftY;

    private double angle;
    private double angleOffset;
    private boolean flipped;

    //CONSTRUCTORS
    public Port(Hex hex, int dir) {
        super();
        bounds = new Rectangle();
        setOpaque(false);
        this.hex = hex;
        setBiome(OCEAN);
        setPoints(dir);
        setGridLocation(hex.getGridRow(), hex.getGridColumn());//Not Location
        setTileFont(new Font("Arial", Font.PLAIN, 10));
    }

    //SETTERS
    public void setAngle(int degrees){
        angle = Math.toRadians(degrees%360);
    }
    private void setPoints(int dir){
        //System.out.println("x: " + bounds.x + " y: " + bounds.y);
        width = LENGTH;//*3/4;
        height = LENGTH;

        bounds.width = getTileWidth();
        bounds.height = getTileHeight();
        bounds.x = -getTileWidth()/2;
        bounds.y = -getTileHeight()/2;

        setAngle(dir*60+240);

        shiftX = (int)(Math.round((float)hex.getTileWidth()/2) * Math.cos(angle));
        shiftY = (int)(Math.round((float)hex.getTileHeight()/2) * Math.sin(angle));
        int yo = 4;
        int xo = 2;
        int ao = 30;
        switch(getAngle()){
            case 0:
                shiftX += xo;
                shiftY += 0;
                break;
            case 60:
                shiftX += xo;
                shiftY += -yo;
                angleOffset = -ao;
                break;
            case 120:
                shiftX += -xo;
                shiftY += -yo;
                angleOffset = ao;
                break;
            case 180:
                shiftX += -xo;
                shiftY += 0;
                break;
            case 240:
                shiftX += -xo;
                shiftY += yo;
                angleOffset = -ao;
                break;
            case 300:
                shiftX += xo;
                shiftY += yo;
                angleOffset = ao;
                break;
        }
        bounds.translate(shiftX,shiftY);
        setBounds(bounds);
    }
    public int getXOffset() {
        return shiftX;
    }
    public int getYOffset() {
        return shiftY;
    }
    //GETTERS
    public Hex getHex(){
        return hex;
    }
    public int getAngle(){
        return (int)Math.round(Math.toDegrees(angle));
    }
    public int getTileWidth(){
        return width + 2 * (int)getStroke().getLineWidth();
    }
    public int getTileHeight(){
        return height + 2 * (int)getStroke().getLineWidth();
    }
    public boolean isFlipped(){
        return flipped;
    }
    public void flip(){
        flipped = !flipped;
    }
    public void setFlipped(boolean flipped){
        this.flipped = flipped;
    }
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


        drawPort(g2d);
        if(isDebug()) {
            displayDebugInfo(g2d);
        }


    }

    private void drawPort(Graphics2D g2d) {
        int biome = getBiome();
        if(biome == Tile.OCEAN && !isDebug()){
            return;
        }
        g2d.setColor(Color.BLACK);
        g2d.drawArc(-width/2,-height/2,width,height, 90, -180);
        int strokeOffset = (int)getStroke().getLineWidth();
        g2d.drawLine(0,-(height+strokeOffset)/2,0,(height+strokeOffset)/2);
        Color portColor = getBiomeColor();
        if(biome != Tile.OCEAN && !isFlipped() && !isDebug()){
            portColor = Tile.getBiomeColor(Tile.DESERT);
        }
        g2d.setColor(portColor);
        g2d.fillArc(-width/2,-height/2,width,height, 90, -180);
    }
    public void displayDebugInfo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(getTileFont().deriveFont(getTileFont().getSize()*.75F));

        String s = Integer.toString(getId());
        //String s = Integer.toString(getAngle());
        int textWidth = g2d.getFontMetrics().stringWidth(s);
        //System.out.println("text width "+textWidth + "id" + getId());
        AffineTransform transform = g2d.getTransform();
        transform.translate(12,-6);
        transform.rotate(-angle, -4,6);
        transform.rotate(Math.toRadians(angleOffset), -4,6);
        g2d.setTransform(transform);

        g2d.drawString(s, (getTileWidth()-textWidth-60)/2, (getTileHeight()-30)/2);
    }
}
