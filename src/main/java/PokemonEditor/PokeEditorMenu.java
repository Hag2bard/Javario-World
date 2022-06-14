package PokemonEditor;

import PokemonEditor.media.MediaPlayer;

import javax.swing.*;
import java.util.LinkedList;

public class PokeEditorMenu extends JMenuBar {

    private final MapPanel mapPanel;
    private final TilePanel tilePanel;
    private final Logic logic;
    private PokeEditor pokeEditor;

    public PokeEditorMenu() {
        mapPanel = MapPanel.getInstance();
        tilePanel = TilePanel.getInstance();
        logic = Logic.getInstance();
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

        menuItemFileSave.addActionListener(e -> new SaveMap(mapPanel));

        menuItemFileOpen.addActionListener(e -> {
            LoadMap loadMap = new PokemonEditor.LoadMap();
            if (loadMap.getMapString() != null) {
                mapPanel.setMapLayer1(loadMap.getLoadedMap()[0]);
                mapPanel.setMapLayer2(loadMap.getLoadedMap()[1]);
                mapPanel.setSelectedX(0);
                mapPanel.setSelectedY(0);
                tilePanel.setSelectedBlocksList(new LinkedList<>());
                pokeEditor = PokeEditor.getInstance();
                pokeEditor.repaintMapCreatorJFrame();
            }
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
            mapPanel.getMapLayer1().clean(GuiData.fieldHeight, GuiData.fieldWidth);
            mapPanel.getMapLayer2().clean(GuiData.fieldHeight, GuiData.fieldWidth);
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
            logic.setMap(mapPanel.getMapLayer1(), mapPanel.getMapLayer2());
            logic.setDeleteActive();
        });
    }
}
