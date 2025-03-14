package ecs.Components;

public class Movable extends Component {
    public double velocityX = 0;
    public double velocityY = 0;
    public double rotation;
    public boolean thrusting = false;
    public double thrustConstant = 0.1; // Adjust thrust power
    public double gravityConstant = 0.04;
    public double fuelRemaining = 20f;

    public Movable(double rotation) {
        this.rotation = rotation;
    }
}