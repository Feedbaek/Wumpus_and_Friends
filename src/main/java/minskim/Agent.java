package minskim;

import minskim.enums.LookDirection;

public class Agent {
    private boolean alive = false;
    private int arrow = 0;
    private boolean haveGold = false;
    private LookDirection direction = LookDirection.EAST;
    private int locRow = 0;
    private int locCol = 0;

    public Agent() {
        alive = true;
        arrow = 2;
        haveGold = false;
        direction = LookDirection.EAST;
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

    public boolean isHaveGold() {
        return haveGold;
    }

    public void setHaveGold(boolean haveGold) {
        this.haveGold = haveGold;
    }

    public LookDirection getDirection() {
        return direction;
    }

    public void setDirection(LookDirection direction) {
        this.direction = direction;
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
        if (arrow == 0) {
            return false;
        }
        arrow -= 1;
        return true;
    }
    public void Climb(State state) {
        System.out.println("탈출 성공!!!");
    }
}
