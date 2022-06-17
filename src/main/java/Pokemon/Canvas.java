package Pokemon;

import PokemonEditor.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {    //Klasse Start

    private BlockArrayList mapLayer1;
    private BlockArrayList mapLayer2;

    private BufferedImage tilesetBufferedImage;
    private final BufferedImage spritesBufferedImage;
    private final int ZOOM = 4;                                                                                         //TODO muss noch eingebaut werden 4 ist Standard
    private final int TILESIZE = 16;
    private final int OFFSET = 11 * ZOOM;                                                                               //umso höher umso höher zeichnet er
    //    private final Physics physics;                                                                                //ohne thread
    private Physics physics;                                                                                            //mit thread
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
    private BufferedImage mapBufferedImage = new BufferedImage(1024, 860, BufferedImage.TYPE_INT_ARGB);
    private Graphics mapGraphics = mapBufferedImage.createGraphics();
    private boolean accessingBufferedImageHeroB = false;

    BufferedImage bufferedImageHeroA;
    BufferedImage bufferedImageHeroB;
    BufferedImage bufferedImageHeroC;
    BufferedImage bufferedImageHeroD;
    Graphics graphicsHero;

    Thread refreshBufferedImagesThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (mario.heroDataChanged()) {
                    refreshHeroBufferedImage(mario.getDirection(), mario.getFeetPosition(), mario.getPositionX(), mario.getPositionY());        //Mario wird gezeichnet, je nach Position, Blickrichtung, Fußposition (Lauf, Sprung, Fall)
                    System.out.println("refreshed");
                } else {
                    try {
                        Thread.sleep(0);                                                                          //Workaround, sonst //TODO
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    });

    public Canvas() {    //Konstruktor Start
        canvas = this;
        physicsThread = new Thread(() -> physics = new Physics(canvas));
        physicsThread.start();
//        physics = new Physics(this);  //Ohne Thread
        keyBinding();
        System.out.println(physics);
        mario = new Hero(this, physics);
        physics.startAnimation();
        loadMap = new LoadMap();

        if (loadMap.getMapString() != null) {
            this.mapLayer1 = loadMap.getLoadedMap()[0];
            this.mapLayer2 = loadMap.getLoadedMap()[1];
        }

        try {
            tilesetBufferedImage = TilePanel.getExistingInstance().getBufferedImage();
        } catch (Exception e) {
            System.err.println("Konnte TilePanel-Image nicht holen");
            e.printStackTrace();
        }

        spritesBufferedImage = loadMap.getBufferedImage(GuiData.filenameSpriteSet);

        refreshBufferedImagesThread.start();
        refreshMapBufferedImage();

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
                refreshHeroBufferedImage(mario.getDirection(), 2, mario.getPositionX(), mario.getPositionY());
                canvas.repaint();

            }
        });
    }

    private void refreshMapBufferedImage() {
        for (int i = 0; i < mapLayer1.size(); i++) {
            mapGraphics.drawImage(canvas.getTilesetBufferedImage(), mapLayer1.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer1.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer1.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer1.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer1.get(i).getSourceX() * TILESIZE, mapLayer1.get(i).getSourceY() * TILESIZE, (mapLayer1.get(i).getSourceX() + 1) * TILESIZE, (mapLayer1.get(i).getSourceY() + 1) * TILESIZE, null);
        }
        for (int i = 0; i < mapLayer2.size(); i++) {
            mapGraphics.drawImage(canvas.getTilesetBufferedImage(), mapLayer2.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer2.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer2.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer2.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer2.get(i).getSourceX() * TILESIZE, mapLayer2.get(i).getSourceY() * TILESIZE, (mapLayer2.get(i).getSourceX() + 1) * TILESIZE, (mapLayer2.get(i).getSourceY() + 1) * TILESIZE, null);
        }
    }

    public void refreshHeroBufferedImage(Direction direction, int feetPosition, int positionX, int positionY) {
        if (System.currentTimeMillis() - last_time > 15 || !isPressingRightButton && !isPressingLeftButton) {
            bufferedImageHeroC = new BufferedImage(1024, 860, BufferedImage.TYPE_INT_ARGB);
            graphicsHero = bufferedImageHeroC.createGraphics();

            if (mario.getDirection().equals(Direction.LEFT)) {
                switch (mario.getFeetPosition()) {                                                                                                                        //+ZOOM=Korrektur
                    case 1 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 1, 91, 17, 118 + 1, null); //links Fuß vorn
                    case 2 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 19, 91, 35, 118 + 1, null); //links stehend
                }
            }
            if (direction.equals(Direction.RIGHT)) {
                switch (feetPosition) {                                                 // offset ist der Wert wieviel über 16 Pixel Block gezeichnet werden soll        //+ZOOM=Korrektur
                    case 1 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 1, 31, 17, 58 + 1, null); //rechts Fuß vorn
                    case 2 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM) + ZOOM, 19, 31, 35, 58 + 1, null); //rechts stehend
                }
            }
            if (direction.equals(Direction.UP)) {               //Springen
                switch (feetPosition) {
                    case 1 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET - ZOOM, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM), 1, 1, 17, 28 + 1, null); //Springen links
                    case 2 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - OFFSET - ZOOM, positionX + TILESIZE * ZOOM, (positionY + TILESIZE * ZOOM), 19, 1, 35, 28 + 1, null); //Springen rechts
                }
            }
            if (direction.equals(Direction.DOWN)) {             //Fallen
                switch (feetPosition) {
                    case 1 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - canvas.getOFFSET() - ZOOM, positionX + TILESIZE * ZOOM, positionY + TILESIZE * ZOOM, 1, 61, 17, 89, null); //Fallen links
                    case 2 -> graphicsHero.drawImage(canvas.getSpritesBufferedImage(), positionX, positionY - canvas.getOFFSET() - ZOOM, positionX + TILESIZE * ZOOM, positionY + TILESIZE * ZOOM, 19, 61, 35, 89, null); //Fallen rechts
                }
            }
            mario.refreshHeroData();
            if (!accessingBufferedImageHeroB) {
                if (bufferedImageHeroB != null) {
                    accessingBufferedImageHeroB = true;
                    bufferedImageHeroD = bufferedImageHeroB;
                }
                accessingBufferedImageHeroB = true;
                bufferedImageHeroB = bufferedImageHeroC;
                accessingBufferedImageHeroB = false;
                bufferedImageHeroC = null;
            }
            last_time = System.currentTimeMillis();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapBufferedImage != null) {
            g.drawImage(mapBufferedImage, 0, 0, this);
        }
        g.drawString("GameLoops " + physics.getGameLoops(), 50, 50);

        if (bufferedImageHeroB != null) {
            bufferedImageHeroD = bufferedImageHeroA;
            if (!accessingBufferedImageHeroB) {
                accessingBufferedImageHeroB = true;
                bufferedImageHeroA = bufferedImageHeroB;
                bufferedImageHeroB = null;
                accessingBufferedImageHeroB = false;
            }
        }
        g.drawImage(bufferedImageHeroA, 0, 0, this);

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