import edu.usu.utils.Tuple2;
import org.joml.Vector3f;

import java.util.*;

import static java.lang.Math.abs;

public class LunarSurfaceGenerator {
    private static final Random random = new Random();
    private float maxWidth;
    private float maxHeight;
    private float roughness;
    private float depth;

    public LunarSurfaceGenerator(float maxWidth, float maxHeight, float roughness, float depth){
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.roughness = roughness;
        this.depth = depth;
    }


    /**
     * Generates a lunar surface using Random Midpoint Displacement.
     * @return List of Vector3f points representing the terrain.
     */

    public Tuple2<List<Vector3f>, List<Tuple2<Vector3f,Vector3f>>> generateLunarSurface(int numOfLandingPads) {

        float xStart =-maxWidth;
        float yStart= random.nextFloat(-0.1f,maxHeight);
        float xEnd = maxWidth;
        float yEnd= random.nextFloat(-0.1f,maxHeight);
        float roughness= this.roughness;
        int depth = (int)this.depth;
        List<Vector3f> points = new ArrayList<>();
        List<Tuple2<Vector3f,Vector3f>> landingPads = new ArrayList<>();
        points.add(0,new Vector3f(xStart, yStart, 0)); // Add start point
        points.add(1,new Vector3f(xEnd, yEnd, 0)); // Add end point
        if(numOfLandingPads == 0){
            subdivide(points,points.get(0),points.get(1), roughness, depth);
        }else if(numOfLandingPads == 1) {
            float startingX = random.nextFloat(-0.85f, 0.73f);
            float endingX = startingX + 0.12f;
            float Y = random.nextFloat(0, maxHeight);
            Vector3f startOfLandingPad = new Vector3f(startingX, Y, 0);
            Vector3f endOfLandingPad = new Vector3f(endingX, Y, 0);
            landingPads.add(new Tuple2<>(startOfLandingPad, endOfLandingPad));
            points.add(startOfLandingPad);
            points.add(endOfLandingPad);
            subdivide(points, points.get(0), startOfLandingPad, roughness, depth);
            subdivide(points, endOfLandingPad, points.get(1), roughness, depth);

        } else if (numOfLandingPads == 2) {
            //pad 1
            float startingX1 = random.nextFloat(-0.85f, -0.01f);
            float endingX1 = startingX1 + 0.15f;
            float Y1 = random.nextFloat(0, maxHeight);
            Vector3f startOfLandingPad1 = new Vector3f(startingX1, Y1, 0);
            Vector3f endOfLandingPad1 = new Vector3f(endingX1, Y1, 0);
            landingPads.add(new Tuple2<>(startOfLandingPad1,endOfLandingPad1));
            points.add(startOfLandingPad1);
            points.add(endOfLandingPad1);
            //pad 2
            float startingX2 = random.nextFloat(0.01f, 0.70f);
            //check for edge case where the pads overlap
            if(endingX1 > startingX2){
                startingX2 = endingX1 + 0.01f;
            }
            float endingX2 = startingX2 + 0.15f;
            float Y2 = random.nextFloat(0, maxHeight);
            Vector3f startOfLandingPad2 = new Vector3f(startingX2, Y2, 0);
            Vector3f endOfLandingPad2 = new Vector3f(endingX2, Y2, 0);
            landingPads.add(new Tuple2<>(startOfLandingPad2,endOfLandingPad2));
            points.add(startOfLandingPad2);
            points.add(endOfLandingPad2);
            //start recursion
            subdivide(points,points.get(0),startOfLandingPad1,roughness,depth);
            subdivide(points,endOfLandingPad1, startOfLandingPad2,roughness,depth);
            subdivide(points,endOfLandingPad2,points.get(1),roughness,depth);




        }


        points.sort(Comparator.comparing(v -> v.x));
        return new Tuple2<>(points,landingPads);
    }

    /**
     * Recursively subdivides the terrain using the midpoint displacement algorithm.
     */
    private void subdivide(List<Vector3f> points, Vector3f left_point, Vector3f right_point,  float roughness, int depth) {
        if (depth <= 0) return;
        depth -= 1;

        // Insert new point
        Vector3f midpoint = nextPoint(left_point,right_point, roughness);
        points.add(midpoint);
        // Recur for left and right segments
        subdivide(points, left_point,midpoint, roughness, depth);
        subdivide(points, midpoint,right_point, roughness, depth);
    }

    public Vector3f nextPoint(Vector3f left_point, Vector3f right_point, float roughness){
        float x1 = left_point.x;
        float y1 = left_point.y;

        float x2 = right_point.x;
        float y2 = right_point.y;

        float midX = (x1+x2) * 0.5f;
        float randY =(float)(roughness * random.nextGaussian() * abs(x2-x1));
        float midY = ((y1+y2)*0.5f) + randY;

        if(midY < -0.1f){
            midY = abs(midY);
        }
        if(midY > maxHeight){
            while (midY > maxHeight) {
                midY = midY *0.95f;
            }
        }

        return new Vector3f(midX, midY, 0);

    }


    // Example Usage
    public static void main(String[] args) {
        LunarSurfaceGenerator test = new LunarSurfaceGenerator(1f,0.56f, 5f, 15);
        Tuple2<List<Vector3f>, List<Tuple2<Vector3f,Vector3f>>> terrain = test.generateLunarSurface(1);

        for (Vector3f point : terrain.item1()) {
            System.out.println(point);
        }
        System.out.println(terrain.item1().size());
    }
}