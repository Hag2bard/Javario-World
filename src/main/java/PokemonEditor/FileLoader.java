package PokemonEditor;

import com.google.gson.Gson;
import util.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Simpler FileLoader, welcher BufferedImages oder Maps lädt.
 */
public class FileLoader {

    public String openJFileChooserForLoadingMap() throws FileNotFoundException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
// test änder..
        int returnValue = jfc.showOpenDialog(null);
//        int returnValue = jfc.showSaveDialog(null);

//        if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = jfc.getSelectedFile();
//        }
        if (selectedFile == null) {
            throw new FileNotFoundException();
        }
        return selectedFile.getAbsolutePath();
    }


    public BlockList[] loadMap() throws IOException {
        String filename = openJFileChooserForLoadingMap();
        String[] mapLayerStringArray = unzipMapAndLoad(filename);
        Gson gson = new Gson();
        BlockList mapLayer1 = gson.fromJson(mapLayerStringArray[0], BlockList.class);
        BlockList mapLayer2 = gson.fromJson(mapLayerStringArray[1], BlockList.class);
        return new BlockList[]{mapLayer1, mapLayer2};
    }

    private String[] unzipMapAndLoad(String filename) throws IOException {
        // Erstellt neuen ZipInputStream, welcher ein FileInputStream bekommt, welcher die Zip lädt.
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filename));

        StringBuilder mapLayer1json = new StringBuilder();
        StringBuilder mapLayer2json = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read = 0;
        ZipEntry firstEntry = zipInputStream.getNextEntry();
        while ((read = zipInputStream.read(buffer, 0, 1024)) >= 0) {
            mapLayer1json.append(new String(buffer, 0, read));
        }
        // Der Zip-Eintrag wird geschlossen.
        zipInputStream.closeEntry();
        ZipEntry secondEntry = zipInputStream.getNextEntry();
        while ((read = zipInputStream.read(buffer, 0, 1024)) >= 0) {
            mapLayer2json.append(new String(buffer, 0, read));
        }
        // Der Zip-Eintrag wird geschlossen.
        zipInputStream.closeEntry();
        // Die komplette Zip wird geschlossen.
        zipInputStream.close();
        String[] mapLayerStringArray = new String[2];
        mapLayerStringArray[0] = mapLayer1json.toString();
        mapLayerStringArray[1] = mapLayer2json.toString();
        return mapLayerStringArray;
    }

    public BufferedImage getBufferedImage(String filename) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new FileInputStream(filename));
        } catch (IOException e) {
            Logger.error("Fehler beim Laden des Sprite Images");
        }
        return bufferedImage;
    }
}

