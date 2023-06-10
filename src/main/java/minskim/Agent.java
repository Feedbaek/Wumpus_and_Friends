package minskim;

import minskim.enums.LookDirection;
import minskim.enums.NextAction;
import minskim.enums.WumpusObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Agent {
    private boolean alive = false;
    private int arrow = 0;
    private boolean shooted = false;
    private boolean haveGold = false;
    private int[] targetCell = null;
    private ArrayList<int[]> nextCell = null;
    private Queue<NextAction> nextActions = null;
    private LookDirection direction = LookDirection.EAST;
    private LookDirection prevDirection = LookDirection.EAST;
    private int locRow = 0;
    private int locCol = 0;

    public Agent() {
        alive = true;
        arrow = 2;
        shooted = false;
        haveGold = false;
        targetCell = new int[] {0, 0};
        nextCell = new ArrayList<>();
        nextActions = new LinkedList<>();
        direction = LookDirection.EAST;
        prevDirection = LookDirection.EAST;
        locRow = 1;
        locCol = 1;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getArrow() {
        return arrow;
    }

    public boolean isShooted() {
        return shooted;
    }

    public void setShooted(boolean shooted) {
        this.shooted = shooted;
    }

    public boolean isHaveGold() {
        return haveGold;
    }

    public void setHaveGold(boolean haveGold) {
        this.haveGold = haveGold;
    }

    public int[] getTargetCell() {
        return targetCell;
    }

    public void setTargetCell(int[] targetCell) {
        this.targetCell = targetCell;
    }

    public ArrayList<int[]> getNextCell() {
        return nextCell;
    }

    public Queue<NextAction> getNextActions() {
        return nextActions;
    }

    public void setNextActions(Queue<NextAction> nextActions) {
        this.nextActions = nextActions;
    }

    public LookDirection getDirection() {
        return direction;
    }

    public void setDirection(LookDirection direction) {
        this.direction = direction;
    }

    public LookDirection getPrevDirection() {
        return prevDirection;
    }

    public void setPrevDirection(LookDirection prevDirection) {
        this.prevDirection = prevDirection;
    }

    public int getLocRow() {
        return locRow;
    }

    public void setLocRow(int locRow) {
        this.locRow = locRow;
    }

    public int getLocCol() {
        return locCol;
    }

    public void setLocCol(int locCol) {
        this.locCol = locCol;
    }

    public void GoForward() {
        // 바라보는 방향으로 이동
        if (direction == LookDirection.NORTH) {
            locRow += 1;
        } else if (direction == LookDirection.SOUTH) {
            locRow -= 1;
        } else if (direction == LookDirection.WEST) {
            locCol -= 1;
        } else if (direction == LookDirection.EAST) {
            locCol += 1;
        }
        prevDirection = direction;
    }
    public void TurnLeft() {
        if (direction == LookDirection.NORTH) {
            direction = LookDirection.WEST;
        } else if (direction == LookDirection.SOUTH) {
            direction = LookDirection.EAST;
        } else if (direction == LookDirection.WEST) {
            direction = LookDirection.SOUTH;
        } else if (direction == LookDirection.EAST) {
            direction = LookDirection.NORTH;
        }
    }
    public void TurnRight() {
        if (direction == LookDirection.NORTH) {
            direction = LookDirection.EAST;
        } else if (direction == LookDirection.SOUTH) {
            direction = LookDirection.WEST;
        } else if (direction == LookDirection.WEST) {
            direction = LookDirection.NORTH;
        } else if (direction == LookDirection.EAST) {
            direction = LookDirection.SOUTH;
        }
    }
    public void Grab() {
        haveGold = true;
    }
    public boolean Shoot() {
        arrow -= 1;
        shooted = true;
        return true;
    }
    public void Climb() {
        System.out.println("탈출 성공!!!");
    }
}
