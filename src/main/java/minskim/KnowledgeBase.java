package minskim;

import minskim.enums.LookDirection;
import minskim.enums.NextAction;
import minskim.enums.Possible;
import minskim.enums.WumpusObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static minskim.WumpusWorld.*;
import static minskim.enums.LookDirection.*;
import static minskim.enums.NextAction.*;
import static minskim.enums.Possible.*;
import static minskim.enums.WumpusObject.*;

public class KnowledgeBase {
    private State stateMap[][] = null;
    private boolean visited[][] = null;
    private WumpusObject myMap[][] = null;
    private Queue<int[]> emptyCell = null;

    private Stack<NextAction> dfsStack = null;
    private Possible canWumpus[][] = null;
    private Possible canPitch[][] = null;

    public KnowledgeBase() {
        stateMap = new State[MAP_ROW][MAP_COL];
        visited = new boolean[MAP_ROW][MAP_COL];
        myMap = new WumpusObject[MAP_ROW][MAP_COL];
        emptyCell = new LinkedList<>();
        dfsStack = new Stack<>();
        canWumpus = new Possible[MAP_ROW][MAP_COL];
        canPitch = new Possible[MAP_ROW][MAP_COL];
    }

    private void checkDanger(Possible[][] map, int r, int c, Possible val, Possible cmp) {
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
        }
        /* 살아있는 경우 */
        if (agent.isAlive()) {
            myMap[row][col] = EMPTY;
        } else  { /* 죽은 경우 이유 분석 */
            if (canWumpus[row][col] == POSSIBLE && canPitch[row][col] == Possible.NEVER) {
                myMap[row][col] = WumpusObject.WUMPUS;
            }
            if (canPitch[row][col] == POSSIBLE && canWumpus[row][col] == Possible.NEVER) {
                myMap[row][col] = WumpusObject.PITCH;
            }
        }
        if (state.isBreeze()) {
            checkDanger(canPitch, row, col, POSSIBLE, Possible.IDK);
        } else {
            canPitch[row + 1][col] = Possible.NEVER;
            canPitch[row - 1][col] = Possible.NEVER;
            canPitch[row][col + 1] = Possible.NEVER;
            canPitch[row][col - 1] = Possible.NEVER;
        }
        if (state.isStench()) {
            checkDanger(canWumpus, row, col, POSSIBLE, Possible.IDK);
        } else {
            canWumpus[row + 1][col] = Possible.NEVER;
            canWumpus[row - 1][col] = Possible.NEVER;
            canWumpus[row][col + 1] = Possible.NEVER;
            canWumpus[row][col - 1] = Possible.NEVER;
        }
        if (state.isScream()) {
            if (agent.getDirection() == NORTH) {
                myMap[row + 1][col] = EMPTY;
                visited[row + 2][col] = false;
                visited[row][col] = false;
                visited[row + 1][col + 1] = false;
                visited[row + 1][col + 2] = false;
                emptyCell.add(new int[] {row + 1, col});
            } else if (agent.getDirection() == WEST) {
                myMap[row][col - 1] = EMPTY;
                visited[row+1][col-1] = false;
                visited[row-1][col-1] = false;
                visited[row][col] = false;
                visited[row][col - 2] = false;
                emptyCell.add(new int[] {row, col - 1});
            } else if (agent.getDirection() == SOUTH) {
                myMap[row - 1][col] = EMPTY;
                visited[row][col] = false;
                visited[row - 2][col] = false;
                visited[row - 1][col - 1] = false;
                visited[row - 1][col + 1] = false;
                emptyCell.add(new int[] {row - 1, col});
            } else if (agent.getDirection() == EAST) {
                myMap[row][col + 1] = EMPTY;
                visited[row+1][col+1] = false;
                visited[row-1][col+1] = false;
                visited[row][col + 2] = false;
                visited[row][col] = false;
                emptyCell.add(new int[] {row, col + 1});
            }
        }
        if (state.isGlitter()) {
            myMap[row][col] = WumpusObject.GOLD;
        }
        if (!state.isBreeze() && !state.isStench()) {
            emptyCell.add(new int[] {row + 1, col});
            emptyCell.add(new int[] {row - 1, col});
            emptyCell.add(new int[] {row, col + 1});
            emptyCell.add(new int[] {row, col - 1});
        }

