package PokemonEditor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewPanel extends JPanel {
    private final TilePanel tilePanel;
    private BufferedImage tilesetBufferedImage;
    private final int TILESIZE = 16;
    public final int ZOOM = 8;
    public static PreviewPanel instance;
    public static Logger LOG = LogManager.getLogger(PreviewPanel.class);

    public PreviewPanel() {
        tilePanel = TilePanel.getExistingInstance();
        try {
            tilesetBufferedImage = tilePanel.getBufferedImage();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Das Tileset-Bild wurde noch nicht geladen");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Point mouseOnPosition = tilePanel.getMouseOnPosition();
        if (mouseOnPosition != null) {
            g.drawImage(tilesetBufferedImage, 0, 0, TILESIZE*ZOOM, TILESIZE*ZOOM, tilePanel.getMouseOnPosition().x * TILESIZE, tilePanel.getMouseOnPosition().y * TILESIZE, (tilePanel.getMouseOnPosition().x + 1) * TILESIZE, (tilePanel.getMouseOnPosition().y + 1) * TILESIZE, null);
        }
    }

    /**
     * Erhalte existierende Instanz, wenn diese existiert, sonst wird eine neue erstellt
     *
     * @return Instanz von PreviewPanel
     */
    public static PreviewPanel getInstance() {
        if (instance == null) {
            instance = new PreviewPanel();
        }
        return instance;
    }

    /**
     * Getter-Methode für die PreviewPanel-Instanz wenn diese existiert
     *
     * @return Nur existierende Instanz von PreviewPanel
     */
    public static PreviewPanel getExistingInstance() {
        if (instance == null) {
            LOG.fatal("Instanz von PreviewPanel existiert noch nicht");
        }
        return instance;
    }


}
