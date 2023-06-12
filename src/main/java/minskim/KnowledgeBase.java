package minskim;

import minskim.enums.LookDirection;
import minskim.enums.NextAction;
import minskim.enums.Possible;
import minskim.enums.WumpusObject;
import neulSung.Map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static minskim.WumpusWorld.*;
import static minskim.enums.LookDirection.*;
import static minskim.enums.NextAction.*;
import static minskim.enums.Possible.*;
import static minskim.enums.Possible.IDK;
import static minskim.enums.WumpusObject.*;

public class KnowledgeBase {
    private State stateMap[][] = null;
    private boolean visited[][] = null;
    private WumpusObject myMap[][] = null;
    private Stack<int[]> emptyCell = null;

    private Stack<NextAction> dfsStack = null;
    private Possible canWumpus[][] = null;
    private Possible canPitch[][] = null;

    private boolean[][] dfsVisited = null;
    private int minSize = 0;

    public boolean[][] getVisited() {
        return visited;
    }

    public KnowledgeBase() {
        stateMap = new State[MAP_ROW][MAP_COL];
        visited = new boolean[MAP_ROW][MAP_COL];
        myMap = new WumpusObject[MAP_ROW][MAP_COL];
        emptyCell = new Stack<>();
        dfsStack = new Stack<>();
        canWumpus = new Possible[MAP_ROW][MAP_COL];
        canPitch = new Possible[MAP_ROW][MAP_COL];
        for (int r=0; r<MAP_ROW; ++r) {
            for (int c=0; c<MAP_COL; ++c) {
                stateMap[r][c] = new State();
                visited[r][c] = false;
                myMap[r][c] = WumpusObject.IDK;
                canWumpus[r][c] = IDK;
                canPitch[r][c] = IDK;
            }
        }
        myMap[1][1] = EMPTY;
    }

