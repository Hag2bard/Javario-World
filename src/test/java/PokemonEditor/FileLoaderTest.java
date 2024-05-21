package PokemonEditor;

import org.junit.jupiter.api.Test;

class FileLoaderTest {
    FileLoader fileLoader = new FileLoader();

    @Test
    void openJFileChooserForLoadingMapLayers() {
        String string = fileLoader.openJFileChooserForLoadingMap();
        System.out.println();
    }

//    @Test
//    void unzipMapAndLoad() throws IOException {
//        String[] mapLayerStringArray = fileLoader.unzipMapAndLoad("C:\\Users\\dch\\Desktop\\z.zip");
//        Gson gson = new Gson();
//        BlockList mapLayer1 = gson.fromJson(mapLayerStringArray[0], BlockList.class);
//        BlockList mapLayer2 = gson.fromJson(mapLayerStringArray[1], BlockList.class);
//        System.out.println(222);
//    }


}