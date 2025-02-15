package com.mycatan;

import java.awt.*;

abstract class ResourceTile extends Tile {
    //STATIC FIELDS
    private static final Color[] biomeColors = {
            new Color(0, 128, 0),//wood
            new Color(128, 255, 0),//sheep
            new Color(255, 255, 0,255),//hay
            new Color(255, 153, 0),//brick
            new Color(153, 153, 153),//rock
            new Color(215, 155, 20),//gold
            new Color(153, 102, 0),//desert
            new Color(0, 120, 255),//ocean
            new Color(255, 255, 255)//unflipped
    };

    //Resource Encodings
    public static final int WOOD = 0;
    public static final int SHEEP = 1;
    public static final int HAY = 2;
    public static final int BRICK = 3;
    public static final int ROCK = 4;
    public static final int GOLD = 5;
    public static final int DESERT = 6;
    public static final int OCEAN = 7;
    public static final int UNFLIPPED_RESOURCE = 8;
    public static final int RESOURCE_MIN = WOOD;
    public static final int RESOURCE_MAX = OCEAN;
    private static final char[] biomeChars = {'W','S','H','B','R','G','D','O','U'};


    //FIELDS
    private int biome;
    private int id;

    //CONSTRUCTORS
    public ResourceTile() {
        this.biome = ResourceTile.UNFLIPPED_RESOURCE;
        this.id = 0;
    }

    //METHODS

    /**
     * <p>Sets the {@code id} field. A collection of tiles must have id values ranging from 0 to
     * {@code getTotalHexCount()}. Negative id values will not be set and an error message will be thrown.</p>
     * @param id a positive integer representing a tile's unique position in its collection
     */
    public void setId(int id) {
        if(id >= 0) {
            this.id = id;
        }
        else{
            System.err.println("Invalid id value: " + id);
        }
    }

    @Override
    public void swap(ISelectable other) {
        ResourceTile otherTile = (ResourceTile) other;
        int tempBiome = otherTile.biome;

        otherTile.biome = this.biome;
        this.biome = tempBiome;


    }



    /**
     * <p>Sets the {@code biome} field using integer encoding to represent each resource.
     * Invalid biome values will not be set and an error message will be thrown.</p>
     * <p>Setting the biome manually should be done using:</p>
     * <p>Random int values ranging from {@code RESOURCE_MIN} to {@code RESOURCE_MAX}, or</p>
     * <p>Constants {@code WOOD}, {@code SHEEP},{@code HAY}, {@code BRICK},
     * {@code ROCK}, {@code GOLD}, {@code DESERT}, {@code OCEAN}.</p>
     * @param biome the integer encoded resource
     */
    public void setBiome(int biome) {
        if(biome >= ResourceTile.RESOURCE_MIN && biome <= ResourceTile.RESOURCE_MAX) {
            this.biome = biome;
        }
        else{
            System.err.println("Invalid biome value: " + biome);
        }
    }


    //GETTERS
    /**
     * @return the tile's id.
     */
    public int getId(){
        return id;
    }

    /**
     * @return the integer encoded biome
     */
    public int getBiome() {
        return biome;
    }


    /**
     * @return the char encoded biome
     */
    public char getBiomeChar(){
        return biomeChars[biome];
    }


    /**
     * <p>If the tile is type {@code UNFLIPPED_TYPE}, return unflipped color instead.</p>
     * @return the {@code Color} object representing the biome or unflipped color
     */
    public Color getBiomeColor(){
        return biomeColors[biome];
    }


    /**
     * <p>Returns the biome color using the integer encoding.</p>
     * @param biome use constants {@code WOOD}, {@code SHEEP}, {@code HAY},
     * {@code BRICK}, {@code ROCK}, {@code GOLD}, {@code DESERT}, {@code OCEAN},
     *             or {@code UNFLIPPED_RESOURCE} to get the color of an unflipped hex
     * @return the biome color
     */
    public static Color getBiomeColor(int biome){
        if(biome >= ResourceTile.RESOURCE_MIN) {
            return biomeColors[Math.min(biome, ResourceTile.UNFLIPPED_RESOURCE)];
        }
        else{
            System.err.println("Invalid biome value: " + biome);
            return biomeColors[ResourceTile.UNFLIPPED_RESOURCE];
        }
    }

    //STRING METHODS
    @Override
    public String toString(){
        return super.toString() + "Biome: " + getBiome();
    }
}
