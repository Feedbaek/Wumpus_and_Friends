package neulSung;

import minskim.Agent;
import minskim.KnowledgeBase;
import minskim.enums.LookDirection;
import minskim.enums.WumpusObject;
import neulSung.Enum.State;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;
public class
Map extends JFrame {
    /*--SWING COMPONENTS--*/
    private JPanel backPanel;
    private JPanel sidePanel;
    private JTable mapTable;
    private JTable perceptsTable;
    private JButton proceedButton;
    private JButton resetButton;
    private JLabel arrowsLabel;
    private DefaultTableModel model;
    private DefaultTableCellRenderer render;
    private DefaultTableModel perceptsModel;
    private DefaultTableCellRenderer perceptsRender;
    /*--SWING COMPONENTS-end--*/


    private Object[][] data;
    private Object[] columnVector;

    private Object[][] curPercepts;
    private Object[] perceptsCol;

    private boolean[][] discovered;

    // Images
    private ImageIcon wumpus;
    private ImageIcon scream;
    private ImageIcon pitch;
    private ImageIcon gold;
    private ImageIcon stench;
    private ImageIcon breeze;
    private ImageIcon glitter;
    private ImageIcon bump;
    private ImageIcon[] agent;

    static final private String WUMPUS_LOC = "Icons/Wumpus_temp.png";
    static final private String PITCH_LOC = "Icons/Pitch_temp.png";
    static final private String GOLD_LOC = "Icons/Gold.png";
    static final private String SCREAM_LOC = "Icons/Scream_temp.png";
    static final private String STENCH_LOC = "Icons/Stench_temp.png";
    static final private String BREEZE_LOC = "Icons/Breeze_temp.png";
    static final private String GLITTER_LOC = "Icons/Glitter.png";
    static final private String BUMP_LOC = "Icons/bump.png";
    static final private String AGENT_UP_LOC = "Icons/Agents/Agent_up.png";
    static final private String AGENT_DOWN_LOC = "Icons/Agents/Agent_down.png";
    static final private String AGENT_RIGHT_LOC = "Icons/Agents/Agent_right.png";
    static final private String AGENT_LEFT_LOC = "Icons/Agents/Agent_left.png";

    static final private int UP = 0;
    static final private int DOWN = 1;
    static final private int RIGHT = 2;
    static final private int LEFT = 3;

    int cur_row=0;
    int cur_row2=0;
    int stack=0;
    int stack2=0;
    int numOfArrows;

    boolean waiting=true;

    boolean reset=false;

    public Map() {
        /*--Image Load--*/;
        wumpus = new ImageIcon(getClass().getClassLoader().getResource(WUMPUS_LOC));
        pitch = new ImageIcon(getClass().getClassLoader().getResource(PITCH_LOC));
        gold = new ImageIcon(getClass().getClassLoader().getResource(GOLD_LOC));
        scream = new ImageIcon(getClass().getClassLoader().getResource(SCREAM_LOC));
        stench = new ImageIcon(getClass().getClassLoader().getResource(STENCH_LOC));
        breeze = new ImageIcon(getClass().getClassLoader().getResource(BREEZE_LOC));
        glitter = new ImageIcon(getClass().getClassLoader().getResource(GLITTER_LOC));
        bump = new ImageIcon(getClass().getClassLoader().getResource(BUMP_LOC));
        //agent
        agent = new ImageIcon[4];
        agent[UP] = new ImageIcon(getClass().getClassLoader().getResource(AGENT_UP_LOC));
        agent[RIGHT]=new ImageIcon(getClass().getClassLoader().getResource(AGENT_RIGHT_LOC));
        agent[LEFT]=new ImageIcon(getClass().getClassLoader().getResource(AGENT_LEFT_LOC));
        agent[DOWN]=new ImageIcon(getClass().getClassLoader().getResource(AGENT_DOWN_LOC));


        /*--Data Init--*/
        data = new Object[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (i == 0 || j == 0 || i == 5 || j == 5) data[i][j] = State.WALL;
                else data[i][j] = State.UNKNOWN;
            }
        }

        /*--# of Arrows--*/
        numOfArrows = 2;
        arrowsLabel.setText(String.valueOf(numOfArrows));

        /*--Table(Grid) View Init--*/
        // tableMap model Init
        columnVector = new Object[6];
        for (int i = 0; i < 6; i++) columnVector[i] = i;
        model = new DefaultTableModel(data, columnVector) {
            public Class getColumnClass(int c) {
                if (c == 5) stack++;
                if (stack == 6) stack = 0;
                if (c == 0) cur_row = stack;
                //if(!discovered[cur_row][c]) return String.class;
                if(getValueAt(cur_row,c).equals(agent[UP])||
                        getValueAt(cur_row,c).equals(agent[DOWN])||
                        getValueAt(cur_row,c).equals(agent[LEFT])||
                        getValueAt(cur_row,c).equals(agent[RIGHT])) return String.class;
                return getValueAt(cur_row, c).getClass();
            }
        };
        // tableMap render Init
        render = new MyTableCellRender();
        try {
            mapTable.setDefaultRenderer(Class.forName("java.lang.Object"), render);
        } catch (Exception e) {
            e.printStackTrace();
        }
        render.setHorizontalAlignment(JLabel.CENTER);


        /*--mapTable data in & Table cell edit--*/
        mapTable.setModel(model);   //add model(table inside)
        mapTable.setRowHeight(125);
        int cellWidth = 125;
        mapTable.getColumnModel().getColumn(0).setMaxWidth(cellWidth);
        mapTable.getColumnModel().getColumn(1).setMaxWidth(cellWidth);
        mapTable.getColumnModel().getColumn(2).setMaxWidth(cellWidth);
        mapTable.getColumnModel().getColumn(3).setMaxWidth(cellWidth);
        mapTable.getColumnModel().getColumn(4).setMaxWidth(cellWidth);
        mapTable.getColumnModel().getColumn(5).setMaxWidth(cellWidth);
        mapTable.setCellSelectionEnabled(false);
        mapTable.setDragEnabled(false);

        /*--Percepts Table Edit--*/
        // current Percepts data
        curPercepts = new Object[3][2];
        clear2DimensionArray(curPercepts);

        // perceptsTable Model
        perceptsCol = new Object[2];
        perceptsCol[0] = 0;
        perceptsCol[1] = 1;
        perceptsModel = new DefaultTableModel(perceptsCol, 2) {
            @Override
            public Class getColumnClass(int c) {
                if (c == 1) stack2++;
                if (stack2 == 3) stack2 = 0;
                if (c == 0) cur_row2 = stack2;
                return getValueAt(cur_row2, c).getClass();
            }
        };
        perceptsModel.setDataVector(curPercepts, perceptsCol);
        // perceptsTable Renderer
        perceptsRender = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setForeground(new Color(255, 255, 255));
                return cell;
            }
        };
        // Percepts Table
        perceptsTable.setModel(perceptsModel);
        try {
            perceptsTable.setDefaultRenderer(Class.forName("java.lang.Object"), perceptsRender);
        } catch (Exception e) {
            e.printStackTrace();
        }
        perceptsTable.setRowHeight(200);


        /*--set frame option--*/
        setContentPane(backPanel);    //add panel
        setTitle("Wumpus World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setResizable(false);

        /*--proceed button action listener--*/
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                waiting = false;
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                System.out.println("Reset BTN Clicked");
                System.out.println("prev reset : " + reset);
                reset = true;
            }
        });
    }

    //=========Interface========================

    public void drawSafe(int row, int column){
        int newRow=5-row;
        //discovered[newRow][column]=true;
        data[newRow][column]=State.SAFE;
        model.setDataVector(data,columnVector);
    }

    public void drawState(State state, int row, int column){
        int newRow=5-row;
        data[newRow][column] = stateConvertToIcon(state);
        model.setDataVector(data,columnVector);
    }
    public void clearCellState(int row,int column){
        int newRow=5-row;
        data[newRow][column]="empty";
    }

    public void drawPercepts(Vector<State> states){
        clear2DimensionArray(curPercepts);
        int size = states.size();
        int col=0,row=0;
        for(State state : states){
            if(row>2){
                System.err.println("ERROR : Too Many Percepts. Number of percepts must be under or same 6");
                break;
            }
            curPercepts[row][col++] = stateConvertToIcon(state);
            if(col>1) {
                row++;
                col=0;
                continue;
            }
        }
        perceptsModel.setDataVector(curPercepts,perceptsCol);
    }

    public void clearPercepts(){
        clear2DimensionArray(curPercepts);
        perceptsModel.setDataVector(curPercepts,perceptsCol);
    }

    /*public void setDiscovered(int row, int column){
        int newRow=5-row;
        discovered[newRow][column]=true;
    }*/

    public void useArrow(){
        if(numOfArrows<=0)
            numOfArrows=0;
        else
            numOfArrows--;
        arrowsLabel.setText(String.valueOf(numOfArrows));
    }

    public void drawWumpusWorld(WumpusObject[][] wumpusObjects, KnowledgeBase knowledgeBase, Agent agent){
        //Object[][] newMap = new Object[6][6];
        for(int i=0;i<6;i++){
            for(int j=0;j<6;j++){
                if(i==0 || j==0 || i==5 || j==5) data[i][j]=State.WALL;
                else data[i][j]=State.UNKNOWN;
            }
        }
        Vector<State> newPercepts = new Vector<>();
        discovered = knowledgeBase.getVisited();
        int agentRow = agent.getLocRow();
        int agentColumn = agent.getLocCol();
        //clear2DimensionArray(data);
        for(int row=0;row<6;row++){
            for(int column=0;column<6;column++) {
                if(discovered[row][column]) data[5-row][column] = stateConvertToIcon(State.SAFE);
                if(wumpusObjects[row][column].equals(WumpusObject.WUMPUS)) data[5-row][column] = stateConvertToIcon(State.WUMPUS);
                else if(wumpusObjects[row][column].equals(WumpusObject.PITCH)) data[5-row][column] = stateConvertToIcon(State.PITCH);
                else if(wumpusObjects[row][column].equals(WumpusObject.WALL)) data[5-row][column] = stateConvertToIcon(State.WALL);
                else if(wumpusObjects[row][column].equals(WumpusObject.GOLD)) data[5-row][column] = stateConvertToIcon(State.GOLD);
            }
        }
        if(agent.getDirection().equals(LookDirection.EAST)) data[5-agentRow][agentColumn] = stateConvertToIcon(State.AGENT_RIGHT);
        else if(agent.getDirection().equals(LookDirection.WEST)) data[5-agentRow][agentColumn] = stateConvertToIcon(State.AGENT_LEFT);
        else if(agent.getDirection().equals(LookDirection.NORTH)) data[5-agentRow][agentColumn] = stateConvertToIcon(State.AGENT_UP);
        else if(agent.getDirection().equals(LookDirection.SOUTH)) data[5-agentRow][agentColumn] = stateConvertToIcon(State.AGENT_DOWN);

        if(knowledgeBase.getStateMap()[agentRow][agentColumn].isStench()) newPercepts.add(State.STENCH);
        if(knowledgeBase.getStateMap()[agentRow][agentColumn].isBreeze()) newPercepts.add(State.BREEZE);
        if(knowledgeBase.getStateMap()[agentRow][agentColumn].isGlitter()) newPercepts.add(State.GLITTER);
        if(knowledgeBase.getStateMap()[agentRow][agentColumn].isScream()) newPercepts.add(State.SCREAM);
        if(knowledgeBase.getStateMap()[agentRow][agentColumn].isBump()) newPercepts.add(State.BUMP);

        arrowsLabel.setText(String.valueOf(agent.getArrow()));
        model.setDataVector(data,columnVector);
        drawPercepts(newPercepts);
    }

    public boolean isWaiting(){
        return waiting;
    }
    public boolean getResetTrigger(){
        return reset;
    }

    public void setWaiting(){ this.waiting=true; }
    public void setResetFalse(){ this.reset=false; }

    //=========Interface-end=====================


    //=========Util==============================
    private Object[][] clear2DimensionArray(Object[][] array){
        int col = array[0].length;
        int row = array.length;
        int i,j;
        for(i=0;i<row;i++){
            for(j=0;j<col;j++)
                array[i][j]="Empty";
        }
        return array;
    }

    private Object stateConvertToIcon(State state){
        if(state.equals(State.WUMPUS)) return wumpus;
        else if(state.equals(State.PITCH)) return pitch;
        else if(state.equals(State.GOLD)) return gold;
        else if(state.equals(State.GLITTER)) return glitter;
        else if(state.equals(State.STENCH)) return stench;
        else if(state.equals(State.BREEZE)) return breeze;
        else if(state.equals(State.SCREAM)) return scream;
        else if(state.equals(State.BUMP)) return bump;
        else if(state.equals(State.AGENT_UP)) return agent[UP];
        else if(state.equals(State.AGENT_DOWN)) return agent[DOWN];
        else if(state.equals(State.AGENT_LEFT)) return agent[LEFT];
        else if(state.equals(State.AGENT_RIGHT)) return agent[RIGHT];
        else return state;
    }

    private State ConvertToState(WumpusObject wumpusObject){
        if(wumpusObject.equals(WumpusObject.WUMPUS)) return State.WUMPUS;
        if(wumpusObject.equals(WumpusObject.PITCH)) return State.PITCH;
        if(wumpusObject.equals(WumpusObject.WALL)) return State.WALL;
        if(wumpusObject.equals(WumpusObject.GOLD)) return State.GLITTER;
        return State.UNKNOWN;
    }

    private void print2DimensionBooleanArray(boolean[][] array){
        System.out.println();
        System.out.println("BOOLEAN ARRAY");
        for(int i = 0; i < array.length; i++ ){
            for(int j = 0; j<array[i].length; j++){
                System.out.print(array[5-i][j]+" | ");
            }
            System.out.println();
            System.out.println("----------------------------------");
        }
    }
    private void print2DimensionArray(Object[][] array){
        System.out.println();
        System.out.println("OBJECT ARRAY");
        for(int i = 0; i < array.length; i++ ){
            for(int j = 0; j<array[i].length; j++){
                System.out.print(array[i][j].toString()+" | ");
            }
            System.out.println();
            System.out.println("----------------------------------");
        }
    }
    //=========Util-end==========================

    class MyTableCellRender extends DefaultTableCellRenderer{



        private static final long serialVersionUID = 1L;

        int cur_row=0;
        int cur_col=0;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.cur_col=column;
            this.cur_row=row;
            cell.setBackground(new Color(0, 0, 0, 255));
            cell.setForeground(new Color(0, 0, 0, 255));
            if (data[row][column].equals(State.SAFE) || discovered[5-row][column]){
                if(row==0||row==5||column==0||column==5){
                    cell.setBackground(new Color(70, 70, 70, 255));
                    cell.setForeground(new Color(70, 70, 70, 255));
                }
                else{
                    cell.setBackground(new Color(143, 255, 78, 183));
                    cell.setForeground(new Color(143, 255, 78, 183));
                }
            }
            if(data[row][column].equals(State.WALL) ){
                 cell.setBackground(new Color(70, 70, 70, 255));
                 cell.setForeground(new Color(70, 70, 70, 255));
             }
            return cell;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D)g;
            g.fillRect(0, 0, 150, 140);
            if(data[this.cur_row][this.cur_col]==agent[Map.UP]) g2d.drawImage(agent[Map.UP].getImage(), 0, 0, null);
            if(data[this.cur_row][this.cur_col]==agent[Map.DOWN]) g2d.drawImage(agent[Map.DOWN].getImage(), 0, 0, null);
            if(data[this.cur_row][this.cur_col]==agent[Map.LEFT]) g2d.drawImage(agent[Map.LEFT].getImage(), 0, 0, null);
            if(data[this.cur_row][this.cur_col]==agent[Map.RIGHT]) g2d.drawImage(agent[Map.RIGHT].getImage(), 0, 0, null);
        }
    }

}
