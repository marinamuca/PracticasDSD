import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Cliente {
    private final static Scanner in = new Scanner(System.in);

    public static void menu(){       
        System.out.println(
            "¿Qué operación desea realizar?\n"
            + "0: Registro\n"
            + "1: Donar\n"
            + "2: Ver Total Donado\n"
            + "3: Salir"
        );  
    }

    public static Opciones getOpcion(){
        int indiceOpcion = in.nextInt();
        System.out.println("-----------------------------------");

        return Opciones.values()[indiceOpcion];
    }

    public static float getCantidad(){
        float cantidad;

        System.out.println(
            "¿Cuántos € desea donar?"
        );

        cantidad = in.nextFloat();
        System.out.println("-----------------------------------");


        return cantidad;
    }
    public static String getNombre(){
        String nombre;

        System.out.println(
            "¿Cuál es su nombre de usuario?"
        );

        nombre = in.nextLine();

        return nombre;
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 3) { //si hay más de 1 parámetro
            throw new Exception("Numero incorrecto de parametros: [host] [port] [id_Replica]");
        } 

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String replica = "Replica" + args[2];
     

        // Crea e instala el gestor de seguridad
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Crea el stub para el cliente especificando el nombre del servidor
            Registry mireg = LocateRegistry.getRegistry(host,port);
            I_Donaciones donar = (I_Donaciones) mireg.lookup(replica);
            // Añade la donacion del cliente
            System.out.println("Invocando el objeto remoto");
           
            Opciones opcion = Opciones.SALIR;
            String nombre  = getNombre();

            do {
                System.out.println("-----------------------------------");

                menu();
                opcion = getOpcion();

                switch (opcion) {
                    case  REGISTRO:
                        try {      
                            System.out.println("Cliente " + nombre + " registrado en replica " + donar.registrarCliente(nombre));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case DONAR: 
                        float cantidad = getCantidad();
                        try {
                            System.out.println("Cliente " + nombre + " dona en replica " + donar.depositarDonacion(nombre, cantidad )+ " " + cantidad + "€.");              
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case TOTAL: 
                        try {
                            System.out.println("Total donado = " + donar.totalDonado(nombre));
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        }      
                        break;

                    case SALIR:
                        break;

                    
                }
            } while (opcion != Opciones.SALIR);    
      

        } catch (NotBoundException | RemoteException e) {
            System.err.println("Exception del sistema: " + e);
        }
        System.exit(0);
    }

}