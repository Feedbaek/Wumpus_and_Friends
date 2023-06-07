package neulSung;

import neulSung.Enum.State;

import javax.swing.*;
import java.util.Random;

public class Cell extends Object {

    private State state;

    private Icon icon;

    Cell(){
        this.state=State.UNKNOWN;
        this.icon=null;
    }

    public State getState(){return state;}

    public void setState(State state){this.state=state;}
    public Icon getIcon() {return icon;}

    public void setIcon(Icon icon) {this.icon = icon;}


}