        /* 상태를 기반으로 추론 */
        for (int r=1; r<=4; ++r) {
            for (int c=1; c<=4; ++c) {
                if (stateMap[r][c].isStench()) {
                    inference(canWumpus, r, c, WumpusObject.WUMPUS);
                }
                if (stateMap[r][c].isBreeze()) {
                    inference(canPitch, r, c, WumpusObject.PITCH);
                }
            }
        }

        visited[row][col] = true;
        stateMap[row][col] = state;
    }

    private void inference(Possible[][] canMap, int row, int col, WumpusObject val) {
        if (canMap[row+1][col] == POSSIBLE &&
                canMap[row-1][col] == Possible.NEVER &&
                canMap[row][col+1] == Possible.NEVER &&
                canMap[row][col-1] == Possible.NEVER) {
            myMap[row+1][col] = val;
        }
        if (canMap[row-1][col] == POSSIBLE &&
                canMap[row+1][col] == Possible.NEVER &&
                canMap[row][col+1] == Possible.NEVER &&
                canMap[row][col-1] == Possible.NEVER) {
            myMap[row-1][col] = val;
        }
        if (canMap[row][col+1] == POSSIBLE &&
                canMap[row+1][col] == Possible.NEVER &&
                canMap[row-1][col] == Possible.NEVER &&
                canMap[row][col-1] == Possible.NEVER) {
            myMap[row][col+1] = val;
        }
        if (canMap[row][col-1] == POSSIBLE &&
                canMap[row+1][col] == Possible.NEVER &&
                canMap[row-1][col] == Possible.NEVER &&
                canMap[row][col+1] == Possible.NEVER) {
            myMap[row][col-1] = val;
        }
    }

    private void tell(Agent agent, NextAction nextAction) {
        if (nextAction == GOFORWARD) {
            agent.GoForward();
        } else if (nextAction == TURNLEFT) {
            agent.TurnLeft();
        } else if (nextAction == TURNRIGHT) {
            agent.TurnRight();
        } else if (nextAction == SHOOT) {
            agent.Shoot();
        } else if (nextAction == GRAB) {
            agent.Grab();
        } else if (nextAction == CLIMB) {
            agent.Climb();
        }
    }

    private NextAction ask(Agent agent, State state) {
        int row = agent.getLocRow();
        int col = agent.getLocCol();

        NextAction nextAction = GOFORWARD;
        /* 에이전트가 사망했는가 */
        if (!agent.isAlive()) {
            nextAction = RESTART;
            return nextAction;
        }
        /* 벽에 부딪쳤는가 */
        if (state.isBump()) {
            if (Math.abs(agent.getDirection().compareTo(agent.getPrevDirection())) == 2) {
                nextAction = GOFORWARD;
            } else {
                nextAction = TURNLEFT;
            }
            return nextAction;
        }

        /* 화살을 쏠 것인지, 움직일 건지 생각해야함 */

        /* 목표위치에 도달했다면 초기화 */
        if (agent.getTargetCell()[0] == row && agent.getTargetCell()[1] == col) {
            agent.setTargetCell(new int[] {0 ,0});
        }
        /* 이미 목표 위치가 있다면 */
        if (agent.getTargetCell()[0] != 0 || agent.getTargetCell()[1] != 0) {
            /* 방향을 찾아서 턴을 하거나 이동을 하는 메서드 */
            nextAction = findDirection(agent);
        }
        else { /* 없는데 */
            /* 이동 할 안전한 셀이 있으면 */
            if (!emptyCell.isEmpty()) {
                agent.setTargetCell(emptyCell.poll());
                nextAction = findDirection(agent);
            } else {
                /* 위험 지역을 목표로 설정 */
                setNextCell(agent);
                if (agent.getTargetCell()[0] == -1) {
                    nextAction = GAMEOVER;
                } else {
                    nextAction = findDirection(agent);
                }
            }
        }

        /* 만약 바라보는 방향으로 냄새가 난다면 */
        if (agent.getNextActions().isEmpty() && state.isStench() && agent.getArrow() > 0) {
            if (agent.getDirection() == NORTH &&
                    canWumpus[row + 1][col] == POSSIBLE) {
                nextAction = SHOOT;
            } else if (agent.getDirection() == WEST &&
                    canWumpus[row][col - 1] == POSSIBLE) {
                nextAction = SHOOT;
            } else if (agent.getDirection() == SOUTH &&
                    canWumpus[row - 1][col] == POSSIBLE) {
                nextAction = SHOOT;

            } else if (agent.getDirection() == EAST &&
                    canWumpus[row][col + 1] == POSSIBLE) {
                nextAction = SHOOT;
            }
        }

        if (state.isGlitter()) {
            nextAction = NextAction.GRAB;
            agent.setTargetCell(new int[] {1, 1});
            return nextAction;
        }
        if (agent.isHaveGold() && row == 1 && col == 1) {
            nextAction = NextAction.CLIMB;
            return nextAction;
        }

        return nextAction;
    }

    private boolean[][] dfsVisited = null;
    private boolean dfsEnd = false;
    private void dfs(Agent agent, int r, int c) {
        if (dfsVisited == null) {
            dfsVisited = new boolean[MAP_ROW][MAP_COL];
            dfsEnd = false;
        }
        if (dfsEnd)
            return;

        dfsVisited[r][c] = true;
        if (r == agent.getTargetCell()[0] && c == agent.getTargetCell()[1]) {
            dfsVisited = null;
            agent.setNextActions(new LinkedList<>(dfsStack));
            dfsEnd = true;
            return;
        }

        int cnt = 0;
        if (dfsVisited[r + 1][c] == false && (visited[r + 1][c] || (r + 1 == agent.getTargetCell()[0] && c == agent.getTargetCell()[1]))) {
            if (agent.getDirection() == NORTH) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            } else if (agent.getDirection() == WEST) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (agent.getDirection() == SOUTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            } else if (agent.getDirection() == EAST) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            }
            dfs(agent, r + 1, c);
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
        if (dfsVisited[r - 1][c] == false && (visited[r - 1][c] || (r - 1 == agent.getTargetCell()[0] && c == agent.getTargetCell()[1]))) {
            if (agent.getDirection() == NORTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            } else if (agent.getDirection() == WEST) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (agent.getDirection() == SOUTH) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            } else if (agent.getDirection() == EAST) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            }
            dfs(agent, r - 1, c);
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
        if (dfsVisited[r][c + 1] == false && (visited[r][c + 1] || (r == agent.getTargetCell()[0] && c + 1 == agent.getTargetCell()[1]))) {
            if (agent.getDirection() == NORTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (agent.getDirection() == WEST) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            } else if (agent.getDirection() == SOUTH) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (agent.getDirection() == EAST) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            }
            dfs(agent, r, c + 1);
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
        if (dfsVisited[r][c - 1] == false && (visited[r][c - 1] || (r == agent.getTargetCell()[0] && c - 1 == agent.getTargetCell()[1]))) {
            if (agent.getDirection() == NORTH) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (agent.getDirection() == WEST) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            } else if (agent.getDirection() == SOUTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (agent.getDirection() == EAST) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            }
            dfs(agent, r, c - 1);
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
    }

    private void setNextCell(Agent agent) {
        for (int r=1; r<MAP_ROW-1; ++r) {
            for (int c=1; c<MAP_COL-1; ++c) {
                if (myMap[r][c] != PITCH && canWumpus[r][c] == POSSIBLE) {
                    agent.setTargetCell(new int[] {r, c});
                    return;
                }
            }
        }
        agent.setTargetCell(new int[] {-1, -1});
    }

    private NextAction findDirection(Agent agent) {
        if (agent.getNextActions().isEmpty()){
            if (agent.getTargetCell()[0] == 0 || agent.getTargetCell()[1] == 0) {
                return GOFORWARD;
            }
            dfs(agent, agent.getLocRow(), agent.getLocCol());
        }
        return agent.getNextActions().poll();
    }

    public NextAction Reasoning(Agent agent, State state) {
        tell(agent, state); // Percept 이후
        NextAction nextAction = ask(agent, state);  // Reasoning
        tell(agent, nextAction);  // Action 이전
        return nextAction;
    }

}
