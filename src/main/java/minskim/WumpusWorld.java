package minskim;

public class WumpusWorld {

    private boolean checkWall(Agent agent, KnowledgeBase kb, State state) {
        if (agent.getLocRow() < 0 || agent.getLocRow() > 8 || agent.getLocCol() < 0 || agent.getLocCol() > 8) {
            state.setBump(true);
            if (agent.getLocRow() < 0) {
                agent.setLocRow(0);
            } else if (agent.getLocRow() > 8) {
                agent.setLocRow(8);
            } else if (agent.getLocCol() < 0) {
                agent.setLocCol(0);
            } else if (agent.getLocCol() > 8) {
                agent.setLocCol(8);
            }
            return true;
        }
        return false;
    }

    private boolean checkWumpus(Agent agent, KnowledgeBase kb, State state) {

        return false;
    }
}
