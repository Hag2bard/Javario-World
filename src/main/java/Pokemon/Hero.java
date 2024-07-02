package Pokemon;


public class Hero {

    private final Physics physics;
    private Canvas canvas;
    private final int TILESIZE;
    private final int ZOOM;
    private int speed = 0;
    private int feetPosition = 2;  //1,2,3            (1 = rechter Fuß, 2 = normal, 3 = linker Fuß) Veraltetes Kommentar, da Wiederverwendung aus anderem Projekt
    private Direction direction = Direction.RIGHT;
    private int positionX = 0;  //Position des Charakters beim Start
    private int positionY;                                                                                              //Position des Charakters beim Start
    private int finalPositionX;                                                                                         //Temporäre Final-Position für den Fußwechsel
    private int finalPositionY;
    private int walkSpeed = 4;
    private int fallDelay = 41;                                                                                         //Umso höher umso langsamer fällt Mario
    private int feetPositionBackup;
    private Direction directionBackup;

    public Hero(Canvas canvas, Physics physics) {
        this.physics = physics;
        this.canvas = canvas;
        ZOOM = canvas.getZOOM();
        TILESIZE = canvas.getTILESIZE();
        positionX = 2 * TILESIZE * ZOOM;
        positionY = 10 * TILESIZE * ZOOM;
        physics.setHeroObject(this);
    }

    public void doJump() {
        if (!physics.getJumping() && !physics.getFalling()) {
            finalPositionY = getPositionY() - physics.getJumpedPixelCounter();
            physics.setStart(System.currentTimeMillis());
            physics.setJumping(true);
        }
    }

    public void move(int speed) {                           //Bewegt Charakter in angegebener Geschwindigkeit (- oder +)
        positionX += speed;
        if (speed != 0) {                                   //Bei Geschwindigkeit 0 muss nicht repainted werden
            canvas.repaint();
        }
    }

    public void moveUp(int pixel) {
        for (int i = 0; i < pixel; i++) {
            positionY--;
            canvas.repaint();
        }
    }

    public void moveDown(int pixel) {
        for (int i = 0; i < pixel; i++) {
            positionY++;
            canvas.repaint();
        }
    }

    public void refreshFinalPositionX() {
        if (speed > 0) {
            finalPositionX = positionX + (canvas.getTILESIZE() / 2) * canvas.getZOOM();  //Wert muss das X fache sein
        }
        if (speed < 0) {
            finalPositionX = positionX - (canvas.getTILESIZE() / 2) * canvas.getZOOM();
        }
    }

    public void changeFeetPosition() {
        if (feetPosition == 1) {                        //rechts1, normal2, links3  //im Mario Projekt kein rechter Fuß
            feetPosition = 2;
        } else if (feetPosition == 2) {
            feetPosition = 1;
        } else {
            feetPosition = 2;
        }
        canvas.repaint();

    }

    /*
    Getter und Setter
     */
    public int getFeetPosition() {
        return feetPosition;
    }

    public Hero setFeetPosition(int feetPosition) {
        this.feetPosition = feetPosition;
        canvas.repaint();
        return this;
    }

    public Direction getDirection() {
        return direction;
    }

    public Hero setDirection(Direction direction) {
        this.direction = direction;
        canvas.repaint();
        return this;
    }

    public int getPositionX() {
        return positionX;
    }

    public Hero setPositionX(int positionX) {
        this.positionX = positionX;
        canvas.repaint();
        return this;
    }

    public int getFinalPositionX() {
        return finalPositionX;
    }

    public Hero setFinalPositionX(int finalPositionX) {
        this.finalPositionX = finalPositionX;
        return this;
    }

    public int getWalkSpeed() {
        return walkSpeed;
    }

    public Hero setWalkSpeed(int walkSpeed) {
        this.walkSpeed = walkSpeed;
        return this;
    }

    public int getFeetPositionBackup() {
        return feetPositionBackup;
    }

    public Hero setFeetPositionBackup(int feetPositionBackup) {
        this.feetPositionBackup = feetPositionBackup;
        return this;
    }

    public Direction getDirectionBackup() {
        return directionBackup;
    }

    public Hero setDirectionBackup(Direction directionBackup) {
        this.directionBackup = directionBackup;
        return this;
    }

    public int getSpeed() {
        return speed;
    }

    public Hero setSpeed(int speed) {
        this.speed = speed;
        return this;
    }


    public int getFallDelay() {
        return fallDelay;
    }

    public Hero setFallDelay(int fallDelay) {
        this.fallDelay = fallDelay;
        return this;
    }


    public int getPositionY() {
        return positionY;
    }

    public int getFinalPositionY() {
        return finalPositionY;
    }

    public Hero setFinalPositionY(int finalPositionY) {
        this.finalPositionY = finalPositionY;
        return this;
    }

}
