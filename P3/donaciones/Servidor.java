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

        //Creo las replicas
        Donaciones replica1 = new Donaciones(host, port, 1);
        Donaciones replica2 = new Donaciones(host, port, 2);

        // Crea e instala el gestor de seguridad
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            // Crea una instancia de donacion
            // System.setProperty("java.rmi.server.hostname","192.168.1.107");
         
            Registry reg = LocateRegistry.createRegistry(port);
            Naming.rebind("Replica1", replica1);
            Naming.rebind("Replica2", replica2);

            try {
                replica1.setReplica(2, replica2);
                replica2.setReplica(1, replica1);
            } catch (NotBoundException nb) {
                System.out.println("Exception: " + nb.getMessage());
            }

            System.out.println("Servidor RemoteException | MalformedURLExceptiondor preparado");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    
}
