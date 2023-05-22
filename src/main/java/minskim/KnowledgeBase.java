package minskim;

public class KnowledgeBase {
    private boolean visited[][] = null;
    private boolean safe[][] = null;

    public KnowledgeBase() {
        visited = new boolean[4][4];
        safe = new boolean[4][4];
    }

}
