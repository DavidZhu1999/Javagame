package gremlins.entity;

/**
 * Class Bullet
 */
public class Bullet extends MoveThing{

    public Bullet(int speed, int x, int y) {
        super(speed, x, y);
    }

    /**
     * bullet moving
     *
     * @param map current map of the game
     */
    public void move(Tile[][] map){
        switch (this.direction) {
            case 0:
                up(map);
                break;
            case 1:
                down(map);
                break;
            case 2:
                left(map);
                break;
            case 3:
                right(map);
                break;
        }
    }

    /**
     * move left
     *
     * @param map current map of the game
     * @return true or false
     */
    public boolean left(Tile[][] map) {
        this.x = this.x - speed;
        return true;
    }

    /**
     * move right
     *
     * @param map current map of the game
     * @return true or false
     */
    public boolean right(Tile[][] map) {
        this.x = this.x + speed;
        return true;
    }

    /**
     * move up
     *
     * @param map current map of the game
     * @return true or false
     */
    public boolean up(Tile[][] map) {
        this.y = this.y - speed;
        return true;
    }


    /**
     * move down
     *
     * @param map current map of the game
     * @return true or false
     */
    public boolean down(Tile[][] map) {
        this.y = this.y + speed;
        return true;
    }

    @Override
    public void reset() {

    }
}
