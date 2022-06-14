package PokemonEditor;

import PokemonEditor.listener.MouseListenerWithDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

public class TilePanel extends JPanel implements MouseListenerWithDefaults, MouseMotionListener {

    public static final int TILESIZE = 16;
    private BufferedImage tilesetBufferedImage;
    private LinkedList<Point> selectedBlocksList;    // - Werte sind X und + Werte sind Y
    private int amountOfSelectedBlocks = 1;    // kann auch 3 sein oder 4 oder 1!!!
    public static TilePanel instance;
    private Point mouseOnPosition;
    public static Logger LOG = LogManager.getLogger(TilePanel.class);


    private TilePanel() {
        selectedBlocksList = new LinkedList<>();
        selectedBlocksList.add(new Point(0, 0));  //Hier wird am Anfang ein ausgewählter Block festgelegt
        loadBufferedImage();
    }

    /**
     * Erhalte existierende Instanz, wenn diese existiert, sonst wird eine neue erstellt
     *
     * @return
     */
    public static TilePanel getInstance() {
        if (instance == null) {
            instance = new TilePanel();
        }
        return instance;
    }

    /**
     * Getter-Methode für die TilePanel-Instanz wenn diese existiert
     *
     * @return
     */
    public static TilePanel getExistingInstance() {
        if (instance == null) {
            throw new RuntimeException("Instanz von TilePanel existiert noch nicht");
        } else {
            return instance;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(tilesetBufferedImage, 0, 0, GuiData.tilesetWidth, GuiData.tilesetHeight, null);
        g.setColor(Color.RED);
        int selectedBlocksX;
        int selectedBlocksY;

        for (Point point : selectedBlocksList) {
            selectedBlocksX = point.x;
            selectedBlocksY = point.y;
            g.drawRect(selectedBlocksX * TILESIZE, selectedBlocksY * TILESIZE, TILESIZE, TILESIZE);//sollte eigentlich fertig sein an dieser Stelle
        }
        PreviewPanel.getExistingInstance().repaint();
    }

    /**
     * Aus Konstruktor ausgelagerter Code, welcher der Instanzvariable tilesetBufferedImage (BufferedImage) das mittels ImageIO gelesene Bild zuweist
     */
    private void loadBufferedImage() {
        try {
            tilesetBufferedImage = ImageIO.read(new FileInputStream(GuiData.filenameTileset));
        } catch (IOException e) {
            LOG.fatal("Fehler beim Laden von " + GuiData.filenameTileset);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Logic.getInstance().setDeleteInactive();
        PokeEditor.getInstance().getBtnPickupTool().setSelected(false);
        selectedBlocksList.clear();                                                         //Selektierte Blöcke abwählen, da ein neuer Klick neue Blöcke auswählt
        for (int y = 0; y < amountOfSelectedBlocks; y++) {                                  //Entsprechende Schleifendurchgänge je nachdem wie viele Blöcke in
            for (int x = 0; x < amountOfSelectedBlocks; x++) {                                  //X bzw. Y Richtung ausgewählt werden sollen
                selectedBlocksList.add(new Point(((e.getPoint().x / TILESIZE) + x), ((e.getPoint().y / TILESIZE) + y)));  //Koordinaten der Maus durch 16 [Pixel pro Block] ergibt int Zahl, die besagt
                //welcher Block in X bzw. Y Richtung ausgewählt ist. Anschließend wird dieser
            }                                                                                   //Durchgangsnummer (beim ersten mal 0) der selectedBlocksListe zugefügt
        }                                                                                       //Zuerst X, dann Y
        repaint();                                                                          //zwingend erforderlich, damit nach Mausklick neu gezeichnet wird
        refreshPreview();
    }


    /**
     * Bewegt ausgewählten Block nach links, wenn einer ausgewählt ist und wenn dieser nicht schon ganz links ist
     */
    public void moveLeft() {
        if (!selectedBlocksList.isEmpty() && selectedBlocksList.get(0).x > 0) {    //Wenn selectedBlocks nicht leer ist und die ganz linke Auswahl größer ist als 0
            Point previousSelected = new Point(selectedBlocksList.get(0).x, selectedBlocksList.get(0).y);               //speichert vor den nachfolgenden Aktionen den gewählten Block zwischen
            selectedBlocksList.clear();                                     //löscht alle selektieren Blocks aus der Liste
            for (int y = 0; y < amountOfSelectedBlocks; y++) {          //durchläuft for Schleife je nach Anzahl der selektierten Blocks und speichert die vorherigen Werte(x-1)
                for (int x = 0; x < amountOfSelectedBlocks; x++) {
                    selectedBlocksList.add(new Point((previousSelected.x - 1) + x, (previousSelected.y) + y));
                }
            }
            repaint();
            refreshPreview();
        }
    }

    /**
     * Bewegt ausgewählten Block nach rechts, wenn einer ausgewählt ist und wenn dieser nicht schon ganz rechts ist
     */
    public void moveRight() {


        if (!selectedBlocksList.isEmpty() && selectedBlocksList.get((selectedBlocksList.size() - 1)).x < ((GuiData.tilesetWidth/TILESIZE)-1)) { //Wenn selectedBlocks nicht leer ist und die ganz rechte Auswahl kleiner ist als 15(bei 256 Pixel Breite)
            Point previousSelected = new Point(selectedBlocksList.get(0).x, selectedBlocksList.get(0).y);                                      //speichert vor den nachfolgenden Aktionen den gewählten Block zwischen
            selectedBlocksList.clear();                                                             //löscht alle selektieren Blocks aus der Liste
            for (int y = 0; y < amountOfSelectedBlocks; y++) {         //durchläuft for Schleife je nach Anzahl der selektierten Blocks und speichert die vorherigen Werte(x+1)
                for (int x = 0; x < amountOfSelectedBlocks; x++) {
                    selectedBlocksList.add(new Point((previousSelected.x + 1) + x, (previousSelected.y) + y));
                }
            }
            repaint();
            refreshPreview();
        }
    }

    /**
     * Bewegt ausgewählten Block nach oben, wenn einer ausgewählt ist und wenn dieser nicht schon ganz oben ist
     */
    public void moveUp() {
        if (!selectedBlocksList.isEmpty() && selectedBlocksList.get(0).y > 0) {                       //Wenn selectedBlocks nicht leer ist und die oberste Auswahl größer ist als 0
            Point previousSelected = new Point(selectedBlocksList.get(0).x, selectedBlocksList.get(0).y);                                  //speichert vor den nachfolgenden Aktionen den gewählten Block zwischen
            selectedBlocksList.clear();                                                         //löscht alle selektieren Blocks aus der Liste
            for (int y = 0; y < amountOfSelectedBlocks; y++) {         //durchläuft for Schleife je nach Anzahl der selektierten Blocks und speichert die vorherigen Werte(y-1)
                for (int x = 0; x < amountOfSelectedBlocks; x++) {
                    selectedBlocksList.add(new Point(previousSelected.x + x, (previousSelected.y - 1) + y));
                }
            }
            repaint();
            refreshPreview();
        }
    }

    /**
     * Bewegt ausgewählten Block nach unten, wenn einer ausgewählt ist und wenn dieser nicht schon ganz unten ist
     */
    public void moveDown() {

        // Dieser Code scrollt nach unten
//        JScrollBar bar = PokeEditor.getInstance().getBar();
//        bar.setValue(bar.getValue() + 100);
//PokeEditor.getInstance().scrollTilePanel();

        if (!selectedBlocksList.isEmpty() && selectedBlocksList.getLast().y < (GuiData.tilesetHeight/TILESIZE-1)) {  //Wenn selectedBlocks nicht leer ist und die unterste Auswahl kleiner ist als 997
            Point previousSelected = new Point(selectedBlocksList.get(0).x, selectedBlocksList.get(0).y);    //speichert vor den nachfolgenden Aktionen den gewählten Block zwischen
            selectedBlocksList.clear();                                                              //löscht alle selektieren Blocks aus der Liste
            for (int y = 0; y < amountOfSelectedBlocks; y++) {          //durchläuft for Schleife je nach Anzahl der selektierten Blocks und speichert die vorherigen Werte(y+1)
                for (int x = 0; x < amountOfSelectedBlocks; x++) {
                    selectedBlocksList.add(new Point(previousSelected.x + x, (previousSelected.y + 1) + y));
                }
            }
            repaint();
            refreshPreview();
        }
    }

    private void refreshSelectedBlocks() {
        if (!selectedBlocksList.isEmpty()) {                                        //Wenn die Liste nicht leer ist dann
            Point selectedBlocks = selectedBlocksList.get(0);                         //hole Punkt
            selectedBlocksList.clear();
            for (int y = 0; y < amountOfSelectedBlocks; y++) {                       //fülle Liste
                for (int x = 0; x < amountOfSelectedBlocks; x++) {
                    selectedBlocksList.add(new Point(((selectedBlocks.x + x)), (selectedBlocks.y + y)));
                }
            }
        }
        PokeEditor.getInstance().refreshTxtFieldAmountOfSelectedBlocks();               //Textfield aktualisieren
    }


    public LinkedList<Point> getSelectedBlocksList() {
        return selectedBlocksList;
    }


    public void setSelectedBlocksList(LinkedList<Point> selectedBlocksList) {
        this.selectedBlocksList = selectedBlocksList;
    }

    public int getAmountOfSelectedBlocks() {
        return amountOfSelectedBlocks;
    }

    public void setAmountOfSelectedBlocks(int amountOfSelectedBlocks) {
        this.amountOfSelectedBlocks = amountOfSelectedBlocks;
        refreshSelectedBlocks();
        repaint();
    }

    /**
     * Getter für Koordinaten des Tiles auf dem ein Mausevent ausgeführt wird
     *
     * @param e
     * @return
     */
    public Point getBlock(MouseEvent e) {
        return new Point((e.getPoint().x / TILESIZE), (e.getPoint().y / TILESIZE));
    }

    /**
     * Getter für das bereits geladene Tileset-BufferedImage
     *
     * @return
     * @throws Exception wenn Tileset-Bild noch nicht geladen wurde
     */
    public BufferedImage getBufferedImage() throws Exception {
        if (tilesetBufferedImage == null) {
            throw new RuntimeException("Das Tileset-Bild wurde noch nicht geladen");
        }
        return tilesetBufferedImage;
    }

    public Point getMouseOnPosition() {
        return mouseOnPosition;
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        PreviewPanel.getExistingInstance().repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseOnPosition = getBlock(e);
        repaint();
        PreviewPanel.getExistingInstance().repaint();
//        wenn maus verlässt dann ausgewählten block in mouseOnPosition schreiben
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseOnPosition = getPointOfFirstSelectedBlock();
        PreviewPanel.getInstance().repaint();
    }

    private Point getPointOfFirstSelectedBlock() {
        if (selectedBlocksList.isEmpty()) {                                                                            //wenn kein Block selektiert ist, dann
            selectedBlocksList.add(new Point(0, 0));                                                              //selektiere Block 0,0
        }
        return selectedBlocksList.getFirst();
    }

    public void refreshPreview() {
        mouseOnPosition = getPointOfFirstSelectedBlock();
        PreviewPanel.getExistingInstance().repaint();
    }
}