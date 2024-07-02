package PokemonEditor;


import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Diese Klasse enthält die Map
 */
public class BlockList {

    // Je nach Größe

    @Getter
    private final ArrayList<Block> actualBlockList;
    @Getter
    private final LinkedList<BlockList> undoBlockList = new LinkedList<>();
    @Getter
    private final LinkedList<BlockList> redoBackupBlockList = new LinkedList<>();


    /**
     * Die ArrayList wird erstellt
     */
    public BlockList(boolean fillMapWithDummys) {
        this.actualBlockList = new ArrayList<>();
        if (fillMapWithDummys) {
            fillMapWithDummyBlocks();
        }
    }

    public void addCloneToUndoBlockList(BlockList blockListClone) {
        undoBlockList.add(blockListClone);
    }

    public void addCloneToRedoBlockList(BlockList blockListClone) {
        redoBackupBlockList.add(blockListClone);
    }

    public BlockList(ArrayList<Block> actualBlockList) {
        this.actualBlockList = actualBlockList;
    }

    /**
     * Diese Methode füllt die gesamte Map mit DummyBlocks, als Workaround für das Pickup-Tool
     */
    private void fillMapWithDummyBlocks() {
        for (int destinationY = 0; destinationY < RGuiSizes.MAP_FIELD_HEIGHT; destinationY++) {
            for (int destinationX = 0; destinationX < RGuiSizes.MAP_FIELD_WIDTH; destinationX++) {
                add(-1, -1, destinationX, destinationY);
            }
        }
        refreshDummyBlocks();
    }

    /**
     * Prüft, ob ein Block mit den gegebenen Zielkoordinaten existiert(und auch kein Dummy ist) und gibt ein boolean zurück.
     *
     * @param destinationX ZielX nicht als Pixelkoordinaten
     * @param destinationY ZielY nicht als Pixelkoordinaten
     * @return wenn im BlockArray existiert, dann gibt diese Methode ein true zurück
     */
    public boolean doesExistAndIsNotADummy(int destinationX, int destinationY) {
        for (Block block : actualBlockList) {
            //gibts diese X und Y Koordinaten in dieser Kombi in der Map?
            if (block.getDestinationX() == destinationX && block.getDestinationY() == destinationY) {
                // wenn Block außerdem kein Dummy ist
                if (!block.isDummy()) {
                    return true;
                }
            }
        }
        return false;                 //wenn das ganze Array durchlaufen wurde und kein true geworfen wurde, dann werfe ein false
    }

    public boolean doesExistButIsADummy(int destinationX, int destinationY) {
        for (Block block : actualBlockList) {
            //gibts diese X und Y Koordinaten in dieser Kombi in der Map?
            if (block.getDestinationX() == destinationX && block.getDestinationY() == destinationY) {
                // wenn Block außerdem kein Dummy ist
                if (block.isDummy()) {
                    return true;
                }
            }
        }
        return false;                 //wenn das ganze Array durchlaufen wurde und kein true geworfen wurde, dann werfe ein false
    }


    /**
     * Gibt index anhand übergebener Koordinaten zurück
     *
     * @param destinationX ZielX nicht als Pixelkoordinaten
     * @param destinationY ZielY nicht als Pixelkoordinaten
     * @return index des BlockArrays an dem die übergebenen Koordinaten liegen, wenn nicht existent, dann wird -1 zurückgegeben
     */
    public int getIndexOfMapLayerFromCoordinates(int destinationX, int destinationY) {
        for (int i = 0; i < actualBlockList.size(); i++) {
            if (actualBlockList.get(i).getDestinationX() == destinationX && actualBlockList.get(i).getDestinationY() == destinationY) {
                return i;
            }
        }
        throw new RuntimeException("Hier stimmt etwas nicht!");
//        return -1;
    }
    // JUNIT Wenn Block existiert aber Rückgabewert -1, dann Exception?

    //Komplizierter Sortier-Algorithmus, der dafür zuständig ist die Blockliste nach den Zielkoordinaten
    // zu sortieren.
    // TODO Grund nachtragen
    public void orderByDestinationCoordinates() {
        actualBlockList.sort(Comparator.comparingInt(Block::getDestinationY).thenComparingInt(Block::getDestinationX));
    }

    /**
     * Fügt Block zur gewünschten Position auf der Map hinzu
     *
     * @param sourceX      Quelle X
     * @param sourceY      Quelle Y
     * @param destinationX Ziel X wird durch Mauskoordinaten ermittelt
     * @param destinationY Ziel Y wird durch Mauskoordinaten ermittelt
     */
    public BlockList add(int sourceX, int sourceY, int destinationX, int destinationY) {
        //existiert bereits ein Nicht-Dummy-Block mit diesen Ziel-Koordinaten?
        if (doesExistAndIsNotADummy(destinationX, destinationY)) {
            if (MapEditor.isReplaceToolActive || actualBlockList // ist Blöcke ersetzen aktiviert oder Block auf Map nur Dummy-Block
                    .get(getIndexOfMapLayerFromCoordinates(destinationX, destinationY)) //Hole entsprechenden Block aus Liste
                    .isDummy()) { //wenn Blöcke ersetzt werden dürfen oder der Block nur ein Dummy Block ist, dann ersetze ihn
                actualBlockList.get(getIndexOfMapLayerFromCoordinates(destinationX, destinationY)).setSource(new Point(sourceX, sourceY)).setIsDummy(false) //Echter Block, kein Leerblock/Dummy
                ;
            }
        }
        // Wird aufgerufen, wenn Dummy-Blocks initial auf Karte gesetzt werden.
        // Wenn es nicht existiert und ein Dummy ist!
        if (!doesExistAndIsNotADummy(destinationX, destinationY)) {
            if (doesExistButIsADummy(destinationX, destinationY)) {
                actualBlockList.remove(getIndexOfMapLayerFromCoordinates(destinationX, destinationY));
            }
            boolean isInitialDummyBlock = sourceX == -1;
            actualBlockList.add(new Block(sourceX, sourceY, destinationX, destinationY, isInitialDummyBlock));
            orderByDestinationCoordinates();
        }
        return this;
    }