    public State[][] getStateMap() {
        return stateMap;
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

    private void checkDanger(Possible[][] map, int r, int c, Possible val, Possible cmp) {
        if (map[r + 1][c] == cmp && !visited[r + 1][c])
            map[r + 1][c] = val;
        if (map[r - 1][c] == cmp && !visited[r - 1][c])
            map[r - 1][c] = val;
        if (map[r][c + 1] == cmp && !visited[r][c + 1])
            map[r][c + 1] = val;
        if (map[r][c - 1] == cmp && !visited[r][c - 1])
            map[r][c - 1] = val;
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

    private void tell(Agent agent, State state, WumpusObject[][] worldMap) {
        int row = agent.getLocRow();
        int col = agent.getLocCol();

        if (state.isBump()) {
            myMap[row][col] = WumpusObject.WALL;
            visited[row][col] = true;
            stateMap[row][col] = state;
            canWumpus[row][col] = NEVER;
            canPitch[row][col] = NEVER;
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
            stateMap[row][col] = state;
            return;
        }
        if (state.isBreeze()) {
            if (agent.isAlive()) {
                checkDanger(canPitch, row, col, POSSIBLE, IDK);
            }
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
            if (agent.isAlive()) {
                checkDanger(canWumpus, row, col, POSSIBLE, IDK);
            }
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
                    canWumpus[row + 1][col] = NEVER;
                    if (!visited[row + 1][col]) {
                        emptyCell.add(new int[] {row + 1, col});
                    }
                } else if (agent.getDirection() == WEST) {
                    myMap[row][col - 1] = EMPTY;
                    canWumpus[row][col - 1] = NEVER;
                    if (!visited[row][col - 1]) {
                        emptyCell.add(new int[]{row, col - 1});
                    }
                } else if (agent.getDirection() == SOUTH) {
                    myMap[row - 1][col] = EMPTY;
                    canWumpus[row - 1][col] = NEVER;
                    if (!visited[row - 1][col]) {
                        emptyCell.add(new int[]{row - 1, col});
                    }
                } else if (agent.getDirection() == EAST) {
                    myMap[row][col + 1] = EMPTY;
                    canWumpus[row][col + 1] = NEVER;
                    if (!visited[row][col + 1]) {
                        emptyCell.add(new int[]{row, col + 1});
                    }
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
            agent.setShooted(false);
        }
        if (state.isGlitter()) {
            myMap[row][col] = GOLD;
            worldMap[row][col] = EMPTY;
        }
        if (!state.isBreeze() && !state.isStench()) {
            if (agent.getDirection() == NORTH) {
                if (!visited[row - 1][col]) {
                    emptyCell.add(new int[]{row - 1, col});
                }
                if (!visited[row][col + 1]) {
                    emptyCell.add(new int[]{row, col + 1});
                }
                if (!visited[row][col - 1]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
                if (!visited[row + 1][col]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
            } else if (agent.getDirection() == WEST) {
                if (!visited[row][col + 1]) {
                    emptyCell.add(new int[]{row, col + 1});
                }
                if (!visited[row - 1][col]) {
                    emptyCell.add(new int[]{row - 1, col});
                }
                if (!visited[row + 1][col]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
                if (!visited[row][col - 1]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
            } else if (agent.getDirection() == SOUTH) {
                if (!visited[row + 1][col]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
                if (!visited[row][col - 1]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
                if (!visited[row][col + 1]) {
                    emptyCell.add(new int[]{row, col + 1});
                }
                if (!visited[row - 1][col]) {
                    emptyCell.add(new int[]{row - 1, col});
                }
            } else if (agent.getDirection() == EAST) {
                if (!visited[row][col - 1]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
                if (!visited[row - 1][col]) {
                    emptyCell.add(new int[]{row - 1, col});
                }
                if (!visited[row + 1][col]) {
                    emptyCell.add(new int[]{row + 1, col});
                }
                if (!visited[row][col + 1]) {
                    emptyCell.add(new int[]{row, col + 1});
                }
            }
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
                if (canWumpus[r][c] == NEVER && canPitch[r][c] == NEVER) {
                    emptyCell.add(new int[] {r, c});
                }
            }
        }

        visited[row][col] = true;
        stateMap[row][col] = state;
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
            while (!emptyCell.isEmpty() && visited[emptyCell.peek()[0]][emptyCell.peek()[1]]) {
                emptyCell.pop();
            }
            if (!emptyCell.isEmpty()) {
                agent.setTargetCell(emptyCell.peek());
                emptyCell.pop();
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
            agent.getNextActions().clear();
            return nextAction;
        }
        if (agent.isHaveGold() && row == 1 && col == 1) {
            nextAction = NextAction.CLIMB;
            return nextAction;
        }

        return nextAction;
    }

    private void dfs(Agent agent, int r, int c, LookDirection direction) {
        if (dfsVisited == null) {
            dfsVisited = new boolean[MAP_ROW][MAP_COL];
            for (int i=0; i<MAP_ROW; ++i) {
                for (int j=0; j<MAP_COL; ++j) {
                    dfsVisited[i][j] = false;
                }
            }
            dfsVisited[r][c] = true;
            dfsStack = new Stack<>();
            minSize = 100;
        }

        if (r == agent.getTargetCell()[0] && c == agent.getTargetCell()[1] && dfsStack.size() < minSize) {
            System.out.println("[dfs] action: ");
            NextAction arr[] = dfsStack.toArray(new NextAction[0]);
            for (int i=0; i<arr.length; ++i) {
                System.out.print(arr[i] + " ");
            }
            System.out.println();
            agent.setNextActions(new LinkedList<>(dfsStack));
            minSize = dfsStack.size();
            return;
        }

        if (r <= 0 || c <= 0 || r >= 5 || c >= 5) {
            System.out.println("[DFS] error!!!!");
            return;
        }

        int cnt = 0;
        if (dfsVisited[r + 1][c] == false && (myMap[r + 1][c] == EMPTY || (r + 1 == agent.getTargetCell()[0] && c == agent.getTargetCell()[1]))) {
            if (direction == NORTH) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            } else if (direction == WEST) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (direction == SOUTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            } else if (direction == EAST) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            }
            dfsVisited[r + 1][c] = true;
            dfs(agent, r + 1, c, NORTH);
            dfsVisited[r + 1][c] = false;
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
        if (dfsVisited[r - 1][c] == false && (myMap[r - 1][c] == EMPTY || (r - 1 == agent.getTargetCell()[0] && c == agent.getTargetCell()[1]))) {
            if (direction == NORTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            } else if (direction == WEST) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (direction == SOUTH) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            } else if (direction == EAST) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            }
            dfsVisited[r - 1][c] = true;
            dfs(agent, r - 1, c, SOUTH);
            dfsVisited[r - 1][c] = false;
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
        if (dfsVisited[r][c + 1] == false && (myMap[r][c + 1] == EMPTY || (r == agent.getTargetCell()[0] && c + 1 == agent.getTargetCell()[1]))) {
            if (direction == NORTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (direction == WEST) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            } else if (direction == SOUTH) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (direction == EAST) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            }
            dfsVisited[r][c + 1] = true;
            dfs(agent, r, c + 1, EAST);
            dfsVisited[r][c + 1] = false;
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
        if (dfsVisited[r][c - 1] == false && (myMap[r][c - 1] == EMPTY || (r == agent.getTargetCell()[0] && c - 1 == agent.getTargetCell()[1]))) {
            if (direction == NORTH) {
                dfsStack.add(TURNLEFT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (direction == WEST) {
                dfsStack.add(GOFORWARD);
                cnt = 1;
            } else if (direction == SOUTH) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 2;
            } else if (direction == EAST) {
                dfsStack.add(TURNRIGHT);
                dfsStack.add(TURNRIGHT);
                dfsStack.add(GOFORWARD);
                cnt = 3;
            }
            dfsVisited[r][c - 1] = true;
            dfs(agent, r, c - 1, WEST);
            dfsVisited[r][c - 1] = false;
            for (int i=0; i<cnt; ++i) {
                dfsStack.pop();
            }
        }
    }

    private void setNextCell(Agent agent) {
        for (int r=0; r<MAP_ROW; ++r) {
            for (int c=0; c<MAP_COL; ++c) {
                if (myMap[r][c] != PITCH && !visited[r][c] && (canWumpus[r][c] == POSSIBLE || canPitch[r][c] == POSSIBLE)) {
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
            if (agent.getTargetCell()[0] == -1 || agent.getTargetCell()[1] == -1) {
                return GAMEOVER;
            }
            System.out.println("\n[KB] dfs start");
            dfsVisited = null;
            dfs(agent, agent.getLocRow(), agent.getLocCol(), agent.getDirection());
            System.out.println("r: " + agent.getTargetCell()[0] + " c: " + agent.getTargetCell()[1]);
            System.out.println("next actions: ");
            NextAction arr[] = agent.getNextActions().toArray(new NextAction[0]);
            for (int i=0; i<agent.getNextActions().size(); ++i) {
                System.out.print(arr[i] + " ");
            }
            System.out.println("\n[KB] dfs end\n");
        }
        System.out.println("[KB] findDirection poll()");
        NextAction ret = agent.getNextActions().peek();
        agent.getNextActions().poll();
        return ret;
    }

    public NextAction Reasoning(Agent agent, State state, WumpusObject[][] worldMap, Map map) {
        tell(agent, state, worldMap); // Percept 이후
        NextAction nextAction = ask(agent, state);  // Reasoning
        map.drawWumpusWorld(worldMap, this, agent);
        tell(agent, nextAction, worldMap);  // Action 이전
        return nextAction;
    }

}
