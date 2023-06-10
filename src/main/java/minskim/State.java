package minskim;

public final class State {
    private boolean Stench = false;
    private boolean Breeze = false;
    private boolean Glitter = false;
    private boolean Bump = false;
    private boolean Scream = false;

    public boolean isStench() {
        return Stench;
    }

    public void setStench(boolean stench) {
        Stench = stench;
    }

    public boolean isBreeze() {
        return Breeze;
    }

    public void setBreeze(boolean breeze) {
        Breeze = breeze;
    }

    public boolean isGlitter() {
        return Glitter;
    }

    public void setGlitter(boolean glitter) {
        Glitter = glitter;
    }

    public boolean isBump() {
        return Bump;
    }

    public void setBump(boolean bump) {
        Bump = bump;
    }

    public boolean isScream() {
        return Scream;
    }

    public void setScream(boolean scream) {
        Scream = scream;
    }

    @Override
    public String toString() {
        return "Stench Breeze Scream Bump Glitter\n" + isStench() + " " + isBreeze() + " " + isScream() + " " + isBump() + " " + isGlitter();
    }
}
