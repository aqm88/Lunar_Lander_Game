package ecs.Systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Triangle;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

public class LandscapeRenderer extends System{
    private final Graphics2D graphics;
    private final float maxHeight;
    private final float maxWidth;

    public LandscapeRenderer(Graphics2D graphics){
        super(ecs.Components.Line.class);
        this.graphics = graphics;
        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        this.maxHeight = (float)height[0]/(float)width[0];
        this.maxWidth = 1.0f;
    }
    @Override
    public void update(double elapsedTime) {
        for(var entity:entities.values()){
            renderLandscape(entity);
        }
    }

    private void renderLandscape(ecs.Entities.Entity entity){
        var appearance = entity.get(ecs.Components.Appearance.class);
        var points = entity.get(ecs.Components.Line.class);
        Rectangle backgroundRect = new Rectangle(-maxWidth, -maxHeight, 2*maxWidth, 2*maxHeight, -1);
        graphics.draw(appearance.image, backgroundRect, Color.WHITE);

        for(int i =0; i < points.getNumPoints()-1; i++){
            graphics.draw(points.getPoint(i), points.getPoint(i+1), Color.WHITE);
            drawTri(points.getPoint(i), points.getPoint(i+1), appearance.color);

        }
    }

    private void drawTri(Vector3f point1, Vector3f point2, Color color){
        Triangle tri1 = new Triangle(point1,point2,new Vector3f(point1.x,maxHeight,0));
        graphics.draw(tri1, color);
        Triangle tri2 = new Triangle(point2, new Vector3f(point1.x, maxHeight,0), new Vector3f(point2.x, maxHeight,0));
        graphics.draw(tri2, color);
    }
}
