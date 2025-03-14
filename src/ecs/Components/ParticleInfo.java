package ecs.Components;

public class ParticleInfo extends Component{
    public float speed;
    public float lifetime;
    public float direction;
    public float timeAlive;
    public ParticleInfo(float speed, float lifetime, float direction, float timeAlive){
        this.speed = speed;
        this.lifetime = lifetime;
        this.direction = direction;
        this.timeAlive = timeAlive;
    }
}
