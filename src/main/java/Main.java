import neulSung.*;
import neulSung.Enum.State;

import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        Map map = new Map();
        map.drawSafe(1,1);
        map.drawState(State.AGENT_RIGHT,1,2);
        map.setDiscovered(1,2);
        /*Vector<State> states = new Vector<>();
        states.add(State.BREEZE);
        states.add(State.STENCH);
        states.add(State.GLITTER);
        //states.add(State.STENCH);
        //states.add(State.STENCH);
        //states.add(State.STENCH);
        states.add(State.STENCH);
        map.drawPercepts(states);
        map.drawState(State.WUMPUS,2,2);
        map.setDiscovered(2,2);
        //map.setSafe(0,1);
        map.useArrow();
        map.clearPercepts();
        map.drawPercepts(states);*/
    }

}
