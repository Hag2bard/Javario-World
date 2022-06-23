package Pokemon;

import PokemonEditor.BlockArrayList;

import java.awt.*;
import java.awt.image.BufferedImage;
import static PokemonEditor.TilePanel.TILESIZE;

/**
 * Management of Images for (KonradN - java-forum.org)
 */
public class ImageManagement {

    private static final int ZOOM = 4;
    private Object viewingImageLock = new Object();

    /**
     * Width of window / images.
     */
    private volatile int width;

    /**
     * Height of window / images.
     */
    private volatile int height;

    private volatile BufferedImage currentDrawingImage;
    private volatile BufferedImage nextDrawingImage1;
    private volatile BufferedImage nextDrawingImage2;

    private volatile BufferedImage currentViewingImage;
    private volatile BufferedImage nextViewingImage;
    private Physics physics;
    private volatile BufferedImage staticMapImage;

    /**
     * Creates a new instance of ImageManagement;
     *
     * @param width  width of images.
     * @param height height of images.
     */
    public ImageManagement(int width, int height, Physics physics) {
        this.width = width;
        this.height = height;
        this.physics = physics;
        currentDrawingImage = createImage();
        nextDrawingImage1 = createImage();
        nextDrawingImage2 = createImage();
    }

    /**
     * Creates a new BufferedImage in the correct size.
     *
     * @return BufferedImage to use.
     */
    private BufferedImage createImage() {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Sets the new size of the images.
     *
     * @param width  Width of images.
     * @param height Height of images.
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the current image to view. Takes care of a new image and moves old image back to queue.
     *
     * @return The BufferedImage that should be displayed.
     */
    public BufferedImage getCurrentViewingImage() {
        if (nextViewingImage != null) {
            synchronized (viewingImageLock) {
                if (nextDrawingImage1 == null) {
                    nextDrawingImage1 = currentViewingImage;
                } else {
                    nextDrawingImage2 = currentViewingImage;
                }
                currentViewingImage = nextViewingImage;
                nextViewingImage = null;
            }
        }
        return currentViewingImage;
    }

    /**
     * Gets the next BufferedImage to draw.
     * <li>
     *     <ul>The last drawn image is moved to be shown next.</ul>
     *     <ul>Gets the next image in the queue</ul>
     *     <ul>Checks the size of the image and creates a new one if required.</ul>
     * </li>
     *
     * @return
     */
    public BufferedImage getNextDrawingImage() {
        synchronized (viewingImageLock) {
            if (nextViewingImage != null) {
                nextDrawingImage2 = nextViewingImage;
            }
            nextViewingImage = currentDrawingImage;
            currentDrawingImage = nextDrawingImage1;
            nextDrawingImage1 = nextDrawingImage2;
            nextDrawingImage2 = null;
        }

//         Check if resize happened
        if (currentDrawingImage == null || currentDrawingImage.getWidth() != width || currentDrawingImage.getHeight() != height)
//        if (physics.getMario().heroDataChanged())
            currentDrawingImage = createImage();

        return currentDrawingImage;
    }

    public BufferedImage getMapImage(){
        BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics mapGraphics = mapImage.createGraphics();
        BlockArrayList mapLayer1 = physics.getCanvas().getMapLayer1();
        BlockArrayList mapLayer2 = physics.getCanvas().getMapLayer2();

        for (int i = 0; i < mapLayer1.size(); i++) {
            mapGraphics.drawImage(physics.getCanvas().getTilesetBufferedImage(), mapLayer1.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer1.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer1.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer1.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer1.get(i).getSourceX() * TILESIZE, mapLayer1.get(i).getSourceY() * TILESIZE, (mapLayer1.get(i).getSourceX() + 1) * TILESIZE, (mapLayer1.get(i).getSourceY() + 1) * TILESIZE, null);
        }
        for (int i = 0; i < mapLayer2.size(); i++) {
            mapGraphics.drawImage(physics.getCanvas().getTilesetBufferedImage(), mapLayer2.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer2.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer2.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer2.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer2.get(i).getSourceX() * TILESIZE, mapLayer2.get(i).getSourceY() * TILESIZE, (mapLayer2.get(i).getSourceX() + 1) * TILESIZE, (mapLayer2.get(i).getSourceY() + 1) * TILESIZE, null);
        }
        mapGraphics.dispose();
        return mapImage;
    }




}