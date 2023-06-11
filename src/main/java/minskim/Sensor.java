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
            System.out.println("[Sensor] WUMPUS");
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
            System.out.println("[Sensor] PITCH");
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

    public boolean checkShot(Agent agent, WumpusObject[][] worldMap, State state) {
        int row = agent.getLocRow();
        int col = agent.getLocCol();

        if (agent.isShooted()) {
            if (agent.getDirection() == LookDirection.NORTH
                    && worldMap[row + 1][col] == WumpusObject.WUMPUS) {
                worldMap[row - 1][col] = WumpusObject.EMPTY;
                state.setScream(true);
            } else if (agent.getDirection() == LookDirection.WEST
                    && worldMap[row][col - 1] == WumpusObject.WUMPUS) {
                worldMap[row][col - 1] = WumpusObject.EMPTY;
                state.setScream(true);
            } else if (agent.getDirection() == LookDirection.SOUTH
                    && worldMap[row - 1][col] == WumpusObject.WUMPUS) {
                worldMap[row - 1][col] = WumpusObject.EMPTY;
                state.setScream(true);
            } else if (agent.getDirection() == LookDirection.EAST
                    && worldMap[row][col + 1] == WumpusObject.WUMPUS) {
                worldMap[row][col + 1] = WumpusObject.EMPTY;
                state.setScream(true);
            }
//            agent.setShooted(false);
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

        if (checkWall(agent, worldMap, state)) {
            return state;
        }
        if (checkGold(agent, worldMap, state)) {
            return state;
        }
        checkWumpus(agent, worldMap, state);
        checkPitch(agent, worldMap, state);
        checkShot(agent, worldMap, state);

        return  state;
    }


}
