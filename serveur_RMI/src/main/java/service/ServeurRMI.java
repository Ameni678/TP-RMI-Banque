package service;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServeurRMI {
    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 1099;
        try {
            LocateRegistry.createRegistry(port);
            BanqueServiceImpl objDistant = new BanqueServiceImpl();
            System.out.println(objDistant.toString());
            Naming.rebind("rmi://localhost:" + port + "/BK", objDistant);
            System.out.println("Serveur pret sur le port " + port + " (nom : BK)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
