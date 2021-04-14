

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class UI {

    static int currentFloor1;
    static int currentFloor2;
    static int currentFloor3;
    static int currentFloor4;

    public static final int WORKING = 0;
    public static final int STUCK = 1;
    public static final int ARRIVED = 2;
    public static final int MOVING = 3;



    JCheckBox checkBox1;
    JCheckBox checkBox2;
    JCheckBox checkBox3;
    JCheckBox checkBox4;
    JCheckBox checkBox5;
    JCheckBox checkBox6;
    JCheckBox checkBox7;
    JCheckBox checkBox8;

    ImageIcon xIcon;
    ImageIcon checkIcon;
    ImageIcon bCirc;
    ImageIcon gCirc;
    ImageIcon image;

    JTextField textField1;
    JTextField textField2;
    JTextField textField3;
    JTextField textField4;

    JLabel label1;
    JLabel label2;
    JLabel label3;
    JLabel label4;

    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    JPanel panel3 = new JPanel();
    JPanel panel4 = new JPanel();

    JFrame frame;
    Border border;


    public UI() {
        xIcon = new ImageIcon("21.png");
        checkIcon = new ImageIcon("33.png");
        bCirc = new ImageIcon("13.png");
        gCirc = new ImageIcon("55.png");

        textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(100,40));
        textField1.setText("Current Floor: " + currentFloor1);
        textField1.setEditable(false);

        textField2 = new JTextField();
        textField2.setPreferredSize(new Dimension(100,40));
        textField2.setText("Current Floor: " + currentFloor2);
        textField2.setEditable(false);

        textField3 = new JTextField();
        textField3.setPreferredSize(new Dimension(100,40));
        textField3.setText("Current Floor: " + currentFloor3);
        textField3.setEditable(false);

        textField4 = new JTextField();
        textField4.setPreferredSize(new Dimension(100,40));
        textField4.setText("Current Floor: " + currentFloor4);
        textField4.setEditable(false);

        panel1 = new JPanel();
        panel1.setBackground(Color.cyan);
        panel1.setBounds(0, 0, 150, 600);
        panel2 = new JPanel();
        panel2.setBackground(Color.pink);
        panel2.setBounds(170, 0, 150, 600);
        panel3 = new JPanel();
        panel3.setBounds(340, 0, 150, 600);
        panel3.setBackground(Color.orange);
        panel4 = new JPanel();
        panel4.setBounds(510, 0, 150, 600);
        panel4.setBackground(Color.yellow);

        checkBox1 = new JCheckBox();
        checkBox2 = new JCheckBox();
        checkBox1.setText("Elevator Status");
        checkBox1.setFocusable(false);
        checkBox1.setIcon(checkIcon);
        //checkBox1.setSelectedIcon(checkIcon);

        checkBox2.setText("Elev arrival");
        checkBox2.setFocusable(false);
        checkBox2.setIcon(bCirc);
        //checkBox2.setSelectedIcon(gCirc);

        checkBox3 = new JCheckBox();
        checkBox4 = new JCheckBox();
        checkBox3.setText("Elevator Status");
        checkBox3.setFocusable(false);
        checkBox3.setIcon(checkIcon);
        //checkBox3.setSelectedIcon(checkIcon);

        checkBox4.setText("Elev arrival");
        checkBox4.setFocusable(false);
        checkBox4.setIcon(bCirc);
        //checkBox4.setSelectedIcon(gCirc);

        checkBox5 = new JCheckBox();
        checkBox6 = new JCheckBox();
        checkBox5.setText("Elevator Status");
        checkBox5.setFocusable(false);
        checkBox5.setIcon(checkIcon);
        //checkBox5.setSelectedIcon(checkIcon);

        checkBox6.setText("Elev arrival");
        checkBox6.setFocusable(false);
        checkBox6.setIcon(bCirc);
        checkBox6.setSelectedIcon(gCirc);

        checkBox7 = new JCheckBox();
        checkBox8 = new JCheckBox();
        checkBox7.setText("Elevator Status");
        checkBox7.setFocusable(false);
        checkBox7.setIcon(checkIcon);
        //checkBox7.setSelectedIcon(checkIcon);

        checkBox8.setText("Elev arrival");
        checkBox8.setFocusable(false);
        checkBox8.setIcon(bCirc);
        //checkBox8.setSelectedIcon(gCirc);




        image =new ImageIcon("12.png");
        border = BorderFactory.createLineBorder(Color.black,2);
        label1 = new JLabel();
        label1.setIcon(image);
        label1.setHorizontalTextPosition(JLabel.CENTER);
        label1.setVerticalTextPosition(JLabel.TOP);
        label1.setText("elevator1");
        label1.setBorder(border);
        label1.setVerticalAlignment(JLabel.TOP);
        label1.setHorizontalAlignment(JLabel.LEFT);
        label1.setBounds(0, 0, 150, 150);

        label2 = new JLabel();
        label2.setIcon(image);
        label2.setHorizontalTextPosition(JLabel.CENTER);
        label2.setVerticalTextPosition(JLabel.TOP);
        label2.setText("elevator2");
        label2.setBorder(border);
        label2.setVerticalAlignment(JLabel.TOP);
        label2.setHorizontalAlignment(JLabel.LEFT);
        label2.setBounds(0, 0, 150, 150);

        label3 = new JLabel();
        label3.setIcon(image);
        label3.setHorizontalTextPosition(JLabel.CENTER);
        label3.setVerticalTextPosition(JLabel.TOP);
        label3.setText("elevator3");
        label3.setBorder(border);
        label3.setVerticalAlignment(JLabel.TOP);
        label3.setHorizontalAlignment(JLabel.LEFT);
        label3.setBounds(0, 0, 150, 150);

        label4 = new JLabel();
        label4.setIcon(image);
        label4.setHorizontalTextPosition(JLabel.CENTER);
        label4.setVerticalTextPosition(JLabel.TOP);
        label4.setText("elevator4");
        label4.setBorder(border);
        label4.setVerticalAlignment(JLabel.TOP);
        label4.setHorizontalAlignment(JLabel.LEFT);
        label4.setBounds(0, 0, 150, 150);


        frame = new JFrame();
        frame.setSize(700,700); //sets dimension
        frame.setVisible(true); // makes visible
        panel1.add(label1);
        panel2.add(label2);
        panel3.add(label3);
        panel4.add(label4);
        frame.setTitle("Elevator GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit gui
        frame.setResizable(true); //can't resize
        frame.setLayout(null);
        frame.add(panel1);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);
        panel1.add(checkBox1);
        panel1.add(checkBox2);
        panel2.add(checkBox3);
        panel2.add(checkBox4);
        panel3.add(checkBox5);
        panel3.add(checkBox6);
        panel4.add(checkBox7);
        panel4.add(checkBox8);
        panel1.add(textField1);
        panel2.add(textField2);
        panel3.add(textField3);
        panel4.add(textField4);
    }

    public void setCurrentFloor2(int currentFloor2, int port) {
        if(port == 28) {
            textField1.setText("Current Floor: " + currentFloor2);
        }
        if(port == 29) {
            textField2.setText("Current Floor: " + currentFloor2);
        }
        if(port == 30) {
            textField3.setText("Current Floor: " + currentFloor2);
        }
        if(port == 31){
            textField4.setText("Current Floor: " + currentFloor2);
        }

    }

    public void setElevatorDoorStatus(int status, int port) {
        if(port == 28) {
            if (status == WORKING) {
                checkBox1.setSelected(true);
            } else if (status == STUCK) {
                checkBox1.setSelected(false);
            }
        }
    }

    public void setElevatorStatus(int status, int port){
        if(status == WORKING) {
            checkBox1.setSelected(true);
        }
        else if(status == STUCK) {
            checkBox1.setSelected(false);
        }
        else if(status == ARRIVED) {
            checkBox2.setSelected(true);
        }
        else if(status == MOVING) {
            checkBox2.setSelected(false);
        }
    }

    public void setArrivalStatus(int port) throws InterruptedException {
        if(port == 28) {
            checkBox2.setIcon(gCirc);
            Thread.sleep(2000);
            checkBox2.setIcon(bCirc);
        }
        if(port == 29) {
            checkBox4.setIcon(gCirc);
            Thread.sleep(2000);
            checkBox4.setIcon(bCirc);
        }
        if(port == 30) {
            checkBox6.setIcon(gCirc);
            Thread.sleep(2000);
            checkBox6.setIcon(bCirc);
        }
        if(port == 31){
            checkBox8.setIcon(gCirc);
            Thread.sleep(2000);
            checkBox8.setIcon(bCirc);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UI gui = new UI();
        Thread.sleep(3000);
        gui.setCurrentFloor2(5, 28);

    }

}


