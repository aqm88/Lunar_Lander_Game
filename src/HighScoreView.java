import edu.usu.graphics.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;


public class HighScoreView extends GameStateView{
    private GameStateEnum nextGameState = GameStateEnum.HighScores;
    private KeyboardInput inputKeyboard;
    private Font normalFont;
    private Font boldFont;
    private Graphics2D graphics;
    private Texture textBackground;
    private Texture background;
    private Serializer serializer;
    private GameState gameStateInfo;
    private ArrayList<Integer> highScoreInfo;
    private float maxWidth;
    private float maxHeight;

    public HighScoreView(Serializer s){
        this.serializer = s;
    }

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);
        this.graphics = graphics;

        normalFont = new Font("resources/fonts/Roboto-Regular.ttf", 48,false);
        boldFont = new Font("resources/fonts/Roboto-Bold.ttf", 48,false);
        textBackground = new Texture("resources/images/Pause_Background.png");
        background = new Texture("resources/images/HighScoreBackground.png");

        gameStateInfo = new GameState();
        serializer.loadGameState(gameStateInfo);

        highScoreInfo = new ArrayList<>();

        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        maxHeight = (float)height[0]/(float)width[0];
        maxWidth = 1.0f;

        inputKeyboard = new KeyboardInput(graphics.getWindow());

        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime)->{
            nextGameState = GameStateEnum.MainMenu;
        });
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.HighScores;

        gameStateInfo = new GameState();
        serializer.loadGameState(gameStateInfo);

        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        maxHeight = (float)height[0]/(float)width[0];
        maxWidth = 1.0f;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public void update(double elapsedTime) {

    }

    @Override
    public void render(double elapsedTime) {
        Rectangle backgroundRect = new Rectangle(-maxWidth, -maxHeight, maxWidth*2, maxHeight*2,0);
        graphics.draw(background,backgroundRect,Color.WHITE);

        if(gameStateInfo.highScores != null){
            highScoreInfo = gameStateInfo.highScores;
        }
        String messageText = "High Scores!";
        float boxWidth = boldFont.measureTextWidth(messageText, maxHeight/3) + 0.02f;
        float boxHeight = (maxHeight/3 + ((maxHeight/4) *5f));
        float top = -maxHeight +((maxHeight * 2) - boxHeight)/2;
        Rectangle textRect = new Rectangle(-(boxWidth/2f), top, boxWidth, boxHeight,1);
        graphics.drawTextByHeight(boldFont,messageText, -(boxWidth/2)+0.01f, top,maxHeight/3,1,  Color.BLUE);
        graphics.draw(textBackground, textRect, Color.WHITE);
        top += maxHeight/3;
        for(int i = 0; i<highScoreInfo.size();i++){
            messageText = i+1 + ": " + highScoreInfo.get(i);
            top = renderScore(normalFont, messageText, top, maxHeight/4, Color.BLUE);
        }
    }

    private float renderScore(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - width / 2, top, height, 1,color);

        return top + height;
    }
}
