package PokemonEditor;


import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

@State(Scope.Benchmark)
public class MapPanelTest extends AbstractBenchmarkHarnessForTests{
    private MapPanel mapPanel = MapPanel.getInstance();

    @Setup
    public void initializeNewMapPanel(){
        System.err.println("setup");
        MapPanel.deleteInstanceForTestOnly();
        mapPanel = MapPanel.getInstance();
    }

    @Benchmark
    public void getInstance() {
        Assertions.assertEquals(mapPanel, MapPanel.getInstance());

    }

    @Benchmark
    public void deleteBlocks() {
        mapPanel.setSelectedLayer(1);
        int destinationX = 32;
        int destinationY = 11;
        int sourceX = 3;
        int sourceY = 5;
        BlockList mapLayer1 = mapPanel.getMapLayer1().add(sourceX, sourceY, destinationX, destinationY);
        Block block = mapLayer1.get(mapLayer1.getIndexOfMapLayerFromCoordinates(destinationX, destinationY));
        Assertions.assertEquals(sourceX, block.getSourceX());
        Assertions.assertEquals(sourceY, block.getSourceY());
        // Wenn hier Block mit sourceX und sourceY -1 gesetzt wurden, dann ist das natürlich ein DummyBlock
        Assertions.assertFalse(block.isDummy());
        Assertions.assertEquals(destinationX, block.getDestinationX());
        Assertions.assertEquals(destinationY, block.getDestinationY());
        mapPanel.deleteBlocks(destinationX, destinationY, 1);
        mapLayer1 = mapPanel.getMapLayer1();
        block = mapLayer1.get(mapLayer1.getIndexOfMapLayerFromCoordinates(destinationX, destinationY));
        Assertions.assertEquals(-1, block.getSourceX());
        Assertions.assertEquals(-1, block.getSourceY());
        Assertions.assertTrue(block.isDummy());
        Assertions.assertEquals(destinationX, block.getDestinationX());
        Assertions.assertEquals(destinationY, block.getDestinationY());
    }


