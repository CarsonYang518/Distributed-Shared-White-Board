/*** This is a Distributed Shared White Board server side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.*;

public class RemoteCall extends UnicastRemoteObject implements IRemote{
    protected RemoteCall() throws RemoteException {

        //System.out.println("Trying to start the rmi server!");
    }
    public Boolean requestConnect(String name) throws RemoteException {
        int option = JOptionPane.showConfirmDialog(null,  "Do you approve User: " + name + " to join?", "Request to join",JOptionPane.YES_NO_OPTION);
        if (option == 0){
            CreateWhiteBoard.idNameMap.put(CreateWhiteBoard.sockets.size(),name);
            return true;
        }
        return false;
    }
}