    /**
     * löscht Block mit übergebenen Ziel-Koordinaten (wird zu Dummy umgewandelt und bekommt Koordinaten -1)
     *
     * @param destinationX ZielX
     * @param destinationY ZielY
     */
    public void delete(int destinationX, int destinationY) {
        for (Block block : actualBlockList) {
            if (block.getDestinationX() == destinationX && block.getDestinationY() == destinationY) {
                block
                        // Ein "gelöschter" Block existiert weiterhin in der blockArrayList, hat aber einmal
                        // das Flag Dummy auf true und die Source auf -1 und -1
                        .setIsDummy(true).setSource(new Point(-1, -1));
            }
        }
    }

    /**
     * Löscht Blöcke mit übergebenen Ziel-Koordinaten aus der Block-Liste.
     *
     * @param destinationX ZielX
     * @param destinationY ZielY
     */
    private void safeDeleteGivenBlock(int destinationX, int destinationY) {
        for (int i = 0; i < actualBlockList.size(); i++) {
            if (actualBlockList.get(i).getDestinationX() == destinationX && actualBlockList.get(i).getDestinationY() == destinationY) {
                actualBlockList.remove(i);
            }
        }
    }

    /**
     * setzt Dummy-Boolean eines Blocks mit ungültigen Koordinaten auf true
     * setzt Dummy-Boolean eines Blocks mit gültigen Koordinaten auf false
     */
    public void refreshDummyBlocks() {
        System.out.println("refreshDummyBlocks");
        for (Block block : actualBlockList) {
            block.refreshDummyStatus();
        }
    }

    /**
     * gibt die Größe der BlockLinkedList zurück
     *
     * @return Größe der BlockLinkedList
     */
    public int size() {
        return actualBlockList.size();
    }

    public Block get(int index) {
        return actualBlockList.get(index);
    }


    //TODO Nochmal schauen ob das hier nötig ist
    // Lösche Blöcke außerhalb des Sichtfeldes, Methode benötigt dafür Sichtfeld
    public void clean(int fieldHeight, int fieldWidth) {
        for (int y = 0; y < fieldHeight + 10; y++) {
            for (int x = fieldWidth; x < fieldWidth + 10; x++) {
                safeDeleteGivenBlock(x, y);
            }
        }
        // Starte bei letztem Feld und gehe noch 10 raus, weil man mehr als 10 Felder außerhalb des Bereichs im
        // GUI sowieso nicht setzen kann.
        for (int y = fieldHeight; y < fieldHeight + 10; y++) {
            // Fang bei x0 an und gehe bis 10 Felder nach draußen um diese zu löschen
            for (int x = 0; x < fieldWidth + 10; x++) {
//                if (doesExistAndIsNotADummy(x, y)) {
                safeDeleteGivenBlock(x, y);
//                }
            }
        }
        orderByDestinationCoordinates();
    }

    /**
     * Klon der internen BlockListe
     *
     * @return Sauberer Klon der internen Liste
     */
    private ArrayList<Block> getCopyOfInternalList() {
        ArrayList<Block> copyOfList = new ArrayList<>();

        for (Block oldBlock : actualBlockList) {
            Block blockCopy = oldBlock.getClone();
            copyOfList.add(blockCopy);
        }
        return copyOfList;
    }

    /**
     * Klone die Map in ihrem jetzigen Zustand
     *
     * @return Klon dieser Map
     */
    public BlockList getClone() {
        return new BlockList(getCopyOfInternalList());
    }

    /**
     * Nach dieser Methode muss ein repaint() erfolgen!
     * Diese BlockList wird mit der gegebenen BlockList ersetzt.
     * Die übergebene BlockList wird sauber gecloned.
     *
     * @param blockListForReplacement Blockliste, die zum Ersetzen genommen werden soll
     */
    public void replaceWithOtherBlockList(BlockList blockListForReplacement) {
        // Aktuelle BlockLinkedListe leeren
        actualBlockList.clear();
        for (int i = 0; i < blockListForReplacement.size(); i++) {
            // Koordinaten aus Block ziehen
            int sourceX = blockListForReplacement.get(i).getSourceX();
            int sourceY = blockListForReplacement.get(i).getSourceY();
            int destinationX = blockListForReplacement.get(i).getDestinationX();
            int destinationY = blockListForReplacement.get(i).getDestinationY();
            boolean isDummy = blockListForReplacement.get(i).isDummy();
            // Neuen Block erstellen und zur leeren Liste hinzufügen
            Block newBlock = new Block(sourceX, sourceY, destinationX, destinationY, isDummy);
            actualBlockList.add(newBlock);
        }
    }
}
