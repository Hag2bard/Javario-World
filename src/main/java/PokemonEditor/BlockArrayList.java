package PokemonEditor;


import java.awt.*;
import java.util.*;

public class BlockArrayList {

    private final LinkedList<Block> blockArrayList;
    private boolean isReplaceable = false;

    /**
     * Die ArrayList wird erstellt
     */
    public BlockArrayList() {
        this.blockArrayList = new LinkedList<>();
    }

    public BlockArrayList(LinkedList<Block> blockArrayList) {
        this.blockArrayList = blockArrayList;
    }

    /**
     * prüft, ob ein Block mit den übergegebenen Zielkoordinaten existiert und gibt ein boolean zurück-
     *
     * @param destinationX ZielX nicht als Pixelkoordinaten
     * @param destinationY ZielY nicht als Pixelkoordinaten
     * @return wenn im BlockArray existiert, dann gibt diese Methode ein true zurück
     */
    public boolean doesExist(int destinationX, int destinationY) {
        for (Block block : blockArrayList) {
            if (block.getDestinationX() == destinationX && block.getDestinationY() == destinationY) { //gibts diese X und Y Koordinaten in dieser Kombi in der Map?
                //übergebenenen Koordinaten verglichen und wenn
                //beide Koordinaten übereinstimmen wird true zurückgegeben
                return true;
            }
        }
        return false;                 //wenn das ganze Array durchlaufen wurde und kein true geworfen wurde, dann werfe ein false
    }


    /**
     * die übergegebenen Ziel-Koordinaten
     *
     * @param destinationX ZielX nicht als Pixelkoordinaten
     * @param destinationY ZielY nicht als Pixelkoordinaten
     * @return index des BlockArrays an dem die übergebenen Koordinaten liegen, wenn nicht existent, dann wird -1 zurückgegeben
     */
    public int indexOfCoordinates(int destinationX, int destinationY) {
        for (int i = 0; i < blockArrayList.size(); i++) {
            if (blockArrayList.get(i).getDestinationX() == destinationX && blockArrayList.get(i).getDestinationY() == destinationY) {
                return i;
            }
        }
        return -1;
    }
    // JUNIT Wenn Block existiert aber Rückgabewert -1, dann Exception???

    public void sort() {
        blockArrayList.sort(Comparator
                .comparingInt(Block::getDestinationY)
                .thenComparingInt(Block::getDestinationX));
    }

    public void add(int sourceX, int sourceY, int destinationX, int destinationY) {
        if (doesExist(destinationX, destinationY)) {
            if (isReplaceable || blockArrayList
                    .get(indexOfCoordinates(destinationX, destinationY))
                    .isDummy()) { //wenn Blöcke ersetzt werden dürfen oder der Block nur ein Dummy Block ist, dann ersetze ihn
                blockArrayList
                        .get(indexOfCoordinates(destinationX, destinationY))
                        .setSource(new Point(sourceX, sourceY))
                        .setDummy(false);
            }
        }
        if (!doesExist(destinationX, destinationY)) {
            blockArrayList.add(new Block(sourceX, sourceY, destinationX, destinationY));
            blockArrayList
                    .getLast()
                    .setDummy(false);
        }
    }


    /**
     * löscht Blöcke mit übergebenen Ziel Koordinaten (wird zu Dummy umgewandelt)
     *
     * @param destinationX ZielX
     * @param destinationY ZielY
     */
    public void delete(int destinationX, int destinationY) {
        for (Block block : blockArrayList) {
            if (block.getDestinationX() == destinationX && block.getDestinationY() == destinationY) {
                block
                        .setDummy(true)
                        .setSource(new Point(-1, -1));
            }
        }
    }

    /**
     * löscht Blöcke mit übergebenen Ziel Koordinaten
     *
     * @param destinationX ZielX
     * @param destinationY ZielY
     */
    public void safeDelete(int destinationX, int destinationY) {
        for (int i = 0; i < blockArrayList.size(); i++) {
            if (blockArrayList.get(i).getDestinationX() == destinationX
                    &&
                    blockArrayList.get(i).getDestinationY() == destinationY) {
                blockArrayList.remove(i);
            }
        }
    }

    /**
     * setzt Dummy-boolean eines Blocks mit ungültigen Koordinaten auf true
     */
    public void deleteDummyBlocks() {
        for (Block block : blockArrayList) {
            if (block.getSourceX() < 0 || block.getSourceX() > 7 || block.getSourceY() > 997) {
//                blockArrayList.remove(i);
                block.setDummy(true);
            }
        }
    }

    /**
     * gibt die Größe der BlockArrayList zurück
     *
     * @return Größe der BlockArrayList
     */
    public int size() {
        return blockArrayList.size();
    }

    public Block get(int index) {
        return blockArrayList.get(index);
    }

    public boolean isReplaceable() {
        return isReplaceable;
    }

    public void setReplaceable(boolean replaceable) {
        isReplaceable = replaceable;
    }

    //TODO Nochmal schauen ob das hier nötig ist
    public void clean(int fieldHeight, int fieldWidth) {
        for (int y = 0; y < fieldHeight + 10; y++) {
            for (int x = fieldWidth; x < fieldWidth + 10; x++) {
                safeDelete(x, y);
            }
        }
        for (int y = fieldHeight; y < fieldHeight + 10; y++) {
            for (int x = 0; x < fieldWidth + 10; x++) {
                if (doesExist(x, y)) {
                    safeDelete(x, y);
                }
            }
        }
    }


    private LinkedList<Block> getCopyOfInternalList() {
        LinkedList<Block> copyOfList = new LinkedList<>(blockArrayList);
        return copyOfList;
    }

    public BlockArrayList getClone() {
        return new BlockArrayList(getCopyOfInternalList());
    }

    public void restore(BlockArrayList backup) {
        blockArrayList.clear();
        for (int i = 0; i < backup.size(); i++) {
            blockArrayList.add(backup.get(i));
        }
    }
}
