package ecs.Systems;

import ecs.Components.Collision;
import ecs.Components.Movable;
import ecs.Components.Position;
import org.joml.Vector2f;

public class Movement extends System {
    public float landerX;
    public float landerY;
    public float landerRotation;
    public float fuelRemaining;
    public Movement() {
        super(ecs.Components.Movable.class, ecs.Components.Position.class);
    }

    @Override
    public void update(double elapsedTime) {
        for (var entity : entities.values()) {
            if(entity.contains(Collision.class)){
                if(!entity.get(Collision.class).Collided){
                    moveEntity(entity, elapsedTime);
                }
            }
        }
    }

    private void moveEntity(ecs.Entities.Entity entity, double elapsedTime) {
        var movable = entity.get(ecs.Components.Movable.class);
        var position = entity.get(ecs.Components.Position.class);

        // Update rotation
        position.rotation += (float) movable.rotation;
        movable.rotation = 0; // Reset rotation after applying
        landerRotation = position.rotation;
        // Apply gravity (always pulls down)
        movable.velocityY += movable.gravityConstant * elapsedTime;
        fuelRemaining = (float)movable.fuelRemaining;
        // Apply thrust (if active)
        if (movable.thrusting && movable.fuelRemaining >0) {
            movable.velocityX += movable.thrustConstant * elapsedTime * Math.cos(position.rotation + (3*Math.PI/2));
            movable.velocityY += movable.thrustConstant * elapsedTime * Math.sin(position.rotation + (3*Math.PI/2));
            movable.fuelRemaining -= elapsedTime;
        }

        // Update position using velocity
        position.x += (float)movable.velocityX * elapsedTime;
        position.y += (float)movable.velocityY * elapsedTime;

        landerX = position.x;
        landerY = position.y;
    }
}