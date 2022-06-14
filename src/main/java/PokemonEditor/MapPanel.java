package PokemonEditor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author dch
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener {

    private int selectedX = -1;
    private int selectedY = -1;
    private BufferedImage tilesetBufferedImage;
    private boolean isPickupActive = false;
    private final int TILESIZE = 16;
    public static final int ZOOM = 2;
    private final List<Point> selectedBlocksOnMapPanel;
    private int selectedLayer = 1;
    private int mousePositionX = 0;
    private int mousePositionY = 0;
    private BlockArrayList mapLayer1;
    private BlockArrayList mapLayer2;
    private final TilePanel tilePanel;
    private static MapPanel instance;
    LinkedList<BlockArrayList> backupLinkedListLayer1 = new LinkedList<>();
    LinkedList<BlockArrayList> backupLinkedListLayer2 = new LinkedList<>();
    private boolean isOnMapPanel = false;
    public static Logger LOG = LogManager.getLogger(MapPanel.class);


    private MapPanel() {
        tilePanel = TilePanel.getInstance();
        mapLayer1 = new BlockArrayList();
        mapLayer2 = new BlockArrayList();
        backupLinkedListLayer1.add(mapLayer1.getClone());
        fillMapWithDummyBlocks(mapLayer1);
        fillMapWithDummyBlocks(mapLayer2);
        selectedBlocksOnMapPanel = new ArrayList<>();
        try {
            tilesetBufferedImage = loadBufferedImage(GuiData.filenameTileset);
        } catch (IOException e) {
            LOG.fatal("Schwerer Fehler beim Laden von " + GuiData.filenameTileset);
            e.printStackTrace();
        }
    }

    public static MapPanel getInstance() {
        if (instance == null) {
            instance = new MapPanel();
        }
        return instance;
    }

    private void paintLayer(Graphics g, BlockArrayList mapLayer) {
        for (int i = 0; i < mapLayer.size(); i++) {
            g.drawImage(tilesetBufferedImage, mapLayer.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer.get(i).getSourceX() * TILESIZE, mapLayer.get(i).getSourceY() * TILESIZE, (mapLayer.get(i).getSourceX() + 1) * TILESIZE, (mapLayer.get(i).getSourceY() + 1) * TILESIZE, null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        float alpha;
        AlphaComposite alcom;
        Graphics2D g2d = (Graphics2D) g;

        //
        switch (selectedLayer) {
            case 1 -> {
                alpha = 1.0f;
                alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                g2d.setComposite(alcom);
                paintLayer(g, mapLayer1);
                alpha = 0.5f;
                alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                g2d.setComposite(alcom);
                paintLayer(g, mapLayer2);
            }
            case 2 -> {
                alpha = 0.5f;
                alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                g2d.setComposite(alcom);
                paintLayer(g, mapLayer1);
                alpha = 1.0f;
                alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                g2d.setComposite(alcom);
                paintLayer(g, mapLayer2);
            }
            case 3 -> {
                alpha = 1.0f;
                alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                g2d.setComposite(alcom);
                paintLayer(g, mapLayer1);
                paintLayer(g, mapLayer2);
            }
        }
        // TODO Hier muss noch eingebaut werden, dass wenn er auf Layer 2 ist eine andere Farbe ausgewählt ist, auf Layer 3 es irgendwie so markieren, dass man sieht
        //  dass hier nur gelesen werden kann (weiß oder so) und hier muss eingebaut werden dass bei mehreren selectedBlocks auch mehrere g.drawRect aufgerufen werden
        //  hier eventuell noch einbauen, dass die ausgewählten Blöcke am Mauscursor dran sind sobald man das Feld betritt (Großer Aufwand)
        g.setColor(Color.RED);
        if (isOnMapPanel) {
            drawVerticalLines(g);
            drawHorizontalLines(g);
        }
//        drawBlocks(g);
//TODO NOTWENDIG??
        tilePanel.refreshPreview();

    }

    private void drawBlocks(Graphics g) {
        final int BLOCKSIZE = TILESIZE * ZOOM;
        int blockX = mousePositionX * BLOCKSIZE;
        int blockY = mousePositionY * BLOCKSIZE;

        for (int x = 0; x < tilePanel.getAmountOfSelectedBlocks(); x++) {
            for (int y = 0; y < tilePanel.getAmountOfSelectedBlocks(); y++) {
                g.drawRect((blockX) + (x * BLOCKSIZE), (blockY), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle
                g.drawRect((blockX) + (y * BLOCKSIZE), (mousePositionY * BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle

                g.drawRect((blockX), (blockY) + (x * BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle
                g.drawRect((blockX), (blockY) + (y * BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle

                g.drawRect((blockX) + (x * BLOCKSIZE), (blockY) + (x * BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle
                g.drawRect((blockX) + (x * BLOCKSIZE), (blockY) + (y * BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle

                g.drawRect((blockX) + (y * BLOCKSIZE), (blockY) + (y * BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle
                g.drawRect((blockX) + (y * BLOCKSIZE), (blockY) + (x * BLOCKSIZE), BLOCKSIZE, BLOCKSIZE);//sollte eigentlich fertig sein an dieser Stelle
            }
        }
    }

    private void drawVerticalLines(Graphics g) {
        int blockX = mousePositionX * TILESIZE * ZOOM;
        int blockY = mousePositionY * TILESIZE * ZOOM;
        final int BLOCKSIZE = TILESIZE * ZOOM;

        for (int x = blockX; x < (blockX + (tilePanel.getAmountOfSelectedBlocks() + 1) * BLOCKSIZE); x += BLOCKSIZE) {
            g.drawLine(x, blockY, x, blockY + (BLOCKSIZE * tilePanel.getAmountOfSelectedBlocks()));
        }
    }

    private void drawHorizontalLines(Graphics g) {
        int blockX = mousePositionX * TILESIZE * ZOOM;
        int blockY = mousePositionY * TILESIZE * ZOOM;
        final int BLOCKSIZE = TILESIZE * ZOOM;

        for (int y = blockY; y < (blockY + (tilePanel.getAmountOfSelectedBlocks() + 1) * BLOCKSIZE); y += BLOCKSIZE) {
            g.drawLine(blockX, y, blockX + (BLOCKSIZE * tilePanel.getAmountOfSelectedBlocks()), y);
        }
    }

    /**
     * Diese Methode füllt die gesamte Map mit DummyBlocks, als Workaround für das Pickup-Tool
     */
    private void fillMapWithDummyBlocks(BlockArrayList mapLayer) {
        for (int destinationY = 0; destinationY < GuiData.fieldWidth; destinationY++) {
            for (int destinationX = 0; destinationX < GuiData.fieldHeight; destinationX++) {
                mapLayer.add(-1, -1, destinationX, destinationY);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        sort();

        mapLayer1.sort();                                                               //Die Liste wird nach Reihenfolge sortiert
        mapLayer2.sort();                                                               //Die Liste wird nach Reihenfolge sortiert

        if (isPickupActive) {
            doPickup(e);
        } else {                //Nicht pickup
            selectedX = e.getPoint().x;                     //Erhalte Mauskoordinanten
            selectedY = e.getPoint().y;
            selectedX = selectedX / (TILESIZE * ZOOM);            //rechne Koordinaten in Blöcken um (aus 20,20 Pixel wird Block 1,1)
            selectedY = selectedY / (TILESIZE * ZOOM);
            Logic logic = Logic.getInstance();
            if (!logic.isDeleteActive()) {                  //Wenn nicht gelöscht wird (hier evtl Performance Probs)

//                int offset = (int) Math.sqrt(tilePanel.getSelectedBlocksList().size());
                int offset = tilePanel.getAmountOfSelectedBlocks();
                //das selbe ist eventuell auch die selectedBlocks

                int x = 0;
                int y = 0;

                // hier auch einbauen, dass nicht belegt werden darf


                for (int i = 0; i < tilePanel.getSelectedBlocksList().size(); i++) {
                    switch (selectedLayer) {
                        case 1 -> mapLayer1.add(tilePanel.getSelectedBlocksList().get(i).x, tilePanel.getSelectedBlocksList().get(i).y, selectedX + x, selectedY + y);
                        case 2 -> mapLayer2.add(tilePanel.getSelectedBlocksList().get(i).x, tilePanel.getSelectedBlocksList().get(i).y, selectedX + x, selectedY + y);
                    }

                    x++;
                    if (x == offset) {
                        x = 0;
                        y++;
                    }
                }
                boolean doesNotExist = true;
                for (int i = 0; i < mapLayer1.size(); i++) {
                    if (mapLayer1.doesExist(selectedX, selectedY)) {
                        doesNotExist = false;
                    }

                }
                if (doesNotExist) {
                    mapLayer1.add(-1, -1, selectedX, selectedY);
                    repaint();
                }
            }
            if (logic.isDeleteActive()) {
                logic.deleteBlock2(selectedX, selectedY, tilePanel.getAmountOfSelectedBlocks());
                repaint();
                tilePanel.repaint();
            }
        }
        repaint();
        mapLayer1.deleteDummyBlocks();
        mapLayer2.deleteDummyBlocks();
    }

    /**
     * Nach einem Mausvorgang (Klicken und Loslassen) wird ein Backup der Map erstellt, damit der Rückgängig-Button Futter bekommt.
     *
     * @param e Der Parameter e ist das Mausevent
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (selectedLayer != 3) {
            backupLinkedListLayer1.add(getMapLayer1().getClone());
            backupLinkedListLayer2.add(getMapLayer2().getClone());
        }
    }

    /**
     * Beim Loslassen der Maus werden gesetzte Blöcke außerhalb des Sichtfeldes gelöscht
     * @param e Der Parameter e ist das Mausevent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        getMapLayer1().clean(GuiData.fieldHeight, GuiData.fieldWidth);
        getMapLayer2().clean(GuiData.fieldHeight, GuiData.fieldWidth);
    }


    @Override
    public void mouseEntered(MouseEvent e) {
        isOnMapPanel = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        isOnMapPanel = false;
        repaint();
    }

    public void sort(List<Point> selectedBlocksOnMapPanel) {
        selectedBlocksOnMapPanel.sort(Comparator
                .comparingDouble(Point::getY)
                .thenComparingDouble(Point::getX));
    }

    private void doPickup(MouseEvent e) {
        if (selectedLayer != 3) {
            LinkedList<Point> backupSelectedBlocksOnTilePanel = new LinkedList<>(tilePanel.getSelectedBlocksList());
            LinkedList<Point> selectedBlocksOnTilePanel = tilePanel.getSelectedBlocksList();
            selectedBlocksOnTilePanel.clear();                            //Befehl zum Löschen aller selektierten Blöcke
            selectedBlocksOnMapPanel.clear();

            // selectedBlocksOnMapPanel füllen
            for (int y = 0; y < tilePanel.getAmountOfSelectedBlocks(); y++) {
                for (int x = 0; x < tilePanel.getAmountOfSelectedBlocks(); x++) {
                    selectedBlocksOnMapPanel.add(new Point(((e.getPoint().x / (TILESIZE * ZOOM)) + x), ((e.getPoint().y / (TILESIZE * ZOOM)) + y)));   //hier holt er sich alle Blöcke die er je nach Größe picken kann

                    sort(selectedBlocksOnMapPanel); //SelectedBlocks sortieren nach Reihe
                    mapLayer1.sort();
                    mapLayer2.sort();
                }
            }

            switch (selectedLayer) {
                case 1:
                    ////Auslagern
                    for (int i = 0; i < mapLayer1.size(); i++) {
                        for (int b = 0; b < selectedBlocksOnMapPanel.size(); b++) {
                            if (selectedBlocksOnMapPanel.get(b).x == mapLayer1.get(i).getDestinationX() && selectedBlocksOnMapPanel.get(b).y == mapLayer1.get(i).getDestinationY()) {
                                selectedBlocksOnTilePanel.add(new Point(mapLayer1.get(i).getSourceX(), mapLayer1.get(i).getSourceY())); /// TODO !!!
                                //TODO Hier werden leere Blöcke ignoriert und das soll nicht sein
                            }
                        }
                    }
                    //TEST JUNIT HIER EINBAUEN


                    break;
                case 2:
                    for (int i = 0; i < mapLayer2.size(); i++) {
                        for (int b = 0; b < selectedBlocksOnMapPanel.size(); b++) {
                            if (selectedBlocksOnMapPanel.get(b).x == mapLayer2.get(i).getDestinationX() && selectedBlocksOnMapPanel.get(b).y == mapLayer2.get(i).getDestinationY()) {
                                selectedBlocksOnTilePanel.add(new Point(mapLayer2.get(i).getSourceX(), mapLayer2.get(i).getSourceY()));
                            }
                        }
                    }
                    break;
                case 3:
                    // TODO
                default:
                    //ssssssss ///Was soll er im Falle eines anderen Layers machen? Soll er Informationen zu den Blöcken anzeigen? Eventuell mit Beschreibung? Beschreibung selber eingeben

            }
            deactivatePickupTool();                     //Das Pickup Tool wird beendet sobald es einmal gepickt hat
            tilePanel.setFocusable(true);
            tilePanel.repaint();
            //PICKUP ENDE
        }
    }

    private BufferedImage loadBufferedImage(String filename) throws IOException {
        return ImageIO.read(new FileInputStream(filename));
    }

    public void setMapLayer1(BlockArrayList mapLayer1) {
        this.mapLayer1 = mapLayer1;
    }

    public void setMapLayer2(BlockArrayList mapLayer2) {
        this.mapLayer2 = mapLayer2;
    }

    public void setSelectedX(int selectedX) {
        this.selectedX = selectedX;
    }

    public void setSelectedY(int selectedY) {
        this.selectedY = selectedY;
    }

    public BlockArrayList getBlockArrayLayer1() {
        return mapLayer1;
    }

    public BlockArrayList getBlockArrayLayer2() {
        return mapLayer2;
    }

    public void activatePickupTool() {
        this.isPickupActive = true;
    }

    public void deactivatePickupTool() {
        this.isPickupActive = false;
        PokeEditor.getInstance().setSelectionBtnPickupTool(false);
    }

    public void setSelectedLayer(int selectedLayer) {
        int previouslySelectedLayer = this.selectedLayer;
        if (selectedLayer < 1 || selectedLayer > 3) {
            JOptionPane.showMessageDialog(null, "Geben Sie eine gültige Zahl von 1-3 ein!");
            // LOG
            this.selectedLayer = previouslySelectedLayer;
        } else {
            this.selectedLayer = selectedLayer;
        }
    }

    public int getSelectedLayer() {
        return this.selectedLayer;
    }

    /**
     * Wenn die Maus gezogen wird, dann werden Mausklicks abgefeuert
     *
     * @param e Das MausEvent e wird an mouseClicked und mouseMoved weiter gegeben
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseClicked(e);                                                        //Hier soll er beim Maus halten immer wieder das mouseClicked Event abfeuern
        mouseMoved(e);                                                          //Beim Ziehen soll er immer wieder das mouseMoved Event abfeuern welches Koordinaten speichert
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePositionX = e.getPoint().x / (TILESIZE * ZOOM);
        mousePositionY = e.getPoint().y / (TILESIZE * ZOOM);
        repaint();             //Zum Neuzeichnen der Kästchen bei jeder Bewegung der Maus
    }

    public BlockArrayList getMapLayer1() {
        return this.mapLayer1;
    }

    public BlockArrayList getMapLayer2() {
        return this.mapLayer2;
    }


}
