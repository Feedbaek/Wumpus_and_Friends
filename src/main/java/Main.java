import minskim.Agent;
import minskim.KnowledgeBase;
import minskim.WumpusWorld;
import neulSung.*;
import neulSung.Enum.State;

import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        WumpusWorld wumpusWorld = new WumpusWorld();
        Agent agent;
        KnowledgeBase kb = new KnowledgeBase();

        wumpusWorld.startLoop();
    }

}
