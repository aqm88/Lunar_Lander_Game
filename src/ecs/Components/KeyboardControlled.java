package ecs.Components;

import java.util.HashMap;
import java.util.Map;

public class KeyboardControlled extends Component{

    public Map<String, Integer> keys;
    public Map<String, Integer> lookup;

    public KeyboardControlled(Map<String, Integer> keys){
        this.keys = keys;

    }
}
