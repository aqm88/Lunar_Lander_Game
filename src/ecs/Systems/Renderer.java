package ecs.Systems;

import ecs.Components.Appearance;
import ecs.Components.Collision;
import edu.usu.graphics.*;
import org.joml.Vector2f;

import java.text.DecimalFormat;

import static org.lwjgl.glfw.GLFW.*;

public class Renderer extends System{
    private final Graphics2D graphics;
    private final Font font;
    private float maxWidth;
    private float maxHeight;
    public Renderer(Graphics2D graphics){
        super(ecs.Components.Appearance.class, ecs.Components.Position.class, ecs.Components.Movable.class);
        font = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);
        this.graphics = graphics;

        int[] height = new int[1];
        int[] width = new int[1];
        glfwGetWindowSize(graphics.getWindow(), width, height);
        this.maxHeight = (height[0]/width[0]);
        this.maxWidth = 1.0f;

    }

    @Override
    public void update(double elapsedTime) {
        for (var entity: entities.values()){
            if(entity.contains(ecs.Components.Collision.class)){
                if(!entity.get(Collision.class).Collided){
                    renderEntity(entity);
                } else if(entity.get(Collision.class).won){
                    renderEntity(entity);
                }
            }
            renderEntityInformation(entity);
        }


    }

    private void renderEntity(ecs.Entities.Entity entity){
        var appearance = entity.get(ecs.Components.Appearance.class);
        var position = entity.get(ecs.Components.Position.class);

        Rectangle area = new Rectangle(position.x, position.y, position.width, position.height, 0);
        Vector2f center = new Vector2f((position.x + (position.width)/2), (position.y + (position.height/2)));
        graphics.draw(appearance.image, area, position.rotation, center, appearance.color);
    }

    private void renderEntityInformation(ecs.Entities.Entity entity){
        var appearance = entity.get(ecs.Components.Appearance.class);
        var position = entity.get(ecs.Components.Position.class);
        var movement = entity.get(ecs.Components.Movable.class);

        double speed = movement.velocityY * 50;
        DecimalFormat speedFormat = new DecimalFormat("#.###");
        DecimalFormat timeFormat = new DecimalFormat("#.##");
        int angle = (int)Math.toDegrees(position.rotation);
        angle = angle %360;
        if(angle < 0){
            angle = 360+angle;
        }

        graphics.drawTextByHeight(font,"Speed: " + speedFormat.format(speed) + "m/s",maxWidth/1.6f, -0.55f,0.05f,0,speed <= 2 ? Color.GREEN : Color.RED);
        graphics.drawTextByHeight(font,"Angle: " + angle + " degrees",maxWidth/1.6f, -0.50f,0.05f,0,angle <= 5 || angle >= 355 ? Color.GREEN : Color.RED);
        graphics.drawTextByHeight(font, "Fuel: " + timeFormat.format(movement.fuelRemaining)+ " s",maxWidth/1.6f, -0.45f,0.05f,0,movement.fuelRemaining > 0 ? Color.GREEN : Color.RED);


    }
}
