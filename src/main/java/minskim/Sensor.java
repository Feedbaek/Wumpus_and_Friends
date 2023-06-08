package minskim;

import minskim.enums.LookDirection;
import minskim.enums.WumpusObject;

public class Sensor {

    public Sensor() {}

    // 벽에 부딫혔는지 검사
    public boolean checkWall(Agent agent, WumpusObject[][] worldMap, State state) {
        if (worldMap[agent.getLocRow()][agent.getLocCol()] == WumpusObject.WALL) {
            state.setBump(true);
            return true;
        }
        return false;
    }

    // Wumpus 검사
    public boolean checkWumpus(Agent agent, WumpusObject[][] worldMap, State state) {
        if (worldMap[agent.getLocRow()][agent.getLocCol()] == WumpusObject.WUMPUS) {
            agent.setAlive(false);
            state.setStench(true);
            return true;
        }
        if (worldMap[agent.getLocRow() - 1][agent.getLocCol()] == WumpusObject.WUMPUS ||
                worldMap[agent.getLocRow() + 1][agent.getLocCol()] == WumpusObject.WUMPUS ||
                worldMap[agent.getLocRow()][agent.getLocCol() - 1] == WumpusObject.WUMPUS ||
                worldMap[agent.getLocRow()][agent.getLocCol() + 1] == WumpusObject.WUMPUS) {
            state.setStench(true);
        }
        return false;
    }

    // Pitch 검사
    public boolean checkPitch(Agent agent, WumpusObject[][] worldMap, State state) {
        if (worldMap[agent.getLocRow()][agent.getLocCol()] == WumpusObject.PITCH) {
            agent.setAlive(false);
            state.setStench(true);
            return true;
        }
        if (worldMap[agent.getLocRow() - 1][agent.getLocCol()] == WumpusObject.PITCH ||
                worldMap[agent.getLocRow() + 1][agent.getLocCol()] == WumpusObject.PITCH ||
                worldMap[agent.getLocRow()][agent.getLocCol() - 1] == WumpusObject.PITCH ||
                worldMap[agent.getLocRow()][agent.getLocCol() + 1] == WumpusObject.PITCH) {
            state.setBreeze(true);
        }
        return false;
    }
    // gold 검사
    public boolean checkGold(Agent agent, WumpusObject[][] worldMap, State state) {
        if (worldMap[agent.getLocRow()][agent.getLocCol()] == WumpusObject.GOLD) {
            state.setGlitter(true);
            return true;
        }
        return false;
    }

    public boolean checkShot(Agent agent, State state) {
        if (agent.isHitted()) {
            agent.setHitted(false);
            state.setScream(true);
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

    public State percept(Agent agent, WumpusObject[][] worldMap) {
        State state = new State();

        checkWall(agent, worldMap, state);
        checkGold(agent, worldMap, state);
        checkWumpus(agent, worldMap, state);
        checkPitch(agent, worldMap, state);
        checkShot(agent, state);

        return  state;
    }


}
