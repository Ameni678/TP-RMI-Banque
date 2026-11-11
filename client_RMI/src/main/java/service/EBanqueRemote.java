package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface EBanqueRemote extends Remote {

    // ----- Partie du TP d'origine -----
    double conversion(double montant) throws RemoteException;
    Date GetServeurDate() throws RemoteException;

    // ----- Ajouts -----
    double conversion(double montant, String devise) throws RemoteException;

    void creerCompte(String numero, double soldeInitial) throws RemoteException;
    void deposer(String numero, double montant) throws RemoteException, CompteInexistantException;
    void retirer(String numero, double montant)
            throws RemoteException, CompteInexistantException, SoldeInsuffisantException;
    void virement(String source, String destination, double montant)
            throws RemoteException, CompteInexistantException, SoldeInsuffisantException;
    double getSolde(String numero) throws RemoteException, CompteInexistantException;
    List<String> getHistorique(String numero) throws RemoteException, CompteInexistantException;

    double calculerInterets(double capital, double tauxAnnuel, int annees) throws RemoteException;
    double calculerMensualite(double montant, double tauxAnnuel, int mois) throws RemoteException;
    int getNombreAppels() throws RemoteException;
}
