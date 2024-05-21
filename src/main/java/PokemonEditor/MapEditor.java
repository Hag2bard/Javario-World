
package PokemonEditor;

import Pokemon.View;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.NoSuchElementException;

public class MapEditor extends KeyAdapter {

    public final JScrollPane tileJScrollPane;
    private final TilePanel tilePanel = TilePanel.getInstance();
    private final MapPanel mapPanel;
    private final PreviewPanel previewPanel;
    private final JScrollPane mapJScrollPane;
    private JButton btnPickupTool;
    private final JButton btnMultipleSelect;
    private final JCheckBox checkBoxDeleteBlock;
    private JTextField txtFieldAmountOfSelectedBlocks;
    private JTextField txtFieldSelectedLayer;
    private JCheckBox checkBoxReplaceBlock;
    private final JFrame mapCreatorJFrame;
    // Pixel eines Tiles
    private static MapEditor instance;
    private final JScrollBar tilePanelScrollBar;
    private boolean scrollDown = false;
    //Flag, ob Blöcke ersetzt werden dürfen
    //TODO DCH Diese Flag muss in einer anderen Klasse behandelt werden, sie hat mit der Block List nichts zu tun
    // Diese Flag wird gesetzt, wenn man im Editor Ersetzen aktiviert
    static boolean isReplaceToolActive = false;
    static boolean isDeleteToolActive = false;
    static boolean isPickupToolActive = false;


