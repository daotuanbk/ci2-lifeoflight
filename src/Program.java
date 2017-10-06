import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import novels.Story;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by huynq on 7/28/17.
 */
public class Program {
    public static void main(String[] args) {
        GameWindow gameWindow = new GameWindow();
        gameWindow.gameLoop();


//             try {
//            // Step 1 read files
//            byte[] bytes = Files.readAllBytes(Paths.get("assets/events/event_arc_0.json"));
//            String content = new String(bytes, StandardCharsets.UTF_8);
//            System.out.println(content);
//
//            //Step 2: Prase json
//            Gson gson = new Gson();
//            TypeToken<List<Story>> token = new TypeToken<List<Story>>(){};
//
//            List<Story> stories = gson.fromJson(content, token.getType());
//            System.out.println(stories);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
