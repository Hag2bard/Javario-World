package Pokemon;

import java.awt.image.BufferedImage;

/**
 * Management of Images for
 */
public class ImageManagement {

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

    /**
     * Creates a new instance of ImageManagement;
     *
     * @param width  width of images.
     * @param height height of images.
     */
    public ImageManagement(int width, int height) {
        this.width = width;
        this.height = height;

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
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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

        // Check if resize happened
        if (currentDrawingImage.getWidth() != width || currentDrawingImage.getHeight() != height)
            currentDrawingImage = createImage();

        return currentDrawingImage;
    }
}