/*** This is a Distributed Shared White Board client side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import javax.swing.*;
import java.awt.*;
public class ClientGUI {
    public static JFrame frame;
    private static JPanel panel;
    private static JPanel buttonPanel;
    private static JPanel colorPanel;
    public static JPanel paintBoard;
    public static JButton[] functionButton;
    public static JButton[] colorButton;
    public static JTextArea outputArea;
    public static JTextArea inputArea;
    public static JButton sendButton;
    public static JButton clearButton;
    public static Graphics graph;
    public static String nowButton = "Pencil";
    public static String nowColor = "black";
    public static int[] coordinates = new int[4];
    public static JTextField textInput;

    public static void setGUI() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        frame = new JFrame("White Board Client");
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        frame.add(panel);
        placeTextComponents(panel);
        frame.setVisible(true);
        graph = paintBoard.getGraphics();
    }

    private static void placeTextComponents(JPanel panel) {
        panel.setLayout(null);

        textInput = new JTextField();
        textInput.setBounds(500,10,275,25);
        textInput.setVisible(false);

        inputArea = new JTextArea();
        JScrollPane scroll_inputArea = new JScrollPane(inputArea);
        scroll_inputArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_inputArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_inputArea.setBounds(500,425,275, 100);

        outputArea = new JTextArea();
        outputArea.append("Welcome to use the white board!\n");
        JScrollPane scroll_outputArea=new JScrollPane(outputArea);
        scroll_outputArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_outputArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_outputArea.setBounds(500,40,275, 375);

        sendButton = new JButton("Send");
        clearButton = new JButton("Clear");
        sendButton.setBounds(500,540, 120,20);
        clearButton.setBounds(655, 540, 120, 20);

        paintBoard = new JPanel();
        paintBoard.setBounds(25,40, 450, 485);
        paintBoard.setBackground(Color.white);

        buttonPanel = new JPanel();
        buttonPanel.setBounds(50,0,400,40);
        String[] functionButtonNames = {"Pencil","Text","Line","Circle","Oval","Rectangle"};
        functionButton = new JButton[functionButtonNames.length];
        for(int i=0; i<functionButtonNames.length; i++){
            functionButton[i] = new JButton(functionButtonNames[i]);
            buttonPanel.add(functionButton[i]);
        }

        colorPanel = new JPanel();
        colorPanel.setBounds(100,525,275,100);
        Color purple  = new Color(128,0,128);
        Color maroon = new Color(128,0,0);
        Color teal = new Color(0,128,128);
        Color olive = new Color(128,128,0);
        Color[] colors = {Color.black, Color.white, Color.pink, Color.orange, Color.magenta, Color.lightGray,
                Color.darkGray, Color.cyan, Color.blue, Color.green, Color.red, Color.yellow, purple, maroon, teal, olive};
        String[] colorButtonNames = {"black","white","pink","orange","magenta",
                "lightGray","darkGray","cyan","blue","green","red","yellow", "purple","maroon","teal","olive"};
        colorButton =new JButton[colorButtonNames.length];
        for(int i=0; i<colorButtonNames.length; i++){
            colorButton[i]=new JButton();
            colorButton[i].setActionCommand(colorButtonNames[i]);
            colorButton[i].setBackground(colors[i]);
            colorPanel.add(colorButton[i]);
        }

        panel.add(textInput);
        panel.add(buttonPanel);
        panel.add(sendButton);
        panel.add(clearButton);
        panel.add(colorPanel);
        panel.add(paintBoard);
        panel.add(scroll_inputArea);
        panel.add(scroll_outputArea);
    }

    public static void showMessage(String msg) {
        outputArea.append(msg + '\n');
    }
}
