package ecs.Systems;

import ecs.Components.Line;
import ecs.Entities.Entity;
import ecs.Entities.LunarLandscape;
import edu.usu.utils.Tuple2;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Collision extends System{

    public Collision(){
        super(ecs.Components.Collision.class);
    }
    @Override
    public void update(double elapsedTime) {
        var landscape = getLandscape(entities);
        for(var entity : entities.values()){
            if(entity.contains(ecs.Components.Collision.class) && entity.contains(ecs.Components.Position.class)){
                var position = entity.get(ecs.Components.Position.class);
                Vector2f circleCenter = new Vector2f(position.x + (position.width/2), position.y + (position.height/2));
                float radius = Math.max(position.height/2, position.width/2) - 0.005f;
                for(var terrain : landscape){
                    var line = terrain.get(Line.class);
                    for(int i = 0; i<line.points.size()-1; i++){
                        Vector2f pt1 = new Vector2f(line.getPoint(i).x, line.getPoint(i).y);
                        Vector2f pt2 = new Vector2f(line.getPoint(i+1).x, line.getPoint(i+1).y);
                        if(lineCircleIntersection(pt1,pt2, circleCenter, radius)){
                           var collision = entity.get(ecs.Components.Collision.class);
                           collision.Collided = true;
                        }
                    }
                }
            }
        }
    }

    public boolean checkWin(){
        var landscape = getLandscape(entities);
        for(var entity : entities.values()){
            if(entity.contains(ecs.Components.Collision.class) && entity.contains(ecs.Components.Position.class)&& entity.contains(ecs.Components.Movable.class)) {
                var collision = entity.get(ecs.Components.Collision.class);
                var position = entity.get(ecs.Components.Position.class);
                var movement = entity.get(ecs.Components.Movable.class);
                if(collision.Collided){
                    int angle = (int)Math.toDegrees(position.rotation);
                    angle = angle %360;
                    if(angle < 0){
                        angle = 360+angle;
                    }
                    double speed = movement.velocityY * 50;
                    if(speed <= 2 && (angle <= 5 || angle >= 355)){
                        List<Tuple2<Vector3f,Vector3f>> landingPads = landscape.get(0).get(Line.class).landingPads;
                        for(var pad : landingPads){
                            if(position.x > pad.item1().x && (position.x + position.width < pad.item2().x)){
                                collision.won = true;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean collisionWithoutWin() {
        for (var entity : entities.values()) {
            if (entity.contains(ecs.Components.Collision.class) && entity.contains(ecs.Components.Position.class) && entity.contains(ecs.Components.Movable.class)) {
                var collision = entity.get(ecs.Components.Collision.class);
                return collision.Collided && !checkWin();
            }
        }
        return false;
    }


    private List<Entity> getLandscape(Map<Long,Entity> entities){
        var lines = new ArrayList<Entity>();

        for(var entity : entities.values()){
            if(entity.contains(ecs.Components.Line.class)){
                lines.add(entity);
            }
        }
        return lines;
    }

    // I (Dean) wrote this for use in my Java implementation of the game.
    // Vector2f is defined in the JOML library.
    private boolean lineCircleIntersection(Vector2f pt1, Vector2f pt2, Vector2f circleCenter, float circleRadius) {
        // Translate points to circle's coordinate system
        Vector2f d = pt2.sub(pt1); // Direction vector of the line
        Vector2f f = pt1.sub(circleCenter); // Vector from circle center to the start of the line

        float a = d.dot(d);
        float b = 2 * f.dot(d);
        float c = f.dot(f) - circleRadius * circleRadius;

        float discriminant = b * b - 4 * a * c;

        // If the discriminant is negative, no real roots and thus no intersection
        if (discriminant < 0) {
            return false;
        }

        // Check if the intersection points are within the segment
        discriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - discriminant) / (2 * a);
        float t2 = (-b + discriminant) / (2 * a);

        if (t1 >= 0 && t1 <= 1) {
            return true;
        }
        if (t2 >= 0 && t2 <= 1) {
            return true;
        }

        return false;
    }
}
