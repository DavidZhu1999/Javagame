package gremlins.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading level files
 */
public class LevelFileUtil {

    /**
     * Convert the contents of the file into a list of characters,
     * a list of characters represents a line of content in the file
     *
     * @param path path
     * @return character list
     */
    public static List<String> readLines(String path){
        try (FileReader reader = new FileReader(path);){
            BufferedReader br = new BufferedReader(reader);
            List<String> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null && !line.equals("")) {
                result.add(line);
            }
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
