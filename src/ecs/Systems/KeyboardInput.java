package ecs.Systems;

import ecs.Components.KeyboardControlled;
import ecs.Components.Movable;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput extends System {
    private final long window;
    public boolean thrusting;
    public float degreesToAdjust;

    public KeyboardInput(long window) {
        super(ecs.Components.KeyboardControlled.class);
        this.window = window;
        this.thrusting = false;
        this.degreesToAdjust = 0;
    }

    @Override
    public void update(double elapsedTime) {
        for (var entity : entities.values()) {
            var movable = entity.get(ecs.Components.Movable.class);
            var input = entity.get(ecs.Components.KeyboardControlled.class);

            // Apply thrust when pressing the thrust key
            if (glfwGetKey(window, input.keys.get("thrust")) == GLFW_PRESS) {
                movable.thrusting = true;
                this.thrusting = true;
            } else {
                movable.thrusting = false;
                this.thrusting = false;
            }

            // Rotate left (counterclockwise)
            if (glfwGetKey(window, input.keys.get("rotateLeft")) == GLFW_PRESS) {
                movable.rotation -= Math.toRadians(1);
                degreesToAdjust -= 1;
            }

            // Rotate right (clockwise)
            if (glfwGetKey(window, input.keys.get("rotateRight")) == GLFW_PRESS) {
                movable.rotation += Math.toRadians(1);
                degreesToAdjust += 1;
            }
        }
    }
}
