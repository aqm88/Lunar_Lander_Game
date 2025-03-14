package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class Lander {
    public static Entity create(Texture texture, float x, float y, float width, float height){
        var lander = new Entity();
        lander.add(new ecs.Components.Appearance(texture, Color.WHITE, null));
        lander.add(new ecs.Components.Movable(0f));
        lander.add(new ecs.Components.Position(x,y,(float)Math.toRadians(275), width,height));
        lander.add(new ecs.Components.KeyboardControlled(
                Map.of(
                        "thrust", GLFW_KEY_UP,
                        "rotateLeft", GLFW_KEY_LEFT,
                        "rotateRight", GLFW_KEY_RIGHT
                )));
        lander.add(new ecs.Components.Collision(false));

        return lander;
    }
}
