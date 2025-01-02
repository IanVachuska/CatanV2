import javax.swing.*;
import java.awt.*;

public abstract class Tile extends JButton {
    //STATIC FIELDS
    public static final int WOOD = 0;
    public static final int SHEEP = 1;
    public static final int HAY = 2;
    public static final int BRICK = 3;
    public static final int ROCK = 4;
    public static final int GOLD = 5;
    public static final int DESERT = 6;
    public static final int OCEAN = 7;
    public static final int UNFLIPPED = 8;
    public static final int RESOURCE_MAX = UNFLIPPED;
    private static final char[] biomeChars = {'W','S','H','B','R','G','D','O','U'};

    //FIELDS
    private final Dimension loc;
    private int biome;
    private int id;
    private int type;//shuffled/fixed/unflipped

    //CONSTRUCTORS
    public Tile() {
        this.loc = new Dimension();
    }

    //SETTERS
    public void setBiome(int biome) {
        this.biome = biome;
    }
    public void setLoc(int row, int col){
        this.loc.setSize(col,row);
    }
    public void setId(int id){
        this.id = id;
    }
    public void setType(int type){this.type = type;}

    //GETTERS
    public int getBiome() {
        return biome;
    }
    public char getBiomeChar(){
        return biomeChars[biome];
    }
    public int getRow(){
        return loc.height;
    }
    public int getColumn(){
        return loc.width;
    }
    public int getId(){ return id; }
    public int getType(){
        return type;
    }


    //STRING METHODS
    public String toString(){
        return  "---" + getClass().getSimpleName()+ "String---\n" +
                "ID: " + id + ", " +
                "Location: (" + getRow() + "," + getColumn() + "), " +
                "Biome: " + getBiome();
    }

    //ABSTRACT
    @Override
    abstract protected void paintComponent(Graphics g);
}
