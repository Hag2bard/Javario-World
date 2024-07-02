package Pokemon;

import PokemonEditor.BlockList;
import PokemonEditor.FileLoader;
import PokemonEditor.RGuiSizes;
import PokemonEditor.TilePanel;
import lombok.Getter;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Canvas extends JPanel {    //Klasse Start

    @Getter
    private BlockList mapLayer1;
    @Getter
    private BlockList mapLayer2;

    @Getter
    private BufferedImage tilesetBufferedImage;
    @Getter
    private final BufferedImage spritesBufferedImage;
    @Getter
    private final int ZOOM = 4; //TODO muss noch eingebaut werden 4 ist Standard
    @Getter
    private final int TILESIZE = 16;
    @Getter
    private final int OFFSET = 11 * ZOOM;  //umso höher umso höher zeichnet er
    //    private final Physics physics;   //ohne thread
    private Physics physics;   //mit thread
    private final Hero mario;
    //    private final MediaPlayer mediaPlayer;
    private final FileLoader fileLoader;
    private boolean isPressingRightButton = false;
    private boolean isPressingLeftButton = false;
    private boolean isPressingSpaceButton = false;
    private Thread physicsThread;
    private Canvas canvas;
    private long last_time;
    private long delta_time;
    private int counter;


    public Canvas() {    //Konstruktor Start
        canvas = this;
        physicsThread = new Thread(() -> physics = new Physics(canvas));
        physicsThread.start();
//        physics = new Physics(this);  //Ohne Thread
        keyBinding();
        System.out.println(physics);
        mario = new Hero(this, physics);
        physics.startAnimation();


//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.playWav();
        fileLoader = new FileLoader();
        BlockList[] loadedMap;
        try {
            loadedMap = fileLoader.loadMap();
            this.mapLayer1 = loadedMap[0];
            this.mapLayer2 = loadedMap[1];
            this.tilesetBufferedImage = TilePanel.getExistingInstance().getBufferedImage();
        } catch (FileNotFoundException fileNotFoundException) {
            String errorMessage = "Sie haben keine Datei ausgewählt!";
            Logger.error(errorMessage);
            JOptionPane.showMessageDialog(null, errorMessage);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }

        spritesBufferedImage = new FileLoader().getBufferedImage(RGuiSizes.FILENAME_SPRITE_SET);

        setFocusable(true);
        requestFocusInWindow();
    }


    public void keyBinding() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "pressedSpace");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "releasedSpace");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "pressedLeft");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "releasedLeft");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "pressedRight");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "releasedRight");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, true), "pressedR");

        am.put("pressedSpace", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if (!pressingSpaceButton && !physics.getJumpTimer().isRunning() && !physics.getFallTimer().isRunning()) {                   //Doppelsprung wird vermieden durch diese If Bedingung
//                if (!pressingSpaceButton && !physics.getJumpTimer().isRunning() && !physics.getFallTimer().isRunning()) {                   //Doppelsprung wird vermieden durch diese If Bedingung
                if (!isPressingSpaceButton && !physics.getJumping() && !physics.getFalling()) {                   //Doppelsprung wird vermieden durch diese If Bedingung
                    isPressingSpaceButton = true;
                    if (mario.getDirection() == Direction.LEFT) {
                        mario.setFeetPosition(1);                                                                       //FeetPosition1 beim Jump heißt, er schaut links
                    }
                    if (mario.getDirection() == Direction.RIGHT) {
                        mario.setFeetPosition(2);
                    }
                    mario.setDirectionBackup(mario.getDirection());                                                     //FeetPosition2 beim Jump heißt, er schaut rechts
                    mario.doJump();
                }
            }
        });

        am.put("releasedSpace", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPressingSpaceButton = false;                                                                            // boolean der speichert, ob die Leertaste gerade gedrückt wird
            }
        });

        am.put("pressedLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPressingLeftButton) {
                    isPressingLeftButton = true;
                    mario.setSpeed(-mario.getWalkSpeed());                                                              //Geschwindigkeit auf negativen Wert setzen, bedeutet, dass Mario nach links läuft
                    mario.setDirection(Direction.LEFT);                                                                         //Wichtig für paintComponent
                    mario.setDirectionBackup(Direction.LEFT);
                    mario.refreshFinalPositionX();
                    if (!physics.getJumping() && !physics.getFalling()) {
                        mario.setFeetPosition(1);                                                                       //Er fängt immer mit einem Fuß vorne an zu laufen
                    }
                }
            }
        });

        am.put("releasedLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPressingLeftButton = false;
                if (!physics.getJumping() && !physics.getFalling()) {
                    mario.setFeetPosition(2);
                }
                if (physics.getJumping() && physics.getFalling()) {
                    mario.setFeetPosition(1);                                                                           //Feet Position 1 bei Jump heißt er guckt links
                }
//                physics.doRepaint();
                repaint();
                if (!isPressingRightButton) {                                                                              //Wenn kein anderer Button gedrückt wird, dann Mario anhalten durch Geschwindigkeit 0
                    mario.setSpeed(0);
                }
            }
        });

        am.put("pressedRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPressingRightButton) {
                    isPressingRightButton = true;
                    mario.setSpeed(mario.getWalkSpeed());                                                               //Geschwindigkeit auf positiven Wert setzen, bedeutet, dass Mario nach rechts läuft
                    mario.setDirection(Direction.RIGHT);                                                                        //Wichtig für paintComponent
                    mario.setDirectionBackup(Direction.RIGHT);
                    mario.refreshFinalPositionX();
                    if (!physics.getJumping() && !physics.getFalling()) {
                        mario.setFeetPosition(1);                                                                       //Er fängt immer mit einem Fuß vorne an zu laufen
                    }
                }
            }
        });

        am.put("releasedRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPressingRightButton = false;
                if (!physics.getJumping() && !physics.getFalling()) {
                    mario.setFeetPosition(2);
                }
                if (physics.getJumping() && physics.getFalling()) {
                    mario.setFeetPosition(2);                                                                           //Feet Position 1 bei Jump heißt er guckt links
                }
//                physics.doRepaint();
                repaint();
                if (!isPressingLeftButton) {                                                                               //Wenn kein anderer Button gedrückt wird, dann Mario anhalten durch Geschwindigkeit 0
                    mario.setSpeed(0);
                }
            }
        });


        am.put("pressedR", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.repaint();
            }
        });
    }


    public void paintLayer(Graphics g, BlockList mapLayer) {
        for (int i = 0; i < mapLayer.size(); i++) {
            g.drawImage(canvas.getTilesetBufferedImage(), mapLayer.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer.get(i).getSourceX() * TILESIZE, mapLayer.get(i).getSourceY() * TILESIZE, (mapLayer.get(i).getSourceX() + 1) * TILESIZE, (mapLayer.get(i).getSourceY() + 1) * TILESIZE, null);
        }
    }

    public void paintHero(Graphics g, Direction direction, int feetPosition, int positionX, int positionY) {
        if (mario.getDirection().equals(Direction.LEFT)) {
            switch (mario.getFeetPosition()) {                                                                                                                        //+ZOOM=Korrektur
                case 1 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 1, 91, 17, 118 + 1, null); //links Fuß vorn
                case 2 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 19, 91, 35, 118 + 1, null); //links stehend
                case 3 -> System.err.println("FALL 3 bei LEFT ist aufgetreten!!!!!!!!!!!!");
            }
        }
        if (direction.equals(Direction.RIGHT)) {
            switch (feetPosition) {                                                 // offset ist der Wert wieviel über 16 Pixel Block gezeichnet werden soll        //+ZOOM=Korrektur
                case 1 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 1, 31, 17, 58 + 1, null); //rechts Fuß vorn
                case 2 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 19, 31, 35, 58 + 1, null); //rechts stehend
                case 3 -> System.err.println("FALL 3 bei RIGHT ist aufgetreten!!!!!!!!!!!!");
            }
        }
        if (direction.equals(Direction.UP)) {               //Springen
            switch (feetPosition) {
                case 1 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET - ZOOM, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM), 1, 1, 17, 28 + 1, null); //Springen links
                case 2 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET - ZOOM, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM), 19, 1, 35, 28 + 1, null); //Springen rechts
                case 3 -> System.err.println("FALL 3 bei UP ist aufgetreten!!!!!!!!!!!!");
            }
        }
        if (direction.equals(Direction.DOWN)) {             //Fallen
            switch (feetPosition) {
                case 1 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - canvas.getOFFSET() - ZOOM, positionX + TILESIZE * ZOOM, positionY + TILESIZE * ZOOM, 1, 61, 17, 89, null); //Fallen links
                case 2 ->
                        g.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - canvas.getOFFSET() - ZOOM, positionX + TILESIZE * ZOOM, positionY + TILESIZE * ZOOM, 19, 61, 35, 89, null); //Fallen rechts
                case 3 -> System.err.println("FALL 3 bei DOWN ist aufgetreten!!!!!!!!!!!!");
            }
        }
        g.dispose();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        last_time = System.nanoTime();
        super.paintComponent(g);
        paintLayer(g, mapLayer1);                                                                                       //Layer1 wird gezeichnet
        paintLayer(g, mapLayer2);                                                                                       //Layer2 wird gezeichnet
        paintHero(g, mario.getDirection(), mario.getFeetPosition(), mario.getPositionX(), mario.getPositionY());        //Mario wird gezeichnet, je nach Position, Blickrichtung, Fußposition (Lauf, Sprung, Fall)

        if (counter > 10) {
            delta_time = (System.nanoTime() - last_time) / 1000000;
            counter = 0;
        }
        g.drawString("FrameTime: " + delta_time, 50, 50);

        counter++;
    }


    public boolean isPressingRightButton() {
        return isPressingRightButton;
    }

    public boolean isPressingLeftButton() {
        return isPressingLeftButton;
    }

    public boolean isPressingSpaceButton() {
        return isPressingSpaceButton;
    }


}