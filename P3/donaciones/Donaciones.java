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
    ArrayList<String> clientes;
    ArrayList<Float> donacionesCliente;
    private float totalLocal;
    
    // Replicas anterior y siguiente para EM
    I_Replica siguiente, anterior; 
    // Indica si tiene o no el Token
    boolean token;
    //Indica si esta o no ejecutando una sección critica
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

  
    public void initAnillo() throws RemoteException{
        if(siguiente == null){
            siguiente = otrasReplicas.get((ID+1) % (otrasReplicas.size()));
            siguiente.setAnteriorReplica((I_Replica) this);
        } 
    }

    public synchronized void darToken() throws RemoteException{
        
        if(token){
            System.out.println("REPLICA " + getID() +  " da el token.");
            System.out.println("REPLICA " + siguiente.getID() +  " Tiene el token.");

            siguiente.setToken(true);
            this.setToken(false);
        }
    }

    public synchronized void esperarToken() throws RemoteException{
        if(!token)
            System.out.println("REPLICA " + getID() +  " Esperando el token.");
       
        while(!token)
            anterior.recibirToken();
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
        replica.donar(nombre, valor);
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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        float total = replicaCliente.calcularTotal();
        System.out.println("REPLICA " + replicaCliente.getID() +  " Devuelvo total donado a cliente " + nombre);
        //Fin SC
        ejecutando = false;

        // Da el token a la siguiente replica
        darToken();

        return total;
    }

  
   
    //INTERFAZ REPLICA (Servidor-Servidor)
    @Override
    public synchronized void registrar(String nombre) throws RemoteException{
       clientes.add(nombre);
       donacionesCliente.add(0f);
    }

    @Override
    public synchronized void donar(String nombre, float valor) throws RemoteException {
        int indiceCliente = clientes.indexOf(nombre);
        donacionesCliente.set((indiceCliente), donacionesCliente.get(indiceCliente)+valor);
        totalLocal += valor;
    }

    @Override
    public float getDonado() throws RemoteException{
        return totalLocal;
    }

    @Override
    public float calcularTotal() throws RemoteException{ 
        float total = 0;
        for (I_Replica otraReplica : otrasReplicas) {
            total += otraReplica.getDonado();
        }
        return total + totalLocal;
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

    public int getID() throws RemoteException{return ID;}
    public ArrayList<String> getClientes() throws RemoteException{return clientes;}
  
    @Override
    public boolean tieneToken() throws RemoteException{
        return token;
    }

    @Override
    public void setToken(boolean token) throws RemoteException{     
        this.token = token;
    }

    @Override
    public void recibirToken() throws RemoteException{
        if(token && !ejecutando)
            darToken(); // Si tiene el token y no esta ejecutando, se lo da
        else if(token == false)
            esperarToken(); 
        // Si no tiene el token, lo pide para darselo a la replica que se lo ha pedido
        // Si está ejecutando no hace nada.
    }

    @Override
    public void setAnteriorReplica(I_Replica anterior) throws RemoteException{     
        this.anterior = anterior;
    }
   
}