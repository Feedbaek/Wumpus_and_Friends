package minskim;

import java.util.Random;

public class WumpusWorld {
    private boolean wumpus[][];
    private boolean pitch[][];
    private boolean gold[][];

    public WumpusWorld() {
        wumpus = new boolean[4][4];
        pitch = new boolean[4][4];
        gold = new boolean[4][4];

        Random random = new Random();

        int r = 0, c = 0;
        do {
            r = random.nextInt(4);
            c = random.nextInt(4);
        } while (r == 0 && c == 0);
        gold[r][c] = true;

        for (int i=0; i<4; ++i) {
            for (int j=0; j<4; ++j) {
                if (random.nextInt(10) == 0) {
                    wumpus[i][j] = true;
                }
                if (random.nextInt(10) == 0) {
                    pitch[i][j] = true;
                }
            }
        }
    }

    // 벽에 부딫혔는지 검사
    private void checkWall(Agent agent, State state) {
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
        }
    }

    // Wumpus 검사
    private boolean checkWumpus(Agent agent, State state) {
        if (wumpus[agent.getLocRow()][agent.getLocCol()]) {
            agent.setAlive(false);
            state.setStench(true);
            return true;
        }
        return false;
    }

    // Pitch 검사
    private boolean checkPitch(Agent agent, State state) {
        if (pitch[agent.getLocRow()][agent.getLocCol()]) {
            agent.setAlive(false);
            state.setStench(true);
            return true;
        }
        return false;
    }
    // gold 검사
    private boolean checkGold(Agent agent, State state) {
        if (pitch[agent.getLocRow()][agent.getLocCol()]) {
            state.setGlitter(true);
            return true;
        }
        return false;
    }

    private boolean isFinish(Agent agent) {
        if (agent.getLocRow() == 1 && agent.getLocCol() == 1 && agent.isHaveGold()) {
            return true;
        }
        return false;
    }

    private boolean oneCoin(Agent agent, KnowledgeBase kb) {
        State state = new State();

        while (true) {
            if (!agent.isAlive()) {
                return false;
            }
            if (agent.getLocRow() == 1 && agent.getLocCol() == 1 && agent.isHaveGold()) {
                return true;
            }

            // 추론
            State newState = new State();
            // 행동
        }
    }

    public void play() {
        KnowledgeBase kb = new KnowledgeBase();

        while (true) {
//            State state = new State();
            Agent agent = new Agent();
            if (oneCoin(agent, kb, state)) {
                break;
            }
        }

    }
}
