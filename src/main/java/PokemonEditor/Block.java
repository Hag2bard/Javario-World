package PokemonEditor;

import java.awt.*;

public class Block {
    private final Point source;
    private final Point destination;
    private boolean isDummy = true;
    private boolean collides;

    public Block(int sourceX, int sourceY, int destinationX, int destinationY, boolean collides) {
        this.source = new Point(sourceX, sourceY);
        this.destination = new Point(destinationX, destinationY);
        this.collides = collides;
    }
    public boolean collides() {
        return collides;
    }

    public Block setCollides(boolean collides) {
        this.collides = collides;
        return this;
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

    public Block setDummy(boolean dummy) {
        isDummy = dummy;
        return this;
    }

}
