package NeulSung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Container.*;

public class Map extends JFrame {
    private JPanel backPanel;
    private JTable mapTable;
    private DefaultTableModel model;


    public Map(){
        model = new DefaultTableModel(4,4);
        mapTable.setModel(model);   //add model(table inside)
        mapTable.setRowHeight(200);
        mapTable.setCellSelectionEnabled(false);

        //backPanel.add(mapTable);
        setContentPane(backPanel);    //add panel

        //set frame option
        setTitle("Wumpus World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setResizable(false);
    }

    public void init(){

    }
}
