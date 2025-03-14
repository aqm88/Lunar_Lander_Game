package ecs.Components;

public class Collision extends Component{
    public boolean Collided;
    public boolean won;
    public Collision(boolean collision){
        this.Collided = collision;
        this.won = false;
    }
}
