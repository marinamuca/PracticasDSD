import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.swing.text.html.HTMLDocument.RunElement;

public class Donaciones extends UnicastRemoteObject implements I_Donaciones, I_Replica {
    private final String HOST;
    private final int PORT;
    private final int ID;
    private Registry regReplica;

    private int idReplicaAnterior;
    ArrayList<I_Replica> otrasReplicas;
    ArrayList<String> clientes;
    private float totalLocal;


    public Donaciones(String host, int port, int idReplica) throws RemoteException {
        HOST = host;
        PORT = port;
        ID = idReplica;
        this.regReplica = LocateRegistry.getRegistry(HOST, PORT);

        clientes = new ArrayList<>();
        otrasReplicas = new ArrayList<>();
        totalLocal = 0;
    }

    public void setReplica (int id) throws AccessException, RemoteException, NotBoundException{

        I_Replica replica = (I_Replica) regReplica.lookup("Replica" + id);
        otrasReplicas.add(replica);
        
    }

    public int getID() throws RemoteException{return ID;}
    public ArrayList<String> getClientes() throws RemoteException{return clientes;}

   
    // INTERFAZ DONACIONES (Cliente-Servidor)
    @Override
    public int registrarCliente(String nombre) throws RemoteException {

        if (replicaRegistrado(nombre) != null)
            throw new RemoteException("El Cliente ya está registrado");

        I_Replica replicaRegistro = this;
        for (I_Replica otraReplica : otrasReplicas) {
            if (replicaRegistro.getNumClientes() > otraReplica.getNumClientes())
            replicaRegistro = otraReplica;
        }
        

        replicaRegistro.registrar(nombre);
        System.out.println("Registro llamado desde REPLICA " + getID());
        System.out.println("REPLICA " + replicaRegistro.getID() + ": Se regista cliente " + nombre);
        System.out.println("REPLICA " + replicaRegistro.getID() + " Se han registrado " + replicaRegistro.getNumClientes() + " clientes");
        
        return replicaRegistro.getID();
    }

    @Override
    public int depositarDonacion(String nombre, float valor) throws RemoteException {

        I_Replica replica =  replicaRegistrado(nombre);
        if (replica == null)
            throw new RemoteException("Cliente no registrado");
        
        if(valor >= 0 ){
            replica.donar(valor);
            System.out.println("REPLICA " + replica.getID() +  " Cliente " + nombre + " deposita " + valor + "€");
        } else
            throw new RemoteException("No se puede donar una cantidad negativa");

        return replica.getID();
    }

    @Override
    public float totalDonado(String nombre) throws RemoteException {

        if (replicaRegistrado(nombre) == null)
            throw new RemoteException("Cliente no registrado");

        System.out.println("REPLICA " + getID() +  " Devuelvo total donado a cliente " + nombre);

        return totalLocal + totalRestoReplicas();
    }

    public float totalRestoReplicas() throws RemoteException{
        float total = 0;
        for (I_Replica otraReplica : otrasReplicas) {
            total += otraReplica.getDonado();
        }

        return total;
    } 

    //INTERFAZ REPLICA (Servidor-Servidor)
    @Override
    public void registrar(String nombre) throws RemoteException{
       clientes.add(nombre);
    }

    @Override
    public void donar(float valor) throws RemoteException {
       totalLocal += valor;
    }

    @Override
    public float getDonado() throws RemoteException{
        return totalLocal;
    }

    @Override
    public int getNumClientes() throws RemoteException {
        return clientes.size();
    }

    @Override
    public boolean clienteRegistrado(String nombre) throws RemoteException{
        return clientes.contains(nombre);
    }

    @Override
    public I_Replica replicaRegistrado(String nombre) throws RemoteException{
        I_Replica replicaCliente = null;

        if(clienteRegistrado(nombre))
            replicaCliente =  (I_Replica)this;
        else
            for (I_Replica otraReplica : otrasReplicas) {
                if(otraReplica.clienteRegistrado(nombre))
                    replicaCliente =  otraReplica;
            }
         

        return replicaCliente;   
    }

    @Override
    public boolean getToken() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setToken(boolean token) throws RemoteException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void recibirToken() throws RemoteException {
        // TODO Auto-generated method stub
        
    }
   
}
