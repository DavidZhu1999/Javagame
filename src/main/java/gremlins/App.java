package gremlins;

import gremlins.config.GameConfiguration;
import gremlins.entity.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.IntList;
import processing.data.JSONObject;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;


/**
 * Game display main class
 * In the game, the calculation time is used instead of the number of frames;
 * 1 s = 60 frames
 */
public class App extends PApplet {

    /**
     * windows width
     */
    public static final int WIDTH = 720;
    /**
     * windows height
     */
    public static final int HEIGHT = 720;
    /**
     * each grid own pixel
     */
    public static final int PIXEL = 20;

    /**
     * The square of the minimum distance from the player to respawn after the enemy dies
     */
    public static final int AWAY_DISTANCE = 100 * PIXEL * PIXEL;

    /**
     * Player character movement speed
     */
    public static final int WIZARD_SPEED = 2;

    /**
     * enemy movement speed
     */
    public static final int GREMLIN_SPEED = 1;

    /**
     * bullet movement speed
     */
    public static final int BULLET_SPEED = 4;

    /**
     * Game screen refresh rate
     */
    public static final int FPS = 60;

    /**
     * gremlins freeze time
     */
    public static final int FREEZE_FPS = 4 * FPS;

    /**
     * configuration file path
     */
    private String configPath;

    /**
     * User operation key list
     */
    private IntList keys = new IntList();

    /**
     * Number formatting tool with three decimal places
     */
    private DecimalFormat decimalFormat = new DecimalFormat("#0.000");

    /**
     * Loaded image cache
     */
    private Map<Character, PImage> cacheImage;
    /**
     * The collection of enemies in the current level of the game
     */
    private List<Gremlins> gremlins;
    /**
     * The collection of bullets for the current level of the game
     */
    private List<Bullet> bullets;
    /**
     * Game configuration class
     */
    private GameConfiguration gameConfig;
    /**
     * Player manipulates objects
     */
    private Wizard wizard;
    /**
     * The current level map of the game
     */
    private Tile[][] map;
    /**
     * Game state 0 normal operation; 100 clearance; 200 failure
     */
    private int status;

    /**
     * Profile object
     */
    private JSONObject conf;

    /**
     * The length of time the enemy is frozen; -1 means not frozen
     */
    private int freezeFrame = -1;
    /**
     * Frozen props respawn for 5 s
     */
    private int rebornFps = 5 * FPS;
    /**
     * Elapsed time (frames) after freezing props are used
     */
    private int rebornCur = -1;

    /**
     * constructor
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    public void settings() {
        size(WIDTH, HEIGHT);
        smooth();
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
    public void setup() {
        frameRate(FPS);
        background(227, 168, 105);
        g.textFont(createDefaultFont(10));
        gremlins = new ArrayList<>();
        bullets = new ArrayList<>();
        // Load images during setup

        conf = loadJSONObject(new File(this.configPath));
        gameConfig = new GameConfiguration(conf);
        map = gameConfig.getLevel().getMap();
        loadImage();
    }

    /**
     * Load the image into the cache to prevent secondary loading
     */
    private void loadImage() {
        this.cacheImage = new HashMap<>();
        this.cacheImage.put('X', loadImage(this.getClass().getResource("stonewall.png").getPath().replace("%20", "")));
        this.cacheImage.put('B', loadImage(this.getClass().getResource("brickwall.png").getPath().replace("%20", "")));
        this.cacheImage.put('E', loadImage(this.getClass().getResource("gate.png").getPath().replace("%20", "")));
        this.cacheImage.put('G', loadImage(this.getClass().getResource("gremlin.png").getPath().replace("%20", "")));
        this.cacheImage.put('S', loadImage(this.getClass().getResource("slime.png").getPath().replace("%20", "")));
        this.cacheImage.put('s', loadImage(this.getClass().getResource("fireball.png").getPath().replace("%20", "")));
        this.cacheImage.put((char) 37, loadImage(this.getClass().getResource("wizard0.png").getPath().replace("%20", "")));
        this.cacheImage.put((char) 38, loadImage(this.getClass().getResource("wizard2.png").getPath().replace("%20", "")));
        this.cacheImage.put((char) 39, loadImage(this.getClass().getResource("wizard1.png").getPath().replace("%20", "")));
        this.cacheImage.put('W', loadImage(this.getClass().getResource("wizard1.png").getPath().replace("%20", "")));
        this.cacheImage.put((char) 40, loadImage(this.getClass().getResource("wizard3.png").getPath().replace("%20", "")));
        this.cacheImage.put('U', loadImage(this.getClass().getResource("freezewall.png").getPath().replace("%20", "")));
        PImage d0 = loadImage(this.getClass().getResource("brickwall_destroyed0.png").getPath().replace("%20", ""));
        this.cacheImage.put((char) 1, d0);
        this.cacheImage.put((char) 2, d0);
        this.cacheImage.put((char) 3, d0);
        this.cacheImage.put((char) 4, d0);
        PImage d1 = loadImage(this.getClass().getResource("brickwall_destroyed1.png").getPath().replace("%20", ""));
        this.cacheImage.put((char) 5, d1);
        this.cacheImage.put((char) 6, d1);
        this.cacheImage.put((char) 7, d1);
        this.cacheImage.put((char) 8, d1);
        PImage d2 = loadImage(this.getClass().getResource("brickwall_destroyed2.png").getPath().replace("%20", ""));
        this.cacheImage.put((char) 9, d2);
        this.cacheImage.put((char) 10, d2);
        this.cacheImage.put((char) 11, d2);
        this.cacheImage.put((char) 12, d2);
        PImage d3 = loadImage(this.getClass().getResource("brickwall_destroyed3.png").getPath().replace("%20", ""));
        this.cacheImage.put((char) 13, d3);
        this.cacheImage.put((char) 14, d3);
        this.cacheImage.put((char) 15, d3);
        this.cacheImage.put((char) 16, d3);

        PImage freeze = loadImage(this.getClass().getResource("freeze.png").getPath().replace("%20", ""));
        this.cacheImage.put('F', freeze);
    }

