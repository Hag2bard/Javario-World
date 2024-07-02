package PokemonEditor;

import lombok.Getter;

import java.awt.*;

public class Block {
    @Getter
    private final Point source;
    @Getter
    private final Point destination;
    private boolean isDummy = true;


    public Block(int sourceX, int sourceY, int destinationX, int destinationY, boolean isDummy) {
        this.source = new Point(sourceX, sourceY);
        this.destination = new Point(destinationX, destinationY);
        this.isDummy = isDummy;
    }

    public void refreshDummyStatus() {
        this.isDummy = source.x < 0 | source.x > RGuiSizes.LAST_X_BLOCK || source.y < 0 | source.y > RGuiSizes.LAST_Y_BLOCK;
    }

    public boolean hasSameDestinationCoordinates(Point destinationCoordinates) {
        if (destinationCoordinates.x == this.getDestinationX() && destinationCoordinates.y == this.getDestinationY()) {
            return true;
        }
        return false;
    }

    public int getSourceX() {
        return source.x;
    }

    public int getSourceY() {
        return source.y;
    }

    public int getDestinationX() {
        return destination.x;
    }

    public int getDestinationY() {
        return destination.y;
    }

    public Block setSource(Point source) {
        this.source.x = source.x;
        this.source.y = source.y;
        return this;
    }


    public boolean isDummy() {
        return isDummy;
    }

    public Block setIsDummy(boolean isDummy) {
        this.isDummy = isDummy;
        return this;
    }

    /**
     * Gibt sauberen Klon dieses Blocks zurück
     *
     * @return Neuer Block mit selben Koordinaten
     */
    public Block getClone() {
        return new Block(getSourceX(), getSourceY(), getDestinationX(), getDestinationY(), this.isDummy);
    }

}
