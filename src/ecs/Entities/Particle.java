package ecs.Entities;

import ecs.Components.Appearance;
import ecs.Components.ParticleInfo;
import ecs.Components.Position;
import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Particle{
    public static Entity create(Texture tex, float x, float y, float width, float height, float speed, float lifetime, float direction, float timeAlive){
        var particle = new Entity();
        particle.add(new Appearance(tex, Color.WHITE,null));
        particle.add(new Position(x,y,0,width,height));
        particle.add(new ParticleInfo(speed, lifetime, direction, timeAlive));

        return  particle;
    }

}
