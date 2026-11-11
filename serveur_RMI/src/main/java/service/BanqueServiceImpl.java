package service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanqueServiceImpl extends UnicastRemoteObject implements EBanqueRemote {
    private static final long serialVersionUID = 1L;

    private final Map<String, Double> comptes = new HashMap<>();
    private final Map<String, List<String>> historiques = new HashMap<>();
    private final Map<String, Double> taux = new HashMap<>(); // taux par rapport a 1 EUR
    private int nbAppels = 0;

    protected BanqueServiceImpl() throws RemoteException {
        super();
        taux.put("TND", 3.3);
        taux.put("USD", 1.08);
        taux.put("GBP", 0.85);
    }

    private synchronized void compter(String op) {
        nbAppels++;
        System.out.println("[" + new Date() + "] Appel #" + nbAppels + " : " + op);
    }

    // ----- TP d'origine -----
    @Override
    public double conversion(double montant) throws RemoteException {
        compter("conversion(" + montant + ")");
        return montant * 3;
    }

    @Override
    public Date GetServeurDate() throws RemoteException {
        compter("GetServeurDate()");
        return new Date();
    }

    // ----- Ajouts -----
    @Override
    public double conversion(double montant, String devise) throws RemoteException {
        compter("conversion(" + montant + ", " + devise + ")");
        Double t = taux.get(devise.toUpperCase());
        if (t == null) throw new RemoteException("Devise non supportee : " + devise);
        return montant * t;
    }

    @Override
    public synchronized void creerCompte(String numero, double soldeInitial) throws RemoteException {
        compter("creerCompte(" + numero + ")");
        if (comptes.containsKey(numero)) throw new RemoteException("Compte deja existant : " + numero);
        comptes.put(numero, soldeInitial);
        historiques.put(numero, new ArrayList<>());
        histo(numero, "Creation, solde initial " + soldeInitial);
    }

    @Override
    public synchronized void deposer(String numero, double montant) throws CompteInexistantException {
        compter("deposer(" + numero + ", " + montant + ")");
        verifier(numero);
        comptes.put(numero, comptes.get(numero) + montant);
        histo(numero, "Depot +" + montant);
    }

    @Override
    public synchronized void retirer(String numero, double montant)
            throws CompteInexistantException, SoldeInsuffisantException {
        compter("retirer(" + numero + ", " + montant + ")");
        verifier(numero);
        if (comptes.get(numero) < montant)
            throw new SoldeInsuffisantException("Solde insuffisant : " + comptes.get(numero));
        comptes.put(numero, comptes.get(numero) - montant);
        histo(numero, "Retrait -" + montant);
    }

    @Override
    public synchronized void virement(String source, String destination, double montant)
            throws CompteInexistantException, SoldeInsuffisantException {
        compter("virement(" + source + " -> " + destination + ", " + montant + ")");
        verifier(source);
        verifier(destination);
        if (comptes.get(source) < montant)
            throw new SoldeInsuffisantException("Solde insuffisant : " + comptes.get(source));
        comptes.put(source, comptes.get(source) - montant);
        comptes.put(destination, comptes.get(destination) + montant);
        histo(source, "Virement -" + montant + " vers " + destination);
        histo(destination, "Virement +" + montant + " de " + source);
    }

    @Override
    public synchronized double getSolde(String numero) throws CompteInexistantException {
        compter("getSolde(" + numero + ")");
        verifier(numero);
        return comptes.get(numero);
    }

    @Override
    public synchronized List<String> getHistorique(String numero) throws CompteInexistantException {
        compter("getHistorique(" + numero + ")");
        verifier(numero);
        return new ArrayList<>(historiques.get(numero));
    }

    @Override
    public double calculerInterets(double capital, double tauxAnnuel, int annees) throws RemoteException {
        compter("calculerInterets(" + capital + ", " + tauxAnnuel + ", " + annees + ")");
        return capital * Math.pow(1 + tauxAnnuel / 100, annees) - capital;
    }

    @Override
    public double calculerMensualite(double montant, double tauxAnnuel, int mois) throws RemoteException {
        compter("calculerMensualite(" + montant + ", " + tauxAnnuel + ", " + mois + ")");
        double t = tauxAnnuel / 100 / 12;
        if (t == 0) return montant / mois;
        return montant * t / (1 - Math.pow(1 + t, -mois));
    }

    @Override
    public synchronized int getNombreAppels() throws RemoteException {
        return nbAppels;
    }

    private void verifier(String numero) throws CompteInexistantException {
        if (!comptes.containsKey(numero))
            throw new CompteInexistantException("Compte introuvable : " + numero);
    }

    private void histo(String numero, String op) {
        historiques.get(numero).add(new Date() + " - " + op);
    }
}
