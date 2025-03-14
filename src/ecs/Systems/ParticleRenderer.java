package ecs.Systems;

import ecs.Components.Appearance;
import ecs.Components.Position;
import ecs.Entities.Entity;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import org.joml.Vector2f;

public class ParticleRenderer extends System{
    private final Graphics2D graphics;
    public  ParticleRenderer(Graphics2D graphics){
        super(ecs.Components.ParticleInfo.class);
        this.graphics = graphics;
    }

    @Override
    public void update(double elapsedTime) {
        for(Entity entity : entities.values()){
            Appearance appearance = entity.get(Appearance.class);
            Position position = entity.get(Position.class);
            Rectangle particleRect = new Rectangle(position.x, position.y, position.width, position.height, 1);
            graphics.draw(appearance.image, particleRect,position.rotation, new Vector2f(position.x + position.width/2, position.y + position.height/2), Color.WHITE);
        }
    }
}
