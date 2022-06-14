//Bereinigen vor Speichern

package PokemonEditor;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveMap {
    private final BlockArrayList mapArrayLayer1;
    private final BlockArrayList mapArrayLayer2;
    private final MapPanel mapPanel;
    private final StringBuilder mapStringBuilder = new StringBuilder();

    public SaveMap(PokemonEditor.MapPanel mapPanel) {
        this.mapPanel = mapPanel;
        mapArrayLayer1 = mapPanel.getBlockArrayLayer1();
        mapArrayLayer2 = mapPanel.getBlockArrayLayer2();
        saveMapStringToFile(convertMapToString(this.mapArrayLayer1, this.mapArrayLayer2));
    }

    private String convertMapToString(BlockArrayList mapArrayLayer1, BlockArrayList mapArrayLayer2) {
        for (int i = 0; i < mapArrayLayer1.size(); i++) {
            mapStringBuilder
                    .append(mapArrayLayer1.get(i).getDestinationX())
                    .append(";")
                    .append(mapArrayLayer1.get(i).getDestinationY())
                    .append(";")
                    .append(mapArrayLayer1.get(i).getSourceX())
                    .append(";")
                    .append(mapArrayLayer1.get(i).getSourceY())
                    .append(";");
        }
        mapStringBuilder.append("NEXTLAYER");
        for (int i = 0; i < mapArrayLayer2.size(); i++) {
            mapStringBuilder
                    .append(mapArrayLayer2.get(i).getDestinationX())
                    .append(";")
                    .append(mapArrayLayer2.get(i).getDestinationY())
                    .append(";")
                    .append(mapArrayLayer2.get(i).getSourceX())
                    .append(";")
                    .append(mapArrayLayer2.get(i).getSourceY())
                    .append(";");
        }
        return mapStringBuilder.toString();
    }


    private void saveMapStringToFile(String mapString) {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // int returnValue = jfc.showOpenDialog(null);
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();


            try (FileWriter writer = new FileWriter(selectedFile);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write(mapString);
            } catch (IOException e) {
                System.err.format("IOException: %s%n", e);
            }
        }
    }


}
