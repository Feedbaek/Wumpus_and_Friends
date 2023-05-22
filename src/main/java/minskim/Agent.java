package minskim;

public class Agent {
    private int arrow = 0;
    private LookDirection direction = LookDirection.EAST;
    private int locRow = 0;
    private int locCol = 0;

    public Agent() {
        arrow = 2;
        direction = LookDirection.EAST;
        locRow = 1;
        locCol = 1;
    }

    public int getArrow() {
        return arrow;
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

    public void GoForward(State state) {
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

        // 벽에 부딫혔는지 검사
        if (locRow < 0 || locRow > 8 || locCol < 0 || locCol > 8) {
            state.setBump(true);
            if (locRow < 0) {
                locRow = 0;
            } else if (locRow > 8) {
                locRow = 8;
            } else if (locCol < 0) {
                locCol = 0;
            } else if (locCol > 8) {
                locCol = 8;
            }
            return ;
        }

        // Wumpus 검사

        // Pitch 검사

        // gold 검사
        System.out.println();
    }
    public void TurnLeft(State state) {
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
    public void TurnRight(State state) {
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
    public void Grab(State state) {

    }
    public void Shoot(State state) {

    }
    public void Climb(State state) {

    }
}
