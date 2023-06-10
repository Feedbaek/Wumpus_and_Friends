package minskim;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        WumpusWorld wumpusWorld = new WumpusWorld();
        Agent agent;
        KnowledgeBase kb = new KnowledgeBase();

        wumpusWorld.startLoop();
    }
}