    private MapEditor() {

        mapCreatorJFrame = new JFrame("Map-Editor");                                                                 //Neues JFrame, dieses Fenster
        // TODO Layout-Manager nutzen
        mapCreatorJFrame.setLayout(null);                                                                                     //ohne Layout-Manager
//Auskommentiert weil nach TilePanel verschoben
//        tilePanel.addMouseListener(tilePanel);
//        tilePanel.addMouseMotionListener(tilePanel);
        tilePanel.addKeyListener(this);
        tilePanel.setFocusable(true);
        tilePanel.setPreferredSize(new Dimension(256, 432));

        mapPanel = MapPanel.getInstance();
        System.err.println("Konstruktor MapEditor");
        mapPanel.addMouseListener(mapPanel);
        mapPanel.addMouseMotionListener(mapPanel);
        mapPanel.addKeyListener(this);
        mapPanel.setFocusable(true);
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        mapPanel.setPreferredSize(new Dimension(RGuiSizes.MAP_FIELD_WIDTH * RGuiSizes.TILESIZE_PIXELS * MapPanel.ZOOM, RGuiSizes.MAP_FIELD_HEIGHT * RGuiSizes.TILESIZE_PIXELS * MapPanel.ZOOM));

//        previewPanel = new PreviewPanel();
        previewPanel = PreviewPanel.getInstance();
        previewPanel.addKeyListener(this);
        previewPanel.setFocusable(true);
        previewPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        previewPanel.setBounds(1050, 500, RGuiSizes.TILESIZE_PIXELS * previewPanel.ZOOM, RGuiSizes.TILESIZE_PIXELS * previewPanel.ZOOM);

        checkBoxDeleteBlock = new JCheckBox("<html>Block <u>l</u>öschen</html>");
        checkBoxDeleteBlock.addKeyListener(this);
        checkBoxDeleteBlock.setBounds(1100, 10, 110, 50);
        checkBoxDeleteBlock.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setDeleteActive();
            }
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                setDeleteInactive();
                tilePanel.repaint();
            }
        });

        btnMultipleSelect = new JButton();

        tileJScrollPane = new JScrollPane(tilePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tileJScrollPane.addKeyListener(this);
//        tileJScrollPane.setBounds(1387, 0, GuiData.TILESET_WIDTH + 22, 772);    //128+22
        tileJScrollPane.setBounds(1257, 0, RGuiSizes.TILESET_WIDTH_PIXELS + 22, 772);    //128+22
        tileJScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        tileJScrollPane.setFocusable(true);
        tilePanelScrollBar = tileJScrollPane.getVerticalScrollBar();

        mapJScrollPane = new JScrollPane(mapPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);   //AS NEEDED!!!!
        mapJScrollPane.addKeyListener(this);
        mapJScrollPane.setBounds(0, 0, (RGuiSizes.MAP_FIELD_WIDTH * RGuiSizes.TILESIZE_PIXELS * MapPanel.ZOOM) + 4, (RGuiSizes.MAP_FIELD_HEIGHT * RGuiSizes.TILESIZE_PIXELS * MapPanel.ZOOM) + 4);
        mapJScrollPane.getVerticalScrollBar().setUnitIncrement(20);  //passen 20?
        mapJScrollPane.setFocusable(true);
        ////////////
        MapEditorMenu mapEditorMenu = new MapEditorMenu();
        mapEditorMenu.addMenu();
        addButtonBar();
        ////////////

        mapCreatorJFrame.setJMenuBar(mapEditorMenu);
        mapCreatorJFrame.add(tileJScrollPane); //Tileset
        mapCreatorJFrame.add(mapJScrollPane); //Karte
        mapCreatorJFrame.add(previewPanel); //Vorschaukästchen (zeigt das worauf Maus aktell steht ODER was ausgewählt ist+
        mapCreatorJFrame.add(checkBoxDeleteBlock); //Checkbox Block löschen (oder shortcut l)
        mapCreatorJFrame.add(btnPickupTool); //Button für das Pipettentool (oder shortcut p)
        mapCreatorJFrame.add(txtFieldAmountOfSelectedBlocks); //Anzahl gewählter Blöcke (1x1 oder 2x2, ...)
        mapCreatorJFrame.add(txtFieldSelectedLayer); //Ausgewählter Layer; momentan 3 Layer; 1, 2 zum Bearbeiten; 3 Vorschau
        mapCreatorJFrame.add(checkBoxReplaceBlock); //Blöcke ersetzen checkbox

        mapCreatorJFrame.addKeyListener(this);

        mapCreatorJFrame.pack();
        mapCreatorJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapCreatorJFrame.setVisible(true);

        mapCreatorJFrame.setSize(1650, 700);
        mapCreatorJFrame.setLocationRelativeTo(null);
        mapCreatorJFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //Kurzfristig

//        View view = new View();
//        mapCreator.setVisible(false);

        //

    }

    /**
     * Methode wird getriggert beim Drücken einer Taste
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> tilePanel.moveLeft();
            case KeyEvent.VK_RIGHT -> tilePanel.moveRight();
            case KeyEvent.VK_UP -> tilePanel.moveUp();
            case KeyEvent.VK_DOWN -> tilePanel.moveDown();
            case 107 -> { // NUMPAD +
                // wenn selectedBlocks weniger als 8 dann um 1 erhöhen
                if (tilePanel.getAmountOfSelectedBlocks() < (RGuiSizes.TILESET_WIDTH_PIXELS / RGuiSizes.TILESIZE_PIXELS))
                    tilePanel.setAmountOfSelectedBlocks(tilePanel.getAmountOfSelectedBlocks() + 1);
                mapPanel.repaint();
            }

            case 109 -> { //NUMPAD -
                // wenn selectedBlocks größer 1, dann um 1 reduzieren
                if (tilePanel.getAmountOfSelectedBlocks() > 1)
                    tilePanel.setAmountOfSelectedBlocks(tilePanel.getAmountOfSelectedBlocks() - 1);
                mapPanel.repaint();
            }

            case KeyEvent.VK_L -> switchDeleteFunction();

            case KeyEvent.VK_P -> switchPickupFunction();

            case KeyEvent.VK_E -> switchReplaceFunction();

            case KeyEvent.VK_W -> {
                // Aktuellen Zustand vor redo Befehl in undo Liste speichern
                mapPanel.getMapLayer1().addCloneToUndoBlockList(mapPanel.getMapLayer1().getClone());
                mapPanel.getMapLayer2().addCloneToUndoBlockList(mapPanel.getMapLayer2().getClone());
                try {
                    // Ersetzen des aktuellen Zustands mit dem vorherigen Zustand aus redo Liste
                    mapPanel.getMapLayer1().replaceWithOtherBlockList(mapPanel.getMapLayer1().getRedoBackupBlockList().getLast());
                    mapPanel.getMapLayer2().replaceWithOtherBlockList(mapPanel.getMapLayer2().getRedoBackupBlockList().getLast());
                    // Letzten Zustand aus redo Liste löschen, weil er wieder hergestellt ist
                    mapPanel.getMapLayer1().getRedoBackupBlockList().removeLast();
                    mapPanel.getMapLayer2().getRedoBackupBlockList().removeLast();
                } catch (NoSuchElementException noSuchElementException) {
                    // Wenn doch kein Replacement stattgefunden hat, muss die Methode wieder rückgängig gemacht werden
                    mapPanel.getMapLayer1().getUndoBlockList().removeLast();
                    mapPanel.getMapLayer2().getUndoBlockList().removeLast();
                    Logger.info("Kein weiteres Redo möglich");
                } finally {
                    mapPanel.repaint();
                }

            }
            case KeyEvent.VK_BACK_SPACE -> {
                // Aktuellen Zustand vor undo Befehl in redo Liste speichern
                mapPanel.getMapLayer1().getRedoBackupBlockList().add(mapPanel.getMapLayer1().getClone());
                mapPanel.getMapLayer2().getRedoBackupBlockList().add(mapPanel.getMapLayer2().getClone());
                try {
                    // Ersetzen des aktuellen Zustands mit dem vorherigen Zustand aus undo Liste
                    mapPanel.getMapLayer1().replaceWithOtherBlockList(mapPanel.getMapLayer1().getUndoBlockList().getLast());
                    mapPanel.getMapLayer2().replaceWithOtherBlockList(mapPanel.getMapLayer2().getUndoBlockList().getLast());
                    // Letztes Backup aus undo Liste löschen, weil es wieder hergestellt ist
                    mapPanel.getMapLayer1().getUndoBlockList().removeLast();
                    mapPanel.getMapLayer2().getUndoBlockList().removeLast();
                } catch (NoSuchElementException noSuchElementException) {
                    // Wenn doch kein Replacement stattgefunden hat, muss die Methode wieder rückgängig gemacht werden
                    mapPanel.getMapLayer1().getRedoBackupBlockList().removeLast();
                    mapPanel.getMapLayer2().getRedoBackupBlockList().removeLast();
                    Logger.info("Kein weiteres Undo möglich");
                } finally {
                    mapPanel.repaint();
                }
            }
            case KeyEvent.VK_V -> tilePanel.refreshPreview();
            case KeyEvent.VK_S -> {
                View view = new View();
                mapCreatorJFrame.setVisible(false);
            }


        }
//        System.out.println(e.getKeyCode());                   //Druckt den Key Code
    }


    private void addButtonBar() {
        Icon pickupIconSelected = new ImageIcon("colorpicker2.png");
        Icon pickupIconDeselected = new ImageIcon(("colorpickerINV.png"));

        btnPickupTool = new JButton("pickupIconSelected");
        btnPickupTool.setIcon(pickupIconSelected);
        btnPickupTool.setBounds(1100, 60, 118, 128);
        btnPickupTool.setBorder(BorderFactory.createLineBorder(Color.black));
        btnPickupTool.setSelectedIcon(pickupIconDeselected);
        btnPickupTool.addKeyListener(this);
        btnPickupTool.addActionListener(e -> {
            if (!btnPickupTool.isSelected()) {
                btnPickupTool.setSelected(true);
                setPickupActive();
                setDeleteInactive();
            } else {
                btnPickupTool.setSelected(false);
                setPickupInactive();
            }
        });

        txtFieldAmountOfSelectedBlocks = new JTextField("Anzahl Blöcke: " + tilePanel.getAmountOfSelectedBlocks());
        txtFieldAmountOfSelectedBlocks.setBounds(1100, 240, 118, 70);
        txtFieldAmountOfSelectedBlocks.setBorder(BorderFactory.createLineBorder(Color.black));
        txtFieldAmountOfSelectedBlocks.addActionListener(e -> {
            int amount = 1;
            try {
                amount = Integer.parseInt(txtFieldAmountOfSelectedBlocks.getText());
            } catch (NumberFormatException exception) {
                try {
                    String fieldText = txtFieldAmountOfSelectedBlocks.getText();
                    amount = Integer.parseInt(fieldText.substring(15, 16));
                } catch (StringIndexOutOfBoundsException | NumberFormatException exception1) {
                    JOptionPane.showMessageDialog(null, "Geben Sie eine gültige Zahl ein!");
                }
            }
            if (amount < 1 || amount > (RGuiSizes.TILESET_WIDTH_PIXELS / RGuiSizes.TILESIZE_PIXELS)) {      //Bei 256 Pixel sind das 16
                JOptionPane.showMessageDialog(null, "Geben Sie eine gültige Zahl von 1-" + (RGuiSizes.TILESET_WIDTH_PIXELS / RGuiSizes.TILESIZE_PIXELS) + " ein!");
                amount = 1;
            }
            tilePanel.setAmountOfSelectedBlocks(amount);
            // Hier muss irgendwo noch ein Label informiert werden, welches die aktuellen Daten zeigt, wie z.b. Layer, Anzahl Blöcke, usw...
            mapCreatorJFrame.requestFocus();
        });

        txtFieldSelectedLayer = new JTextField("Layer: " + mapPanel.getSelectedLayer());
        txtFieldSelectedLayer.setBounds(1100, 310, 118, 70);
        txtFieldSelectedLayer.setBorder(BorderFactory.createLineBorder(Color.black));
        txtFieldSelectedLayer.addActionListener(e -> {
            int layer = 0;
            int previouslySelectedLayer = mapPanel.getSelectedLayer();
            try {
                layer = Integer.parseInt(txtFieldSelectedLayer.getText());
            } catch (NumberFormatException exception) {
                try {
                    String fieldText = txtFieldSelectedLayer.getText();
                    layer = Integer.parseInt(fieldText.substring(7, 8));
                } catch (StringIndexOutOfBoundsException | NumberFormatException exception1) {
                    mapPanel.setSelectedLayer(previouslySelectedLayer);
                }
            }
            mapPanel.setSelectedLayer(layer);
            txtFieldSelectedLayer.setText("Layer: " + mapPanel.getSelectedLayer());
            mapPanel.repaint();
            mapCreatorJFrame.requestFocus();
        });

        checkBoxReplaceBlock = new JCheckBox("Blöcke ersetzen");
        checkBoxReplaceBlock.setBounds(1100, 390, 118, 70);
        checkBoxReplaceBlock.setBorder(BorderFactory.createLineBorder(Color.black));
        checkBoxReplaceBlock.addKeyListener(this);
        checkBoxReplaceBlock.addActionListener(e -> {
            /*
              Beim Klick auf die Checkbox wird der Status geändert (wenn aktiviert -> dann deaktivieren, wenn deaktiviert -> dann aktivieren)
             */
            if (!isReplaceToolActive) {
                checkBoxReplaceBlock.setSelected(true);
                setReplaceActive();
                setDeleteInactive();
                setPickupInactive();
                System.out.println("if");
            } else {
                checkBoxReplaceBlock.setSelected(false);
                setReplaceInactive();
                System.out.println("else");

            }
        });

    }

    /**
     * Aktiviere bzw. deaktiviere die Ersetzen-Funktion
     */
    private void switchReplaceFunction() {
        MapEditor.isReplaceToolActive = !MapEditor.isReplaceToolActive;
        // Nur wenn das Ersetzen aktiviert wurde, muss die DeleteBox deaktiviert werden
        if (MapEditor.isReplaceToolActive) {
            checkBoxDeleteBlock.setSelected(false);
        }
        checkBoxReplaceBlock.setSelected(MapEditor.isReplaceToolActive);

        setDeleteInactive();
        setPickupInactive();
    }

    public void switchPickupFunction() {
        MapEditor.isPickupToolActive = !MapEditor.isPickupToolActive;
        btnPickupTool.setSelected(MapEditor.isPickupToolActive);
        setDeleteInactive();
        setReplaceInactive();
    }

    public void switchDeleteFunction() {
        MapEditor.isDeleteToolActive = !MapEditor.isDeleteToolActive;
        checkBoxDeleteBlock.setSelected(MapEditor.isDeleteToolActive);

//        setPickupInactive();
//        setReplaceInactive();
//        tilePanel.repaint();
//        mapPanel.repaint();
//        mapCreatorJFrame.repaint();
    }

    public void setDeleteActive() {
        MapEditor.isDeleteToolActive = true;
        checkBoxDeleteBlock.setSelected(true);

        MapEditor.isPickupToolActive = false;
        btnPickupTool.setSelected(false);

        MapEditor.isReplaceToolActive = false;
        checkBoxReplaceBlock.setSelected(false);
    }

    public void setDeleteInactive() {
        MapEditor.isDeleteToolActive = false;
        getCheckBoxDeleteBlock().setSelected(false);
    }

    public void setPickupActive() {
        MapEditor.isPickupToolActive = true;
        btnPickupTool.setSelected(true);

        MapEditor.isDeleteToolActive = false;
        checkBoxDeleteBlock.setSelected(false);

        MapEditor.isReplaceToolActive = false;
        checkBoxReplaceBlock.setSelected(false);
    }

    public void setPickupInactive() {
        MapEditor.isPickupToolActive = false;
        btnPickupTool.setSelected(false);
    }

    public void setReplaceActive() {
        MapEditor.isReplaceToolActive = true;
        checkBoxReplaceBlock.setSelected(true);
    }

    public void setReplaceInactive() {
        MapEditor.isReplaceToolActive = false;
        checkBoxReplaceBlock.setSelected(false);
    }

    public void repaintMapCreatorJFrame() {
        mapCreatorJFrame.repaint();
    }

    /**
     * Singleton-Pattern
     *
     * @param caller Wer ruft die Methode auf?
     * @return
     */
    public static MapEditor getInstance(String caller) {
        if (instance == null) {
            System.err.println("Instanz ist NULL von PokeEditor, Aufrufer: " + caller);
            instance = new MapEditor();
        }
        System.out.println(caller);
        return instance;
    }

    public void updateTxtFieldAmountOfSelectedBlocks() {
        txtFieldAmountOfSelectedBlocks.setText("Anzahl Blöcke: " + tilePanel.getAmountOfSelectedBlocks());
    }


    public JCheckBox getCheckBoxDeleteBlock() {
        return checkBoxDeleteBlock;
    }


}
