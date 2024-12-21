import java.awt.*;

public class Port extends Tile {
    //STATIC FIELDS
    public static final int SIDES = 6;
    public static final int LENGTH = 46;

    //FIELDS
    //local coordinates
    private final float[] xPoints;
    private final float[] yPoints;

    private double angle;
    private boolean valid;

    //CONSTRUCTORS
    public Port() {
        super();
        setBiome(UNFLIPPED);
        xPoints = new float[SIDES];
        yPoints = new float[SIDES];
        setPoints();
    }
    public Port(Hex hex) {
        this();
        setLoc(hex.getRow(), hex.getColumn());
    }

    //SETTERS
    public void setAngle(int degrees){
        angle = Math.toRadians(degrees);
    }
    private void setPoints(){
        int offset = 2;
        xPoints[0] = offset;
        yPoints[0] = offset + (float)LENGTH/3;

        xPoints[1] = offset + LENGTH;
        yPoints[1] = offset + (float)LENGTH/3;

        xPoints[2] = offset + (float)LENGTH/2;
        yPoints[2] = offset;
    }

    //GETTERS
    public int getAngle(){
        return (int)Math.toDegrees(angle);
    }

    @Override
    protected void paintComponent(Graphics g) {
        float x = xPoints[0];
        float y = yPoints[0];
    }
}
