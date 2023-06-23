package gremlins;


import gremlins.config.GameConfiguration;
import gremlins.entity.*;
import gremlins.util.LevelFileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {



    private GameConfiguration gameConfig;

    @BeforeEach
    public void init(){
        gameConfig = new GameConfiguration( PApplet.loadJSONObject(new File("config.json")));
    }

    @Test
    public void testReadLevel() {
        List<String> strings = LevelFileUtil.readLines("level1.txt");
        assertNotNull(strings);
        assertTrue(strings.size() > 0);


        List<String> nothing =  LevelFileUtil.readLines("aaa.txt");
        assertEquals(nothing.size(), 0);

    }

    @Test
    public void testPlaySound(){
        SoundEffect.DEATH.play();
    }

    @Test
    public void testConfiguration(){
        assertNotNull(gameConfig);
        assertEquals(1, gameConfig.getLevelNumber());
        assertEquals(3, gameConfig.getLiveNumber());
        gameConfig.nextLevel();
        assertEquals(2, gameConfig.getLevelNumber());
        gameConfig.reduceLive();
        assertEquals(2, gameConfig.getLiveNumber());
    }


    @Test
    public void testLevel(){
        assertEquals(0.3333, gameConfig.getLevel().getWizardCooldown());
        assertEquals(3, gameConfig.getLevel().getEnemyCooldown());
        gameConfig.nextLevel();
        assertEquals(2, gameConfig.getLevel().getWizardCooldown());
        assertEquals(1, gameConfig.getLevel().getEnemyCooldown());
    }

    @Test
    public void testTile(){
        Tile[][] tiles = gameConfig.getLevel().getMap();
        assertEquals(tiles.length,  33);
        assertEquals(tiles[0].length,  36);

        assertEquals(tiles[0][0].getCharacter(), 'X');
        assertEquals(tiles[1][2].getCharacter(), 'W');
        assertEquals(tiles[3][6].getX(), 6);
        assertEquals(tiles[3][6].getY(), 3);

        assertFalse(tiles[0][0].moveTo());
        assertTrue(tiles[1][1].moveTo());
        assertEquals(tiles[4][4].getInitCharacter(), ' ');

        tiles[4][4].setCharacter('X');
        assertEquals(tiles[4][4].getCharacter(), 'X');

        assertEquals(tiles[4][4].getFrame(), -1);
        tiles[4][4].addFrame();
        assertEquals(tiles[4][4].getFrame(), 0);
        tiles[4][4].addFrame();
        assertEquals(tiles[4][4].getFrame(), 1);
    }


    @Test
    public void testWizard(){
        Wizard wizard = new Wizard(20, 20, 40);
        assertEquals(wizard.getX(), 20);
        assertEquals(wizard.getY(), 40);


        assertEquals(wizard.getDirection(), 3);
        wizard.updateDirection(2);
        assertEquals(wizard.getDirection(), 2);

        Tile[][] map = gameConfig.getLevel().getMap();

        wizard.left(map);
        assertEquals(wizard.getX(), 20);
        assertEquals(wizard.getY(), 40);


        wizard.down(map);
        assertEquals(wizard.getX(), 20);
        assertEquals(wizard.getY(), 60);

        wizard.right(map);
        assertEquals(wizard.getX(), 20);
        assertEquals(wizard.getY(), 60);

        wizard.reset();
        assertEquals(wizard.getX(), 20);
        assertEquals(wizard.getY(), 40);

        wizard.up(map);
        assertEquals(wizard.getX(), 20);
        assertEquals(wizard.getY(), 20);

        wizard.down(map);
        assertEquals(wizard.getX(), 20);
        assertEquals(wizard.getY(), 40);

        wizard.updateDirection(1);

        assertEquals(wizard.getShootTime(), 0L);
        Bullet bullet = wizard.shoot(3);
        assertEquals(bullet.getX(), wizard.getX());
        assertEquals(bullet.getY(), wizard.getY() + 4);
        assertTrue(wizard.getShootTime() > 0);

        bullet.move(map);
        assertEquals(bullet.getX(), wizard.getX());
        assertEquals(bullet.getY(), wizard.getY() + 8);

        bullet.updateDirection(0);
        bullet.move(map);
        assertEquals(bullet.getX(), wizard.getX());
        assertEquals(bullet.getY(), wizard.getY() + 4);


        bullet.updateDirection(2);
        bullet.move(map);
        assertEquals(bullet.getX(), wizard.getX() - 4);
        assertEquals(bullet.getY(), wizard.getY() + 4);

        bullet.updateDirection(3);
        bullet.move(map);
        assertEquals(bullet.getX(), wizard.getX());
        assertEquals(bullet.getY(), wizard.getY() + 4);

        bullet.up(map);
        assertEquals(bullet.getX(), wizard.getX());
        assertEquals(bullet.getY(), wizard.getY());

        bullet.down(map);
        assertEquals(bullet.getX(), wizard.getX());
        assertEquals(bullet.getY(), wizard.getY() + 4);

        bullet.left(map);
        assertEquals(bullet.getX(), wizard.getX() - 4);
        assertEquals(bullet.getY(), wizard.getY() + 4);

        bullet.right(map);
        assertEquals(bullet.getX(), wizard.getX());
        assertEquals(bullet.getY(), wizard.getY() + 4);

        Tile[][] reset = gameConfig.getLevel().reset();
        assertNotEquals(reset, map);
        Tile[][] map1 = gameConfig.getLevel().getMap();
        assertEquals(reset, map1);
    }

    @Test
    public void testGremlins(){
        Gremlins gremlins1 = new Gremlins(1, 100, 40);
        Gremlins gremlins2 = new Gremlins(10, 100, 40);
        assertEquals(gremlins1, gremlins2);
        Tile[][] map = gameConfig.getLevel().getMap();
        boolean canMove = gremlins1.right(map);
        assertTrue(canMove);
        assertEquals(gremlins1.getX(), 101);
        assertEquals(gremlins1.getY(), 40);

        gremlins1.setSpeed(10);
        canMove = gremlins1.right(map);
        assertTrue(canMove);
        assertEquals(gremlins1.getX(), 111);
        assertEquals(gremlins1.getY(), 40);

        gremlins1.awayFrom(100, 60, map);

        double distance2 = Math.pow(gremlins1.getX() - 100, 2) + Math.pow(gremlins1.getY() - 60, 2);
        assertTrue(distance2 >= 40000);

        gremlins1.reset();
        assertEquals(gremlins1.getX(), 100);
        assertEquals(gremlins1.getY(), 40);

        gremlins1.updateDirection(3);
        assertEquals(gremlins1.getDirection(), 3);

        assertEquals(gremlins1.getShootTime(), 0);
        Bullet shoot = gremlins1.shoot(0.333);
        assertTrue(gremlins1.getShootTime() > 0);
        assertEquals(shoot.getX(), gremlins1.getX() + 4);
        assertEquals(shoot.getY(), gremlins1.getY());


        shoot.updateDirection(0);
        shoot.move(map);
        assertEquals(shoot.getX(), gremlins1.getX() + 4);
        assertEquals(shoot.getY(), gremlins1.getY() - 4);

        shoot.updateDirection(1);
        shoot.move(map);
        assertEquals(shoot.getX(), gremlins1.getX() + 4);
        assertEquals(shoot.getY(), gremlins1.getY());

        shoot.updateDirection(2);
        shoot.move(map);
        assertEquals(shoot.getX(), gremlins1.getX());
        assertEquals(shoot.getY(), gremlins1.getY());

        shoot.up(map);
        assertEquals(shoot.getX(), gremlins1.getX());
        assertEquals(shoot.getY(), gremlins1.getY() - 4);

        shoot.down(map);
        assertEquals(shoot.getX(), gremlins1.getX());
        assertEquals(shoot.getY(), gremlins1.getY());

        shoot.left(map);
        assertEquals(shoot.getX(), gremlins1.getX() - 4);
        assertEquals(shoot.getY(), gremlins1.getY());

        shoot.right(map);
        assertEquals(shoot.getX(), gremlins1.getX());
        assertEquals(shoot.getY(), gremlins1.getY());

    }
}
