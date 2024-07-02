package PokemonEditor;

import PokemonEditor.media.MediaPlayer;
import util.Logger;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class MapEditorMenu extends JMenuBar {

    private final MapPanel mapPanel;
    private final TilePanel tilePanel;
    private MapEditor mapEditor;

    public MapEditorMenu(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
        System.err.println("Konstruktor MapEditorMenu");
        tilePanel = TilePanel.getInstance();
    }


    public void addMenu() {
//        menuBar = new JMenuBar();
        JMenu menuFile =
                new JMenu("Datei");
        JMenu menuEdit =
                new JMenu("Bearbeiten");
        JMenu menuHelp =
                new JMenu("Hilfe");

        this.add(menuFile);
        this.add(menuEdit);
        this.add(menuHelp);
        JMenu menuFileNew =
                new JMenu("Neu");

        menuFile.add(menuFileNew);

        //Hinzufügen von Menüeinträgen in das Dateimenü
        JMenuItem menuItemFileNewText =
                new JMenuItem("Text");
        JMenuItem menuItemFileNewImage =
                new JMenuItem("Bild");
        JMenuItem menuItemFileOpen =
                new JMenuItem("Öffnen");
        JMenuItem menuItemFileSave =
                new JMenuItem("Speichern");
        JMenuItem menuItemFileSaveAs =
                new JMenuItem("Speichern als");
        JMenuItem menuItemFileExit =
                new JMenuItem("Beenden");

        menuItemFileSave.addActionListener(e -> new MapSaver().openJFileChooserForSavingMapLayers(mapPanel.getMapLayer1(), mapPanel.getMapLayer2()));

        menuItemFileOpen.addActionListener(e -> {
            FileLoader fileLoader = new FileLoader();
            BlockList[] loadedMap;
            try {
                loadedMap = fileLoader.loadMap();
                mapPanel.setMapLayer1(loadedMap[0]);
                mapPanel.setMapLayer2(loadedMap[1]);
            }
            catch (FileNotFoundException fileNotFoundException) {
                String errorMessage = "Sie haben keine Datei ausgewählt!";
                Logger.error(errorMessage);
                JOptionPane.showMessageDialog(null, errorMessage);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            mapPanel.setSelectedX(0);
            mapPanel.setSelectedY(0);
            tilePanel.setSelectedBlocksList(new LinkedList<>());
            mapEditor = MapEditor.getInstance("Methode addMenu in MapEditorMenu");
            mapEditor.repaintMapCreatorJFrame();
        });


        menuFileNew.add(menuItemFileNewText);
        menuFileNew.add(menuItemFileNewImage);
        menuFile.add(menuItemFileOpen);
        menuFile.add(menuItemFileSave);
        menuFile.add(menuItemFileSaveAs);
        menuFile.addSeparator();
        menuFile.add(menuItemFileExit);

        JMenuItem menuItemEditDelete =
                new JMenuItem("Block Löschen");
        JMenuItem menuItemEditClean =
                new JMenuItem("Bereinigen");
        JMenuItem menuItemEditPaste =
                new JMenuItem("Einfügen");

        menuItemEditClean.addActionListener(e -> {
            mapPanel.getMapLayer1().clean(RGuiSizes.MAP_FIELD_HEIGHT, RGuiSizes.MAP_FIELD_WIDTH);
            mapPanel.getMapLayer2().clean(RGuiSizes.MAP_FIELD_HEIGHT, RGuiSizes.MAP_FIELD_WIDTH);
        });

        menuEdit.add(menuItemEditDelete);
        menuEdit.add(menuItemEditClean);
        menuEdit.add(menuItemEditPaste);

        //Hinzufügen von Menüeinträgen in das Hilfemenü
        JMenuItem menuItemHelpHelp =
                new JMenuItem("Musik");
        menuItemHelpHelp.addActionListener(e -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.playWav();
        });
        menuHelp.add(menuItemHelpHelp);

        //Hinzufügen der Menüleiste zum Frame


        menuItemEditDelete.addActionListener(e -> {
            mapEditor.switchDeleteFunction();
        });
    }
}
