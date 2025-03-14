package ecs.Systems;

import ecs.Components.ParticleInfo;
import ecs.Components.Position;
import ecs.Entities.Entity;
import ecs.Entities.Particle;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;
import edu.usu.utils.MyRandom;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem extends System{
    private final MyRandom random = new MyRandom();
    private ArrayList<Entity> deadParticles;

    private final Texture tex;
    private final float sizeMean;
    private final float sizeStdDev;
    private final float speedMean;
    private final float speedStdDev;
    private final float lifetimeMean;
    private final float lifetimeStdDev;
    public float rotationLowerBound;
    public float rotationUpperBound;

    public ParticleSystem(Texture tex, float sizeMean, float sizeStdDev, float speedMean, float speedStdDev, float lifetimeMean, float lifetimeStdDev, float rotationLowerBound, float rotationUpperBound){
        super(ParticleInfo.class);

        deadParticles = new ArrayList<>();
        this.tex = tex;
        this.sizeMean = sizeMean;
        this.sizeStdDev = sizeStdDev;
        this.speedMean = speedMean;
        this.speedStdDev = speedStdDev;
        this.lifetimeMean = lifetimeMean;
        this.lifetimeStdDev = lifetimeStdDev;
        this.rotationLowerBound = rotationLowerBound;
        this.rotationUpperBound = rotationUpperBound;
    }

    @Override
    public void update(double elapsedTime) {
        for(Entity entity : entities.values()){
            var position = entity.get(Position.class);
            var particle = entity.get(ParticleInfo.class);

            particle.timeAlive += (float) elapsedTime;
            position.x += (float)(elapsedTime * particle.speed * (Math.cos(particle.direction)));
            position.y += (float)(elapsedTime * particle.speed * Math.sin(particle.direction));
            position.rotation = (float)(particle.speed/0.5f);

            if(particle.timeAlive > particle.lifetime){
                deadParticles.add(entity);
            }

        }
    }

    //Texture tex, float x, float y, float width, float height, float speed, float lifetime, float direction, float timeAlive

    public Entity create(float x, float y){
        float size = (float)random.nextGaussian(this.sizeMean, this.sizeStdDev);
        var particle = Particle.create(this.tex,
                x,
                y,
                size, size,
                (float) random.nextGaussian(this.speedMean, this.speedStdDev),
                (float)random.nextGaussian(this.lifetimeMean, this.lifetimeStdDev),
                random.nextFloat(rotationLowerBound, rotationUpperBound),
                0f);

        return particle;
    }

    public ArrayList<Entity> getDeadParticles(){
        return deadParticles;
    }


}
