package gremlins.entity;

// this class present a grid in the game
public class Tile {

    /**
     * Initial character (since some props or bricks may disappear,
     * the disappearing props need to be redisplayed when the game is reset,
     * so it is used to record the starting props)
     */
    private int initCharacter;

    /**
     * Current character (props or objectives in some locations change as the game progresses)
     */
    private char character;

    /**
     * x coordinate
     */
    private int x;

    /**
     * y coordinate
     */
    private int y;

    /**
     * 自How long has it been since the bullet hit
     */
    private int frame = -1;

    /**
     * constructor
     *
     * @param character char
     * @param x x coordinate
     * @param y y coordinate
     */
    public Tile(char character, int x, int y) {
        this.character = character;
        this.initCharacter = character;
        this.x = x;
        this.y = y;
    }

    /**
     * The frame under the current coordinate is used to record the different pictures every
     * 4 frames after the bricks are concentrated, and the four pictures are up to 16 frames
     * After 16 frames, the bricks disappear completely,
     * you need to set the current character to ' '
     */
    public void addFrame() {
        this.frame ++;
        if (this.frame > 16) {
            setCharacter(' ');
            this.frame = -1;
        }
    }

    /**
     * Get the number of frames currently traversed
     *
     * @return current frame
     */
    public int getFrame() {
        return frame;
    }

    /**
     * set the current character
     *
     * @param character incoming character
     */
    public void setCharacter(char character) {
        this.character = character;
    }

    /**
     * get the current character
     *
     * @return current character
     */
    public char getCharacter() {
        return character;
    }

    /**
     * get current x coordinate
     *
     * @return x coordinate
     */
    public int getX() {
        return x;
    }


    /**
     * get the current y coordinate
     *
     * @return y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Whether the current position can be moved
     *
     * @return true or false
     */
    public boolean moveTo() {
        return character == ' ' || character == 'E' || character == 'W' || character == 'F';
    }

    /**
     * get initial character
     *
     * @return initial character
     */
    public int getInitCharacter() {
        return initCharacter;
    }

}
