package ecs.Components;

import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Texture;

public class Appearance extends Component {
    public Texture image;
    public Color color;
    public Font font;

    public Appearance(Texture image, Color color, Font font) {
        this.image = image;
        this.color = color;
        this.font = font;
    }
}
