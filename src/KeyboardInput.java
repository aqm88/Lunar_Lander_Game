import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput {

    /**
     * The type of method to invoke when a keyboard event is invoked
     */
    public interface ICommand {
        void invoke(double elapsedTime);
    }

    public KeyboardInput(long window) {
        this.window = window;

        // ✅ Set up the key callback ONCE in the constructor
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                synchronized (this) {
                    lastKeyPressed = key; // Store the key pressed
                }
            }
        });
    }

    public void registerCommand(int key, boolean keyPressOnly, ICommand callback) {
        commandEntries.put(key, new CommandEntry(key, keyPressOnly, callback));
        // Start out by assuming the key isn't currently pressed
        keysPressed.put(key, false);
    }

    /**
     * Go through all the registered command and invoke the callbacks as appropriate
     */
    public void update(double elapsedTime) {
        for (var entry : commandEntries.entrySet()) {
            if (entry.getValue().keyPressOnly && isKeyNewlyPressed(entry.getValue().key)) {
                entry.getValue().callback.invoke(elapsedTime);
            } else if (!entry.getValue().keyPressOnly && glfwGetKey(window, entry.getKey()) == GLFW_PRESS) {
                entry.getValue().callback.invoke(elapsedTime);
            }

            // For the next time around, remember the current state of the key (pressed or not)
            keysPressed.put(entry.getKey(), glfwGetKey(window, entry.getKey()) == GLFW_PRESS);
        }
    }
    //needed a way to manually enter values into the keypressed map for when the game is switching states
    //if both screens had a registered command (i.e.) enter to select it would automatically trigger enter
    //on the new screen
    public void keyHold(int key){
        keysPressed.put(key, true);
    }

    public int getKeyPress() {
        synchronized (this) {
            lastKeyPressed = -1; // Reset before capturing a new key press
        }

        while(glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS){
            glfwPollEvents();
        }
        // ✅ Loop until a new key is detected
        while (true) {
            glfwPollEvents(); // Process GLFW events

            synchronized (this) {
                if (lastKeyPressed != -1) { // A key has been pressed
                    int valToReturn = lastKeyPressed;
                    lastKeyPressed = -1; // Reset for next call
                    while(glfwGetKey(window, valToReturn) == GLFW_PRESS){
                        glfwPollEvents();
                    }
                    return valToReturn;
                }
            }
        }
    }

    /**
     * Returns true if the key is newly pressed.  If it was already pressed, then
     * it returns false
     */
    private boolean isKeyNewlyPressed(int key) {
        return (glfwGetKey(window, key) == GLFW_PRESS) && !keysPressed.get(key);
    }

    private final long window;
    // Table of registered callbacks
    private final HashMap<Integer, CommandEntry> commandEntries = new HashMap<>();
    // Table of registered callback keys previous pressed state
    private final HashMap<Integer, Boolean> keysPressed = new HashMap<>();
    // Used to poll events that are happening
    static int lastKeyPressed = -1;

    /**
     * Used to keep track of the details associated with a registered command
     */
    private record CommandEntry(int key, boolean keyPressOnly, ICommand callback) {
    }
}