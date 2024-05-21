package PokemonEditor;

public record RGuiSizes() {
    public static final int TILESIZE_PIXELS = 16;
    // Größe der Map (X-Achse), soll bei Bedarf geändert werden können -> TODO
    public static final int MAP_FIELD_HEIGHT = 20;
    // Größe der Map (Y-Achse), soll bei Bedarf geändert werden können -> TODO
    public static final int MAP_FIELD_WIDTH = 20;
    public static final String FILENAME_TILESET = "mario/142736.png";
    public static final String FILENAME_SPRITE_SET = "mario/Sprite.png";
    // Breite in Pixel
    public static final int TILESET_WIDTH_PIXELS = 256;
    // Höhe in Pixel
    public static final int TILESET_HEIGHT_PIXELS = 683;
    public static final int LAST_X_BLOCK = 16;
    public static final int LAST_Y_BLOCK = 997;

}
