
package PokemonEditor;

import Pokemon.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PokeEditor extends KeyAdapter {

    public final JScrollPane tileJScrollPane;
    private final TilePanel tilePanel = TilePanel.getInstance();
    private final MapPanel mapPanel;
    private final PreviewPanel previewPanel;
    private final JScrollPane mapJScrollPane;
    private JButton btnPickupTool;
    private final JButton btnMultipleSelect;
    private final JCheckBox btnDeleteBlock;
    private JTextField txtFieldAmountOfSelectedBlocks;
    private JTextField txtFieldSelectedLayer;
    private JCheckBox checkBoxReplaceBlock;
    private final JFrame mapCreator;
    private final int TILESIZE = 16;
    private final Logic logic;
    private static PokeEditor instance;
    private final JScrollBar tilePanelScrollBar;
    private boolean scrollDown = false;
    private int lastBlock = 47;

    private PokeEditor() {

        mapCreator = new JFrame("Pokemap-Creator");                                                                 //Neues JFrame, dieses Fenster
        mapCreator.setLayout(null);                                                                                     //ohne Layout-Manager

        tilePanel.addMouseListener(tilePanel);
        tilePanel.addMouseMotionListener(tilePanel);
        tilePanel.addKeyListener(this);
        tilePanel.setFocusable(true);
        tilePanel.setPreferredSize(new Dimension(256, 432));

        mapPanel = MapPanel.getInstance();
        mapPanel.addMouseListener(mapPanel);
        mapPanel.addMouseMotionListener(mapPanel);
        mapPanel.addKeyListener(this);
        mapPanel.setFocusable(true);
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        mapPanel.setPreferredSize(new Dimension(GuiData.fieldWidth * TILESIZE * MapPanel.ZOOM, GuiData.fieldHeight * TILESIZE * MapPanel.ZOOM));

//        previewPanel = new PreviewPanel();
        previewPanel = PreviewPanel.getInstance();
        previewPanel.addKeyListener(this);
        previewPanel.setFocusable(true);
        previewPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        previewPanel.setBounds(1200, 500, TILESIZE * previewPanel.ZOOM, TILESIZE * previewPanel.ZOOM);

        logic = Logic.getInstance();
        btnDeleteBlock = new JCheckBox("<html>Block <u>l</u>öschen</html>");
        btnDeleteBlock.addKeyListener(this);
        btnDeleteBlock.setBounds(1250, 10, 110, 50);
        btnDeleteBlock.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                logic.setMap(mapPanel.getMapLayer1(), mapPanel.getMapLayer2());
                logic.setDeleteActive();
            }
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                logic.setDeleteInactive();
                tilePanel.repaint();
            }
        });

        btnMultipleSelect = new JButton();

        tileJScrollPane = new JScrollPane(tilePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tileJScrollPane.addKeyListener(this);
        tileJScrollPane.setBounds(1387, 0, GuiData.tilesetWidth + 22, 772);    //128+22
        tileJScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        tileJScrollPane.setFocusable(true);
        tilePanelScrollBar = tileJScrollPane.getVerticalScrollBar();

        mapJScrollPane = new JScrollPane(mapPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);   //AS NEEDED!!!!
        mapJScrollPane.addKeyListener(this);
        mapJScrollPane.setBounds(0, 0, (GuiData.fieldWidth * TILESIZE * MapPanel.ZOOM) + 4, (GuiData.fieldHeight * TILESIZE * MapPanel.ZOOM) + 4);
        mapJScrollPane.getVerticalScrollBar().setUnitIncrement(20);  //passen 20?
        mapJScrollPane.setFocusable(true);
        ////////////
        PokemonEditor.PokeEditorMenu pokeEditorMenu = new PokemonEditor.PokeEditorMenu();
        pokeEditorMenu.addMenu();
        addButtonBar();
        ////////////

        mapCreator.setJMenuBar(pokeEditorMenu);
        mapCreator.add(tileJScrollPane);
        mapCreator.add(mapJScrollPane);
        mapCreator.add(previewPanel);
        mapCreator.add(btnDeleteBlock);
        mapCreator.add(btnPickupTool);
        mapCreator.add(txtFieldAmountOfSelectedBlocks);
        mapCreator.add(txtFieldSelectedLayer);
        mapCreator.add(checkBoxReplaceBlock);

        mapCreator.addKeyListener(this);

        mapCreator.pack();
        mapCreator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapCreator.setVisible(true);

        mapCreator.setSize(1650, 700);
        mapCreator.setLocationRelativeTo(null);
        mapCreator.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //Kurzfristig

        View view = new View();
        mapCreator.setVisible(false);

        //

    }


    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> tilePanel.moveLeft();
            case KeyEvent.VK_RIGHT -> tilePanel.moveRight();
            case KeyEvent.VK_UP -> tilePanel.moveUp();
            case KeyEvent.VK_DOWN -> tilePanel.moveDown();


            case 107 -> {                                                       //NUMPAD +
                if (tilePanel.getAmountOfSelectedBlocks() < (GuiData.tilesetWidth / TILESIZE))  //TODO Wieviele Blöcke in X Richtung
                    tilePanel.setAmountOfSelectedBlocks(tilePanel.getAmountOfSelectedBlocks() + 1);
                //wenn selectedBlocks weniger als 8 dann um 1 erhöhen
                mapPanel.repaint();
            }

            case 109 -> {                                                       //NUMPAD -
                if (tilePanel.getAmountOfSelectedBlocks() > 1)
                    tilePanel.setAmountOfSelectedBlocks(tilePanel.getAmountOfSelectedBlocks() - 1);
                //wenn selectedBlocks mehr als 1 dann um 1 reduzieren
                mapPanel.repaint();
            }

            case KeyEvent.VK_L -> btnDeleteBlock.setSelected(!btnDeleteBlock.isSelected());

            case KeyEvent.VK_P -> {
                if (!btnPickupTool.isSelected()) {
                    btnPickupTool.setSelected(true);
                    mapPanel.activatePickupTool();
                    //TODO Löschen deselektieren
                    logic.setDeleteInactive();
                    btnDeleteBlock.setSelected(false);
                } else {
                    btnPickupTool.setSelected(false);
                }
            }
            case KeyEvent.VK_E -> {
                switchCheckBoxReplaceBlock();
                checkBoxReplaceBlock.setSelected(!checkBoxReplaceBlock.isSelected());
            }
            case KeyEvent.VK_BACK_SPACE -> {
                //Kopie wieder einspielen
                mapPanel.getMapLayer1().restore(mapPanel.backupLinkedListLayer1.getLast());
                mapPanel.getMapLayer2().restore(mapPanel.backupLinkedListLayer2.getLast());
                mapPanel.repaint();
                if (mapPanel.backupLinkedListLayer1.size() > 1) {
                    mapPanel.backupLinkedListLayer1.removeLast();
                }
                if (mapPanel.backupLinkedListLayer2.size() > 1) {
                    mapPanel.backupLinkedListLayer2.removeLast();
                }
            }
            case KeyEvent.VK_V -> tilePanel.refreshPreview();
            case KeyEvent.VK_S -> {
                View view = new View();
                mapCreator.setVisible(false);
            }


        }
