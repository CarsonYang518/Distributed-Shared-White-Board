/*** This is a Distributed Shared White Board server side for COMP90015 2021 S1 Assignment2
 * @author Kaixun Yang, a student of Unimelb (Master of Information Technology)
 * @version 22/05/2021
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemote extends Remote{
    public Boolean requestConnect(String name) throws RemoteException;
}
