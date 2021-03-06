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

    private BlockArrayList[] convertStringToMap(String mapString) {
        layerStringArray = this.mapString.split("NEXTLAYER");
        mapLayer1StringArray = layerStringArray[0].split(";");
        mapLayer2StringArray = layerStringArray[1].split(";");
        String temp0 = null;
        String temp1 = null;
        String temp2 = null;
        String temp3 = null;
        int tempInt0 = 0;
        int tempInt1 = 0;
        int tempInt2 = 0;
        int tempInt3 = 0;

        BlockArrayList mapLayer1 = new BlockArrayList();
        BlockArrayList mapLayer2 = new BlockArrayList();
        int counter = 0;
        for (int i = 0; i < mapLayer1StringArray.length; i++) {

            switch (counter) {
                case 0 -> {
                    temp0 = mapLayer1StringArray[i];
                    temp0 = temp0.trim();
                }
                case 1 -> {
                    temp1 = mapLayer1StringArray[i];
                    temp1 = temp1.trim();
                }
                case 2 -> {
                    temp2 = mapLayer1StringArray[i];
                    temp2 = temp2.trim();
                }
                case 3 -> {
                    temp3 = mapLayer1StringArray[i];
                    temp3 = temp3.trim();

                }
                default -> throw new IllegalStateException("Unexpected value: " + counter);
            }

            counter++;
            if (counter == 4) {
                counter = 0;
                tempInt0 = Integer.parseInt(temp0);
                tempInt1 = Integer.parseInt(temp1);
                tempInt2 = Integer.parseInt(temp2);
                tempInt3 = Integer.parseInt(temp3);
                mapLayer1.add(tempInt2, tempInt3, tempInt0, tempInt1);
            }
        }
        for (int i = 0; i < mapLayer2StringArray.length; i++) {

            switch (counter) {
                case 0 -> {
                    temp0 = mapLayer2StringArray[i];
                    temp0 = temp0.trim();
                }
                case 1 -> {
                    temp1 = mapLayer2StringArray[i];
                    temp1 = temp1.trim();
                }
                case 2 -> {
                    temp2 = mapLayer2StringArray[i];
                    temp2 = temp2.trim();
                }
                case 3 -> {
                    temp3 = mapLayer2StringArray[i];
                    temp3 = temp3.trim();

                }
                default -> throw new IllegalStateException("Unexpected value: " + counter);
            }

            counter++;
            if (counter == 4) {
                counter = 0;
                tempInt0 = Integer.parseInt(temp0);
                tempInt1 = Integer.parseInt(temp1);
                tempInt2 = Integer.parseInt(temp2);
                tempInt3 = Integer.parseInt(temp3);
                mapLayer2.add(tempInt2, tempInt3, tempInt0, tempInt1);
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
            // TODO Hier muss noch geloggt werden
        }
        return bufferedImage;
    }
}