//        System.out.println(e.getKeyCode());                   //Druckt den Key Code
    }

    private void addButtonBar() {


        Icon pickupIconSelected = new ImageIcon("colorpicker2.png");
        Icon pickupIconDeselected = new ImageIcon(("colorpickerINV.png"));

        btnPickupTool = new JButton("pickupIconSelected");
        btnPickupTool.setIcon(pickupIconSelected);
        btnPickupTool.setBounds(1250, 60, 118, 128);
        btnPickupTool.setBorder(BorderFactory.createLineBorder(Color.black));
        btnPickupTool.setSelectedIcon(pickupIconDeselected);
        btnPickupTool.addKeyListener(this);
        btnPickupTool.addActionListener(e -> {
            if (!btnPickupTool.isSelected()) {
                btnPickupTool.setSelected(true);
                mapPanel.activatePickupTool();
                logic.setDeleteInactive();
                btnDeleteBlock.setSelected(false);
            } else {
                btnPickupTool.setSelected(false);
                mapPanel.deactivatePickupTool();
            }
        });

        txtFieldAmountOfSelectedBlocks = new JTextField("Anzahl Blöcke: " + tilePanel.getAmountOfSelectedBlocks());
        txtFieldAmountOfSelectedBlocks.setBounds(1250, 240, 118, 70);
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
            if (amount < 1 || amount > (GuiData.tilesetWidth/TILESIZE)) {      //Bei 256 Pixel sind das 16
                JOptionPane.showMessageDialog(null, "Geben Sie eine gültige Zahl von 1-"+(GuiData.tilesetWidth/TILESIZE)+" ein!");
                amount = 1;
            }
            tilePanel.setAmountOfSelectedBlocks(amount);
            // Hier muss irgendwo noch ein Label informiert werden, welches die aktuellen Daten zeigt, wie z.b. Layer, Anzahl Blöcke, usw...
            mapCreator.requestFocus();
        });

        txtFieldSelectedLayer = new JTextField("Layer: " + mapPanel.getSelectedLayer());
        txtFieldSelectedLayer.setBounds(1250, 310, 118, 70);
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
            mapCreator.requestFocus();
        });

        checkBoxReplaceBlock = new JCheckBox("Blöcke ersetzen");
        checkBoxReplaceBlock.setBounds(1250, 390, 118, 70);
        checkBoxReplaceBlock.setBorder(BorderFactory.createLineBorder(Color.black));
        checkBoxReplaceBlock.addKeyListener(this);
        checkBoxReplaceBlock.addActionListener(e -> {
            /*
              Beim Klick auf die Checkbox wird der Status geändert (wenn aktiviert -> dann deaktivieren, wenn deaktiviert -> dann aktivieren)
             */
            switchCheckBoxReplaceBlock();
        });

    }

    private void switchCheckBoxReplaceBlock() {
        if (mapPanel.getMapLayer1().isReplaceable() && mapPanel.getMapLayer2().isReplaceable()) {
            mapPanel.getMapLayer1().setReplaceable(false);
            mapPanel.getMapLayer2().setReplaceable(false);
        } else {
            mapPanel.getMapLayer1().setReplaceable(true);
            mapPanel.getMapLayer2().setReplaceable(true);
        }
    }

    public void setSelectionBtnPickupTool(boolean setting) {////
        btnPickupTool.setSelected(setting);
    }

    public void repaintMapCreatorJFrame() {
        mapCreator.repaint();
    }

    public static PokeEditor getInstance() {
        if (instance == null) {
            System.err.println("Instanz ist NULL von PokeEditor");
            instance = new PokeEditor();
        }
        return instance;
    }

    public void refreshTxtFieldAmountOfSelectedBlocks() {
        txtFieldAmountOfSelectedBlocks.setText("Anzahl Blöcke: " + tilePanel.getAmountOfSelectedBlocks());
    }

    public void scrollTilePanel() {
        //heftig langer Code für simple Sache
        //wenn der letzte ausgewählte Block bei einem gewissen Wert ist und man dann runter drückt, wird gescrollt

        if (tilePanel.getSelectedBlocksList().getLast().y >= lastBlock) {
            scrollDown = true;
        }
        if (scrollDown) {
            PokeEditor.getInstance().tileJScrollPane.getVerticalScrollBar().setValue(PokeEditor.getInstance().tileJScrollPane.getVerticalScrollBar().getValue() + TILESIZE);
            lastBlock++;
            scrollDown = false;
        }
    }

    public JScrollBar getTilePanelScrollBar() {
        return tilePanelScrollBar;
    }

    public JButton getBtnPickupTool() {
        return btnPickupTool;
    }

    public JCheckBox getBtnDeleteBlock() {
        return btnDeleteBlock;
    }

}
