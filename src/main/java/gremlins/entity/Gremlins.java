package gremlins.entity;

import gremlins.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * enemy class
 */
public class Gremlins extends MoveThing{

    /**
     * get the random number to control the gremlins move direction
     */
    public static final Random randomGenerator = new Random();

    public Gremlins(int speed, int x, int y) {
        super(speed, x, y);
        direction =  randomGenerator.nextInt(4);
    }


    /**
     * judge the two gremlins whether the same one
     * if the origin coordinate same, they are the same one
     *
     * @param obj target of the gremlins
     * @return true or false
     */
    @Override
    public boolean equals(Object obj) {
        Gremlins other = (Gremlins) obj;
        return other.initX == initX && other.initY == initY;
    }

    /**
     * when the gremlins touch the wall, change their direction randomly.
     */
    private void updateDirection() {
        int direction = randomGenerator.nextInt(4);
        while ((this.direction  == 0 && direction == 1) ||
                (this.direction  == 1 && direction == 0) ||
                (this.direction  == 2 && direction == 3) ||
                (this.direction  == 3 && direction == 2) ||
                (direction == this.direction)) {
            direction = randomGenerator.nextInt(4);
        }
        this.direction = direction;
    }

    /**
     * gremlins move
     *
     * @param map the map of the current level
     */
    public void move(Tile[][] map){
        boolean turn = false;
        switch (this.direction) {
            case 0:
                turn = up(map);
                break;
            case 1:
                turn = down(map);
                break;
            case 2:
                turn = left(map);
                break;
            case 3:
                turn = right(map);
                break;
        }
        if (!turn) {
            updateDirection();
        }
    }

    /**
     * gremlins attack
     *
     * @param coldTime cooldown
     * @return Bullet obj
     */
    @Override
    public Bullet shoot(double coldTime) {
        if (speed == 0) {
            return null;
        }
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
            SlimeProjectile slimeProjectile = new SlimeProjectile(App.BULLET_SPEED, shootX, shootY);
            slimeProjectile.direction = direction;
            return slimeProjectile;
        }
        return null;
    }

    /**
     * reset the gremlins position when wizard die.
     */
    @Override
    public void reset() {
        this.x = initX;
        this.y = initY;
    }

    /**
     * move the gremlins away the wizard when gremlins die.
     *
     * @param x wizard x coordinate
     * @param y wizard y coordinate
     * @param map the map of current level
     */
    public void awayFrom(int x, int y, Tile[][] map){
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                Tile tile = map[i][j];
                double radius = Math.pow(x - j * App.PIXEL, 2) + Math.pow(y - i * App.PIXEL, 2);
                if (tile.getCharacter() == ' ' && radius > App.AWAY_DISTANCE) {
                    xs.add(i);
                    ys.add(j);
                }
            }
        }
        int index = randomGenerator.nextInt(xs.size());
        this.x = ys.get(index) * App.PIXEL;
        this.y = xs.get(index) * App.PIXEL;
    }
}
