/*** This is a Distributed Shared White Board server side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerGUI {
    public static JFrame frame;
    private static JPanel panel;
    public static JPanel paintBoard;
    public static JButton yesButton;
    public static JTextField kickOutInput;
    public static JTextArea outputArea;
    public static JTextArea inputArea;
    public static JTextArea userListArea;
    public static Graphics graph;
    private static JLabel kickOutLabel;
    private static JMenuBar menuBar;
    private static JMenu options;
    public static JMenuItem newOne;
    public static JMenuItem close;
    public static JMenuItem open;
    public static JMenuItem save;
    public static JMenuItem saveAs;
    public static JButton sendButton;

    public static void setGUI() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        frame = new JFrame("White Board Server");
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        frame.add(panel);
        placeTextComponents(panel);
        frame.setVisible(true);
        graph = paintBoard.getGraphics();
    }

    private static void placeTextComponents(JPanel panel){
        panel.setLayout(null);

        paintBoard = new JPanel();
        paintBoard.setBounds(25,50, 450, 485);
        paintBoard.setBackground(Color.white);

        outputArea = new JTextArea();
        outputArea.append("Chatting area:\n");
        JScrollPane scroll_outputArea=new JScrollPane(outputArea);
        scroll_outputArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_outputArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_outputArea.setBounds(500,50,275, 200);

        inputArea = new JTextArea();
        JScrollPane scroll_inputArea = new JScrollPane(inputArea);
        scroll_inputArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_inputArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_inputArea.setBounds(500,260,275, 100);

        userListArea = new JTextArea();
        userListArea.append("List of users (ID + username) :\n");
        JScrollPane scroll_userListArea=new JScrollPane(userListArea);
        scroll_userListArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_userListArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_userListArea.setBounds(500,390,275, 100);

        kickOutLabel = new JLabel("  Input the ID of user who you want to kick out: ");
        kickOutLabel.setBounds(500,490,275,25);
        kickOutInput = new JTextField();
        kickOutInput.setBounds(500, 515, 275, 25);

        yesButton = new JButton("Yes");
        yesButton.setBounds(580,540,100,25);

        sendButton = new JButton("Send");
        sendButton.setBounds(580,360,100,25);

        menuBar = new JMenuBar();
        menuBar.setBounds(0,0,800,25);
        options = new JMenu("Options");
        open = new JMenuItem("open");
        save = new JMenuItem("save");
        saveAs = new JMenuItem("saveAs");
        newOne = new JMenuItem("new");
        close = new JMenuItem("close");

        menuBar.add(options);
        options.add(newOne);
        options.add(open);
        options.add(save);
        options.add(saveAs);
        options.add(close);

        panel.add(sendButton);
        panel.add(scroll_inputArea);
        panel.add(paintBoard);
        panel.add(scroll_outputArea);
        panel.add(scroll_userListArea);
        panel.add(kickOutInput);
        panel.add(kickOutLabel);
        panel.add(yesButton);
        panel.add(menuBar);
    }

    public static void showMessage(String msg) {
        outputArea.append(msg + '\n');
    }

    public static void showUserList(ConcurrentHashMap<Integer, String> idNameMap){
        userListArea.setText("List of users (ID + username) :\n");
        for(Integer id:idNameMap.keySet()){
            userListArea.append("ID: " + id +"   Username: " + idNameMap.get(id) + "\n");
        }
    }
}
