package PokemonEditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadMap {

    private String mapString;
    private String[] mapLayer1StringArray;
    private String[] mapLayer2StringArray;
    private String[] layerStringArray;

    private BlockArrayList[] loadedMap;

    public LoadMap() {
        this.mapString = loadMapStringFromFile();
        if (this.mapString != null) {
            loadedMap = convertStringToMap(this.mapString);
        }
        if (this.mapString == null) {
            JOptionPane.showMessageDialog(null, "Sie haben keine Datei ausgewählt!");
        }
    }

    private String loadMapStringFromFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        String mapString = null;
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();


            Path fileName = Path.of(selectedFile.getAbsolutePath());
            String actual = null;
            try {
                actual = Files.readString(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mapString = actual;

        }
        return mapString;
    }

    /**
     * Der String wird erstmal an der Stelle NEXTLAYER getrennt und in 2 Arrays verteilt.
     * Danach werden die Arrays in einer for Schleife durchlaufen und den Semikolons(;) geteilt und in 5 temporäre Strings gespeichert
     *
     * @param mapString
     * @return
     */
    private BlockArrayList[] convertStringToMap(String mapString) {
        layerStringArray = this.mapString.split("NEXTLAYER");                                                     //Der String wird erstmal an der Stelle NEXTLAYER getrennt
        mapLayer1StringArray = layerStringArray[0].split(";");                                                    //Es entsteht ein String Array mit 2 Indizes.
        mapLayer2StringArray = layerStringArray[1].split(";");                                                    //Diese Strings werden an den Semikolons(;) getrennt und in ein jeweils eigenes StringArray gespeichert
        String tempStringSourceX = null;                                                                                            //int SourceX
        String tempStringSourceY = null;                                                                                            //int SourceY
        String tempStringDestinationX = null;                                                                                            //int DestinationX
        String tempStringDestinationY = null;                                                                                            //int DestinationY
        String tempStringKollision = null;                                                                                            //Boolean Kollision
        int tempIntSourceX = 0;                                                                                               //int SourceX
        int tempIntSourceY = 0;                                                                                               //int SourceY
        int tempIntDestinationX = 0;                                                                                               //int DestinationX
        int tempIntDestinationY = 0;                                                                                               //int DestinationY
        boolean tempBooleanCollision;                                                                                            //Boolean Kollision

        BlockArrayList mapLayer1 = new BlockArrayList();                                                                //Hier entstehen bereits die BlockArrayLists
        BlockArrayList mapLayer2 = new BlockArrayList();                                                                //Hier entstehen bereits die BlockArrayLists
        int counter = 0;                                                                                                //Counter geht bis 5 und wird dann wieder 0, da alle 5 Stellen des String Arrays einmal alle Informationen für einen Block gespeichert sind
        for (int i = 0; i < mapLayer1StringArray.length; i++) {                                                         //Das Layer1 String Array wird durchlaufen

            switch (counter) {
                case 0 -> {                                                                                             //An Stelle 0 haben wir SourceX
                    tempStringSourceX = mapLayer1StringArray[i];
                    tempStringSourceX = tempStringSourceX.trim();
                }
                case 1 -> {                                                                                             //An Stelle 1 haben wir SourceY
                    tempStringSourceY = mapLayer1StringArray[i];
                    tempStringSourceY = tempStringSourceY.trim();
                }
                case 2 -> {                                                                                             //An Stelle 2 haben wir DestinationX
                    tempStringDestinationX = mapLayer1StringArray[i];
                    tempStringDestinationX = tempStringDestinationX.trim();
                }
                case 3 -> {                                                                                             //An Stelle 3 haben wir DestinationY
                    tempStringDestinationY = mapLayer1StringArray[i];
                    tempStringDestinationY = tempStringDestinationY.trim();
                }
                case 4 -> {                                                                                             //An Stelle 4 haben wir Kollision
                    tempStringKollision = mapLayer1StringArray[i];
                    tempStringKollision = tempStringKollision.trim();
                }
                default -> throw new IllegalStateException("Der Counter wurde nicht zurückgesetzt: " + counter);
            }

            counter++;
            if (counter == 5) {                                                                                         //Nach 5 Erhöhungen ist der erste Datensatz durchlaufen
                counter = 0;                                                                                            //Counter wird wieder auf 0 gesetzt, nächster Datensatz
                try {
                tempIntSourceX = Integer.parseInt(tempStringSourceX);
                }
                catch (NumberFormatException e) {
                    System.out.println("0");
                    System.out.println("tempStringSourceX!!!");
                    System.out.println(tempStringSourceX);
                    System.out.println("tempStringSourceY");
                    System.out.println(tempStringSourceY);
                    System.out.println("tempStringDestinationX");
                    System.out.println(tempStringDestinationX);
                    System.out.println(i);
                System.out.println();
            }
                try {
                    tempIntSourceY = Integer.parseInt(tempStringSourceY);
                } catch (NumberFormatException e) {
                    System.out.println("1");
                    System.out.println(tempStringSourceY);
                    System.out.println(i);
                    System.out.println();
                }
                try {
                    tempIntDestinationX = Integer.parseInt(tempStringDestinationX);
                } catch (NumberFormatException e) {

                    System.out.println("2");
                    System.out.println("tempStringSourceX:");
                    System.out.println(tempStringSourceX);

                    System.out.println("tempStringSourceY:");
                    System.out.println(tempStringSourceY);

                    System.out.println("tempStringDestinationX:");
                    System.out.println(tempStringDestinationX);
                    System.out.println(i);
                    System.out.println("tempStringDestinationY:");
                    System.out.println(tempStringDestinationY);

                    System.out.println();
                }
                try {
                    tempIntDestinationY = Integer.parseInt(tempStringDestinationY);
                } catch (NumberFormatException e) {
                    System.out.println("3");
                    System.out.println("tempStringSourceY:");
                    System.out.println(tempStringSourceY);
                    System.out.println("tempStringDestinationX:");
                    System.out.println(tempStringDestinationX);
                    System.out.println("tempStringDestinationY");
                    System.out.println(tempStringDestinationY);
                    System.out.println(i);
                    System.out.println();

                }
                tempBooleanCollision = Boolean.parseBoolean(tempStringKollision);
                mapLayer1.add(tempIntDestinationX, tempIntDestinationY, tempIntSourceX, tempIntSourceY, tempBooleanCollision);
            }
        }
        for (int i = 0; i < mapLayer2StringArray.length; i++) {

            switch (counter) {
                case 0 -> {                                                                                             //An Stelle 0 haben wir SourceX
                    tempStringSourceX = mapLayer2StringArray[i];
                    tempStringSourceX = tempStringSourceX.trim();
                }
                case 1 -> {                                                                                             //An Stelle 1 haben wir SourceY
                    tempStringSourceY = mapLayer2StringArray[i];
                    tempStringSourceY = tempStringSourceY.trim();
                }
                case 2 -> {                                                                                             //An Stelle 2 haben wir DestinationX
                    tempStringDestinationX = mapLayer2StringArray[i];
                    tempStringDestinationX = tempStringDestinationX.trim();
                }
                case 3 -> {                                                                                             //An Stelle 3 haben wir DestinationY
                    tempStringDestinationY = mapLayer2StringArray[i];
                    tempStringDestinationY = tempStringDestinationY.trim();
                }
                case 4 -> {                                                                                             //An Stelle 4 haben wir Kollision
                    tempStringKollision = mapLayer2StringArray[i];
                    tempStringKollision = tempStringKollision.trim();
                }
                default -> throw new IllegalStateException("Unexpected value: " + counter);
            }

            counter++;
            if (counter == 5) {                                                                                         //Nach 5 Erhöhungen ist der erste Datensatz durchlaufen
                counter = 0;                                                                                            //Counter wird wieder auf 0 gesetzt, nächster Datensatz
                tempIntSourceX = Integer.parseInt(tempStringSourceX);
                tempIntSourceY = Integer.parseInt(tempStringSourceY);
                tempIntDestinationX = Integer.parseInt(tempStringDestinationX);
                try {
                    tempIntDestinationY = Integer.parseInt(tempStringDestinationY);
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                    System.err.println("7");
                    System.out.println("tempStringSourceY:");
                    System.out.println(tempStringSourceY);
                    System.out.println("tempStringDestinationX:");
                    System.out.println(tempStringDestinationX);
                    System.out.println("tempStringDestinationY!!");
                    System.out.println(tempStringDestinationY);
                    System.out.println(i);
                    System.out.println();
                }

                tempBooleanCollision = Boolean.parseBoolean(tempStringKollision);
                mapLayer2.add(tempIntDestinationX, tempIntDestinationY, tempIntSourceX, tempIntSourceY, tempBooleanCollision);
            }
        }

        BlockArrayList[] map = new BlockArrayList[2];
        map[0] = mapLayer1;
        map[1] = mapLayer2;

        return map;
    }

    public BlockArrayList[] getLoadedMap() {
        return loadedMap;
    }

    public String getMapString() {
        return mapString;
    }

    public BufferedImage getBufferedImage(String filename) {

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new FileInputStream(filename));

        } catch (IOException e) {
            System.out.println("Fehler beim Laden vom Sprite Image");
            // TODO Hier muss noch geloggt werden > fatal
        }
        return bufferedImage;
    }
}

