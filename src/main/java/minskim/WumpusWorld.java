package minskim;

import minskim.enums.NextAction;
import minskim.enums.WumpusObject;
import neulSung.Map;

import java.util.Random;
import java.util.Scanner;

import static java.lang.Thread.sleep;
import static minskim.enums.NextAction.*;
import static minskim.enums.WumpusObject.*;


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
                    worldMap[i][j] = WALL;
                } else {
                    worldMap[i][j] = EMPTY;
                }
            }
        }

        int r = 0, c = 0;
        do {
            r = random.nextInt(MAP_ROW);
            c = random.nextInt(MAP_COL);
        } while (r == 0 || c == 0 || r == MAP_ROW-1 || c == MAP_COL-1 || (r == 1 && c == 1));
        worldMap[r][c] = WumpusObject.GOLD;

        for (int i=1; i<MAP_ROW-1; ++i) {
            for (int j=1; j<MAP_COL-1; ++j) {
                if (i == 1 && j == 1) {
                    continue;
                }
                if (random.nextInt(10) == 0) {
                    worldMap[i][j] = WUMPUS;
                    continue;
                }
                if (random.nextInt(10) == 0) {
                    worldMap[i][j] = PITCH;
                }
            }
        }
    }

    private State Percept(Agent agent) {
        State state = sensor.percept(agent, worldMap);
        System.out.println(state);
        return state;
    }

    private NextAction Reasoning(Agent agent, KnowledgeBase kb, State state) {
        NextAction nextAction = kb.Reasoning(agent, state, worldMap);
        return nextAction;
    }

    private void Action(Agent agent, NextAction nextAction) {
        if (nextAction == RESTART) {
            stopGame = true;
        }
        if (nextAction == GAMEOVER || nextAction == CLIMB) {
            stopGame = true;
            endGame = true;
        }
        if (nextAction == GAMEOVER) {
            System.out.println("why gameOver: ");
            for (int i=MAP_ROW-1; i>=0; --i) {
                for (int j=0; j<MAP_COL; ++j) {
                    System.out.print(worldMap[i][j] + " ");
                }
                System.out.println();
            }
        }
    }

    public boolean play(Agent agent, KnowledgeBase kb, Map map) throws InterruptedException {
        State state;
        NextAction nextAction;
        stopGame = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("play!!!");
        while (!stopGame) {
            kb.printMap(worldMap, agent);
//            String input = sc.next();
//            if (input.equals("0")) {
//                kb.printMap(worldMap, agent);
//                System.out.println("강제종료");
//                System.exit(0);
//            }
            map.setWaiting();
            while (map.isWaiting()) {
                sleep(10);
            }

            /* 상태를 인식함 */
            state = Percept(agent);
            /* 현재 kb를 기반으로 추론을 해서 다음 행동을 결정함 */
            nextAction = Reasoning(agent, kb, state);
            /* 결정된 행동을 실행하고, 상태를 kb에 저장함 */
            Action(agent, nextAction);
            if (nextAction == RESTART) {
                System.out.println("에이전트가 사망하였습니다 ㅜㅜ");
                System.out.println("row: " + agent.getLocRow() + " col: " + agent.getLocCol());
                kb.printMap(worldMap, agent);
            } else {
                System.out.println("Action: " + nextAction);
            }
            map.drawWumpusWorld(worldMap, kb, agent);
        }
        if (endGame) {
            return true;
        }
        return false;
    }

    public void startLoop() throws InterruptedException {
        KnowledgeBase kb = new KnowledgeBase();
        Map map = new Map();
        /* 게임 계속 실행 */
        while (true) {
            /* 새로운 에이전트 */
            Agent agent = new Agent();
            if (play(agent, kb, map)) {
                break;
            };
        }
        System.out.println("Game Over!!!");
    }
}