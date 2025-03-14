import ecs.Entities.*;
import ecs.Systems.*;
import ecs.Systems.KeyboardInput;
import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.*;
import edu.usu.utils.Tuple2;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.System;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class GameModel
{
    private ecs.Systems.LandscapeRenderer sysLandscapeRenderer;
    private ecs.Systems.Renderer sysRenderer;
    private ecs.Systems.KeyboardInput sysKeyboardInput;
    private ecs.Systems.Movement sysMovement;
    private ecs.Systems.Collision sysCollision;
    private ecs.Systems.ParticleSystem sysThrustParticles;
    private ecs.Systems.ParticleSystem sysCrashParticles;
    private ecs.Systems.ParticleRenderer sysParticleRenderer;
    Graphics2D graphics;
    private float landerWidth;
    private float landerHeight;
    public int stage;
    private boolean paused;
    private ArrayList<Entity> particlesToKill;
    public boolean collisionWithoutWin;
    public boolean won;
    public boolean exploded;
    private Texture landerTex;
    private Texture background;
    private Texture smoke;
    private Texture fire;
    private SoundManager audio;
    private Sound crash;
    private Sound thrust;
    private Sound success;
    public float score;
    public boolean highScoreUpdated;

    public GameModel(boolean paused, int stage){
        this.paused = paused;
        this.stage = stage;
    }

    public void initialize(Graphics2D graphics){
        landerTex = new Texture("resources/images/Lunar_Lander.png");
        background = new Texture("resources/images/Game_Background.png");
        smoke = new Texture("resources/images/smoke.png");
        fire = new Texture("resources/images/fire.png");
        this.graphics = graphics;
        sysLandscapeRenderer = new LandscapeRenderer(graphics);
        sysKeyboardInput = new KeyboardInput(graphics.getWindow());
        sysMovement = new Movement();
        sysRenderer = new Renderer(graphics);
        sysCollision = new Collision();
        sysThrustParticles = new ParticleSystem(
                smoke,
                0.01f,
                0.005f,
                0.12f, 0.05f,
                0.5f,1,
                (float)Math.toRadians(-20),
                (float)Math.toRadians(20));
        sysCrashParticles = new ParticleSystem(
                fire,
                0.01f,
                0.005f,
                0.12f, 0.05f,
                0.5f,1, (float)Math.toRadians(0), (float)Math.toRadians(360));
        sysParticleRenderer = new ParticleRenderer(graphics);
        won = false;
        collisionWithoutWin = false;
        particlesToKill = new ArrayList<>();
        exploded = false;
        highScoreUpdated = false;

        audio = new SoundManager();
        thrust = audio.load("thrust", "resources/audio/thrust.ogg", false);
        success = audio.load("success", "resources/audio/success.ogg", false);
        crash = audio.load("crash", "resources/audio/crashed.ogg", false);

        score = 240;

        initializeTerrain(graphics);
        initializeLander(graphics);
    }

    public void update(double elapsedTime, boolean paused){
        sysThrustParticles.rotationUpperBound += Math.toRadians(sysKeyboardInput.degreesToAdjust);
        sysThrustParticles.rotationLowerBound += Math.toRadians(sysKeyboardInput.degreesToAdjust);
        sysKeyboardInput.degreesToAdjust = 0;
        particlesToKill = sysThrustParticles.getDeadParticles();
        for(Entity entity : particlesToKill){
            removeEntity(entity);
        }
        particlesToKill = sysCrashParticles.getDeadParticles();
        for(Entity entity : particlesToKill){
            removeEntity(entity);
        }
        particlesToKill.clear();

        if(sysKeyboardInput.thrusting && sysMovement.fuelRemaining >= 0 && !won && !exploded){
            score -= 2 * elapsedTime;
            if(!thrust.isPlaying()){
                thrust.play();
            }
            float centerX = sysMovement.landerX + landerWidth / 2.0f;
            float centerY = sysMovement.landerY + landerHeight / 2.0f;

            // Offset from the center (adjust as needed)
            float offsetX = -(landerWidth/12);  // Centered
            float offsetY = landerHeight / 3.0f; // Below the center (thruster position)

            // Apply rotation
            float rotatedX = (float) (offsetX * Math.cos(sysMovement.landerRotation) - offsetY * Math.sin(sysMovement.landerRotation));
            float rotatedY = (float) (offsetX * Math.sin(sysMovement.landerRotation) + offsetY * Math.cos(sysMovement.landerRotation));

            // Final position
            float particleX = centerX + rotatedX;
            float particleY = centerY + rotatedY;
            for(int i =0; i < 10; i++) {
                // Spawn particle
                addEntity(sysThrustParticles.create(particleX, particleY));
            }
        }

        if(collisionWithoutWin && !exploded){
            crash.play();
            float centerX = sysMovement.landerX + landerWidth / 2.0f;
            float centerY = sysMovement.landerY + landerHeight / 2.0f;
            for(int i = 0; i < 5000; i++){
                addEntity(sysCrashParticles.create(centerX,centerY));
            }
            exploded = true;
        }
        sysLandscapeRenderer.update(elapsedTime);
        sysRenderer.update(elapsedTime);
        if(!paused){
            score -= (float)elapsedTime;
            sysKeyboardInput.update(elapsedTime);
            sysMovement.update(elapsedTime);
            sysCollision.update(elapsedTime);
            sysParticleRenderer.update(elapsedTime);
            sysCrashParticles.update(elapsedTime);
            sysThrustParticles.update(elapsedTime);
        }

        won = sysCollision.checkWin();
        if(won){
            if(!success.isPlaying()){
                success.play();
            }
            if(!highScoreUpdated){
                score += sysMovement.fuelRemaining;
                highScoreUpdated = true;
            }

        }
        collisionWithoutWin = sysCollision.collisionWithoutWin();
    }

    private void initializeLander(Graphics2D graphics){
        Random rand = new Random();
        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        float maxHeight = (float)height[0]/(float)width[0];
        float maxWidth = 1.0f;

        landerHeight = 1.0f/12f;
        landerWidth = 1.0f/12f;

        float landerX = rand.nextFloat(-maxWidth + 0.01f, maxWidth/1.8f);
        float landerY = -maxHeight+0.01f;

        var lander = Lander.create(landerTex,landerX,landerY, landerWidth,landerHeight);
        addEntity(lander);
    }

    private void initializeTerrain(Graphics2D graphics){
        int[] height = new int[1];
        int[] width = new int[1];

        glfwGetWindowSize(graphics.getWindow(), width, height);
        float maxHeight = (float)height[0]/(float)width[0];
        float maxWidth = 1.0f;

        LunarSurfaceGenerator test = new LunarSurfaceGenerator(maxWidth,maxHeight,0.4f, 14);
        Tuple2<List<Vector3f>, List<Tuple2<Vector3f,Vector3f>>> result;
        List<Vector3f> points = new ArrayList<>();
        List<Tuple2<Vector3f,Vector3f>> landingPads = new ArrayList<>();
        if(stage == 1){
            result = test.generateLunarSurface(2);
        }else {
            result = test.generateLunarSurface(1);
        }
        var surface = LunarLandscape.create(result.item1(), result.item2(), background);
        addEntity(surface);
    }

    private void addEntity(Entity entity) {
        sysLandscapeRenderer.add(entity);
        sysRenderer.add(entity);
        sysMovement.add(entity);
        sysKeyboardInput.add(entity);
        sysCollision.add(entity);
        sysThrustParticles.add(entity);
        sysCrashParticles.add(entity);
        sysParticleRenderer.add(entity);
    }

    private void removeEntity(Entity entity) {
        sysLandscapeRenderer.remove(entity.getId());
        sysRenderer.remove(entity.getId());
        sysMovement.remove(entity.getId());
        sysKeyboardInput.remove(entity.getId());
        sysCollision.remove(entity.getId());
        sysThrustParticles.remove(entity.getId());
        sysCrashParticles.remove(entity.getId());
        sysParticleRenderer.remove(entity.getId());
    }
}
