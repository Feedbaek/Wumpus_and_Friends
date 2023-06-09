package neulSung;

import neulSung.Enum.State;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private Icon wumpus;
    private Icon pitch;
    private Icon stench;
    private Icon breeze;
    private Icon glitter;
    private Icon[] agent;

    static final private String WUMPUS_LOC = "src/main/java/neulSung/Icons/Wumpus_temp.png";
    static final private String PITCH_LOC = "src/main/java/neulSung/Icons/Pitch_temp.png";
    static final private String STENCH_LOC = "src/main/java/neulSung/Icons/Stench_temp.png";
    static final private String BREEZE_LOC = "src/main/java/neulSung/Icons/Breeze_temp.png";
    static final private String GLITTER_LOC = "src/main/java/neulSung/Icons/Gold(Glittering)_temp.png";
    static final private String AGENT_UP_LOC = "src/main/java/neulSung/Icons/Agents/Agent_up.png";
    static final private String AGENT_DOWN_LOC = "src/main/java/neulSung/Icons/Agents/Agent_down.png";
    static final private String AGENT_RIGHT_LOC = "src/main/java/neulSung/Icons/Agents/Agent_right.png";
    static final private String AGENT_LEFT_LOC = "src/main/java/neulSung/Icons/Agents/Agent_left.png";

    static final private int UP = 0;
    static final private int DOWN = 1;
    static final private int RIGHT = 2;
    static final private int LEFT = 3;

    int cur_row=0;
    int cur_row2=0;
    int stack=0;
    int stack2=0;
    int numOfArrows;

    public Map(){
        /*--Image Load--*/
        wumpus = new ImageIcon(WUMPUS_LOC);
        pitch = new ImageIcon(PITCH_LOC);
        stench = new ImageIcon(STENCH_LOC);
        breeze = new ImageIcon(BREEZE_LOC);
        glitter = new ImageIcon(GLITTER_LOC);
        //agent
        agent = new ImageIcon[4];
        agent[UP]=new ImageIcon(AGENT_UP_LOC);
        agent[RIGHT]=new ImageIcon(AGENT_RIGHT_LOC);
        agent[LEFT]=new ImageIcon(AGENT_LEFT_LOC);
        agent[DOWN]=new ImageIcon(AGENT_DOWN_LOC);



        /*--Data Init--*/
        data = new Object[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                data[i][j]=State.UNKNOWN;
            }
        }
        data[0][0]=State.SAFE;
        data[0][1]=agent[LEFT];

        /*--Discovered Init--*/
        discovered = new boolean[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                discovered[i][j]=false;
            }
        }
        discovered[0][0]=true;
        discovered[0][1]=true;

        /*--# of Arrows--*/
        numOfArrows=2;
        arrowsLabel.setText(String.valueOf(numOfArrows));

        /*--Table(Grid) View Init--*/
        // tableMap model Init
        columnVector = new Object[4];
        for(int i=0;i<4;i++) columnVector[i]=i;
        model = new DefaultTableModel(data,columnVector){
            public Class getColumnClass(int c) {
                if(c==3) stack++;
                if(stack==4) stack=0;
                if(c==0) cur_row=stack;
                if(!discovered[cur_row][c]) return String.class;
                return getValueAt(cur_row,c).getClass();
            }
        };
        model.setValueAt(State.SAFE,0,0);
        // tableMap render Init
        render = new MyTableCellRender();
        try {
            mapTable.setDefaultRenderer(Class.forName("java.lang.Object"), render);
        }catch(Exception e){
            e.printStackTrace();
        }
        render.setHorizontalAlignment(JLabel.CENTER);


        /*--mapTable data in & Table cell edit--*/
        mapTable.setModel(model);   //add model(table inside)
        mapTable.setRowHeight(200);
        mapTable.setCellSelectionEnabled(false);
        mapTable.setDragEnabled(false);

        /*--Percepts Table Edit--*/
        // current Percepts data
        curPercepts = new Object[3][2];
        //debug
        curPercepts[0][0]=breeze;
        curPercepts[0][1]=stench;
        curPercepts[1][0]=glitter;
        //debug-end

        // perceptsTable Model
        perceptsCol = new Object[2];
        perceptsCol[0] = 0; perceptsCol[1] = 1;
        perceptsModel = new DefaultTableModel(perceptsCol,2){
            @Override
            public Class getColumnClass(int c){
                if(c==1) stack2++;
                if(stack2==3) stack2=0;
                if(c==0) cur_row2=stack2;
                return getValueAt(cur_row2,c).getClass();
            }
        };
        perceptsModel.setDataVector(curPercepts,perceptsCol);
        // perceptsTable Renderer
        perceptsRender = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setForeground(new Color(255,255,255));
                return cell;
            }
        };
        // Percepts Table
        perceptsTable.setModel(perceptsModel);
        try{perceptsTable.setDefaultRenderer(Class.forName("java.lang.Object"),perceptsRender);}catch(Exception e){e.printStackTrace();}
        perceptsTable.setRowHeight(200);


        /*--set frame option--*/
        setContentPane(backPanel);    //add panel
        setTitle("Wumpus World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setResizable(false);
    }

    //=========Interface========================

    public void setSafe(int row,int column){
        discovered[row][column]=true;
        data[row][column]=State.SAFE;
        model.setDataVector(data,columnVector);
    }

    public void setState(State state,int row, int column){
        data[row][column] = stateConvertToIcon(state);
        model.setDataVector(data,columnVector);
    }

    public void setCurPercepts(Vector<State> states){
        curPercepts = clear2DimensionArray(curPercepts);
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

    public void setDiscovered(int row, int column){
        discovered[row][column]=true;
    }

    public void useArrow(){
        if(numOfArrows<=0)
            numOfArrows=0;
        else
            numOfArrows--;
        arrowsLabel.setText(String.valueOf(numOfArrows));
    }
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
        else if(state.equals(State.STENCH)) return stench;
        else if(state.equals(State.BREEZE)) return breeze;
        else if(state.equals(State.GLITTER)) return glitter;
        else return state;
    }
    //=========Util-end==========================

    class MyTableCellRender extends DefaultTableCellRenderer{



        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            try {
                 if (data[row][column].equals(State.SAFE) && discovered[row][column]){
                     cell.setBackground(new Color(143, 255, 78, 183));
                     cell.setForeground(new Color(143, 255, 78, 183));
                 }
                 else {
                     cell.setBackground(new Color(0, 0, 0, 255));
                     cell.setForeground(new Color(0, 0, 0, 255));
                 }
            }catch(Exception e){
                System.out.println(cell.getName());
                //System.out.println(++debug);
            }
            return cell;
        }
    }

}
