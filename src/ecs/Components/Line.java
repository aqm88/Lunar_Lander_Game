package ecs.Components;

import edu.usu.utils.Tuple2;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Line  extends Component{
    public List<Vector3f> points = new ArrayList<>();
    public List<Tuple2<Vector3f,Vector3f>> landingPads = new ArrayList<>();

    public Line(List<Vector3f> generatedPoints, List<Tuple2<Vector3f,Vector3f>> landingPads)
    {
        this.points = generatedPoints;
        this.landingPads = landingPads;
    }

    public Vector3f getPoint(int pointNum){
        return points.get(pointNum);
    }

    public int getNumPoints(){
        return points.size();
    }
}