    /**
     * Testet die mousePressed-Methode, welche einen Klon der Map erstellt.
     * Getestet wird die Sortierfunktion der Map nach dem adden eines Blocks,
     */
    @Benchmark
    public void mousePressed() {
        BlockList mapLayer1 = mapPanel.getMapLayer1();
        BlockList mapLayer2 = mapPanel.getMapLayer2();
        int sourceX1 = 99;
        int sourceX2 = 33;
        int sourceY1 = 98;
        int sourceY2 = 32;
        int destinationX1 = 0;
        int destinationX2 = 0;
        int destinationY1 = 0;
        int destinationY2 = 0;

        mapLayer1.add(sourceX1, sourceY1, destinationX1, destinationY1);
        mapLayer2.add(sourceX2, sourceY2, destinationX2, destinationY2);
        // Todo brauchts die nächsten 2 Zeilen?
        LinkedList<BlockList> undoBlockListLayer1 = mapPanel.getUndoBlockListLayer1();
        LinkedList<BlockList> undoBlockListLayer2 = mapPanel.getUndoBlockListLayer2();
        // Diese Methode erstellt einen Klon der Map und schmeißt ihn in die undoBlockList
        mapPanel.mousePressed(new MouseEvent(new Button(), 0, 0L, 88, 0, 0, 0, false, 0));
        //Index der übergebenen Ziel-Koordinaten
        int indexOfMapLayer1FromCoordinates = undoBlockListLayer1.getFirst().getIndexOfMapLayerFromCoordinates(destinationX1, destinationY1);
        int indexOfMapLayer2FromCoordinates = undoBlockListLayer2.getFirst().getIndexOfMapLayerFromCoordinates(destinationX2, destinationY2);
        // Teste, dass Listen sortiert sind.
        Assertions.assertEquals(0, indexOfMapLayer1FromCoordinates);
        Assertions.assertEquals(0, indexOfMapLayer2FromCoordinates);
        // Testet, dass erste Backup Liste noch jungfräulich ist, also nur Dummy Blöcke hat.
        Assertions.assertEquals(-1, undoBlockListLayer1.get(0).get(indexOfMapLayer1FromCoordinates).getSourceX());
        Assertions.assertEquals(-1, undoBlockListLayer1.get(0).get(indexOfMapLayer1FromCoordinates).getSourceY());
        Assertions.assertEquals(-1, undoBlockListLayer2.get(0).get(indexOfMapLayer2FromCoordinates).getSourceX());
        Assertions.assertEquals(-1, undoBlockListLayer2.get(0).get(indexOfMapLayer2FromCoordinates).getSourceY());
        // Nach dem 1. Durchgang der mousePressed-Methode besitzt die Backup-Liste nun 2 Einträge.
        // Der 2. Eintrag ist unser hinzugefügter Block
        Block blockOfLayer1 = undoBlockListLayer1.get(1).get(indexOfMapLayer1FromCoordinates);
        Block blockOfLayer2 = undoBlockListLayer2.get(1).get(indexOfMapLayer2FromCoordinates);
        Assertions.assertEquals(blockOfLayer1.getDestinationX(), destinationX1);
        Assertions.assertEquals(blockOfLayer1.getDestinationY(), destinationY1);
        Assertions.assertEquals(blockOfLayer1.getSourceX(), sourceX1);
        Assertions.assertEquals(blockOfLayer1.getSourceY(), sourceY1);
        Assertions.assertEquals(blockOfLayer2.getDestinationX(), destinationX2);
        Assertions.assertEquals(blockOfLayer2.getDestinationY(), destinationY2);
        Assertions.assertEquals(blockOfLayer2.getSourceX(), sourceX2);
        Assertions.assertEquals(blockOfLayer2.getSourceY(), sourceY2);
        Assertions.assertEquals(2, undoBlockListLayer1.size());
        Assertions.assertEquals(2, undoBlockListLayer2.size());
        //Frisst wahrscheinlich Performance, aber es geht ja auch nicht darum die reele Geschwindigkeit,
        //sondern einen Referenz-Wert zu bekommen. Dies erstellt neues MapPanel
        this.initializeNewMapPanel();
    }


    @Benchmark
    public void mouseReleased() {
        // 20, 16
        BlockList mapLayer1 = mapPanel.getMapLayer1();
        BlockList mapLayer2 = mapPanel.getMapLayer2();

        mapLayer1.add(1, 1, 0, 0);
        //6,7ter Index
        //Dies führt dazu, dass wir einen Eintrag mehr haben als FIELD_HEIGHT*FIELD_WITH
        mapLayer1.add(2, 2, 16, 0);
        Assertions.assertEquals((RGuiSizes.MAP_FIELD_WIDTH * RGuiSizes.MAP_FIELD_HEIGHT) + 1, mapLayer1.size());
        mapLayer2.add(4, 4, 0, 6);
        Assertions.assertEquals((RGuiSizes.MAP_FIELD_WIDTH * RGuiSizes.MAP_FIELD_HEIGHT), mapLayer2.size());
        //Dies führt zum Größer werden der Liste um einen Eintrag
        mapLayer2.add(4, 4, 0, 20);
        Assertions.assertEquals((RGuiSizes.MAP_FIELD_WIDTH * RGuiSizes.MAP_FIELD_HEIGHT) + 1, mapLayer2.size());
        // Reinigen der Map, also Löschen der Blöcke, die nicht im Sichtbereich sind.
        mapPanel.getMapLayer1().clean(RGuiSizes.MAP_FIELD_HEIGHT, RGuiSizes.MAP_FIELD_WIDTH);
        Assertions.assertEquals((RGuiSizes.MAP_FIELD_WIDTH * RGuiSizes.MAP_FIELD_HEIGHT), mapLayer1.size());
        mapPanel.getMapLayer2().clean(RGuiSizes.MAP_FIELD_HEIGHT, RGuiSizes.MAP_FIELD_WIDTH);
        Assertions.assertEquals((RGuiSizes.MAP_FIELD_WIDTH * RGuiSizes.MAP_FIELD_HEIGHT), mapLayer2.size());
    }

}