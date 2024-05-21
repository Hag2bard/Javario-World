//Bereinigen vor Speichern

package PokemonEditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MapSaver {

    /**
     * @param filePath  "C:\\Entwicklung\\test.zip"
     * @param mapLayer1
     * @param mapLayer2
     */
    private void saveMapFromMapLayersToGivenFilePath(String filePath, BlockList mapLayer1, BlockList mapLayer2) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String mapLayer1AsJsonString = gson.toJson(mapLayer1);
        String mapLayer2AsJsonString = gson.toJson(mapLayer2);
        try {
            zipMultipleJsonStringsAndSave(new String[]{mapLayer1AsJsonString, mapLayer2AsJsonString}, filePath);
        } catch (IOException e) {
            System.err.println("Konnte Datei aus unbekannten Gründen nicht unter "
                    + filePath +
                    " speichern! " +
                    "Hier ist der Stacktrace: " + e.getMessage());
        }
    }

    private void zipMultipleJsonStringsAndSave(String[] jsonStrings, String filename) throws IOException {
        // Erstellt neues File Objekt mit Namen der im JFileChooser vergeben wurde. Im Zweifel wird ein .zip dran gehängt.
        if (!filename.contains(".zip")) {
            filename = filename + ".zip";
        }
        File outputFile = new File(filename);
        // Erstellt neuen ZipOutputStream, welcher ein FileOutputStream bekommt, damit er die Zips darein speichern kann
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputFile));
        // Erstellt neue Zip-Einträge und vergibt Datei-Namen
        ZipEntry zipEntryForFirstLayer = new ZipEntry("firstLayer.json");
        ZipEntry zipEntryForSecondLayer = new ZipEntry("secondLayer.json");
        // Stellt ZipOutputStream auf erste Zip ein
        zipOutputStream.putNextEntry(zipEntryForFirstLayer);
        // Der Json-String wird in byte-Array umgewandelt
        byte[] bytesOfFirstJsonString = jsonStrings[0].getBytes();
        // Schreibt byteArray in erste Zip-Datei
        zipOutputStream.write(bytesOfFirstJsonString, 0, bytesOfFirstJsonString.length);
        zipOutputStream.closeEntry();

        zipOutputStream.putNextEntry(zipEntryForSecondLayer);
        // Der Json-String wird in byte-Array umgewandelt
        byte[] bytesOfSecondJsonString = jsonStrings[1].getBytes();
        // Schreibt byteArray in zweite Zip-Datei
        zipOutputStream.write(bytesOfSecondJsonString, 0, bytesOfSecondJsonString.length);
        // Der Zip-Eintrag wird geschlossen
        zipOutputStream.closeEntry();
        // Die komplette Zip wird geschlossen und somit gespeichert
        zipOutputStream.close();
    }


    public void openJFileChooserForSavingMapLayers(BlockList mapLayer1, BlockList mapLayer2) {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // int returnValue = jfc.showOpenDialog(null);
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            saveMapFromMapLayersToGivenFilePath(selectedFile.getAbsolutePath(), mapLayer1, mapLayer2);
        }
    }


}
