package ecs.Components;

public class Position extends Component {
    public float x;
    public float y;
    public float rotation;
    public float width;
    public float height;

    public Position(float x, float y, float rotation, float width, float height) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.width = width;
        this.height = height;
    }
}