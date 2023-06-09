package minskim;

import minskim.enums.NextAction;
import minskim.enums.WumpusObject;

import java.util.Random;


public class WumpusWorld {
    private WumpusObject worldMap[][] = null;
    private Sensor sensor = null;
    private boolean endGame = false;
    private boolean stopGame = false;

    public static final int MAP_ROW = 6;
    public static final int MAP_COL = 6;

    public WumpusWorld() {
        mapInit();
        sensor = new Sensor();
        endGame = false;
        stopGame = false;
    }

    // method
    private void mapInit() {
        worldMap = new WumpusObject[MAP_ROW][MAP_COL];

        Random random = new Random();

        for (int i=0; i<MAP_ROW; ++i) {
            for (int j=0; j<MAP_COL; ++j) {
                if (i == 0 || j == 0 || i == MAP_ROW-1 || j == MAP_COL-1) {
                    worldMap[i][j] = WumpusObject.WALL;
                }
            }
        }

        int r = 0, c = 0;
        do {
            r = random.nextInt(MAP_ROW);
            c = random.nextInt(MAP_COL);
        } while (r == 0 || c == 0 || r == MAP_ROW-1 || c == MAP_COL-1);
        worldMap[r][c] = WumpusObject.GOLD;

        for (int i=0; i<4; ++i) {
            for (int j=0; j<4; ++j) {
                if (random.nextInt(10) == 0) {
                    worldMap[i][j] = WumpusObject.WUMPUS;
                    continue;
                }
                if (random.nextInt(10) == 0) {
                    worldMap[i][j] = WumpusObject.PITCH;
                }
            }
        }
    }

    private State Percept(Agent agent) {
        State state = sensor.percept(agent, worldMap);
        return state;
    }

    private NextAction Reasoning(Agent agent, KnowledgeBase kb, State state) {
        NextAction nextAction = kb.Reasoning(agent, state);
        return nextAction;
    }

    private void Action(Agent agent, NextAction nextAction) {
        if (nextAction == NextAction.RESTART) {
            stopGame = true;
        }
        if (nextAction == NextAction.GAMEOVER) {
            stopGame = true;
            endGame = true;
        }
    }

    private boolean play(Agent agent, KnowledgeBase kb) {
        State state;
        NextAction nextAction;
        stopGame = false;

        while (!stopGame) {
            /* 상태를 인식함 */
            state = Percept(agent);
            /* 현재 kb를 기반으로 추론을 해서 다음 행동을 결정함 */
            nextAction = Reasoning(agent, kb, state);
            /* 결정된 행동을 실행하고, 상태를 kb에 저장함 */
            Action(agent, nextAction);
        }
        if (endGame) {
            return true;
        }
        return false;
    }

    public void start() {
        KnowledgeBase kb = new KnowledgeBase();

        /* 게임 계속 실행 */
        while (true) {
            /* 새로운 에이전트 */
            Agent agent = new Agent();
            if (play(agent, kb)) {
                break;
            };
        }
        System.out.println("Game Over!!!");
    }
}