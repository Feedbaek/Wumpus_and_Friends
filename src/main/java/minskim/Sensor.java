package minskim;

import minskim.enums.WumpusObject;

public class Sensor {
    private WumpusObject[][] worldMap = null;

    public Sensor(WumpusObject[][] map) {
        worldMap = map;
    }

    // 벽에 부딫혔는지 검사
    public void checkWall(Agent agent, State state) {
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
    public boolean checkWumpus(Agent agent, State state) {
        if (worldMap[agent.getLocRow()][agent.getLocCol()] == WumpusObject.WUMPUS) {
            agent.setAlive(false);
            state.setStench(true);
            return true;
        }
        return false;
    }

    // Pitch 검사
    public boolean checkPitch(Agent agent, State state) {
        if (worldMap[agent.getLocRow()][agent.getLocCol()] == WumpusObject.PITCH) {
            agent.setAlive(false);
            state.setStench(true);
            return true;
        }
        return false;
    }
    // gold 검사
    public boolean checkGold(Agent agent, State state) {
        if (worldMap[agent.getLocRow()][agent.getLocCol()] == WumpusObject.GOLD) {
            state.setGlitter(true);
            return true;
        }
        return false;
    }

    public boolean isFinish(Agent agent) {
        if (agent.getLocRow() == 1 && agent.getLocCol() == 1 && agent.isHaveGold()) {
            return true;
        }
        return false;
    }
}
