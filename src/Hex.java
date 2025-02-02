import java.awt.*;
import java.awt.geom.AffineTransform;

public class Hex extends Tile {
    //STATIC FIELDS
    public static final int SIDES = 6;
    public static final int LENGTH = 50;

    //FIELDS
    private int token;
    //local coordinates
    private final int[] xPoints;
    private final int[] yPoints;

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
        setPoints();

        setGridLocation(row, col);
        setToken(-1);
        setType(Tile.SHUFFLED);
        setDebug(debug);

        setTileFont(new Font("Arial", Font.BOLD, 15));
        setOpaque(false);
    }

    //SETTERS
    public void setToken(int token){
        this.token = token;
    }
    private void setPoints() {
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

    //GETTERS
    public int getToken(){
        return token;
    }
    public int getTileWidth() {
        return xPoints[1]-xPoints[4] + (int)getStroke().getLineWidth();
    }
    public int getTileHeight() {
        return yPoints[0]-yPoints[3] + (int)(getStroke().getLineWidth());
    }

    //STRING METHODS
    public String toString(){
        return super.toString() +
                ", Token: " + getToken();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(getStroke());
        if(isDebug()) {
            drawBoundingBox(g2d);
        }

        AffineTransform transform = g2d.getTransform();
        transform.concatenate(getScale());

        g2d.setTransform(transform);

        Polygon p = new Polygon(xPoints, yPoints, SIDES);
        drawHex(g2d,p);

        if(isDebug()) {
            displayDebugInfo(g2d);
        }
    }
    private void drawHex(Graphics2D g2d, Polygon p) {
        Color oldColor = g2d.getColor();
        Font oldFont = getTileFont();
        Font newFont = oldFont.deriveFont(oldFont.getSize() * 1.4F);

        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(p);

        g2d.setColor(getBiomeColor());
        g2d.fill(p);

        g2d.setColor(Color.BLACK);
        if(token > 0 && (getType() != Tile.UNFLIPPED_TYPE || isDebug())) {
            String tokenString = Integer.toString(getToken());
            g2d.setFont(newFont);
            int textWidth = g2d.getFontMetrics().stringWidth(tokenString);
            int textHeight = g2d.getFontMetrics().getHeight() - 8;
            int arcRadius = 20;
            g2d.drawArc(
                    getTileWidth() / 2 - arcRadius,
                    getTileHeight() / 2 - arcRadius,
                    arcRadius*2,arcRadius*2,
                    0,360);
            g2d.setColor(new Color(200, 200, 180));
            g2d.fillArc(
                    getTileWidth() / 2 - arcRadius,
                    getTileHeight() / 2 - arcRadius,
                    arcRadius*2,arcRadius*2,
                    0,360);
            g2d.setColor(Color.BLACK);
            g2d.drawString(tokenString,
                    (getTileWidth() - textWidth) / 2,
                    (getTileHeight() + textHeight) / 2);
        }
        g2d.setColor(oldColor);
        g2d.setFont(oldFont);
    }
    private void displayDebugInfo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g2d.getFontMetrics();
        // rows/columns
        String s = getGridRow() + "," + getGridColumn();
        int textWidth = fm.stringWidth(s);
        g2d.drawString(s, (getTileWidth()-textWidth)/2, (getTileHeight()+34)/2);
        //ID (spiral)
        s = Integer.toString(getId());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s, (getTileWidth()-textWidth)/2, (getTileHeight()-20)/2);
        //Type
        int textHeight = fm.getHeight()-4;
        s = String.valueOf(getTypeChar());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s, (getTileWidth()-textWidth-30)/2, ((getTileHeight() + textHeight)/2));
        s = String.valueOf(getBiomeChar());
        textWidth = fm.stringWidth(s);
        g2d.drawString(s, (getTileWidth()-textWidth+30)/2, ((getTileHeight() + textHeight)/2));
    }
}
