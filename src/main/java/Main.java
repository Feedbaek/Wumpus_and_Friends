import neulSung.*;
import neulSung.Enum.State;

import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        Map map = new Map();
        Vector<State> states = new Vector<>();
        states.add(State.BREEZE);
        states.add(State.STENCH);
        states.add(State.GLITTER);
        //states.add(State.STENCH);
        //states.add(State.STENCH);
        //states.add(State.STENCH);
        states.add(State.STENCH);
        map.setCurPercepts(states);
        //map.setState(State.WUMPUS,2,2);
        //map.setDiscovered(2,2);
        map.setSafe(0,1);
    }

}
