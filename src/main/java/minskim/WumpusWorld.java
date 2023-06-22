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
    private Map map;

    public static final int MAP_ROW = 6;
    public static final int MAP_COL = 6;

    public WumpusWorld() {
        mapInit();
        sensor = new Sensor();
        endGame = false;
        stopGame = false;
        map = new Map();
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
                if (random.nextInt(10000) < 1000) {
                    worldMap[i][j] = PITCH;
                }
                if (random.nextInt(10000) < 1000) {
                    worldMap[i][j] = WUMPUS;
                }
            }
        }
    }

    private State Percept(Agent agent) {
        State state = sensor.percept(agent, worldMap);
        System.out.println(state);
        return state;
    }

    private NextAction Reasoning(Agent agent, KnowledgeBase kb, State state, Map map) {
        NextAction nextAction = kb.Reasoning(agent, state, worldMap, map);
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
//        map.drawWumpusWorld(worldMap, kb, agent);
        while (!stopGame) {
            System.out.println("++++++++ start ++++++++");

            /* 상태를 인식함 */
            state = Percept(agent);
            /* 현재 kb를 기반으로 추론을 해서 다음 행동을 결정함 */
            nextAction = Reasoning(agent, kb, state, map);
            /* 결정된 행동을 실행하고, 상태를 kb에 저장함 */
            Action(agent, nextAction);
            if (nextAction == RESTART) {
                System.out.println("에이전트가 사망하였습니다 ㅜㅜ");
                System.out.println("row: " + agent.getLocRow() + " col: " + agent.getLocCol());
                kb.printMap(worldMap, agent);
                return false;
            } else {
                System.out.println("Action: " + nextAction);
            }

            map.setWaiting();
            while (map.isWaiting()) {
                if(map.getResetTrigger()) {
                    return true;
                }
                sleep(10);
            }
//            map.drawWumpusWorld(worldMap, kb, agent);
            System.out.println("========= end =========\n");
        }
        if (endGame) {
            return true;
        }
        return false;
    }

    public void startLoop() throws InterruptedException {
        KnowledgeBase kb = new KnowledgeBase();
        /* 게임 계속 실행 */
        while (true) {
            if(map.getResetTrigger()) {
                map.setResetFalse();
                kb = new KnowledgeBase();
                mapInit();
                sensor = new Sensor();
            }
            /* 새로운 에이전트 */
            Agent agent = new Agent();
            play(agent, kb, map);
            while(endGame){
                if(map.getResetTrigger()) {
                    mapInit();
                    endGame = false;
                    stopGame = false;
                    break;
                }
                sleep(10);
            }
        }
        //System.out.println("Game Over!!!");

    }
}