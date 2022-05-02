import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.swing.text.html.HTMLDocument.RunElement;

public class Servidor {

    public static void main(String[] args) throws RemoteException {

        String host = "127.0.0.1";
        int port = Integer.parseInt(args[0]);
        int nReplicas = Integer.parseInt(args[1]);

        //Creo las replicas
        ArrayList<Donaciones> replicas = new ArrayList<>();
   

        // Crea e instala el gestor de seguridad
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            // Crea una instancia de donacion
            // System.setProperty("java.rmi.server.hostname","192.168.1.107");
         
            Registry reg = LocateRegistry.createRegistry(port);

            for (int i = 0; i < nReplicas ; i++) {
                Donaciones replica = new Donaciones(host, port, i+1);
              
                int id = i+1;
                Naming.rebind("Replica" +  id, replica);

                
                
                replicas.add(replica);   
            }

            for (int i = 0; i < replicas.size(); i++) {
                for (int j = 0; j < replicas.size(); j++) {
                    int id = j+1;
                    if( i!=j) {
                        replicas.get(i).addReplica("Replica" + id);
                    };             
                }
            }

            for (Donaciones replica : replicas) {
                replica.initAnillo();
            }
           
            //Le doy el token a la replica 1
            replicas.get(0).setToken(true);

            System.out.println("Servidor RemoteException | MalformedURLExceptiondor preparado");
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    
}
