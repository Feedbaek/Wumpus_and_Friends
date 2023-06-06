package minskim;

import minskim.enums.WumpusObject;

public class KnowledgeBase {
//    private boolean visited[][] = null;
//    private boolean safe[][] = null;
//    private boolean wumpus[][] = null;
//    private boolean pitch[][] = null;
//    private boolean gold[][] = null;
    private State stateMap[][] = null;
    private WumpusObject objectMap[][] = null;

    public KnowledgeBase() {
//        visited = new boolean[4][4];
//        safe = new boolean[4][4];
//        wumpus = new boolean[4][4];
//        pitch = new boolean[4][4];
//        gold = new boolean[4][4];
        stateMap = new State[4][4];
        objectMap = new WumpusObject[4][4];
    }

}
