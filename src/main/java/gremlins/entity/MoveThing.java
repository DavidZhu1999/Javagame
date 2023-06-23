package gremlins.entity;

import gremlins.App;

import java.awt.*;

/**
 * The abstract class of the things that can move in the game
 */
public abstract class MoveThing implements Resetable{

    /**
     * mark the entity whether can be deleted.
     */
    private boolean markForDelete = false;

    /**
     * the current move direction
     */
    protected int direction;

    /**
     * the current move speed
     */
    protected int speed;

    /**
     * the thing current x coordinate
     */
    protected int x;

    /**
     * the thing current y coordinate
     */
    protected int y;

    /**
     * the thing initial x coordinate
     */
    protected int initX;

    /**
     * the thing initial y coordinate
     */
    protected int initY;

    /**
     * record the last shoot time
     */
    protected long shootTime;

    /**
     * constructor
     *
     * @param speed speed
     * @param x initial x coordinate
     * @param y initial y coordinate
     */
    public MoveThing(int speed, int x, int y) {
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.initX = x;
        this.initY = y;
    }

    /**
     * get current x coordinate
     *
     * @return current x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * get current y coordinate
     *
     * @return current y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * refresh the now moving direction
     *
     * @param direction  0 --- up, 1-- down, 2 -- left, 3 -- right
     */
    public void updateDirection(int direction) {
        this.direction = direction;
    }

    /**
     * get current moving direction
     *
     * @return 0 --- up, 1-- down, 2 -- left, 3 -- right
     */
    public int getDirection() {
        return direction;
    }

    /**
     * refresh current move speed
     *
     * @param speed refresh speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * move different with different things
     *
     * @param map now map level
     */
    public void move(Tile[][] map){

    }

    /**
     * move left
     *
     * @param map current map level
     * @return true or false
     */
    public boolean left(Tile[][] map) {
        int newX = this.x - speed;
        Tile upTile = map[y / App.PIXEL][newX / App.PIXEL];
        Tile downTile = map[(y + App.PIXEL - 1) / App.PIXEL][newX / App.PIXEL];
        if (upTile.moveTo() && downTile.moveTo()) {
            updateCharacter(map);
            this.x = this.x - speed;
            return true;
        }
        return false;
    }

    /**
     * move right
     *
     * @param map current map level
     * @return true or false
     */
    public boolean right(Tile[][] map) {
        int newX = this.x + App.PIXEL + 1;
        Tile upTile = map[y / App.PIXEL][newX / App.PIXEL];
        Tile downTile = map[(y + App.PIXEL - 1) / App.PIXEL][newX / App.PIXEL];
        if (upTile.moveTo() && downTile.moveTo()) {
            updateCharacter(map);
            this.x = this.x + speed;
            return true;
        } else {
            if (newX % App.PIXEL == 0) {
                updateCharacter(map);
                this.x = this.x + speed;
                return true;
            }
        }
        return false;
    }

    /**
     * move up
     *
     * @param map current map level
     * @return true can move successfully,
     * false Immovable (hit a wall, etc.)
     */
    public boolean up(Tile[][] map) {
        int newY = this.y - speed;
        Tile leftTile = map[newY / App.PIXEL][x / App.PIXEL];
        Tile rightTile = map[newY / App.PIXEL][(x + App.PIXEL - 1) / App.PIXEL];
        if (leftTile.moveTo() && rightTile.moveTo()) {
            updateCharacter(map);
            this.y = this.y - speed;
            return true;
        }
        return false;
    }

    /**
     * get current shoot time
     */
    public void setShootTime() {
        this.shootTime = System.currentTimeMillis();
    }

    /**
     * get last shoot time
     *
     * @return last shoot time(ms)
     */
    public long getShootTime() {
        return shootTime;
    }

    /**
     * move down
     *
     * @param map current map level
     * @return true or false
     */
    public boolean down(Tile[][] map) {
        int newY = this.y + App.PIXEL + 1;
        Tile leftTile = map[newY / App.PIXEL][x / App.PIXEL];
        Tile rightTile = map[newY / App.PIXEL][(x + App.PIXEL - 1) / App.PIXEL];
        if (leftTile.moveTo() && rightTile.moveTo()) {
            updateCharacter(map);
            this.y = this.y + speed;
            return true;
        } else {
            if (newY % App.PIXEL == 0) {
                updateCharacter(map);
                this.y = this.y + speed;
                return true;
            }
        }
        return false;
    }

    /**
     * shoot different with different things
     *
     * @param coldTime cooldown
     * @return class of bullet
     */
    public MoveThing shoot(double coldTime) {
        return null;
    }

    /**
     * refresh the map
     *
     * @param map current map level
     */
    private void updateCharacter(Tile[][] map){
        Tile tile = map[this.y / App.PIXEL][this.x / App.PIXEL];
        if (tile.getCharacter() != 'E' && tile.getCharacter() != 'F') {
            tile.setCharacter(' ');
        }
    }


    /**
     * mark the things is deletable
     */
    public void markForDelete() {
        this.markForDelete = true;
    }

    /**
     * judge the status whether deletable
     *
     * @return true or false
     */
    public boolean isMarkForDelete() {
        return markForDelete;
    }

    /**
     * whether the thing out of range of map
     *
     * @return true or false(inside the map)
     */
    public boolean outOfBound(){
        return x < - App.PIXEL || x > App.WIDTH || y < -App.PIXEL || y >= App.HEIGHT;
    }

    /**
     * whether the things are collision
     *
     * @param another other move things
     * @return true or false
     */
    public boolean collideWith(MoveThing another) {
        Rectangle r1 = new Rectangle(x, y, App.PIXEL, App.PIXEL);
        Rectangle r2 = new Rectangle(another.x, another.y, App.PIXEL, App.PIXEL);
        return r1.intersects(r2);
    }

    /**
     * judge the collision with wall(bullet)
     *
     * @param another wall
     * @return true or false
     */
    public boolean collideWith(Tile another) {
        Rectangle r1 = new Rectangle(x, y, App.PIXEL, App.PIXEL);
        Rectangle r2 = new Rectangle(another.getX() * App.PIXEL, another.getY() * App.PIXEL, App.PIXEL, App.PIXEL);
        return r1.intersects(r2);
    }
}
