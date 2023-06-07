package neulSung;

import neulSung.Enum.State;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class Map extends JFrame {
    private JPanel backPanel;
    private JTable mapTable;
    private DefaultTableModel model;
    private DefaultTableCellRenderer render;

    //private Cell[][] cells;

    private Object[][] data;

    // Images
    private Icon wumpus;
    private Icon pitch;
    private Icon stench;
    private Icon breeze;
    private Icon glitter;

    static final private String WUMPUS_LOC = "src/main/java/neulSung/Icons/Wumpus_temp.png";
    static final private String PITCH_LOC = "src/main/java/neulSung/Icons/Pitch_temp.png";
    static final private String STENCH_LOC = "src/main/java/neulSung/Icons/Stench_temp.png";
    static final private String BREEZE_LOC = "src/main/java/neulSung/Icons/Breeze_temp.png";
    static final private String GLITTER_LOC = "src/main/java/neulSung/Icons/Gold(Glittering)_temp.png";

    int cur_row=0;
    int stack=0;

    public Map(){
        // Image Load
        wumpus = new ImageIcon(WUMPUS_LOC);
        pitch = new ImageIcon(PITCH_LOC);
        stench = new ImageIcon(STENCH_LOC);
        breeze = new ImageIcon(BREEZE_LOC);
        glitter = new ImageIcon(GLITTER_LOC);

        // Data Init
        //cells = new Cell[4][4];
        //cells[0][0].setState(State.SAFE);
        data = new Object[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                data[i][j]=State.UNKNOWN;
            }
        }
        data[0][0]=State.SAFE;

        //debug============================================

        data[2][2]=wumpus;
        data[1][0]=stench;
        data[3][3]=glitter;
        data[0][3]=pitch;

        //debug-End============================================

        //Table(Grid) View Init
        Object[] columnVector = new Object[4];
        columnVector[0] = 0;
        columnVector[1] = 1;
        columnVector[2] = 2;
        columnVector[3] = 3;
        model = new DefaultTableModel(data,columnVector){
            @Override
            /*public Class getColumnClass(int c) {
                if(c==3) stack++;
                if(c==0) cur_row=stack;
                if(cur_row < 4 && getValueAt(cur_row,c) instanceof Icon) {
                    return
                }
                return String.class;
            }*/
            public Class getColumnClass(int c) {
                if(c==3) stack++;
                if(stack==4) stack=0;
                if(c==0) cur_row=stack;
                return getValueAt(cur_row,c).getClass();
            }
        };
        /*for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                model.setValueAt(data[i][j],i,j);
            }
        }*/
        model.setValueAt(State.SAFE,0,0);
        render = new MyTableCellRender();
        try {
            mapTable.setDefaultRenderer(Class.forName("java.lang.Object"), render);
        }catch(Exception e){
            e.printStackTrace();
        }
        render.setHorizontalAlignment(JLabel.CENTER);


        mapTable.setModel(model);   //add model(table inside)
        mapTable.setRowHeight(200);
        mapTable.setCellSelectionEnabled(false);
        mapTable.setDragEnabled(false);


        //backPanel.add(mapTable);
        setContentPane(backPanel);    //add panel

        //set frame option
        setTitle("Wumpus World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setResizable(false);
    }

    public void setSafe(int row,int column){
        model.setValueAt(State.SAFE,row,column);
    }
    public void setState(State state,int row, int column){
        if(state.equals(State.WUMPUS)) data[row][column] = wumpus;
        else if(state.equals(State.PITCH)) data[row][column] = pitch;
        else if(state.equals(State.STENCH)) data[row][column] = stench;
        else if(state.equals(State.BREEZE)) data[row][column] = pitch;
        else if(state.equals(State.GLITTER)) data[row][column] = pitch;
        else data[row][column] = state;
    }

    class MyTableCellRender extends DefaultTableCellRenderer{

        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            try {
                 if (data[row][column].equals(State.SAFE)){
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
