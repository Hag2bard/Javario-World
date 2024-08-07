package PokemonEditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dch
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener {

    private int selectedX = -1;
    private int selectedY = -1;
    private BufferedImage tilesetBufferedImage;
    private final int TILESIZE = 16;
    public static final int ZOOM_FACTOR = 2;
    private final List<Point> selectedBlocksOnMapPanel;
    private int selectedLayer = 1;
    private int mousePositionX = 0;
    private int mousePositionY = 0;
    private BlockList mapLayer1;
    private BlockList mapLayer2;
    private final TilePanel tilePanel;
    private static MapPanel instance;
    //    @Getter
//    LinkedList<BlockList> undoBlockListLayer1 = new LinkedList<>();
//    @Getter
//    LinkedList<BlockList> undoBlockListLayer2 = new LinkedList<>();
//    @Getter
//    LinkedList<BlockList> redoBackupBlockListLayer1 = new LinkedList<>();
//    @Getter
//    LinkedList<BlockList> redoBackupBlockListLayer2 = new LinkedList<>();
    private boolean isOnMapPanel = false;


    public MapPanel() {
        tilePanel = TilePanel.getInstance();
        //Aktuell sind 2 Layer editierbar, der 3. ist nur als Vorschau gedacht
        mapLayer1 = new BlockList(true);
        mapLayer2 = new BlockList(true);
        mapLayer1.addCloneToUndoBlockList(mapLayer1.getClone());
        mapLayer2.addCloneToUndoBlockList(mapLayer2.getClone());
//        undoBlockListLayer1.add(mapLayer1.getClone());
//        undoBlockListLayer2.add(mapLayer2.getClone());
        selectedBlocksOnMapPanel = new ArrayList<>();
        try {
            tilesetBufferedImage = loadBufferedImage(RGuiSizes.FILENAME_TILESET);
        } catch (IOException e) {
            System.err.println("Schwerer Fehler beim Laden von " + RGuiSizes.FILENAME_TILESET);
            e.printStackTrace();
        }
    }

    private void paintLayer(Graphics g, BlockList mapLayer) {
        for (int i = 0; i < mapLayer.size(); i++) {
            g.drawImage(tilesetBufferedImage, mapLayer.get(i).getDestinationX() * TILESIZE * ZOOM_FACTOR, mapLayer.get(i).getDestinationY() * TILESIZE * ZOOM_FACTOR, (mapLayer.get(i).getDestinationX() + 1) * TILESIZE * ZOOM_FACTOR, (mapLayer.get(i).getDestinationY() + 1) * TILESIZE * ZOOM_FACTOR, mapLayer.get(i).getSourceX() * TILESIZE, mapLayer.get(i).getSourceY() * TILESIZE, (mapLayer.get(i).getSourceX() + 1) * TILESIZE, (mapLayer.get(i).getSourceY() + 1) * TILESIZE, null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        float alpha;
        AlphaComposite alcom;
        Graphics2D g2d = (Graphics2D) g;
        //Je nach dem, welcher Layer ausgewählt ist, wird dieser normal gezeichnet und die anderen Layer blass
        switch (selectedLayer) {
            // Layer 1 mit voller Deckung zeichnen
            // Layer 2 mit halber Deckung zeichnen
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
            // Layer 1 mit halber Deckung zeichnen
            // Layer 2 mit voller Deckung zeichnen
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
            // Layer 1 mit voller Deckung zeichnen
            // Layer 2 mit voller Deckung zeichnen
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
//        drawBlocks(g); //Nicht löschen, evtl andere Performance //TODO
//TODO NOTWENDIG??
        tilePanel.refreshPreview();

    }

    private void drawBlocks(Graphics g) {
        final int BLOCKSIZE = TILESIZE * ZOOM_FACTOR;
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
        int blockX = mousePositionX * TILESIZE * ZOOM_FACTOR;
        int blockY = mousePositionY * TILESIZE * ZOOM_FACTOR;
        final int BLOCKSIZE = TILESIZE * ZOOM_FACTOR;

        for (int x = blockX; x < (blockX + (tilePanel.getAmountOfSelectedBlocks() + 1) * BLOCKSIZE); x += BLOCKSIZE) {
            g.drawLine(x, blockY, x, blockY + (BLOCKSIZE * tilePanel.getAmountOfSelectedBlocks()));
        }
    }

    private void drawHorizontalLines(Graphics g) {
        int blockX = mousePositionX * TILESIZE * ZOOM_FACTOR;
        int blockY = mousePositionY * TILESIZE * ZOOM_FACTOR;
        final int BLOCKSIZE = TILESIZE * ZOOM_FACTOR;

        for (int y = blockY; y < (blockY + (tilePanel.getAmountOfSelectedBlocks() + 1) * BLOCKSIZE); y += BLOCKSIZE) {
            g.drawLine(blockX, y, blockX + (BLOCKSIZE * tilePanel.getAmountOfSelectedBlocks()), y);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("mouseClicked in MapPanel");

        mapLayer1.orderByDestinationCoordinates();                                                               //Die Liste wird nach Reihenfolge sortiert
        mapLayer2.orderByDestinationCoordinates();                                                               //Die Liste wird nach Reihenfolge sortiert

        if (MapEditor.isPickupToolActive) {
            doPickup(e);
        } else {   //Kein Pickup
            //Erhalte Mauskoordinaten:
            selectedX = e.getPoint().x;
            selectedY = e.getPoint().y;
            //rechne Koordinaten in Blöcken um (aus 20,20 Pixel wird Block 1,1):
            selectedX = selectedX / (TILESIZE * ZOOM_FACTOR);
            selectedY = selectedY / (TILESIZE * ZOOM_FACTOR);
            //Wenn nicht gelöscht wird: (hier evtl. Performance Probs)
            if (!MapEditor.isDeleteToolActive) {
                //das selbe ist eventuell auch die selectedBlocks
                int offset = tilePanel.getAmountOfSelectedBlocks();
                int x = 0;
                int y = 0;
                // hier auch einbauen, dass nicht belegt werden darf
                for (int i = 0; i < tilePanel.getSelectedBlocksList().size(); i++) {
                    switch (selectedLayer) {
                        case 1 ->
                                mapLayer1.add(tilePanel.getSelectedBlocksList().get(i).x, tilePanel.getSelectedBlocksList().get(i).y, selectedX + x, selectedY + y);
                        case 2 ->
                                mapLayer2.add(tilePanel.getSelectedBlocksList().get(i).x, tilePanel.getSelectedBlocksList().get(i).y, selectedX + x, selectedY + y);
                    }
                    // Ich war leider so blöd hier nicht direkt beim Coden zu kommentieren, ich weiß nicht mehr was der
                    // hier macht.. Todo
                    x++;
                    if (x == offset) {
                        x = 0;
                        y++;
                    }
                }

                boolean doesNotExist = true;
                for (int i = 0; i < mapLayer1.size(); i++) {
                    if (mapLayer1.doesExistAndIsNotADummy(selectedX, selectedY)) {
                        doesNotExist = false;
                    }
                }
                if (doesNotExist) {
                    mapLayer1.add(-1, -1, selectedX, selectedY);
                    repaint();
                }
            }
            if (MapEditor.isDeleteToolActive) {
                deleteBlocks(selectedX, selectedY, tilePanel.getAmountOfSelectedBlocks());
                this.repaint();
                tilePanel.repaint();
            }
        }
        repaint();
        mapLayer1.refreshDummyBlocks();
        mapLayer2.refreshDummyBlocks();
    }

    /**
     * Löscht gewisse Anzahl an Blöcken, je nachdem wie groß das Auswahlquadrat gestellt ist.
     *
     * @param selectedX              Blockposition auf x-Achse.
     * @param selectedY              Blockposition auf y-Achse.
     * @param amountOfSelectedBlocks Größe des Auswahlquadrats.
     */
    public void deleteBlocks(int selectedX, int selectedY, int amountOfSelectedBlocks) {
        for (int y = 0; y < amountOfSelectedBlocks; y++) {
            for (int x = 0; x < amountOfSelectedBlocks; x++) {
                switch (getSelectedLayer()) {
                    case 1 -> mapLayer1.delete(selectedX + x, selectedY + y);
                    case 2 -> mapLayer2.delete(selectedX + x, selectedY + y);
                    case 3 -> {
                        mapLayer1.delete(selectedX + x, selectedY + y);     //Wenn Layer 3 ausgewählt ist, löscht er alles was gesetzt wurde
                        mapLayer2.delete(selectedX + x, selectedY + y);
                    }
                }
            }
        }
    }


    /**
     * Nach einem Mausvorgang (Klicken und Loslassen) wird ein Backup der Map erstellt, damit der Rückgängig-Button Futter bekommt.
     *
     * @param e Der Parameter e ist das Mausevent
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (selectedLayer != 3) {
            //Nur Kopie erstellen, wenn Mausklick etwas bezweckt hat //ToDO /if booleanChangedSomething
            mapLayer1.addCloneToUndoBlockList(getMapLayer1().getClone());
            mapLayer2.addCloneToUndoBlockList(getMapLayer2().getClone());
        }
    }

    /**
     * Beim Loslassen der Maus werden gesetzte Blöcke außerhalb des Sichtfeldes gelöscht
     *
     * @param e Der Parameter e ist das Mausevent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        getMapLayer1().clean(RGuiSizes.MAP_FIELD_HEIGHT, RGuiSizes.MAP_FIELD_WIDTH);
        getMapLayer2().clean(RGuiSizes.MAP_FIELD_HEIGHT, RGuiSizes.MAP_FIELD_WIDTH);
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

    /**
     * Methode zum Sortieren nach Koordinaten
     * (0,0 | 0,1 | 1,0 | 1,1 ...)
     *
     * @param pointListOfBlocks Liste mit Blocks und jeweils 2 Koordinaten.
     */
    public void orderPointListOfBlocks(List<Point> pointListOfBlocks) {
        pointListOfBlocks.sort(Comparator
                .comparingDouble(Point::getY)
                .thenComparingDouble(Point::getX));
    }

    private void doPickup(MouseEvent e) {
        if (selectedLayer != 3) {
            LinkedList<Point> selectedBlocksOnTilePanel = tilePanel.getSelectedBlocksList();
            // Löschen aller selektierten Blöcke
            selectedBlocksOnTilePanel.clear();
            selectedBlocksOnMapPanel.clear();

            //////////////////////////////////////////////////////TODO AUSLAGERN
            // selectedBlocksOnMapPanel füllen
            for (int y = 0; y < tilePanel.getAmountOfSelectedBlocks(); y++) {
                for (int x = 0; x < tilePanel.getAmountOfSelectedBlocks(); x++) {
                    // Dies ist die Logik, welches die Mauskoordinaten, welche man vom MouseEvent
                    // erhält, in Blöcke runterrechnet. Je nachdem wie viele Pixel ein Block hat
                    // und ob ein Zoom-Faktor eingestellt ist
                    selectedBlocksOnMapPanel.add(new Point(
                            ((e.getPoint().x / (TILESIZE * ZOOM_FACTOR)) + x), ((e.getPoint().y / (TILESIZE * ZOOM_FACTOR)) + y)));
                    // Ordnet die Liste der selektierten Blöcke, sowie die Block-Listen der Layer.
                    orderPointListOfBlocks(selectedBlocksOnMapPanel);
                    mapLayer1.orderByDestinationCoordinates();
                    mapLayer2.orderByDestinationCoordinates();
                }
            }
            ///////////////////////////////////////////////////TODO AUSLAGERN
            // selectedBlocksOnTilePanel füllen
            switch (selectedLayer) {
                case 1:
                    for (int i = 0; i < mapLayer1.size(); i++) {
                        for (Point point : selectedBlocksOnMapPanel) {
                            // Wenn ein selectedBlock dem aktuellen Block(i) aus der BlockListe(i) entspricht,
                            // dann holen wir uns die Source-Koordinaten vom Block(i) für einen neuen Eintrag
                            // in die Liste selectedBlocksOnTilePanel
                            if (mapLayer1.get(i).hasSameDestinationCoordinates(point)) {
                                selectedBlocksOnTilePanel.add(new Point(mapLayer1.get(i).getSource())); /// TODO !!!
                            }
                            //TODO Hier werden leere Blöcke ignoriert und das soll nicht sein
                        }
                    }
                    break;
                //TEST JUNIT HIER EINBAUEN
                case 2:
                    for (int i = 0; i < mapLayer2.size(); i++) {
                        for (Point point : selectedBlocksOnMapPanel) {
                            if (mapLayer2.get(i).hasSameDestinationCoordinates(point)) {
                                selectedBlocksOnTilePanel.add(new Point(mapLayer2.get(i).getSource()));
                            }
                        }
                    }
                    break;
                case 3:
                    // TODO Achtung case 3 ist unreachable! Die Behandlung von Case 3 muss außerhalb
                default:
                    throw new RuntimeException("Ungültiger Layer ausgewählt!");
            }
            // Das Pickup Tool wird beendet sobald es einmal gepickt hat
            MapEditor.getInstance("MapPanel").setPickupInactive();
            tilePanel.setFocusable(true);
            tilePanel.repaint();
        }
        else {
            // Case 3 behandeln!
            // Was soll er beim Pickup in den dritten Layer machen?
            // Soll er Informationen zu den Blöcken anzeigen? Eventuell mit Beschreibung? Beschreibung selber eingeben
        }
    } // doPickup Ende

    private BufferedImage loadBufferedImage(String filename) throws IOException {
        return ImageIO.read(new FileInputStream(filename));
    }

    public void setMapLayer1(BlockList mapLayer1) {
        this.mapLayer1 = mapLayer1;
    }

    public void setMapLayer2(BlockList mapLayer2) {
        this.mapLayer2 = mapLayer2;
    }

    public void setSelectedX(int selectedX) {
        this.selectedX = selectedX;
    }

    public void setSelectedY(int selectedY) {
        this.selectedY = selectedY;
    }

    public BlockList getBlockArrayLayer1() {
        return mapLayer1;
    }

    public BlockList getBlockArrayLayer2() {
        return mapLayer2;
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
        mousePositionX = e.getPoint().x / (TILESIZE * ZOOM_FACTOR);
        mousePositionY = e.getPoint().y / (TILESIZE * ZOOM_FACTOR);
        repaint();             //Zum Neuzeichnen der Kästchen bei jeder Bewegung der Maus
    }


    public BlockList getMapLayer1() {
        return this.mapLayer1;
    }

    public BlockList getMapLayer2() {
        return this.mapLayer2;
    }


}
