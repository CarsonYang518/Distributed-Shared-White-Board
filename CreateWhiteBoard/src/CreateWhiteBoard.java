/*** This is a Distributed Shared White Board server side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import static java.lang.Thread.sleep;

public class CreateWhiteBoard {
    private static int port;
    private static String username;
    public static Vector<Socket> sockets = new Vector<>();
    private static Vector<BufferedReader> inputs = new Vector<>();
    private static Vector<BufferedWriter> outputs = new Vector<>();
    public static ConcurrentHashMap<Integer, String> idNameMap = new ConcurrentHashMap<>();
    public static Vector<String> history = new Vector<>();
    public static Vector<String> openHistory = new Vector<>();
    public static HashSet<Integer> hasClosed = new HashSet<>();
    private static Color purple  = new Color(128,0,128);
    private static Color maroon = new Color(128,0,0);
    private static Color teal = new Color(0,128,128);
    private static Color olive = new Color(128,128,0);

    public static void main(String[] args) {
        try {
            handleArgs(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        int rmi_port = port-1;
        try {
            IRemote remoteCall = new RemoteCall();
            Registry registry = LocateRegistry.createRegistry(rmi_port);
            registry.bind("Get", remoteCall);
//            System.out.println("RMI registry  is ready!");

        } catch (RemoteException e){
            System.out.println("Error: Something wrong with the rmi, please try it later!");
            System.exit(0);
        } catch (AlreadyBoundException e2){
            System.out.println("Error: RMI Port number has been used, need to change another one!");
            System.exit(0);
        }

        startGUI();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running!");
            System.out.println(username + " is the manager!");
            while(true){
                Socket clientSocket = serverSocket.accept();
                sockets.add(clientSocket);
                // System.out.println("client "+ (sockets.size()-1) +" is connected!");
                ServerGUI.showUserList(idNameMap);
                InputStream byteStreamIn = clientSocket.getInputStream();
                OutputStream byteStreamOut = clientSocket.getOutputStream();
                InputStreamReader characterStreamIn = new InputStreamReader(byteStreamIn, StandardCharsets.UTF_8);
                OutputStreamWriter characterStreamOut = new OutputStreamWriter(byteStreamOut, StandardCharsets.UTF_8);
                BufferedReader in = new BufferedReader(characterStreamIn);
                BufferedWriter out = new BufferedWriter(characterStreamOut);
                inputs.add(in);
                outputs.add(out);
                MessageThread thread = new MessageThread(sockets.size()-1, in, outputs);
                thread.start();
            }
        } catch (BindException e){
            System.out.println("Error: Port number has been used, need to change another one");
            System.exit(0);
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void handleArgs(String[] args) throws Exception{
        if (args.length != 2) throw new Exception("Error: Wrong number of arguments, need two arguments Port number and Username.");
        try {
            port = Integer.parseInt(args[0]);
            username = args[1];
        } catch (Exception e) {
            throw new Exception("Error: Wrong type of arguments, need Port number to be Integer and Username to be String.");
        }
        if (port < 1024 || port > 65535) throw new Exception("Error: Wrong value of Port number, need Port number to be between 1024 and 65535.");
    }

    private static void startGUI(){
        ServerGUI.setGUI();
        ServerGUI.yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = ServerGUI.kickOutInput.getText().trim();
                if(!text.equals("")){
                    for(Character c:text.toCharArray()){
                        if(!Character.isDigit(c)) {
                            JOptionPane.showMessageDialog(null, "Sorry! Invalid input, please input user ID!");
                            return;
                        }
                    }
                    int uid = Integer.parseInt(text);
                    if(!idNameMap.containsKey(uid))
                        JOptionPane.showMessageDialog(null, "Sorry! User does not exists!");
                    else{
                        try {
                            BufferedWriter output = outputs.get(uid);
                            output.write("Close"+ "\n");
                            output.flush();
                            JOptionPane.showMessageDialog(null, "Success kicking out!");
                            ServerGUI.kickOutInput.setText("");
                        } catch (IOException e2){
                            System.out.println("Error: Something wrong with the connection!");
                            System.exit(0);
                        }
                    }
                }
            }
        });
        ServerGUI.close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Server has been closed!");
                System.exit(0);
            }
        });
        ServerGUI.newOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    history.clear();
                    ServerGUI.paintBoard.repaint();
                    for (int i=0; i<outputs.size(); i++){
                        if(hasClosed.contains(i)) continue;
                        outputs.get(i).write("New"+ "\n");
                        outputs.get(i).flush();
                    }
                } catch (IOException e2) {
                    System.out.println("Error: Something wrong with the connection!");
                    System.exit(0);
                }
            }
        });
        ServerGUI.save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = new File("default_save_file");
                    if(!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutput = new FileOutputStream("default_save_file");
                    ObjectOutputStream objOutput = new ObjectOutputStream(fileOutput);
                    objOutput.writeObject(history);
                    objOutput.close();
                    fileOutput.close();
                    JOptionPane.showMessageDialog(null, "Success saving default_save_file");
                }
                catch (IOException e2) {
                    System.out.println(e2.getMessage());
                }
            }
        });
        ServerGUI.open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String filename = JOptionPane.showInputDialog("Please input the opening file name:");
                    if (filename == null) return;
                    FileInputStream fileInput = new FileInputStream(filename);
                    ObjectInputStream objInput = new ObjectInputStream(fileInput);
                    openHistory = (Vector<String>) objInput.readObject();
                    fileInput.close();
                    objInput.close();
                    try {
                        history.clear();
                        ServerGUI.graph.setColor(Color.white);
                        ServerGUI.graph.fillRect(0,0,450,485);
                        for (String msg:openHistory) {
                            try {
                                String[] splitMsg = msg.split("&");
                                String type = splitMsg[0];
                                String color = splitMsg[1];
                                if(type.equals("Pencil")) sleep(5);
                                else sleep(100);
                                switch (color){
                                    case"black":
                                        ServerGUI.graph.setColor(Color.black);
                                        break;
                                    case"white":
                                        ServerGUI.graph.setColor(Color.white);
                                        break;
                                    case"pink":
                                        ServerGUI.graph.setColor(Color.pink);
                                        break;
                                    case"orange":
                                        ServerGUI.graph.setColor(Color.orange);
                                        break;
                                    case"magenta":
                                        ServerGUI.graph.setColor(Color.magenta);
                                        break;
                                    case"lightGray":
                                        ServerGUI.graph.setColor(Color.lightGray);
                                        break;
                                    case"darkGray":
                                        ServerGUI.graph.setColor(Color.darkGray);
                                        break;
                                    case"cyan":
                                        ServerGUI.graph.setColor(Color.cyan);
                                        break;
                                    case"blue":
                                        ServerGUI.graph.setColor(Color.blue);
                                        break;
                                    case"green":
                                        ServerGUI.graph.setColor(Color.green);
                                        break;
                                    case"red":
                                        ServerGUI.graph.setColor(Color.red);
                                        break;
                                    case"yellow":
                                        ServerGUI.graph.setColor(Color.yellow);
                                        break;
                                    case"purple":
                                        ServerGUI.graph.setColor(purple);
                                        break;
                                    case"maroon":
                                        ServerGUI.graph.setColor(maroon);
                                        break;
                                    case"teal":
                                        ServerGUI.graph.setColor(teal);
                                        break;
                                    case"olive":
                                        ServerGUI.graph.setColor(olive);
                                        break;
                                }
                                if(splitMsg[0].equals("Text")){
                                    int x = Integer.parseInt(splitMsg[2]);
                                    int y = Integer.parseInt(splitMsg[3]);
                                    String text = splitMsg[4];
                                    ServerGUI.graph.drawString(text, x, y);
                                } else {
                                    int x1 = Integer.parseInt(splitMsg[2]);
                                    int y1 = Integer.parseInt(splitMsg[3]);
                                    int x2 = Integer.parseInt(splitMsg[4]);
                                    int y2 = Integer.parseInt(splitMsg[5]);
                                    switch (type){
                                        case "Line":
                                            ServerGUI.graph.drawLine(x1, y1, x2, y2);
                                            break;
                                        case "Circle":
                                            ServerGUI.graph.drawArc(x1, y1, x2, y2, 0,360);
                                            break;
                                        case "Oval":
                                            ServerGUI.graph.drawOval(x1, y1, x2, y2);
                                            break;
                                        case "Rectangle":
                                            ServerGUI.graph.drawRect(x1, y1, x2, y2);
                                            break;
                                        case "Pencil":
                                            ServerGUI.graph.drawLine(x1, y1, x2, y2);
                                            break;
                                    }
                                }
                            } catch (InterruptedException e2){
                                System.out.println("Error: Something wrong with opening file!");
                            }
                        }

                        for (int i=0; i<outputs.size(); i++) {
                            if (hasClosed.contains(i)) continue;
                            outputs.get(i).write("Open" + "\n");
                            outputs.get(i).flush();
                        }

                        for (int i=0; i<outputs.size(); i++){
                            if(hasClosed.contains(i)) continue;
                            for (String msg:openHistory) {
                                outputs.get(i).write(msg + "\n");
                                outputs.get(i).flush();
                            }
                        }

                        history = openHistory;

                    } catch (IOException e2){
                        System.out.println("Error: Something wrong with the connection!");
                        System.exit(0);
                    }
                } catch (IOException | ClassNotFoundException e2) {
                    JOptionPane.showMessageDialog(null, "Can not find the file, please check it again!");
                }
            }
        });
        ServerGUI.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = ServerGUI.inputArea.getText().replaceAll("\n", "").trim();
                if (!text.equals("")) {
                    String msg = "Message" + "&" + username + "&" + text + "&" + "Manager";
                    try {
                        for (int i=0; i<outputs.size(); i++){
                            if(hasClosed.contains(i)) continue;
                            outputs.get(i).write(msg + "\n");
                            outputs.get(i).flush();
                        }
                    } catch (IOException e2) {
                        System.out.println("Error: Something wrong with the connection!");
                        System.exit(0);
                    }
                    ServerGUI.inputArea.setText("");
                    SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = dataformat.format(new Date());
                    String title = "User " + username + " (ID: Manager) said:";
                    ServerGUI.showMessage(time);
                    ServerGUI.showMessage(title);
                    ServerGUI.showMessage(text);
                    ServerGUI.showMessage("");
                }
            }
        });
        ServerGUI.saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = JOptionPane.showInputDialog("Please input the saving file name:");
                if (filename == null) return;
                try {
                    File file = new File(filename);
                    if(!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutput = new FileOutputStream(filename);
                    ObjectOutputStream objOutput = new ObjectOutputStream(fileOutput);
                    objOutput.writeObject(history);
                    objOutput.close();
                    fileOutput.close();
                    JOptionPane.showMessageDialog(null, "Success saving " + filename);
                }
                catch (IOException e2) {
                    JOptionPane.showMessageDialog(null, e2.getMessage());

                }

            }
        });
    }
}
