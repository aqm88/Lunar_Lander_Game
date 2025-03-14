import edu.usu.graphics.*;

import java.util.ArrayList;
import java.util.Collections;

import static org.lwjgl.glfw.GLFW.*;

public class GamePlayView extends GameStateView{
    private enum MenuState {
        Resume,
        Menu;

        public MenuState next() {
            int nextOrdinal = (this.ordinal() + 1) % MenuState.values().length;
            return MenuState.values()[nextOrdinal];
        }

        public MenuState previous() {
            int previousOrdinal = (this.ordinal() - 1) % MenuState.values().length;
            if (previousOrdinal < 0) {
                previousOrdinal = Menu.ordinal();
            }
            return MenuState.values()[previousOrdinal];
        }
    }

    private KeyboardInput inputKeyboard;
    private KeyboardInput pausedKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.GamePlay;
    private GameModel gameModel;
    private MenuState currentSelection;
    private Boolean paused;
    private float maxHeight;
    private float maxWidth;
    private Font normalFont;
    private Font boldFont;
    private int stage;
    private Texture pauseBackground;
    private Texture background;
    private float timeBetweenGames;
    private GameState storedInfo;
    public Serializer serializer;
    private ArrayList<Integer> highScoreList;
    private boolean highScoreSaved;

    public GamePlayView(Serializer s){
        this.serializer = s;
    }

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        normalFont = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);
        boldFont = new Font("resources/fonts/Roboto-Bold.ttf", 48, false);
        pauseBackground = new Texture("resources/images/Pause_Background.png");
        background = new Texture("resources/images/Game_Background.png");

        highScoreSaved = false;

        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        maxHeight = (float)height[0]/(float)width[0];
        maxWidth = 1.0f;

        timeBetweenGames = 5f;
        stage = 1;

        paused = false;
        currentSelection = MenuState.Resume;


        storedInfo = new GameState();
        highScoreList = new ArrayList<>();
        serializer.loadGameState(storedInfo);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        pausedKeyboard = new KeyboardInput(graphics.getWindow());

        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            paused = !paused;
        });

        pausedKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            currentSelection = currentSelection.previous();
        });
        pausedKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            currentSelection = currentSelection.next();
        });
        // When Enter is pressed, set the appropriate new game state
        pausedKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
            switch (currentSelection) {
                case Resume -> paused = !paused;
                case Menu -> nextGameState = GameStateEnum.MainMenu;

            };

        });
    }

    @Override
    public void initializeSession() {
        highScoreSaved = false;

        stage = 1;
        inputKeyboard.keyHold(GLFW_KEY_ENTER);
        paused = false;
        gameModel = new GameModel(paused,stage);
        gameModel.initialize(graphics);
        nextGameState = GameStateEnum.GamePlay;

        storedInfo = new GameState();
        highScoreList = new ArrayList<>();
        serializer.loadGameState(storedInfo);
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        inputKeyboard.update(elapsedTime);
        if(paused){
            pausedKeyboard.update(elapsedTime);
        }
        return nextGameState;

    }

    @Override
    public void update(double elapsedTime) {
        if(storedInfo.highScores != null){
            highScoreList = storedInfo.highScores;
        }
        if(gameModel.won){
            if(!paused){
                if(stage < 2) {
                    timeBetweenGames -= (float) elapsedTime;
                    if (timeBetweenGames <= 0) {
                        timeBetweenGames = 5f;
                        stage++;
                        gameModel = new GameModel(paused, stage);
                        gameModel.initialize(graphics);
                    }
                }else{
                    timeBetweenGames -= (float) elapsedTime;
                    if(timeBetweenGames <= 0){
                        timeBetweenGames = 5f;
                        nextGameState = GameStateEnum.MainMenu;
                    }
                    if(!highScoreSaved){
                        highScoreList.add((int)gameModel.score);
                        Collections.sort(highScoreList, Collections.reverseOrder());
                        if(highScoreList.size() > 5){
                            highScoreList.remove(highScoreList.size()-1);
                        }
                        serializer.saveGameState(new GameState(highScoreList));
                        highScoreSaved =true;
                    }
                }
            }
        }
        gameModel.update(elapsedTime, paused);
    }

    @Override
    public void render(double elapsedTime) {
        graphics.drawTextByHeight(normalFont,"Score: " + (int)gameModel.score,-maxWidth +0.01f, -maxHeight + 0.01f,0.05f,1,Color.GREEN);
        if(paused){
            Rectangle pauseRect = new Rectangle(-maxWidth, -maxHeight,maxWidth*2,maxHeight*2,1);
            graphics.draw(pauseBackground, pauseRect,Color.WHITE);
            final float HEIGHT_MENU_ITEM = maxHeight/6;
            float top = 0 - HEIGHT_MENU_ITEM;
            top = renderMenuItem(currentSelection == MenuState.Resume ? boldFont : normalFont, "Resume", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.Resume ? Color.YELLOW : Color.BLUE);
            top = renderMenuItem(currentSelection == MenuState.Menu ? boldFont : normalFont, "Return To Menu", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.Menu ? Color.YELLOW : Color.BLUE);
        }else if(gameModel.won && stage >= 2){
            Rectangle pauseRect = new Rectangle(-maxWidth, -maxHeight,maxWidth*2,maxHeight*2,1);
            graphics.draw(pauseBackground, pauseRect,Color.WHITE);
            final float HEIGHT_MENU_ITEM = maxHeight/6;
            float top = 0 - HEIGHT_MENU_ITEM;
            top = renderMenuItem(normalFont,"Great Job!!! You Completed The Game!!!", top,HEIGHT_MENU_ITEM,Color.GREEN);
            top = renderMenuItem(normalFont, "Returning to Main Menu in " + String.valueOf(Math.round(timeBetweenGames)) + "s", top,HEIGHT_MENU_ITEM,Color.GREEN);
        }else if(gameModel.won){
            Rectangle pauseRect = new Rectangle(-maxWidth, -maxHeight,maxWidth*2,maxHeight*2,1);
            graphics.draw(pauseBackground, pauseRect,Color.WHITE);
            final float HEIGHT_MENU_ITEM = maxHeight/6;
            float top = 0 - HEIGHT_MENU_ITEM;
            top = renderMenuItem(normalFont,"Great Job!!!", top,HEIGHT_MENU_ITEM,Color.GREEN);
            top = renderMenuItem(normalFont, "Starting next level in " + String.valueOf(Math.round(timeBetweenGames)) + "s", top,HEIGHT_MENU_ITEM,Color.GREEN);
        }else if(gameModel.collisionWithoutWin){
            final float HEIGHT_MENU_ITEM = maxHeight/6;
            float top = 0 - HEIGHT_MENU_ITEM;
            String message1 = "Oh No!!! Better Luck Next Time!";
            String message2 = "Press Escape To Return To Menu! ";
            float width1 = normalFont.measureTextWidth(message1, HEIGHT_MENU_ITEM);
            float width2 = normalFont.measureTextWidth(message2, HEIGHT_MENU_ITEM);
            float rectWidth = Math.max(width1,width2) + 0.02f;
            Rectangle backRect = new Rectangle(-rectWidth/2, top-0.01f, rectWidth, HEIGHT_MENU_ITEM *2 + 0.02f);
            graphics.draw(pauseBackground, backRect, Color.WHITE);
            top = renderMenuItem(normalFont,message1, top,HEIGHT_MENU_ITEM,Color.RED);
            top = renderMenuItem(normalFont,message2 , top,HEIGHT_MENU_ITEM,Color.RED);
        }

    }

    private float renderMenuItem(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - width / 2, top, height,1, color);

        return top + height;
    }
}
