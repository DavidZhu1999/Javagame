package gremlins.entity;

import gremlins.util.LevelFileUtil;

import java.util.List;

/**
 * game level class
 */
public class GameLevel {

    /**
     * map of level
     */
    private Tile[][] map;

    /**
     * cooldown of the wizard atttack
     */
    private double wizardCooldown;

    /**
     * cooldown of the gremlins
     */
    private double enemyCooldown;

    /**
     * path of the game level
     */
    private String levelPath;

    /**
     * Constructor
     *
     * @param levelPath
     * @param wizardCooldown
     * @param enemyCooldown
     */
    public GameLevel(String levelPath, double wizardCooldown, double enemyCooldown){
        this.levelPath = levelPath;
        map = new Tile[33][36];
        List<String> lines = LevelFileUtil.readLines(levelPath);
        for (int i = 0; i < lines.size(); i++) {
            char[] chars = lines.get(i).toCharArray();
            for (int j = 0; j < chars.length; j ++) {
                map[i][j] = new Tile(chars[j] ,  j , i);
            }
        }
        this.wizardCooldown = wizardCooldown;
        this.enemyCooldown = enemyCooldown;
    }

    /**
     * get the map of the level
     *
     * @return  two dimension array of the level map
     */
    public Tile[][] getMap() {
        return map;
    }

    /**
     * reset the current level
     *
     * @return two dimension array of the level map after reset
     */
    public Tile[][] reset() {
        map = new Tile[33][36];
        List<String> lines = LevelFileUtil.readLines(levelPath);
        for (int i = 0; i < lines.size(); i++) {
            char[] chars = lines.get(i).toCharArray();
            for (int j = 0; j < chars.length; j ++) {
                map[i][j] = new Tile(chars[j] ,  j , i);
            }
        }
        return map;
    }

    /**
     * get the cooldown of the wizard
     *
     * @return cooldown of the wizard
     */
    public double getWizardCooldown() {
        return wizardCooldown;
    }

    /**
     * get the cooldown of the gremlins
     *
     * @return cooldown of the gremlins
     */
    public double getEnemyCooldown() {
        return enemyCooldown;
    }
}
