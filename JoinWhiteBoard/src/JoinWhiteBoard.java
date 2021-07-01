/*** This is a Distributed Shared White Board client side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinWhiteBoard {
    private static IRemote remoteCall;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static int port;
    private static String serverAddress;
    private static String username;
    private static String[] functionButtonNames = {"Pencil","Text","Line","Circle","Oval","Rectangle"};
    private static String[] colorButtonNames = {"black","white","pink","orange","magenta",
            "lightGray","darkGray","cyan","blue","green","red","yellow", "purple","maroon","teal","olive"};
    private static Color purple  = new Color(128,0,128);
    private static Color maroon = new Color(128,0,0);
    private static Color teal = new Color(0,128,128);
    private static Color olive = new Color(128,128,0);

    public static void main(String[] args) {
        try {
            handleArgs(args);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        int rmi_port = port-1;
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress,rmi_port);
            remoteCall = (IRemote)registry.lookup("Get");
            System.out.println("Waitting for the approval of server/manager...");
            Boolean res = remoteCall.requestConnect(username);
            if(!res){
                System.out.println("Sorry! Manager did not approve, try it later!");
                System.exit(0);
            }
        }catch (RemoteException e1){
            System.out.println("Error: Something wrong with the RMI, please try another ip or serverAddress!");
            System.exit(0);
        }catch (NotBoundException e2){
            System.out.println("Error: Cannot find the RMI method, please it later!");
            System.exit(0);
        }

        try {
            clientSocket = new Socket(serverAddress, port);
        } catch (UnknownHostException e) {
            System.out.println("Error: Invalid hostname for server, please try other serverAddress or port.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Error: Connection refused, please try it later or try other serverAddress or port.");
            System.exit(0);
        }

        try {
            InputStream byteStreamIn = clientSocket.getInputStream();
            OutputStream byteStreamOut = clientSocket.getOutputStream();
            InputStreamReader characterStreamIn = new InputStreamReader(byteStreamIn, StandardCharsets.UTF_8);
            OutputStreamWriter characterStreamOut = new OutputStreamWriter(byteStreamOut, StandardCharsets.UTF_8);
            in = new BufferedReader(characterStreamIn);
            out = new BufferedWriter(characterStreamOut);
        } catch (IOException e) {
            System.out.println("Error: Can't get I/O streams, please try it later.");
            System.exit(0);
        }

        startGUI();
        ClientReceive receiveThread = new ClientReceive(in);
        receiveThread.start();
    }

    private static void startGUI(){
        ClientGUI.setGUI();
        ClientGUI.paintBoard.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                ClientGUI.coordinates[0]=e.getX();
                ClientGUI.coordinates[1]=e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                ClientGUI.coordinates[2]=e.getX();
                ClientGUI.coordinates[3]=e.getY();
                switch(ClientGUI.nowButton){
                    case "Line":
                        ClientGUI.graph.drawLine(ClientGUI.coordinates[0],ClientGUI.coordinates[1],ClientGUI.coordinates[2],ClientGUI.coordinates[3]);
                        sendMsg("Line");
                        break;
                    case "Oval":
                        int x1=Math.min(ClientGUI.coordinates[0],ClientGUI.coordinates[2]);
                        int y1=Math.min(ClientGUI.coordinates[1],ClientGUI.coordinates[3]);
                        int width=Math.abs(ClientGUI.coordinates[0]-ClientGUI.coordinates[2]);
                        int height=Math.abs(ClientGUI.coordinates[1]-ClientGUI.coordinates[3]);
                        ClientGUI.coordinates[0]=x1;
                        ClientGUI.coordinates[1]=y1;
                        ClientGUI.coordinates[2]=width;
                        ClientGUI.coordinates[3]=height;
                        ClientGUI.graph.drawOval(ClientGUI.coordinates[0],ClientGUI.coordinates[1],ClientGUI.coordinates[2],ClientGUI.coordinates[3]);
                        sendMsg("Oval");
                        break;
                    case "Rectangle":
                        x1=Math.min(ClientGUI.coordinates[0],ClientGUI.coordinates[2]);
                        y1=Math.min(ClientGUI.coordinates[1],ClientGUI.coordinates[3]);
                        width=Math.abs(ClientGUI.coordinates[0]-ClientGUI.coordinates[2]);
                        height=Math.abs(ClientGUI.coordinates[1]-ClientGUI.coordinates[3]);
                        ClientGUI.coordinates[0]=x1;
                        ClientGUI.coordinates[1]=y1;
                        ClientGUI.coordinates[2]=width;
                        ClientGUI.coordinates[3]=height;
                        ClientGUI.graph.drawRect(ClientGUI.coordinates[0],ClientGUI.coordinates[1],ClientGUI.coordinates[2],ClientGUI.coordinates[3]);
                        sendMsg("Rectangle");
                        break;
                    case "Circle":
                        x1=Math.min(ClientGUI.coordinates[0],ClientGUI.coordinates[2]);
                        y1=Math.min(ClientGUI.coordinates[1],ClientGUI.coordinates[3]);
                        width=Math.abs(ClientGUI.coordinates[0]-ClientGUI.coordinates[2]);
                        height=Math.abs(ClientGUI.coordinates[1]-ClientGUI.coordinates[3]);
                        ClientGUI.coordinates[0]=x1;
                        ClientGUI.coordinates[1]=y1;
                        ClientGUI.coordinates[2]=Math.max(width, height);
                        ClientGUI.coordinates[3]=Math.max(width, height);
                        ClientGUI.graph.drawArc(ClientGUI.coordinates[0],ClientGUI.coordinates[1],ClientGUI.coordinates[2],ClientGUI.coordinates[3],0,360);
                        sendMsg("Circle");
                        break;
                    case "Text":
                        if(ClientGUI.textInput.getText().trim().equals("")) break;
                        x1=Math.min(ClientGUI.coordinates[0],ClientGUI.coordinates[2]);
                        y1=Math.min(ClientGUI.coordinates[1],ClientGUI.coordinates[3]);
                        ClientGUI.coordinates[0]=x1;
                        ClientGUI.coordinates[1]=y1;
                        ClientGUI.graph.drawString(ClientGUI.textInput.getText(), x1, y1);
                        sendMsg("Text");
                        break;
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        ClientGUI.paintBoard.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(ClientGUI.nowButton.equals("Pencil")){
                    ClientGUI.coordinates[2]=ClientGUI.coordinates[0];
                    ClientGUI.coordinates[3]=ClientGUI.coordinates[1];
                    ClientGUI.coordinates[0]=e.getX();
                    ClientGUI.coordinates[1]=e.getY();
                    ClientGUI.graph.drawLine(ClientGUI.coordinates[0],ClientGUI.coordinates[1],ClientGUI.coordinates[2],ClientGUI.coordinates[3]);
                    sendMsg("Pencil");
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        for(int i=0; i<functionButtonNames.length; i++){
            ClientGUI.functionButton[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ClientGUI.nowButton = e.getActionCommand();
                    if(ClientGUI.nowButton.equals("Text")){
                        ClientGUI.textInput.setVisible(true);
                        ClientGUI.textInput.setText("Please Input Text!");
                    }
                    else {
                        ClientGUI.textInput.setVisible(false);
                        ClientGUI.textInput.setText("");
                    }
                }
            });
        }
        for(int i=0; i<colorButtonNames.length; i++){
            ClientGUI.colorButton[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = e.getActionCommand();
                    switch (name){
                        case"black":
                            ClientGUI.graph.setColor(Color.black);
                            ClientGUI.nowColor = "black";
                            break;
                        case"white":
                            ClientGUI.graph.setColor(Color.white);
                            ClientGUI.nowColor = "white";
                            break;
                        case"pink":
                            ClientGUI.graph.setColor(Color.pink);
                            ClientGUI.nowColor = "pink";
                            break;
                        case"orange":
                            ClientGUI.graph.setColor(Color.orange);
                            ClientGUI.nowColor = "orange";
                            break;
                        case"magenta":
                            ClientGUI.graph.setColor(Color.magenta);
                            ClientGUI.nowColor = "magenta";
                            break;
                        case"lightGray":
                            ClientGUI.graph.setColor(Color.lightGray);
                            ClientGUI.nowColor = "lightGray";
                            break;
                        case"darkGray":
                            ClientGUI.graph.setColor(Color.darkGray);
                            ClientGUI.nowColor = "darkGray";
                            break;
                        case"cyan":
                            ClientGUI.graph.setColor(Color.cyan);
                            ClientGUI.nowColor = "cyan";
                            break;
                        case"blue":
                            ClientGUI.graph.setColor(Color.blue);
                            ClientGUI.nowColor = "blue";
                            break;
                        case"green":
                            ClientGUI.graph.setColor(Color.green);
                            ClientGUI.nowColor = "green";
                            break;
                        case"red":
                            ClientGUI.graph.setColor(Color.red);
                            ClientGUI.nowColor = "red";
                            break;
                        case"yellow":
                            ClientGUI.graph.setColor(Color.yellow);
                            ClientGUI.nowColor = "yellow";
                            break;
                        case"purple":
                            ClientGUI.graph.setColor(purple);
                            ClientGUI.nowColor = "purple";
                            break;
                        case"maroon":
                            ClientGUI.graph.setColor(maroon);
                            ClientGUI.nowColor = "maroon";
                            break;
                        case"teal":
                            ClientGUI.graph.setColor(teal);
                            ClientGUI.nowColor = "teal";
                            break;
                        case"olive":
                            ClientGUI.graph.setColor(olive);
                            ClientGUI.nowColor = "olive";
                            break;
                    }
                }
            });
        }
        ClientGUI.clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientGUI.inputArea.setText("");
            }
        });
        ClientGUI.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = ClientGUI.inputArea.getText().replaceAll("\n", "").trim();
                if (!text.equals("")) {
                    sendMsg("Message");
                    ClientGUI.inputArea.setText("");
                }
            }
        });
    }

    private static void handleArgs(String[] args) throws Exception{
        if (args.length != 3){
            throw new Exception("Error: Wrong number of arguments, need three arguments Port IP address of server, Port number and Username.");
        }
        try {
            serverAddress = args[0];
            port = Integer.parseInt(args[1]);
            username = args[2];
        } catch (Exception e){
            throw new Exception("Error: Wrong type of arguments, need IP address of server(String), Port number(Integer) and Username(String).");
        }
        if (port < 1024 || port > 65535){
            throw new Exception("Error: Wrong value of Port number, need Port number(Integer) to be between 1024 and 65535.");
        }
    }

    private static void sendMsg(String operation){
        String msg = "";
        if(operation.equals("Message")){
            msg = operation + "&" + username + "&" +ClientGUI.inputArea.getText().replaceAll("\n", "");
        }
        else if(operation.equals("Text")){
            msg = operation + "&" + ClientGUI.nowColor + "&" + ClientGUI.coordinates[0]
                    + "&" + ClientGUI.coordinates[1] + "&" + ClientGUI.textInput.getText()  + "&" + username;
        }
        else {
            msg = operation + "&" + ClientGUI.nowColor + "&" + ClientGUI.coordinates[0]
                    + "&" + ClientGUI.coordinates[1] + "&" + ClientGUI.coordinates[2] + "&" + ClientGUI.coordinates[3] + "&" + username;
        }
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            System.out.println( "Error: Can't get I/O streams, please try it later.");
            System.exit(0);
        }
    }

}
