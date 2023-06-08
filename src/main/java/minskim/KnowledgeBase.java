package minskim;

import minskim.enums.LookDirection;
import minskim.enums.NextAction;
import minskim.enums.Possible;
import minskim.enums.WumpusObject;
import org.jetbrains.annotations.NotNull;

public class KnowledgeBase {
    private State stateMap[][] = null;
    private WumpusObject myMap[][] = null;
    private Possible canWumpus[][] = null;
    private Possible canPitch[][] = null;
    private Possible canGold[][] = null;

    public KnowledgeBase() {
        stateMap = new State[WumpusWorld.MAP_ROW][WumpusWorld.MAP_COL];
        myMap = new WumpusObject[WumpusWorld.MAP_ROW][WumpusWorld.MAP_COL];
        canWumpus = new Possible[WumpusWorld.MAP_ROW][WumpusWorld.MAP_COL];
        canPitch = new Possible[WumpusWorld.MAP_ROW][WumpusWorld.MAP_COL];
        canGold = new Possible[WumpusWorld.MAP_ROW][WumpusWorld.MAP_COL];
    }

    private void checkDanger(Object[][] map, int r, int c, Object val, Object cmp) {
        if (map[r + 1][c] == cmp)
            map[r + 1][c] = val;
        if (map[r - 1][c] == cmp)
            map[r - 1][c] = val;
        if (map[r][c + 1] == cmp)
            map[r][c + 1] = val;
        if (map[r][c - 1] == cmp)
            map[r][c - 1] = val;
    }

    private void tell(Agent agent, State state) {
        int row = agent.getLocRow();
        int col = agent.getLocCol();

        if (state.isBump()) {
            myMap[row][col] = WumpusObject.WALL;
            canWumpus[row][col] = Possible.NEVER;
            canPitch[row][col] = Possible.NEVER;
            canGold[row][col] = Possible.NEVER;
        }
        if (state.isBreeze()) {
            if (agent.isAlive()) {
                canPitch[row][col] = Possible.NEVER;
            } else {
                myMap[row][col] = WumpusObject.PITCH;
            }
            checkDanger(canPitch, row, col, Possible.POSSIBLE, Possible.IDK);
        }
        if (state.isStench()) {
            if (agent.isAlive()) {
                canWumpus[row][col] = Possible.NEVER;
            } else {
                myMap[row][col] = WumpusObject.WUMPUS;
            }
            checkDanger(canWumpus, row, col, Possible.POSSIBLE, Possible.IDK);
        }
        if (state.isScream()) {
            if (agent.getDirection() == LookDirection.NORTH) {
                myMap[row + 1][col] = WumpusObject.EMPTY;
            } else if (agent.getDirection() == LookDirection.WEST) {
                myMap[row][col - 1] = WumpusObject.EMPTY;
            } else if (agent.getDirection() == LookDirection.SOUTH) {
                myMap[row - 1][col] = WumpusObject.EMPTY;
            } else if (agent.getDirection() == LookDirection.EAST) {
                myMap[row][col + 1] = WumpusObject.EMPTY;
            }
        }
        if (state.isGlitter()) {
            myMap[row][col] = WumpusObject.GOLD;
        }
        stateMap[row][col] = state;
    }

    private void tell(Agent agent, NextAction nextAction, State state) {

    }

    private NextAction ask(Agent agent, State state) {
        int row = agent.getLocRow();
        int col = agent.getLocCol();

        NextAction nextAction = NextAction.GOFORWARD;
        /* 에이전트가 사망했는가 */
        if (!agent.isAlive()) {
            nextAction = NextAction.RESTART;
            return nextAction;
        }
        /* 벽에 부딪쳤는가 */
        if (state.isBump()) {
            if (Math.abs(agent.getDirection().compareTo(agent.getPrevDirection())) == 2) {
                nextAction = NextAction.GOFORWARD;
            } else {
                nextAction = NextAction.TURNLEFT;
            }
            return nextAction;
        }
        if (state.isBreeze()) {

        }
        if (state.isStench()) {

        }
        if (state.isScream()) {

        }
        if (state.isGlitter()) {
            nextAction = NextAction.GRAB;
            return nextAction;
        }
        return nextAction;
    }

    public NextAction reasoning(Agent agent, WumpusObject[][] worldMap, State state) {
        tell(agent, state);
        NextAction nextAction = ask(agent, state);
        tell(agent, nextAction, state);
        return nextAction;
    }

}