    /**
     * Item Respawn Timing
     */
    private void rebornTimer() {
        if (rebornCur >= 0) {
            rebornCur++;
            if (rebornCur >= rebornFps) {
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[i].length; j++) {
                        Tile tile = map[i][j];
                        if (tile.getInitCharacter() == 'F') {
                            tile.setCharacter('F');
                        }
                    }
                }
                rebornCur = -1;
            }
        }
    }


    /**
     * The main interface renders the picture. The rendering logic is as follows:
     * For objects that cannot be moved, such as bricks, the image buffer is obtained and
     * loaded according to the symbol of the map two-dimensional array, and then rendered
     * to the specified position according to the coordinates of the two-dimensional array.
     * Players, bullets, enemies, etc. can move objects. According to the object in the current
     * class, use the object's x and y coordinates to render the image to the corresponding position
     *
     */
    private void showImage() {
        rebornTimer();
        if (freezeFrame >= 0) {
            freezeFrame++;
            if (freezeFrame >= FREEZE_FPS) {
                freezeFrame = -1;
            }
        }
        if (freezeFrame < 0) {
            for (Gremlins g : this.gremlins) {
                g.setSpeed(GREMLIN_SPEED);
            }
        }
        if (wizard != null) {
            // Players use frozen prop detection
            if (map[wizard.getY() / PIXEL][wizard.getX() / PIXEL].getCharacter() == 'F') {
                map[wizard.getY() / PIXEL][wizard.getX() / PIXEL].setCharacter(' ');
                freezeEnemy();
                rebornCur = 1;
            }
            // Player moves to exit detection
            if (map[wizard.getY() / PIXEL][wizard.getX() / PIXEL].getCharacter() == 'E') {
                if (gameConfig.getLevelNumber() == 2) {
                    // win ，At the last level
                    status = 100;
                } else {
                    // next Level, This is the final stage
                    gameConfig.nextLevel();
                    map = gameConfig.getLevel().getMap();
                    wizard = null;
                    bullets.clear();
                    gremlins.clear();
                }
                return;
            }
        }
        // Rendering interface display effect
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                Tile tile = map[i][j];
                PImage image = cacheImage.get(tile.getCharacter());
                if (tile.getCharacter() == 'W') {
                    if (wizard == null) {
                        wizard = new Wizard(WIZARD_SPEED, tile.getX() * PIXEL, tile.getY() * PIXEL);
                        wizard.setImage(image);
                    }
                } else if (tile.getCharacter() == 'G') {
                    // render enemies
                    Gremlins gremlin = new Gremlins(GREMLIN_SPEED, tile.getX() * PIXEL, tile.getY() * PIXEL);
                    if (!this.gremlins.contains(gremlin)) {
                        this.gremlins.add(gremlin);
                    }
                } else {
                    // Render bricks and other objects
                    if (tile.getCharacter() == 'B') {
                        if (tile.getFrame() >= 0) {
                            tile.addFrame();
                            PImage r = cacheImage.get((char) tile.getFrame());
                            if (r != null) {
                                image(r, tile.getX() * PIXEL, tile.getY() * PIXEL);
                            }
                        } else {
                            image(image, tile.getX() * PIXEL, tile.getY() * PIXEL);
                        }
                    } else {
                        if (image != null) {
                            image(image, tile.getX() * PIXEL, tile.getY() * PIXEL);
                        }
                    }
                }
            }
        }
        // The consumer player has pressed the button
        if (keys.size() > 0) {
            consumeKeys();
        } else {
            continueMove();
        }
        // Render the player image
        image(wizard.getImage(), wizard.getX(), wizard.getY());

        for (Gremlins g : this.gremlins) {
            Bullet shoot = g.shoot(gameConfig.getLevel().getEnemyCooldown());
            if (shoot != null) {
                bullets.add(shoot);
            }
            // enemy moves
            g.move(map);
            // Player and enemy collision detection
            if (wizard.collideWith(g)) {
                afterWizardDead();
                return;
            }

            // Bullet impact detection
            for (MoveThing bullet : this.bullets) {
                for (Bullet bullet1 : this.bullets) {
                    if (bullet == bullet1) {
                        continue;
                    }
                    // Player bullet and enemy bullet collision detection
                    if (bullet.collideWith(bullet1) && bullet.getClass() != bullet1.getClass()) {
                        bullet.markForDelete();
                        bullet1.markForDelete();
                        break;
                    }
                }

                // Bullet hits brick detection
                for (Tile[] tiles : map) {
                    for (Tile tile : tiles) {
                        if (bullet.collideWith(tile)) {
                            if (tile.getCharacter() == 'X') {
                                bullet.markForDelete();
                            } else if (tile.getCharacter() == 'B') {
                                tile.addFrame();
                                bullet.markForDelete();
                            } else if (tile.getCharacter() == 'U') {
                                freezeEnemy();
                                tile.setCharacter(' ');
                                bullet.markForDelete();
                            }
                            break;
                        }
                    }
                }

                if (bullet.isMarkForDelete()) {
                    continue;
                }

                // Enemy hits player bullet detection
                if (g.collideWith(bullet) && bullet instanceof FireBall) {
                    g.awayFrom(wizard.getX(), wizard.getY(), map);
                    bullet.markForDelete();
                    break;
                }
            }
            image(cacheImage.get('G'), g.getX(), g.getY());
        }

        // Delete marked bit deleted object (bullet)
        this.bullets.removeIf(MoveThing::isMarkForDelete);

        for (MoveThing bullet : this.bullets) {
            bullet.move(map);
            // Enemy bullet hits player detection
            if (bullet instanceof SlimeProjectile && wizard.collideWith(bullet)) {
                afterWizardDead();
                return;
            }
            if (bullet instanceof SlimeProjectile) {
                image(cacheImage.get('S'), bullet.getX(), bullet.getY());
            } else {
                image(cacheImage.get('s'), bullet.getX(), bullet.getY());
            }
            // Bullets move out of the map outside detection
            if (bullet.outOfBound()) {
                bullet.markForDelete();
            }
        }
    }

    /**
     * Logic after player death
     * 1. Deduction of life
     * 2. Judging whether the life is enough, if it is insufficient,
     * it shows failure, and it is enough to restart the current level
     */
    private void afterWizardDead() {
        gameConfig.reduceLive();
        SoundEffect.DEATH.play();
        if (gameConfig.getLiveNumber() <= 0) {
            status = 200;
        } else {
            resetGame();
        }
    }

    /**
     * Enemy freeze effect with 0 speed
     */
    private void freezeEnemy() {
        for (Gremlins g : this.gremlins) {
            g.setSpeed(0);
        }
        SoundEffect.POWERUP.play();
        freezeFrame = 0;
    }

    /**
     * Show game wins
     */
    private void showWin() {
        text("You win ", 300, 360);
    }

    /**
     * show game failed
     */
    private void showFail() {
        text("Game over ", 300, 360);
    }


    /**
     * restart the current level
     * 1 map reset
     * 2 Player coordinates reset
     * 3 Enemy coordinates reset
     * 4 screen bullet clear
     */
    private void resetGame() {
        map = gameConfig.getLevel().reset();
        wizard.reset();
        wizard.setImage(cacheImage.get('W'));
        for (Gremlins g : gremlins) {
            g.reset();
        }
        this.bullets.clear();
    }

    /**
     * Display current game information
     */
    private void showGameInfo() {
        // show life
        text("Lives: ", PIXEL, 695);
        int liveNumber = gameConfig.getLiveNumber();
        PImage image = cacheImage.get('W');
        for (int i = 0; i < liveNumber; i++) {
            set(3 * PIXEL + PIXEL * (i + 1), 680, image);
        }

        // Show current level
        text("Level " + gameConfig.getLevelNumber() + "/2", 8 * PIXEL, 695);

        // Shows remaining enemy cooldown time
        if (freezeFrame > 0) {
            text("Left Freeze Time: " + (FREEZE_FPS - freezeFrame) / FPS, 14 * PIXEL, 695);
        }

        // Displays player's bullet release cooldown
        if (wizard != null) {
            long l1 = System.currentTimeMillis() - wizard.getShootTime();
            double wizardCooldown = gameConfig.getLevel().getWizardCooldown();
            double d = (l1) * 0.001;
            if (d < wizardCooldown) {
                text("wizard cool Time: " + decimalFormat.format(wizardCooldown - d), 23 * PIXEL, 695);
            }
        }

    }


    /**
     * Receive key pressed signal from the keyboard.
     */
    public void keyPressed() {
        if (status == 100 || status == 200) {
            status = 0;
            gameConfig = new GameConfiguration(conf);
            map = gameConfig.getLevel().getMap();
            gremlins.clear();
            bullets.clear();
            wizard = null;
            return;
        }
        //                38  up
        //                40  down
        //                37  left
        //                39  right
        //                32  space
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            if ((keys.hasValue(37) || keys.hasValue(38) || keys.hasValue(39) || keys.hasValue(40)) || needMove()) {

            } else if (!needMove()) {
                keys.append(keyCode);
            }
        } else if (!keys.hasValue(keyCode)) {
            keys.append(keyCode);
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    public void keyReleased() {
        if (keys.hasValue(keyCode)) {
            keys.removeValue(keyCode);
        }

    }

    /**
     * Determine if the player should move (stuck wall state detection)
     *
     * @return
     * true: The representative is in the stuck wall position and needs to continue to move.
     * false: Delegate does not need to continue moving
     */
    private boolean needMove() {
        if (status == 0) {
            boolean xIsOk = wizard.getX() % 20 == 0;
            boolean yIsOk = wizard.getY() % 20 == 0;
            if (!xIsOk || !yIsOk) {
                return true;
            }
        }
        return false;
    }

    /**
     * Player key effect logic
     */
    private void consumeKeys() {
        for (int keyCode : keys) {
            PImage image = cacheImage.get((char) keyCode);
            switch (keyCode) {
                case 38:
                    wizard.up(map);
                    wizard.updateDirection(0);
                    break;
                case 40:
                    wizard.down(map);
                    wizard.updateDirection(1);
                    break;
                case 37:
                    wizard.left(map);
                    wizard.updateDirection(2);
                    break;
                case 39:
                    wizard.right(map);
                    wizard.updateDirection(3);
                    break;
                case 32:
                    Bullet bullet = wizard.shoot(gameConfig.getLevel().getWizardCooldown());
                    if (bullet != null) {
                        this.bullets.add(bullet);
                    }
                    break;
            }
            if (image != null) {
                wizard.setImage(image);
            }
        }
    }


    /**
     * Draw all elements in the game by current frame.
     */
    public void draw() {
        // background color
        background(227, 168, 105);
        // Display different content according to the game state
        if (status == 100) {
            textSize(50);
            showWin();
            textSize(12);
        } else if (status == 200) {
            textSize(50);
            showFail();
            textSize(12);
        } else {
            showImage();
            showGameInfo();
        }
    }


    /**
     * Compensation movement logic after player detects stuck wall state
     */
    private void continueMove() {
        if (wizard == null) {
            return;
        }
        if (needMove()) {
            switch (wizard.getDirection()) {
                case 0:
                    wizard.up(map);
                    break;
                case 1:
                    wizard.down(map);
                    break;
                case 2:
                    wizard.left(map);
                    break;
                case 3:
                    wizard.right(map);
                    break;
            }
        }

    }

    /**
     * game start
     *
     * @param args parameter
     */
    public static void main(String[] args) {
        PApplet.main("gremlins.App");
    }
}
