package gremlins.config;

import gremlins.entity.GameLevel;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * the current config of the game
 */
public class GameConfiguration {

    /**
     * the current level
     */
    private int currentLevel = 1;

    /**
     * all level
     */
    private List<GameLevel> gameLevels;

    /**
     * the life of wizard
     */
    private int liveNumber;

    /**
    * get the config from the txt
    */
    public GameConfiguration(JSONObject jsonObject) {
        gameLevels = new ArrayList<>();
        gameLevels.add(null);
        this.liveNumber = jsonObject.getInt("lives");
        JSONArray levels = (JSONArray) jsonObject.get("levels");
        int size = levels.size();
        for (int i = 0; i < size; i++) {
            JSONObject object = (JSONObject) levels.get(i);
            gameLevels.add(
                    new GameLevel(object.getString("layout"),
                    object.getDouble("wizard_cooldown"),
                    object.getDouble("enemy_cooldown"))
            );
        }
    }

    /**
     * add the level
     */
    public void nextLevel(){
        this.currentLevel ++;
    }

    /**
     * get the current level number
     *
     * @return current level number
     */
    public int getLevelNumber() {
        return currentLevel;
    }

    /**
     * get the current level config
     *
     * @return current level config
     */
    public GameLevel getLevel() {
        return gameLevels.get(currentLevel);
    }

    /**
     * get the current life of wizard
     *
     * @return current life of wizard
     */
    public int getLiveNumber() {
        return liveNumber;
    }

    /**
     * reduce the current life of wizard
     */
    public void reduceLive(){
        this.liveNumber --;
    }
}
