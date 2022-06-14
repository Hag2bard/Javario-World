package Pokemon;

import PokemonEditor.BlockArrayList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Physics {


    private final double gravitation = 9.81;                //    m/s           //Veränderte Gravitation lässt Mario langsamer hoch gleiten
    private int pixelPerTimerPass = 1;                      //muss verändert werden für Geschwindigkeit,
    private final double timerPassInMs = 17;                           //so viele ms pro TimerDurchlauf
    private long timeElapsedInMs;
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
    private boolean isRunning = true;
    Runnable animationRunnable;
    private Thread animationThread;
    private volatile BufferedImage buffImageA;           //wird gerade angezeigt
    private volatile BufferedImage buffImageB;           //steht parat für nächstes paint (kann NULL sein)
    private volatile BufferedImage buffImageC;           //Wird gerade gemalt
    private volatile BufferedImage buffImageD;           //Steht parat für das nächste Malen (ggf 2 Bilder)
    private boolean isPainting = false;
    Graphics graphicsBuffImageC;
    boolean isDoRepaintActive = false;
    private int sleepDuration = 10;


    private int FPS = 0;
// Ein Counterdurchgang = 17ms
    // in 1 s = 58 Durchgänge = 58 Pixel
    // Mario 20 Pixel hoch = 2m?
    //Sprung = 300 Pixel = 30m?
    // Bei 58 Pixel = 5,8m pro Sekunde muss man für 20m/s oder 200 Pixel/s  gleich 20m/s durch

    public Physics(Canvas canvas) {
        this.canvas = canvas;
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
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
                    //////////////////////////////////// MOVETIMER VORBEI
                    if (isJumping) {
                        mario.setDirection(Direction.UP);
                        timerDrivenJumpMethod();
                    }
                    if (isFalling) {
                        mario.setDirection(Direction.DOWN);                                                             //Wenn Mario fällt, dann setze Direction auf "down"
                        timerDrivenFallMethod(jumpDownTimerSpeed);
                    }
                    try {
                        Thread.sleep(sleepDuration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                canvas.repaint();
            }
        };
        animationThread = new Thread(animationRunnable);
    }


    public void startAnimation() {
        animationThread.start();
    }

    private void timerDrivenFallMethod(int jumpDownTimerSpeed) {
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
    }

    private void timerDrivenJumpMethod() {
        setTimeElapsedInMs();     //Aktuell vergangene Zeit setzen

        for (int i = 0; i < getPixelPerTimerPass(); i++) {
            setTimeElapsedInMs();     //Aktuell vergangene Zeit setzen //TODO kann vielleicht weg
            refreshPixelPerTimerPass(getPixelPerTimerPass());

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

    public Physics refreshPixelPerTimerPass(int pixelPerTimerPass) {
        this.pixelPerTimerPass = pixelPerTimerPass;
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


    public Physics setHeroObject(Hero mario) {
        this.mario = mario;
        return this;
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


}
