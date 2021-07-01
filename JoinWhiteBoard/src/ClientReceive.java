/*** This is a Distributed Shared White Board client side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ClientReceive extends Thread {
    private static Color purple  = new Color(128,0,128);
    private static Color maroon = new Color(128,0,0);
    private static Color teal = new Color(0,128,128);
    private static Color olive = new Color(128,128,0);
    private BufferedReader input;

    public ClientReceive (BufferedReader input){
        this.input=input;
    }

    public void run(){
        try {
            String msg;
            while((msg = input.readLine()) != null){
                //System.out.println(msg);
                String[] splitMsg = msg.split("&");

                if(splitMsg[0].equals("Close")){
                    JOptionPane.showMessageDialog(null, "Sorry! Manager has kicked you out, please try it later!");
                    System.exit(0);
                }

                if(splitMsg[0].equals("Pencil")) sleep(15);
                else sleep(200);

                if (splitMsg[0].equals("Message")) showMsg(msg);
                else if (splitMsg[0].equals("New")){
                    ClientGUI.paintBoard.repaint();
                    JOptionPane.showMessageDialog(null, "Manager has created a new canvas.");
                } else if(splitMsg[0].equals("Open")){
                    ClientGUI.paintBoard.repaint();
                    JOptionPane.showMessageDialog(null, "Manager has opened a canvas.");
                } else showPaint(msg);
            }
            JOptionPane.showMessageDialog(null, "Sorry! Server has been closed, please try it later!");
            System.exit(0);
        } catch (Exception e){
            System.out.println("Error: Something wrong with the connection!");
            System.exit(0);
        }
    }

    private void showMsg(String msg){
        String[] splitMsg = msg.split("&");
        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String userid = splitMsg[3];
        String time = dataformat.format(new Date());
        String title = "User " + splitMsg[1] + " (ID: " + userid +") said:";
        String text = splitMsg[2];
        ClientGUI.showMessage(time);
        ClientGUI.showMessage(title);
        ClientGUI.showMessage(text);
        ClientGUI.showMessage("");
    }

    private void showPaint(String msg){
        String[] splitMsg = msg.split("&");
        String type = splitMsg[0];
        String color = splitMsg[1];
        switch (color){
            case"black":
                ClientGUI.graph.setColor(Color.black);
                break;
            case"white":
                ClientGUI.graph.setColor(Color.white);
                break;
            case"pink":
                ClientGUI.graph.setColor(Color.pink);
                break;
            case"orange":
                ClientGUI.graph.setColor(Color.orange);
                break;
            case"magenta":
                ClientGUI.graph.setColor(Color.magenta);
                break;
            case"lightGray":
                ClientGUI.graph.setColor(Color.lightGray);
                break;
            case"darkGray":
                ClientGUI.graph.setColor(Color.darkGray);
                break;
            case"cyan":
                ClientGUI.graph.setColor(Color.cyan);
                break;
            case"blue":
                ClientGUI.graph.setColor(Color.blue);
                break;
            case"green":
                ClientGUI.graph.setColor(Color.green);
                break;
            case"red":
                ClientGUI.graph.setColor(Color.red);
                break;
            case"yellow":
                ClientGUI.graph.setColor(Color.yellow);
                break;
            case"purple":
                ClientGUI.graph.setColor(purple);
                break;
            case"maroon":
                ClientGUI.graph.setColor(maroon);
                break;
            case"teal":
                ClientGUI.graph.setColor(teal);
                break;
            case"olive":
                ClientGUI.graph.setColor(olive);
                break;
        }
        if(splitMsg[0].equals("Text")){
            int x = Integer.parseInt(splitMsg[2]);
            int y = Integer.parseInt(splitMsg[3]);
            String text = splitMsg[4];
            ClientGUI.graph.drawString(text, x, y);
        } else {
            int x1 = Integer.parseInt(splitMsg[2]);
            int y1 = Integer.parseInt(splitMsg[3]);
            int x2 = Integer.parseInt(splitMsg[4]);
            int y2 = Integer.parseInt(splitMsg[5]);
            switch (type){
                case "Line":
                    ClientGUI.graph.drawLine(x1, y1, x2, y2);
                    break;
                case "Circle":
                    ClientGUI.graph.drawArc(x1, y1, x2, y2, 0,360);
                    break;
                case "Oval":
                    ClientGUI.graph.drawOval(x1, y1, x2, y2);
                    break;
                case "Rectangle":
                    ClientGUI.graph.drawRect(x1, y1, x2, y2);
                    break;
                case "Pencil":
                    ClientGUI.graph.drawLine(x1, y1, x2, y2);
                    break;
            }
        }
        switch (ClientGUI.nowColor){
            case"black":
                ClientGUI.graph.setColor(Color.black);
                break;
            case"white":
                ClientGUI.graph.setColor(Color.white);
                break;
            case"pink":
                ClientGUI.graph.setColor(Color.pink);
                break;
            case"orange":
                ClientGUI.graph.setColor(Color.orange);
                break;
            case"magenta":
                ClientGUI.graph.setColor(Color.magenta);
                break;
            case"lightGray":
                ClientGUI.graph.setColor(Color.lightGray);
                break;
            case"darkGray":
                ClientGUI.graph.setColor(Color.darkGray);
                break;
            case"cyan":
                ClientGUI.graph.setColor(Color.cyan);
                break;
            case"blue":
                ClientGUI.graph.setColor(Color.blue);
                break;
            case"green":
                ClientGUI.graph.setColor(Color.green);
                break;
            case"red":
                ClientGUI.graph.setColor(Color.red);
                break;
            case"yellow":
                ClientGUI.graph.setColor(Color.yellow);
                break;
            case"purple":
                ClientGUI.graph.setColor(purple);
                break;
            case"maroon":
                ClientGUI.graph.setColor(maroon);
                break;
            case"teal":
                ClientGUI.graph.setColor(teal);
                break;
            case"olive":
                ClientGUI.graph.setColor(olive);
                break;
        }
    }
}
