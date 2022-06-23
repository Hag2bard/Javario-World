package Pokemon;

import PokemonEditor.BlockArrayList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Physics implements Runnable {


    private final double gravitation = 9.81;                //    m/s           //Veränderte Gravitation lässt Mario langsamer hoch gleiten
    private int pixelPerTimerPass = 1;                      //muss verändert werden für Geschwindigkeit,
    private final double timerPassInMs = 17;                           //so viele ms pro TimerDurchlauf
    private long timeElapsedInMs;  //TODO ersetzen durch gameLoopDurchgänge????
    private int jumpedPixelCounter = 0;
    private long fallStartTime;
    private long fallDuration = 0;
    private double startSpeedInMeterPerSecond = 11;                     //   11 braucht er für 300 hoch Dieser Wert bestimmt die zu erreichende Höhe
    private final int minimumSpeed = 4;              //3Mindestens 2 -> je höher dieser Wert, desto höher die Geschwindigkeit des Heros bei Kehrtwende von oben nach unten
    private final int jumpDownTimerSpeed = 30;
    private long start;
    private List<Integer> pixelPerTimerPassList;
    private Hero mario;
    private Canvas canvas;
    private long startTime = System.currentTimeMillis();
    private long duration = 0;
    private int counterFrames = 0;
    private boolean isJumping = false;
    private boolean isFalling = false;
    private boolean isRunning = false;                                                                                   //Gameloop
    Runnable animationRunnable;
    private final int sleepDuration = 10;
    private int gameLoopCounter = 0;
    private long startTime2 = 0;
    private long end;//nötig??
    ImageManagement imageManagement;
    private final int ZOOM = 4;                                                                                         //TODO muss noch eingebaut werden 4 ist Standard
    private final int TILESIZE = 16;
    private final int OFFSET = 11 * ZOOM;
    private String performance;

    // in 1 s = 58 Durchgänge = 58 Pixel
    // Mario 20 Pixel hoch = 2m?
    //Sprung = 300 Pixel = 30m?
    // Bei 58 Pixel = 5,8m pro Sekunde muss man für 20m/s oder 200 Pixel/s  gleich 20m/s durch

    public Physics(Canvas canvas) {
        this.canvas = canvas;
        imageManagement = new ImageManagement(1024, 860, this);
    }

    /**
     * @param graphicsHero
     * @param direction
     * @param feetPosition
     * @param positionX
     * @param positionY
     */
    public void drawHero(Graphics graphicsHero, Direction direction, int feetPosition, int positionX, int positionY) {


        BlockArrayList mapLayer1 = getCanvas().getMapLayer1();
        BlockArrayList mapLayer2 = getCanvas().getMapLayer2();

        for (int i = 0; i < mapLayer1.size(); i++) {
            graphicsHero.drawImage(getCanvas().getTilesetBufferedImage(), mapLayer1.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer1.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer1.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer1.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer1.get(i).getSourceX() * TILESIZE, mapLayer1.get(i).getSourceY() * TILESIZE, (mapLayer1.get(i).getSourceX() + 1) * TILESIZE, (mapLayer1.get(i).getSourceY() + 1) * TILESIZE, null);
        }
        for (int i = 0; i < mapLayer2.size(); i++) {
            graphicsHero.drawImage(getCanvas().getTilesetBufferedImage(), mapLayer2.get(i).getDestinationX() * TILESIZE * ZOOM, mapLayer2.get(i).getDestinationY() * TILESIZE * ZOOM, (mapLayer2.get(i).getDestinationX() + 1) * TILESIZE * ZOOM, (mapLayer2.get(i).getDestinationY() + 1) * TILESIZE * ZOOM, mapLayer2.get(i).getSourceX() * TILESIZE, mapLayer2.get(i).getSourceY() * TILESIZE, (mapLayer2.get(i).getSourceX() + 1) * TILESIZE, (mapLayer2.get(i).getSourceY() + 1) * TILESIZE, null);
        }

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
        graphicsHero.dispose();
    }

    private void gameloopDrivenFallMethod(int jumpDownTimerSpeed) {
        setFallDuration(System.currentTimeMillis() - (getFallStartTime()));
        jumpDownTimerSpeed = (int) (jumpDownTimerSpeed / getGravitation()) * (int) (getFallDuration() / mario.getFallDelay());
        for (int i = 0; i < jumpDownTimerSpeed; i++) {
            mario.moveDown(1);                                                                                     //1 Pixel nach unten

            if (mario.getPositionY() == mario.getFinalPositionY()) {
                isFalling = false;
                setFallDuration(0);
                mario.setDirection(mario.getDirectionBackup());                                                         //Wenn während des Sprungs/Falls die Richtung durch Tastendruck gewechselt wird, wird das DirectionBackup damit überschrieben
                mario.setFeetPosition(2);                                                                               //FußPosition 2 -> Mario steht
                canvas.repaint();
                break;
            }
        }
        canvas.repaint();
    }

    private void gameloopDrivenJumpMethod() {
        setTimeElapsedInMs();     //Aktuell vergangene Zeit setzen

        for (int i = 0; i < getPixelPerTimerPass(); i++) {
            setTimeElapsedInMs();     //Aktuell vergangene Zeit setzen //TODO kann vielleicht weg
            refreshPixelPerTimerPass();

            //So viel PixelSprung pro Schleifendurchgang
            mario.moveUp(1);                                                                                       //eins nach oben pro Schleifendurchgang
            increaseJumpedPixelCounter(1);

            if (getPixelPerTimerPass() < getMinimumSpeed()) {                                                           //Methode 1: Wenn er weniger als 3 Pixel pro Durchlauf Geschwindigkeit hat, hört er auf -> unterschiedliche Sprunghöhen
//                if (getJumpedPixelCounter() == 288) {                                                                 //Methode 2: Wenn er 288 Pixel erreicht hat, stoppt er den JumpUpTimer -> wenn nie erreicht bleibt er stehen -> unterschiedliche Sprungzeiten
//            if (getDurationJump() >= getJumpTimeInMs()) {                                                             //Methode 3: Wenn Zeit abgelaufen, dann stoppt er den JumpUpTimer -> unterschiedliche Sprunghöhen?

                isJumping = false;                                                                                      //Jumping abgeschlossen
                mario.setFinalPositionY(mario.getPositionY() + getJumpedPixelCounter());                                //FinalPositionY berechnen durch aktuelle PositionY - Sprunghöhe
                System.out.println("gesprungene Pixel:");                                                               //debug
                System.out.println(getJumpedPixelCounter());                                                            //debug
                System.out.println("Vergangene Zeit");                                                                  //debug
                System.out.println(getTimeElapsedInMs());                                                               //debug
                System.out.println();                                                                                   //debug

                setJumpedPixelCounter(0);                                                                               //Gesprungene Pixel wieder auf 0 setzen, da Sprung zurückgesetzt wird
                setFallStartTime(System.currentTimeMillis());
                isFalling = true;                                                                                       //Fallen eingeleitet
                break;                                                                                                  //Wenn If Bedingung eintritt, for Schleife abbrechen
            }
        }
    }

    /**
     * Formel zum Berechnen der Sprungdauer bei gegebener Start-Geschwindigkeit
     *
     * @return
     */
    public double getJumpTimeInMs() {
        double time = (-this.startSpeedInMeterPerSecond / -this.gravitation);
        return time * 1000;
    }

    public long getDurationJump() {
        return System.currentTimeMillis() - start;
    }

    /**
     * Formel zum Berechnen der Geschwindigkeit nach einer gewissen Zeit
     *
     * @param timeElapsedInMs
     * @return
     */
    public double getSpeedAfterTimeInMeterPerSecond(long timeElapsedInMs) {
//        return startSpeedInMeterPerSecond - gravitation * (timeElapsedInMs / 1000.0);
        return ((this.startSpeedInMeterPerSecond * 1000) - (this.gravitation * timeElapsedInMs)) / 1000;
    }

//    public void calculateJump() {
//        pixelPerTimerPassList = new ArrayList<>();
//        for (int i = 0; i < getJumpTimeInMs(); i++) {       //JumpTime 500ms
//            pixelPerTimerPassList.add(getNeededPixelPerTimerPassWithGivenSpeedInMeterPerSecond(getSpeedAfterTimeInMeterPerSecond(i)));
//        }
//    }


    public int getNeededPixelPerTimerPassWithGivenSpeedInMeterPerSecond(double speedInMeterPerSecond) {
        return (int) (speedInMeterPerSecond);
    }

    public Physics refreshPixelPerTimerPass() {
        this.pixelPerTimerPass = getPixelPerTimerPass();
        return this;
    }

    public int getPixelPerTimerPass() {                         //Pixel die er pro TimerDurchgang hoch springt
        int speed = (int) (getSpeedAfterTimeInMeterPerSecond(timeElapsedInMs));
        if (speed > 0) return (int) (getSpeedAfterTimeInMeterPerSecond(timeElapsedInMs));
        else return 1;
    }

    public Physics setTimeElapsedInMs() {
        long timeElapsedInMs = System.currentTimeMillis() - start;
        this.timeElapsedInMs = timeElapsedInMs;
        return this;
    }


    public double getStartSpeedInMeterPerSecond() {
        return startSpeedInMeterPerSecond;
    }

    public void setStartSpeedInMeterPerSecond(double startSpeedInMeterPerSecond) {
        this.startSpeedInMeterPerSecond = startSpeedInMeterPerSecond;
    }

    public double getGravitation() {
        return gravitation;
    }

    public long getTimeElapsedInMs() {
        return timeElapsedInMs;
    }

    public int getJumpedPixelCounter() {
        return jumpedPixelCounter;
    }

    public Physics setJumpedPixelCounter(int jumpedPixelCounter) {
        this.jumpedPixelCounter = jumpedPixelCounter;
        return this;
    }

    public Physics increaseJumpedPixelCounter(int pixel) {
        jumpedPixelCounter = jumpedPixelCounter + pixel;
        return this;
    }

    public long getFallStartTime() {
        return fallStartTime;
    }

    public Physics setFallStartTime(long fallStartTime) {
        this.fallStartTime = fallStartTime;
        return this;
    }

    public long getFallDuration() {
        return fallDuration;
    }

    public Physics setFallDuration(long fallDuration) {
        this.fallDuration = fallDuration;
        return this;
    }

    public int getMinimumSpeed() {
        return minimumSpeed;
    }


    public long getStart() {
        return start;
    }

    public Physics setStart(long start) {
        this.start = start;
        return this;
    }


    public List<Integer> getPixelPerTimerPassList() {
        return pixelPerTimerPassList;
    }


    public boolean getJumping() {
        return isJumping;
    }

    public Physics setJumping(boolean jumping) {
        this.isJumping = jumping;
        return this;
    }

    public boolean getFalling() {
        return isFalling;
    }

    public Physics setFalling(boolean falling) {
        this.isFalling = falling;
        return this;
    }


    @Override
    public void run() {
        final int MAX_FRAMES_PER_SECOND = 100; //FPS     //Todo begrenzen!ö
        final int MAX_UPDATES_PER_SECOND = 100; //UPS

        final double uOPTIMAL_TIME = 1000000000 / MAX_UPDATES_PER_SECOND;
        final double fOPTIMAL_TIME = 1000000000 / MAX_FRAMES_PER_SECOND;

        double uDeltaTime = 0, fDeltaTime = 0;
        int frames = 0;
        int updates = 0;
        long startTime = System.nanoTime();
        long timer = System.currentTimeMillis();


        while (isRunning) {

            //calculate difference in time
            long currentTime = System.nanoTime();
            uDeltaTime += (currentTime - startTime);
            fDeltaTime += (currentTime - startTime);
            startTime = currentTime;

            if (uDeltaTime >= uOPTIMAL_TIME) {
                update();
                updates++;
                uDeltaTime -= uOPTIMAL_TIME;
            }

            if (fDeltaTime >= fOPTIMAL_TIME) {
                render();
                frames++;
                fDeltaTime -= fOPTIMAL_TIME;
            }


            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("UPS: " + updates + ", FPS: " + frames);
                performance = "UPS: " + updates + ", FPS: " + frames;

                updates = 0;
                frames = 0;
                timer += 1000;
            }


        }
    }

    public void update() {

        if (mario == null) {
            mario = canvas.getMario();
            long start = System.currentTimeMillis();
            while (mario == null) {
                if (System.currentTimeMillis() - start >= 100) {
                    System.out.println("Wait for getting mario object...");
                    start = System.currentTimeMillis();
                }
            }
        }


        if (canvas.isPressingLeftButton() && canvas.isPressingRightButton() && mario.getSpeed() == 0) {
            mario.setFeetPosition(2);
        }

        if (mario.getSpeed() > 0) {
            if (isJumping || isFalling) {
                mario.setFeetPosition(2);                                                                   //FeetPosition 2 beim Jump heißt, dass Mario rechts guckt
            }
            if (mario.getPositionX() >= mario.getFinalPositionX() && !isJumping && !isFalling) {            //Wenn 16 Pixel/halber Block erreicht dann feetPosition changen
                mario.changeFeetPosition();
                mario.refreshFinalPositionX();
            }
        }
        if (mario.getSpeed() < 0) {
            if (isJumping || isFalling) {
                mario.setFeetPosition(1);                                                                   //Wenn Mario links guckt, muss das entsprechende Bild gezeichnet werden
            }
            if (mario.getPositionX() <= mario.getFinalPositionX() && !isJumping && !isFalling) {            //Wenn 16 Pixel/halber Block erreicht dann feetPosition changen
                mario.changeFeetPosition();
                mario.refreshFinalPositionX();
            }
        }
        mario.move(mario.getSpeed());
        //////////////////////////////////// Ende des MoveEvents
        if (isJumping) {
            mario.setDirection(Direction.UP);                                                               //Wenn Mario springt, dann setze Direction auf "up" //TODO doppelt?
            gameloopDrivenJumpMethod();
        }
        if (isFalling) {                                                                                    //TODO Dieser Code verhindert die Möglichkeit die Fußstellung noch vor dem Erreichen des Bodens zurückzustellen
            mario.setDirection(Direction.DOWN);                                                             //Wenn Mario fällt, dann setze Direction auf "down"
            gameloopDrivenFallMethod(jumpDownTimerSpeed);
        }

    }


    public void render() {
        BufferedImage heroBufferedImage = imageManagement.getNextDrawingImage();
        Graphics heroGraphics = heroBufferedImage.getGraphics();
        if (mario == null) {
            mario = canvas.getMario();
            long start = System.currentTimeMillis();
            while (mario == null) {
                if (System.currentTimeMillis() - start >= 100) {
                    System.out.println("Wait for getting mario object...");
                    start = System.currentTimeMillis();
                }
            }
        }
        drawHero(heroGraphics, mario.getDirection(), mario.getFeetPosition(), mario.getPositionX(), mario.getPositionY());
        mario.setPromptRepaintHero(true);
        canvas.repaint();
    }


    public Canvas getCanvas() {
        return canvas;
    }


    public Physics setRunning(boolean running) {
        isRunning = running;
        return this;
    }

    public void setHeroObject(Hero mario) {
        this.mario = mario;
    }


    public String getPerformance() {
        return performance;
    }


}
