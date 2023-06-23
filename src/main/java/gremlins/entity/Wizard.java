package gremlins.entity;

import gremlins.App;
import processing.core.PImage;

/**
 * wizard class
 */
public class Wizard extends MoveThing{

    /**
     * wizard image
     */
    private PImage image;

    /**
     * constructor
     *
     * @param speed wizard speed
     * @param x wizard initial x coordinate
     * @param y wizard initial y coordinate
     */
    public Wizard(int speed, int x, int y) {
        super(speed, x, y);
        this.direction = 3;
    }

    /**
     * Set the player's current image
     *
     * @param image current image
     */
    public void setImage(PImage image) {
        this.image = image;
    }

    /**
     * Get the picture the player is currently showing
     *
     * @return current image
     */
    public PImage getImage() {
        return image;
    }

    /**
     * player fires bullet
     *
     * @param coldTime cooldown
     * @return bullet class
     */
    @Override
    public Bullet shoot(double coldTime) {
        long shootTime = getShootTime();
        long millis = System.currentTimeMillis();
        long diff = millis - shootTime;
        if (diff * 0.001 > coldTime) {
            setShootTime();
            int shootX = 0;
            int shootY = 0;
            if (direction == 0) {
                shootX = getX();
                shootY = getY() - 4;
            }
            if (direction == 1) {
                shootX = getX();
                shootY = getY() + 4;
            }
            if (direction == 2) {
                shootX = getX() - 4;
                shootY = getY();
            }
            if (direction == 3) {
                shootX = getX() + 4;
                shootY = getY();
            }
            FireBall fireBall = new FireBall(App.BULLET_SPEED, shootX, shootY);
            fireBall.direction = direction;
            return fireBall;
        }
        return null;
    }

    /**
     * Reset player info. reset its coordinates to the initial position
     */
    @Override
    public void reset() {
        this.x = initX;
        this.y = initY;
    }
}
