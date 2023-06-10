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

    public boolean[][] getVisited() {
        return visited;
    }

    public KnowledgeBase() {
        stateMap = new State[MAP_ROW][MAP_COL];
        visited = new boolean[MAP_ROW][MAP_COL];
        myMap = new WumpusObject[MAP_ROW][MAP_COL];
        emptyCell = new LinkedList<>();
        dfsStack = new Stack<>();
        canWumpus = new Possible[MAP_ROW][MAP_COL];
        canPitch = new Possible[MAP_ROW][MAP_COL];
        for (int r=1; r<MAP_ROW; ++r) {
            for (int c=1; c<MAP_COL; ++c) {
                stateMap[r][c] = new State();
                visited[r][c] = false;
                myMap[r][c] = EMPTY;
                canWumpus[r][c] = IDK;
                canPitch[r][c] = IDK;
            }
        }
    }

    public State[][] getStateMap() {
        return stateMap;
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

    public void printMap(Object[][] map, Agent agent) {
        System.out.println("++++++++++++++++++");
        for (int i=MAP_ROW-1; i>=0; --i) {
            for (int j=0; j<MAP_COL; ++j) {
                if (agent.getLocRow() == i && agent.getLocCol() == j) {
                    if (agent.getDirection() == NORTH) {
                        System.out.print("  ^   ");
                    } else if (agent.getDirection() == WEST) {
                        System.out.print("  <   ");
                    } else if (agent.getDirection() == EAST) {
                        System.out.print("  >   ");
                    } else if (agent.getDirection() == SOUTH) {
                        System.out.print("  v   ");
                    }
                } else {
                    System.out.print(map[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println("==================");
    }

    private void tell(Agent agent, State state, WumpusObject[][] worldMap) {
        int row = agent.getLocRow();
        int col = agent.getLocCol();

        if (state.isBump()) {
            myMap[row][col] = WumpusObject.WALL;
            return;
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
            System.out.println("꽥!!!");
        }
        if (state.isBreeze()) {
            checkDanger(canPitch, row, col, POSSIBLE, Possible.IDK);
        } else {
            if (visited[row + 1][col] == false)
                canPitch[row + 1][col] = Possible.NEVER;
            if (visited[row - 1][col] == false)
                canPitch[row - 1][col] = Possible.NEVER;
            if (visited[row][col + 1] == false)
                canPitch[row][col + 1] = Possible.NEVER;
            if (visited[row][col - 1] == false)
                canPitch[row][col - 1] = Possible.NEVER;
        }
        if (state.isStench()) {
            checkDanger(canWumpus, row, col, POSSIBLE, Possible.IDK);
        } else {
            if (visited[row + 1][col] == false)
                canWumpus[row + 1][col] = Possible.NEVER;
            if (visited[row - 1][col] == false)
                canWumpus[row - 1][col] = Possible.NEVER;
            if (visited[row][col + 1] == false)
                canWumpus[row][col + 1] = Possible.NEVER;
            if (visited[row][col - 1] == false)
                canWumpus[row][col - 1] = Possible.NEVER;
        }

        if (agent.isShooted()) {
            if (state.isScream()) {
                if (agent.getDirection() == NORTH) {
                    myMap[row + 1][col] = EMPTY;
                    visited[row + 2][col] = false;
                    visited[row][col] = false;
                    visited[row + 1][col + 1] = false;
                    visited[row + 1][col + 2] = false;
                    canWumpus[row + 1][col] = NEVER;
                    emptyCell.add(new int[] {row + 1, col});
                } else if (agent.getDirection() == WEST) {
                    myMap[row][col - 1] = EMPTY;
                    visited[row+1][col-1] = false;
                    visited[row-1][col-1] = false;
                    visited[row][col] = false;
                    visited[row][col - 2] = false;
                    canWumpus[row][col - 1] = NEVER;
                    emptyCell.add(new int[] {row, col - 1});
                } else if (agent.getDirection() == SOUTH) {
                    myMap[row - 1][col] = EMPTY;
                    visited[row][col] = false;
                    visited[row - 2][col] = false;
                    visited[row - 1][col - 1] = false;
                    visited[row - 1][col + 1] = false;
                    canWumpus[row - 1][col] = NEVER;
                    emptyCell.add(new int[] {row - 1, col});
                } else if (agent.getDirection() == EAST) {
                    myMap[row][col + 1] = EMPTY;
                    visited[row+1][col+1] = false;
                    visited[row-1][col+1] = false;
                    visited[row][col + 2] = false;
                    visited[row][col] = false;
                    canWumpus[row][col + 1] = NEVER;
                    emptyCell.add(new int[] {row, col + 1});
                }
            } else {
                if (agent.getDirection() == NORTH) {
                    canWumpus[row + 1][col] = NEVER;
                } else if (agent.getDirection() == WEST) {
                    canWumpus[row][col - 1] = NEVER;
                } else if (agent.getDirection() == SOUTH) {
                    canWumpus[row - 1][col] = NEVER;
                } else if (agent.getDirection() == EAST) {
                    canWumpus[row][col + 1] = NEVER;
                }
            }
        }
        if (state.isGlitter()) {
            myMap[row][col] = GOLD;
            worldMap[row][col] = EMPTY;
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

    private void tell(Agent agent, NextAction nextAction, WumpusObject[][] worldMap) {
        int row = agent.getLocRow();
        int col = agent.getLocCol();

        if (nextAction == GOFORWARD) {
            agent.GoForward();
        } else if (nextAction == TURNLEFT) {
            agent.TurnLeft();
        } else if (nextAction == TURNRIGHT) {
            agent.TurnRight();
        } else if (nextAction == SHOOT) {
            if (agent.getDirection() == NORTH && worldMap[row + 1][col] == WUMPUS) {
                worldMap[row + 1][col] = EMPTY;
            } else if (agent.getDirection() == WEST && worldMap[row][col - 1] == WUMPUS)  {
                worldMap[row][col - 1] = EMPTY;
            } else if (agent.getDirection() == SOUTH && worldMap[row + 1][col] == WUMPUS) {
                worldMap[row + 1][col] = EMPTY;
            } else if (agent.getDirection() == EAST && worldMap[row][col + 1] == WUMPUS) {
                worldMap[row][col + 1] = EMPTY;
            }
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
                /* 벽에 충돌하면 목표 초기화 */
                agent.setTargetCell(new int[] {0, 0});
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
                System.out.println("[KB] 활쏠 준비");
                nextAction = SHOOT;
            } else if (agent.getDirection() == WEST &&
                    canWumpus[row][col - 1] == POSSIBLE) {
                System.out.println("[KB] 활쏠 준비");
                nextAction = SHOOT;
            } else if (agent.getDirection() == SOUTH &&
                    canWumpus[row - 1][col] == POSSIBLE) {
                System.out.println("[KB] 활쏠 준비");
                nextAction = SHOOT;
            } else if (agent.getDirection() == EAST &&
                    canWumpus[row][col + 1] == POSSIBLE) {
                System.out.println("[KB] 활쏠 준비");
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
    private int minSize = 0;
    private void dfs(Agent agent, int r, int c) {
        if (dfsVisited == null) {
            dfsVisited = new boolean[MAP_ROW][MAP_COL];
            for (int i=1; i<MAP_ROW; ++i) {
                for (int j=1; j<MAP_COL; ++j) {
                    dfsVisited[i][j] = false;
                }
            }
            dfsStack = new Stack<>();
            minSize = 100;
        }

        dfsVisited[r][c] = true;
        if (r == agent.getTargetCell()[0] && c == agent.getTargetCell()[1] && dfsStack.size() < minSize) {
            agent.setNextActions(new LinkedList<>(dfsStack));
            minSize = dfsStack.size();
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
                if (myMap[r][c] != PITCH && (canWumpus[r][c] == POSSIBLE || canPitch[r][c] == POSSIBLE)) {
                    agent.setTargetCell(new int[] {r, c});
                    System.out.println("[KB] targetCell: " + r + ", " + c);
                    return;
                }
            }
        }
        agent.setTargetCell(new int[] {-1, -1});
    }

    private NextAction findDirection(Agent agent) {
        System.out.println("[KB] findDirection start");
        if (agent.getNextActions().isEmpty()) {
            if (agent.getTargetCell()[0] == 0 || agent.getTargetCell()[1] == 0) {
                return GAMEOVER;
            }
            dfsVisited = null;
            System.out.println("[KB] dfs start");
            dfs(agent, agent.getLocRow(), agent.getLocCol());
            System.out.println("r: " + agent.getTargetCell()[0] + " c: " + agent.getTargetCell()[1]);
            System.out.println("[KB] dfs end");
        }
        System.out.println("[KB] findDirection poll");
        return agent.getNextActions().poll();
    }

    public NextAction Reasoning(Agent agent, State state, WumpusObject[][] worldMap) {
        tell(agent, state, worldMap); // Percept 이후
        NextAction nextAction = ask(agent, state);  // Reasoning
        tell(agent, nextAction, worldMap);  // Action 이전
        return nextAction;
    }

}
