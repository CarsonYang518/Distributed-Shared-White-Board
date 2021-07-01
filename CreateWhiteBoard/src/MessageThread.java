/*** This is a Distributed Shared White Board server side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MessageThread extends Thread{
    private BufferedReader input;
    private Vector<BufferedWriter> outputs;
    private int socketNumber = 0;
    private static Color purple  = new Color(128,0,128);
    private static Color maroon = new Color(128,0,0);
    private static Color teal = new Color(0,128,128);
    private static Color olive = new Color(128,128,0);

    public MessageThread (int socketNumber, BufferedReader input, Vector<BufferedWriter> outputs){
        this.input=input;
        this.outputs=outputs;
        this.socketNumber=socketNumber;
    }

    public void run(){
        try {
            for(int i=0; i < CreateWhiteBoard.history.size(); i++){
                outputs.get(socketNumber).write(CreateWhiteBoard.history.get(i)+ "\n");
                outputs.get(socketNumber).flush();
            }
            String msg;
            while((msg = input.readLine()) != null){
                //System.out.println(msg);
                String[] splitMsg = msg.split("&");
                if (splitMsg[0].equals("Message")) propogateMessage(msg);
                else {
                    propogateDraw(msg);
                    CreateWhiteBoard.history.add(msg);
                }
            }
            CreateWhiteBoard.hasClosed.add(socketNumber);
            //System.out.println("client "+ socketNumber +" is disconnected!");
            CreateWhiteBoard.idNameMap.remove(socketNumber);
            ServerGUI.showUserList(CreateWhiteBoard.idNameMap);
        }catch (Exception e){
            System.out.println("Error: Something wrong with the connection!");
            System.exit(0);
        }
    }

    private synchronized void propogateDraw(String msg){
        showPaint(msg);
        try {
            for(int i=0; i < outputs.size(); i++){
                if(i == socketNumber || CreateWhiteBoard.hasClosed.contains(i)) continue;
                outputs.get(i).write(msg+ "\n");
                outputs.get(i).flush();
            }
        } catch (IOException e) {
            System.out.println("Error: Something wrong with the connection!");
            System.exit(0);
        }
    }

    private synchronized void propogateMessage(String msg){
        msg = msg + "&" + socketNumber;
        showMsg(msg);
        try {
            for(int i=0; i < outputs.size(); i++){
                if(CreateWhiteBoard.hasClosed.contains(i)) continue;
                outputs.get(i).write(msg+ "\n");
                outputs.get(i).flush();
            }
        } catch (IOException e) {
            System.out.println("Error: Something wrong with the connection!");
            System.exit(0);
        }
    }

    private void showMsg(String msg){
        String[] splitMsg = msg.split("&");
        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int userid = Integer.parseInt(splitMsg[3]);
        String time = dataformat.format(new Date());
        String title = "User " + splitMsg[1] + " (ID: " + userid +") said:";
        String text = splitMsg[2];
        ServerGUI.showMessage(time);
        ServerGUI.showMessage(title);
        ServerGUI.showMessage(text);
        ServerGUI.showMessage("");
    }

    private void showPaint(String msg){
        String[] splitMsg = msg.split("&");
        String type = splitMsg[0];
        String color = splitMsg[1];
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
    }
}
