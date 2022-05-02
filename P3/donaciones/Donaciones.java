import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;


public class Donaciones extends UnicastRemoteObject implements I_Donaciones, I_Replica {
    private final String HOST;
    private final int PORT;
    private final int ID;
    private Registry regReplica;

    ArrayList<I_Replica> otrasReplicas;
    I_Replica siguiente, anterior;
    ArrayList<String> clientes;
    ArrayList<Integer> donacionesCliente;
    private float totalLocal;

    boolean token;
    boolean ejecutando;


    public Donaciones(String host, int port, int idReplica) throws RemoteException {
        HOST = host;
        PORT = port;
        ID = idReplica;
        this.regReplica = LocateRegistry.getRegistry(HOST, PORT);

        clientes = new ArrayList<>();
        donacionesCliente = new ArrayList<>();
        otrasReplicas = new ArrayList<>();
        totalLocal = 0;

        token = false;
        ejecutando = false;
    }

    public void addReplica (String id) throws AccessException, RemoteException, NotBoundException{
        I_Replica replica = (I_Replica) regReplica.lookup(id);
        otrasReplicas.add(replica); 
    }

    public int getID() throws RemoteException{return ID;}
    public ArrayList<String> getClientes() throws RemoteException{return clientes;}

    public void initAnillo() throws RemoteException{
        if(siguiente == null){
            siguiente = otrasReplicas.get((ID+1) % (otrasReplicas.size()));
    
            siguiente.setAnteriorReplica((I_Replica) this);
        } 
    }

   
    // INTERFAZ DONACIONES (Cliente-Servidor)
    @Override
    public int registrarCliente(String nombre) throws RemoteException {
        System.out.println("-----------------------------------------------");

        if (replicaRegistrado(nombre) != null)
        throw new RemoteException("El Cliente ya está registrado");

        //Espera a tener el token
        esperarToken();
        
        // Inicio SC
        ejecutando = true;
        I_Replica replicaRegistro = this;
        for (I_Replica otraReplica : otrasReplicas) {
            if (replicaRegistro.getNumClientes() > otraReplica.getNumClientes())
                replicaRegistro = otraReplica;
        }

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
        
        I_Replica replica =  replicaRegistrado(nombre);
        if (replica == null)
        throw new RemoteException("Cliente no registrado");
        
        if(valor < 0 )
            throw new RemoteException("No se puede donar una cantidad negativa");

        //Espera a tener el token
        esperarToken();

        //Inicio SC
        ejecutando = true;
        replica.donar(valor);
        System.out.println("REPLICA " + replica.getID() +  " Cliente " + nombre + " deposita " + valor + "€");
        //Fin SC
        ejecutando = false;

        // Da el token a la siguiente replica
        darToken();

        return replica.getID();
    }

    @Override
    public float totalDonado(String nombre) throws RemoteException {

        System.out.println("-----------------------------------------------");
        
        I_Replica replicaCliente = replicaRegistrado(nombre);
        if ( replicaCliente == null)
            throw new RemoteException("Cliente no registrado.");
        if(replicaCliente.clienteHaDonado(nombre) == false){
            throw new RemoteException("No puede consultar las donaciones sin haber donado previamente.");
        }

        //Espera a tener el token
        esperarToken();

        //Inicio SC
        ejecutando = true;      
        System.out.println("REPLICA " + getID() +  " Devuelvo total donado a cliente " + nombre);
        // try {
        //     Thread.sleep(5000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        float total = totalLocal + totalOtrasReplicas();
        //Fin SC
        ejecutando = false;

        // Da el token a la siguiente replica
        darToken();

        return total;
    }

    public float totalOtrasReplicas() throws RemoteException{
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
       donacionesCliente.add(0);
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
    public boolean clienteHaDonado(String nombre) throws RemoteException{
        return donacionesCliente.get(clientes.indexOf(nombre))>0;
    }

    @Override
    public I_Replica replicaRegistrado(String nombre) throws RemoteException{
        I_Replica replicaCliente = null;

        if(clienteRegistrado(nombre))
            replicaCliente =  (I_Replica)this;
        else 
        for (I_Replica otraReplica : otrasReplicas) {
            if(otraReplica.clienteRegistrado(nombre)){
                replicaCliente =  otraReplica;
            }
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
        System.out.println("REPLICA " + siguiente.getID() +  " pide el token.");
        if(token && !ejecutando)
            darToken(); // Si tiene el token, se lo da
        else
            esperarToken(); // Si no tiene el toke, lo pide para darselo a la replica que se lo ha pedido
    }

    public void darToken() throws RemoteException{
        
        if(token){
            System.out.println("REPLICA " + getID() +  " da el token.");
            System.out.println("REPLICA " + siguiente.getID() +  " Tiene el token.");

            siguiente.setToken(true);
            this.setToken(false);
        }
    }

    public void esperarToken() throws RemoteException{
        System.out.println("REPLICA " + getID() +  " Esperando el token.");
       
        while(!token)
            anterior.recibirToken();
    }

    public void setAnteriorReplica(I_Replica anterior) throws RemoteException{     
        this.anterior = anterior;
    }
   
}