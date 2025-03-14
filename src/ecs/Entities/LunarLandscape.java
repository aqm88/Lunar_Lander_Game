package ecs.Entities;

import ecs.Components.Line;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;
import edu.usu.utils.Tuple2;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class LunarLandscape {
    public static Entity create(List<Vector3f> points, List<Tuple2<Vector3f,Vector3f>> landingPads, Texture background){

        var LunarLandscape = new Entity();
        LunarLandscape.add(new ecs.Components.Appearance(background, new Color(0.8f,0.5f,0.5f), null)); //grey color
        LunarLandscape.add(new Line(points, landingPads));
        LunarLandscape.add(new ecs.Components.Collision(false));

        return LunarLandscape;
    }
}
