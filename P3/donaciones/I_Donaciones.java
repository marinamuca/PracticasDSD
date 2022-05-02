import java.rmi.Remote;
import java.rmi.RemoteException;

public interface I_Donaciones extends Remote {
    int registrarCliente(String nombre) throws RemoteException;

    int depositarDonacion(String nombre, float valor) throws RemoteException;

    float totalDonado(String nombre) throws RemoteException;

}