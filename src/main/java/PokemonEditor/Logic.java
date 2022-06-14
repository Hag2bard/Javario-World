package PokemonEditor;


public class Logic {

    private BlockArrayList mapLayer1;
    private BlockArrayList mapLayer2;
    private boolean isDeleteActive = false;
    private final MapPanel mapPanel;
    private static Logic instance;

    private Logic() {
        this.mapPanel = MapPanel.getInstance();
    }

    public static Logic getInstance() {
        if (instance == null) {
            instance = new Logic();
        }
        return instance;
    }

    public void setDeleteActive() {
        isDeleteActive = true;
    }

    public void setDeleteInactive() {
        isDeleteActive = false;
        PokeEditor.getInstance().getBtnDeleteBlock().setSelected(false);
    }

    public void deleteBlock2(int selectedX, int selectedY, int amountOfSelectedBlocks) {
        for (int y = 0; y < amountOfSelectedBlocks; y++) {
            for (int x = 0; x < amountOfSelectedBlocks; x++) {
                switch (mapPanel.getSelectedLayer()) {
                    case 1 -> mapLayer1.delete(selectedX + x, selectedY + y);
                    case 2 -> mapLayer2.delete(selectedX + x, selectedY + y);
                    case 3 -> {
                        mapLayer1.delete(selectedX + x, selectedY + y);     //Wenn Layer 3 ausgewählt ist, löscht er alles was gesetzt wurde
                        mapLayer2.delete(selectedX + x, selectedY + y);
                    }
                }
            }
        }
    }


    public void setMap(BlockArrayList mapLayer1, BlockArrayList mapLayer2) {
        this.mapLayer1 = mapLayer1;
        this.mapLayer2 = mapLayer2;
    }

    public boolean isDeleteActive() {
        return isDeleteActive;
    }


}
