package Pokemon;

import PokemonEditor.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static PokemonEditor.MapPanel.ZOOM;
import static PokemonEditor.TilePanel.TILESIZE;

public class Canvas extends JPanel {    //Klasse Start

    private BlockArrayList mapLayer1 = null;
    private BlockArrayList mapLayer2 = null;

    private BufferedImage tilesetBufferedImage;
    private final BufferedImage spritesBufferedImage;
    private static final int ZOOM = 4;                                                                                         //TODO muss noch eingebaut werden 4 ist Standard
    private final int TILESIZE = 16;
    private final int OFFSET = 11 * ZOOM;                                                                               //umso höher umso höher zeichnet er
    //    private final Physics physics;                                                                                //ohne thread
    private Physics physics;                                                                                            //mit thread

    public Hero getMario() {
        return mario;
    }

    private final Hero mario;
    //    private final MediaPlayer mediaPlayer;
    private final LoadMap loadMap;
    private boolean isPressingRightButton = false;
    private boolean isPressingLeftButton = false;
    private boolean isPressingSpaceButton = false;
    private final Thread physicsThread;
    private final Canvas canvas;
    private long last_time;
    private long delta_time;
    private int counter;
    private BufferedImage mapBufferedImage = null;
    private int timer = 0;

    public Canvas setDrawingImage(BufferedImage drawingImage) {
        this.drawingImage = drawingImage;
        return this;
    }

    BufferedImage drawingImage;


    public Canvas() {    //Konstruktor Start
        canvas = this;

        loadMap = new LoadMap();
        if (loadMap.getMapString() != null) {
            this.mapLayer1 = loadMap.getLoadedMap()[0];
            this.mapLayer2 = loadMap.getLoadedMap()[1];
        }

        physics = new Physics(this);
        physicsThread = new Thread(physics);
        physicsThread.start();
//        physics = new Physics(this);  //Ohne Thread
        physics.setRunning(true);
        mario = new Hero(this, physics);


        try {
            tilesetBufferedImage = TilePanel.getExistingInstance().getBufferedImage();
        } catch (Exception e) {
            System.err.println("Konnte TilePanel-Image nicht holen");
            e.printStackTrace();
        }

        spritesBufferedImage = loadMap.getBufferedImage(GuiData.filenameSpriteSet);


        setFocusable(true);
        requestFocusInWindow();
        keyBinding();

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
//                mario.setDirection(mario.getDirectionBackup());                                                         //Wenn während des Sprungs/Falls die Richtung durch Tastendruck gewechselt wird, wird das DirectionBackup damit überschrieben
//                mario.setFeetPosition(2);

                System.out.println("Pressed R!");
                canvas.repaint();

            }
        });
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapBufferedImage == null) {
            mapBufferedImage = physics.imageManagement.getMapImage();  //??
        }
        if (mapBufferedImage != null) {
            g.drawImage(mapBufferedImage, 0, 0, this);
        }

        g.drawImage(physics.imageManagement.getCurrentViewingImage(), 0, 0, this);

        if (physics.getPerformance() != null)
            g.drawString(physics.getPerformance(), 50, 50);
    }


    /*
    Getter und Setter - Der spannendste Teil meines Codes
     */
    public BlockArrayList getMapLayer1() {
        return mapLayer1;
    }

    public BlockArrayList getMapLayer2() {
        return mapLayer2;
    }

    public BufferedImage getTilesetBufferedImage() {
        return tilesetBufferedImage;
    }

    public BufferedImage getSpritesBufferedImage() {
        return spritesBufferedImage;
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

    public int getOFFSET() {
        return OFFSET;
    }

    public int getZOOM() {
        return ZOOM;
    }

    public int getTILESIZE() {
        return TILESIZE;
    }

}