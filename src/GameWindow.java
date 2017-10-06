
import bases.GameObject;
import bases.events.EventManager;
import bases.inputs.CommandListener;
import bases.inputs.InputManager;
import bases.uis.InputText;
import bases.uis.StatScreen;
import bases.uis.TextScreen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import novels.Story;
import settings.Settings;
import novels.Choice;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import novels.Time.*;
import settings.TimedExit;


import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by huynq on 7/28/17.
 */
public class GameWindow extends JFrame {

    private BufferedImage backBufferImage;
    private Graphics2D backBufferGraphics;

    private int playerX = 2;
    private int playerY = 3;

    private int mapWidth = 5;
    private int mapHeight = 5;

    private int trapX = 2;
    private int trapY = 2;

    HashMap<String, Story> storyMap = new HashMap<>();

    Story currentStory;


    private void showMap() {

        EventManager.pushUIMessageNewLine("");
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (x == playerX && y == playerY) {
                    EventManager.pushUIMessage(" @ ");
                }
                else if (x == trapX && y == trapY) {
                    EventManager.pushUIMessage(" + ");
                }
                else if (playerX == trapX && playerY == trapY)
                {
                    EventManager.pushClearUI();
                    EventManager.pushUIMessageNewLine(";#FF0000Bạn đã dẫm phải bẫy, mất máu quá nhiều nên đã tèo, xin vui lòng thử lại sau; Hệ thống sẽ tắt sau 5s");
                    new TimedExit();
                }
                else
                    EventManager.pushUIMessage(" x ");
            }
            EventManager.pushUIMessageNewLine("");
        }

    }

    void changeStory(String newStoryId) {
        currentStory = storyMap.get(newStoryId);
        if (currentStory.isType("Timeout")) {
            EventManager.pushUIMessageNewLine("Gõ ;#FF0000next; để tiếp tục");
        }
        EventManager.pushUIMessageNewLine(currentStory.text);
    }

    void loadArc(Integer arcNo) {
        try {
            // Step 1 read files
            String url = "assets/events/event_arc_" + arcNo.toString() + ".json";
            byte[] bytes = Files.readAllBytes(Paths.get(url));
            String content = new String(bytes, StandardCharsets.UTF_8);

            storyMap.clear();
            //Step 2: Prase json
            Gson gson = new Gson();
            TypeToken<List<Story>> token = new TypeToken<List<Story>>() {
            };
            List<Story> stories = gson.fromJson(content, token.getType());
            for (Story story : stories) {
                if (storyMap.get(story.id) != null) {
                    System.out.println("Duplicate id: " + story.id);
                } else {
                    storyMap.put(story.id, story);
                }
            }
            currentStory = stories.get(0);
            EventManager.pushUIMessageNewLine(currentStory.text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long lastTimeUpdate = -1;

    int currentArc = 0;

    public GameWindow() {
        setupFont();
        setupPanels();
        setupWindow();

        loadArc(currentArc);


        InputManager.instance.addCommandListener(new CommandListener() {
            @Override
            public void onCommandFinished(String command) {
                EventManager.pushUIMessageNewLine(" ");
                EventManager.pushUIMessageNewLine(command);
                EventManager.pushUIMessageNewLine(" ");

                if (currentStory.isType("Input")) {
                    for (Choice choice : currentStory.choices) {
                        if (choice.match(command)) {
                            changeStory(choice.to);
                            break;
                        }
                    }
                } else if (currentStory.isType("Timeout")) {
                    if (command.equalsIgnoreCase("next")) {
                        changeStory(currentStory.time.to);
                    }
                } else if (currentStory.isType("NextArc")) {
                    currentArc++;
                    loadArc(currentArc);
                }
                else if (currentStory.isType("Map"))     {
                    showMap();
                    if (command.equalsIgnoreCase("up")) {
                        playerY--;
                       // EventManager.pushClearUI();
                        EventManager.pushUIMessageNewLine("Bạn vừa đi ;#00FF00lên trên;\nGõ;#FF0000map; để hiện map");
                        if (playerY == 0) {
                            EventManager.pushUIMessageNewLine("Bạn vừa ;#FF0000đập đầu vào tường;");
                        }
                    }
                    else if (command.equalsIgnoreCase("down")) {
                        playerY++;
                        //EventManager.pushClearUI();
                        EventManager.pushUIMessageNewLine("Bạn vừa đi ;#00FF00xuống dưới;\nGõ;#FF0000map; để hiện map");
                        if (playerY == mapHeight - 1) {
                            EventManager.pushUIMessageNewLine("Bạn vừa ;#FF0000đập đầu vào tường;");
                        }

                    }
                    else if (command.equalsIgnoreCase("left")) {
                        playerX--;
                        //EventManager.pushClearUI();
                        EventManager.pushUIMessageNewLine("Bạn vừa đi ;#00FF00sang trái;\nGõ;#FF0000map; để hiện map");
                        if (playerX == 1) {
                            EventManager.pushUIMessageNewLine("Bạn vừa ;#FF0000đập đầu vào tường;");
                        }
                    }
                    else if (command.equalsIgnoreCase("right")) {
                        playerX++;
                        //EventManager.pushClearUI();
                        EventManager.pushUIMessageNewLine("Bạn vừa đi ;#00FF00sang phải;\nGõ;#FF0000map; để hiện map");
                        if (playerX == mapWidth - 1) {
                            EventManager.pushUIMessageNewLine("Bạn vừa ;#FF0000đập đầu vào tường;");
                        }
                    }
                    else if (command.equalsIgnoreCase("map")){
                        EventManager.pushClearUI();
                        showMap();
                    }
                    else if (command.equalsIgnoreCase("help")) {
                        EventManager.pushUIMessageNewLine("Gõ ;#00FF00up, down, right, left; để di chuyển\nGõ ;#00FF00map để hiện map;");
                    }

                    else {
                        EventManager.pushClearUI();
                        EventManager.pushUIMessageNewLine("Lệnh này không hợp lệ, gõ ;#FF0000help; để được trợ giúp");
                    }

                }
            }

            @Override
            public void commandChanged(String command) {

            }
        });
    }

    private void setupFont() {

    }

    private void setupPanels() {
        TextScreen textScreenPanel = new TextScreen();
        textScreenPanel.setColor(Color.BLACK);
        textScreenPanel.getSize().set(
                Settings.TEXT_SCREEN_SCREEN_WIDTH,
                Settings.TEXT_SCREEN_SCREEN_HEIGHT);
        pack();
        textScreenPanel.getOffsetText().set(getInsets().left + 20, getInsets().top + 20);
        GameObject.add(textScreenPanel);


        InputText commandPanel = new InputText();
        commandPanel.getPosition().set(
                0,
                Settings.SCREEN_HEIGHT
        );
        commandPanel.getOffsetText().set(20, 20);
        commandPanel.getSize().set(
                Settings.CMD_SCREEN_WIDTH,
                Settings.CMD_SCREEN_HEIGHT
        );
        commandPanel.getAnchor().set(0, 1);
        commandPanel.setColor(Color.BLACK);
        GameObject.add(commandPanel);


        StatScreen statsPanel = new StatScreen();
        statsPanel.getPosition().set(
                Settings.SCREEN_WIDTH,
                0
        );

        statsPanel.getAnchor().set(1, 0);
        statsPanel.setColor(Color.BLACK);
        statsPanel.getSize().set(
                Settings.STATS_SCREEN_WIDTH,
                Settings.STATS_SCREEN_HEIGHT
        );
        GameObject.add(statsPanel);

        InputManager.instance.addCommandListener(textScreenPanel);
    }


    private void setupWindow() {
        this.setSize(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        this.setVisible(true);
        this.setTitle(Settings.GAME_TITLE);
        this.addKeyListener(InputManager.instance);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        backBufferImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        backBufferGraphics = (Graphics2D) backBufferImage.getGraphics();
    }

    public void gameLoop() {
        while (true) {
            if (-1 == lastTimeUpdate) lastTimeUpdate = nanoTime();

            long currentTime = nanoTime();

            if (currentTime - lastTimeUpdate > 17000000) {
                lastTimeUpdate = currentTime;
                GameObject.runAll();
                InputManager.instance.run();
                render(backBufferGraphics);
                repaint();
            }
        }
    }

    private void render(Graphics2D g2d) {
        g2d.setFont(Settings.DEFAULT_FONT);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);

        GameObject.renderAll(g2d);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(backBufferImage, 0, 0, null);
    }
}
