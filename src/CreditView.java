import edu.usu.graphics.*;

import static org.lwjgl.glfw.GLFW.*;

public class CreditView extends GameStateView{

    private Graphics2D graphics;
    private KeyboardInput inputKeyboard;
    private GameStateEnum nextState = GameStateEnum.Credits;
    private Texture background;
    private Font font;
    private float maxWidth;
    private float maxHeight;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        this.graphics = graphics;

        font = new Font("resources/fonts/Roboto-Bold.ttf", 64,false);
        background = new Texture("resources/images/CreditBackgorund.png");

        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        maxHeight = (float)height[0]/(float)width[0];
        maxWidth = 1.0f;

        inputKeyboard = new KeyboardInput(graphics.getWindow());

        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true,(double elapsedTime) -> {
            nextState = GameStateEnum.MainMenu;
        });



    }

    @Override
    public void initializeSession() {
        super.initializeSession();
        nextState = GameStateEnum.Credits;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        inputKeyboard.update(elapsedTime);
        return nextState;
    }

    @Override
    public void update(double elapsedTime) {

    }

    @Override
    public void render(double elapsedTime) {
        Rectangle backgroundRect = new Rectangle(-maxWidth, -maxHeight,maxWidth*2,maxHeight*2,0);
        graphics.draw(background,backgroundRect, Color.WHITE);
        float textHeight = maxHeight/3;
        float text1Width = font.measureTextWidth("Made By Ammon Hanks", textHeight);
        float text2Width = font.measureTextWidth("With Help And Assets From ChatGPT", textHeight/2);

        graphics.drawTextByHeight(font, "Made By Ammon Hanks", -maxWidth + (maxWidth *2 - text1Width)/2, -maxHeight+0.01f, textHeight, Color.BLUE);
        graphics.drawTextByHeight(font, "With Help And Assets From ChatGPT", -maxWidth + (maxWidth *2 - text2Width)/2, -maxHeight+ textHeight+0.01f, textHeight/2, Color.BLUE);

    }
}
