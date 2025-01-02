import java.awt.*;

public class Hex extends Tile {
    //STATIC FIELDS
    public static final int SIDES = 6;
    public static final int LENGTH = 46;

    public static final int SHUFFLED = 0;
    public static final int FIXED = 1;
    //public static final int UNFLIPPED = 8; Defined in tile

    //FIELDS
    //local coordinates
    private final float[] xPoints;
    private final float[] yPoints;

    private int token;

    //CONSTRUCTORS
    public Hex() {
        super();
        setBiome(UNFLIPPED);
        setToken(0);
        setType(SHUFFLED);
        xPoints = new float[SIDES];
        yPoints = new float[SIDES];
        setPoints();
    }
    public Hex(int row, int col) {
        this();
        setLoc(row, col);
    }

    //SETTERS
    public void setToken(int token){
        this.token = token;
    }
    private void setPoints() {
        for (int i = 0; i < SIDES; i++) {
            xPoints[i] = (float)(LENGTH + LENGTH * Math.sin(i * 2 * Math.PI / SIDES));
            yPoints[i] = (float)(LENGTH + LENGTH * Math.cos(i * 2 * Math.PI / SIDES));
        }
    }

    //GETTERS
    public int getToken(){
        return token;
    }

    //STRING METHODS
    public String toString(){
        return super.toString() +
                ", Token: " + getToken();
    }

    @Override
    protected void paintComponent(Graphics g) {
        float x = xPoints[0];
        float y = yPoints[0];
    }
}
