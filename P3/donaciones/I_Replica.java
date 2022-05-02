import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface I_Replica extends Remote {
    void registrar(String nombre) throws RemoteException;
    void donar(float valor) throws RemoteException;
    float getDonado() throws RemoteException;
    int getNumClientes() throws RemoteException;
    public boolean clienteRegistrado(String nombre) throws RemoteException;
    public I_Replica replicaRegistrado(String nombre) throws RemoteException;
    int getID() throws RemoteException;
    public ArrayList<String> getClientes() throws RemoteException;
}