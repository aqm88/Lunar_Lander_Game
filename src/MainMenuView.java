import edu.usu.graphics.*;

import java.time.temporal.Temporal;

import static org.lwjgl.glfw.GLFW.*;

public class MainMenuView extends GameStateView {

    private enum MenuState {
        NewGame,
        HighScores,
        Credits,
        Quit;

        public MenuState next() {
            int nextOrdinal = (this.ordinal() + 1) % MenuState.values().length;
            return MenuState.values()[nextOrdinal];
        }

        public MenuState previous() {
            int previousOrdinal = (this.ordinal() - 1) % MenuState.values().length;
            if (previousOrdinal < 0) {
                previousOrdinal = Quit.ordinal();
            }
            return MenuState.values()[previousOrdinal];
        }
    }

    private MenuState currentSelection = MenuState.NewGame;
    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.MainMenu;
    private Font fontMenu;
    private Font fontSelected;
    private Texture background;
    private Texture textBackground;
    private float maxHeight;
    private float maxWidth;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        fontMenu = new Font("resources/fonts/Roboto-Regular.ttf", 64, false);
        fontSelected = new Font("resources/fonts/Roboto-Bold.ttf", 64, false);
        background = new Texture("resources/images/MainMenuBackground.png");
        textBackground = new Texture("resources/images/Pause_Background.png");

        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        maxHeight = (float)height[0]/(float)width[0];
        maxWidth = 1.0f;

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // Arrow keys to navigate the menu
        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            currentSelection = currentSelection.previous();
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            currentSelection = currentSelection.next();
        });
        // When Enter is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
            nextGameState = switch (currentSelection) {
                case NewGame -> GameStateEnum.GamePlay;
                case HighScores -> GameStateEnum.HighScores;
                case Credits -> GameStateEnum.Credits;
                case Quit -> GameStateEnum.Quit;

            };

        });


    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.MainMenu;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        // Updating the keyboard can change the nextGameState
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void render(double elapsedTime) {
        Rectangle backgroundRect = new Rectangle(-maxWidth, -maxHeight, maxWidth*2, maxHeight *2,-1);
        graphics.draw(background,backgroundRect,Color.WHITE);
        final float HEIGHT_MENU_ITEM = maxHeight/4;
        float top = -maxHeight + 0.01f;
        top = renderMenuItem(fontSelected, "Lunar Landers!", top, HEIGHT_MENU_ITEM *2, Color.BLUE);
        top = renderMenuItem(currentSelection == MenuState.NewGame ? fontSelected : fontMenu, "New Game", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.NewGame ? Color.YELLOW : Color.BLUE);
        top = renderMenuItem(currentSelection == MenuState.HighScores ? fontSelected : fontMenu, "High Scores", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.HighScores ? Color.YELLOW : Color.BLUE);
        top = renderMenuItem(currentSelection == MenuState.Credits ? fontSelected : fontMenu, "Credits", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.Credits ? Color.YELLOW : Color.BLUE);
        renderMenuItem(currentSelection == MenuState.Quit ? fontSelected : fontMenu, "Quit", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.Quit ? Color.YELLOW : Color.BLUE);
    }

    /**
     * Centers the text horizontally, at the specified top position.
     * It also returns the vertical position to draw the next menu item
     */
    private float renderMenuItem(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - width / 2, top, height, color);

        return top + height;
    }
}
