import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;

import java.util.*;

import javax.net.ssl.HostnameVerifier;
import javax.print.attribute.standard.QueuedJobCount;
import javax.swing.text.html.HTMLDocument.RunElement;

public class Donaciones extends UnicastRemoteObject implements I_Donaciones, I_Replica {
    private final String HOST;
    private final int PORT;
    private final int ID;
    private Registry regReplica;

    I_Replica otraReplica;
    ArrayList<String> clientes;
    ArrayList<String> solicitando;
    private float totalLocal;

    boolean token;
    boolean ejecutando;


    public Donaciones(String host, int port, int idReplica) throws RemoteException {
        HOST = host;
        PORT = port;
        ID = idReplica;
        this.regReplica = LocateRegistry.getRegistry(HOST, PORT);

        clientes = new ArrayList<>();
        solicitando = new ArrayList<>();
        totalLocal = 0;

        token = false;
        ejecutando = false;

    }

    public void setReplica (int id) throws AccessException, RemoteException, NotBoundException{
        otraReplica= (I_Replica) regReplica.lookup("Replica" + id);
    }

    public int getID() throws RemoteException{return ID;}
    public ArrayList<String> getClientes() throws RemoteException{return clientes;}

   
    // INTERFAZ DONACIONES (Cliente-Servidor)
    @Override
    public int registrarCliente(String nombre) throws RemoteException {
        System.out.println("-----------------------------------------------");

        //Espera a tener el token
        esperarToken();

        //Inicio SC
        ejecutando = true;
        if (replicaRegistrado(nombre) != null)
            throw new RemoteException("El Cliente ya está registrado");

        I_Replica replicaRegistro = this;
        if (replicaRegistro.getNumClientes() > otraReplica.getNumClientes())
            replicaRegistro = otraReplica;

        replicaRegistro.registrar(nombre);
        System.out.println("Registro llamado desde REPLICA " + getID());
        System.out.println("REPLICA " + replicaRegistro.getID() + ": Se regista cliente " + nombre);
        System.out.println("REPLICA " + replicaRegistro.getID() + " Se han registrado " + replicaRegistro.getNumClientes() + " clientes");
        //Fin SC
        ejecutando = false;

        // Da el token a la siguiente replica
        darToken();

        
        return replicaRegistro.getID();
    }

    @Override
    public int depositarDonacion(String nombre, float valor) throws RemoteException {
        System.out.println("-----------------------------------------------");
        
        //Espera a tener el token
        esperarToken();
        
        //Inicio SC
        ejecutando = true;
        I_Replica replica =  replicaRegistrado(nombre);
        if (replica == null)
            throw new RemoteException("Cliente no registrado");
        
        if(valor >= 0 ){
            replica.donar(valor);
            System.out.println("REPLICA " + replica.getID() +  " Cliente " + nombre + " deposita " + valor + "€");
        } else
            throw new RemoteException("No se puede donar una cantidad negativa");
        //Fin SC
        ejecutando = false;

        // Da el token a la siguiente replica
        darToken();

        return replica.getID();
    }

    @Override
    public float totalDonado(String nombre) throws RemoteException {

        System.out.println("-----------------------------------------------");
      
        //Espera a tener el token
        esperarToken();
        //Inicio SC
        ejecutando = true;
        if (replicaRegistrado(nombre) == null)
            throw new RemoteException("Cliente no registrado");

        System.out.println("REPLICA " + getID() +  " Devuelvo total donado a cliente " + nombre);
        // try {
        //     Thread.sleep(5000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        //Fin SC
        ejecutando = false;

        // Da el token a la siguiente replica
        darToken();

        return totalLocal + otraReplica.getDonado();
    }
   
    //INTERFAZ REPLICA (Servidor-Servidor)
    @Override
    public void registrar(String nombre) throws RemoteException{
       clientes.add(nombre);
    }

    @Override
    public synchronized void donar(float valor) throws RemoteException {
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
        else if(otraReplica != null && otraReplica.clienteRegistrado(nombre)){
            replicaCliente =  otraReplica;
        } 

        return replicaCliente;   
    }

    @Override
    public boolean getToken() throws RemoteException{
        return token;
    }

    @Override
    public void setToken(boolean token) throws RemoteException{     
        this.token = token;
    }

    @Override
    public void recibirToken() throws RemoteException{
        System.out.println("REPLICA " + otraReplica.getID() +  " pide el token.");
        if(token && !ejecutando)
            darToken();
    }

    public void darToken() throws RemoteException{

        if(token){
            System.out.println("REPLICA " + getID() +  " da el token.");
            System.out.println("REPLICA " + otraReplica.getID() +  " Tiene el token.");

            otraReplica.setToken(true);
            this.setToken(false);
        }
    }

    public void esperarToken() throws RemoteException{
        while(!token)
            otraReplica.recibirToken();
    }
   
}
