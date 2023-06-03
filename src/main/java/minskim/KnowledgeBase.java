package minskim;

public class KnowledgeBase {
    private boolean visited[][] = null;
    private boolean safe[][] = null;
    private boolean wumpus[][] = null;
    private boolean pitch[][] = null;
    private boolean gold[][] = null;

    public KnowledgeBase() {
        visited = new boolean[4][4];
        safe = new boolean[4][4];
        wumpus = new boolean[4][4];
        pitch = new boolean[4][4];
        gold = new boolean[4][4];
    }

}
