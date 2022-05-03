import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface I_Replica extends Remote {
    // Se regista un cliente en la replica
    void registrar(String nombre) throws RemoteException;

    // Registra la donacion de un cliente a la replica.
    void donar(String nombre, float valor) throws RemoteException;

    //Devuelve el total donado local de la replica.
    float getDonado() throws RemoteException;

    // Calcula el total de donaciones de todas las replicas.
    public float calcularTotal() throws RemoteException;

    //Devuelve el numero de clientes están registrados en la réplica
    int getNumClientes() throws RemoteException;

    /** 
    * Indica si el cliente correspondiente con el nombre de usuario pasado 
    * como parámetro está registrado en la replica.
    */
    public boolean clienteRegistrado(String nombre) throws RemoteException;

    /** 
    * Indica si el cliente correspondiente con el nombre de usuario pasado 
    * como parámetro ha donado en la replica.
    */
    public boolean clienteHaDonado(String nombre) throws RemoteException;

    // Devuelve el ID de la replica.
    int getID() throws RemoteException;

    // Devuelve el array de clientes de la replica
    public ArrayList<String> getClientes() throws RemoteException;

    // Devuelve si la replica tiene o no el token
    boolean tieneToken() throws RemoteException;

    // Cambia el valor del toke por el pasado como parámetro.
    void setToken(boolean token) throws RemoteException;

    /** 
    * Invocado por la replica anterior para pedir el token.  
    * Si la replica que recibe la invoación tiene el token y no esta 
    * ejecutando ningun metodo invocado por un cliente, le da el token 
    * a su siguiente replica. Si no lo tiene, le pide el token a la anterior.
    */
    void recibirToken() throws RemoteException;

    // Modifica el valor de la replica anterior.
    public void setAnteriorReplica(I_Replica anterior) throws RemoteException;

}