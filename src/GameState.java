import java.util.ArrayList;

public class GameState {

    public GameState() {
        initialized = false;
    }

    public GameState(ArrayList<Integer> highScores) {
        this.highScores= highScores;
        this.initialized = true;
    }
    public ArrayList<Integer> highScores;
    public boolean initialized = false;
}
