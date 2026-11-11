package service;

import java.rmi.Naming;
import java.util.Scanner;

/** Client console : d'abord le test du TP, puis un menu interactif. */
public class ClientRMI {
    public static void main(String[] args) {
        String hote = (args.length > 0) ? args[0] : "localhost";
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : 1099;

        try (Scanner sc = new Scanner(System.in)) {
            EBanqueRemote stub = (EBanqueRemote) Naming.lookup("rmi://" + hote + ":" + port + "/BK");

            // --- Test du TP d'origine ---
            System.out.println(stub.conversion(1000));
            System.out.println(stub.GetServeurDate());

            int choix;
            do {
                System.out.println("\n===== SERVICE BANQUE =====");
                System.out.println("1.Creer compte  2.Depot  3.Retrait  4.Virement  5.Solde  6.Historique");
                System.out.println("7.Conversion devise  8.Interets  9.Mensualite credit  10.Infos serveur  0.Quitter");
                System.out.print("Choix : ");
                choix = sc.nextInt();
                try {
                    switch (choix) {
                        case 1 -> { System.out.print("N compte : "); String n = sc.next();
                                    System.out.print("Solde initial : ");
                                    stub.creerCompte(n, sc.nextDouble()); System.out.println("Compte cree."); }
                        case 2 -> { System.out.print("N compte : "); String n = sc.next();
                                    System.out.print("Montant : ");
                                    stub.deposer(n, sc.nextDouble()); System.out.println("Depot effectue."); }
                        case 3 -> { System.out.print("N compte : "); String n = sc.next();
                                    System.out.print("Montant : ");
                                    stub.retirer(n, sc.nextDouble()); System.out.println("Retrait effectue."); }
                        case 4 -> { System.out.print("Compte source : "); String s = sc.next();
                                    System.out.print("Compte destination : "); String d = sc.next();
                                    System.out.print("Montant : ");
                                    stub.virement(s, d, sc.nextDouble()); System.out.println("Virement effectue."); }
                        case 5 -> { System.out.print("N compte : ");
                                    System.out.printf("Solde : %.2f%n", stub.getSolde(sc.next())); }
                        case 6 -> { System.out.print("N compte : ");
                                    stub.getHistorique(sc.next()).forEach(System.out::println); }
                        case 7 -> { System.out.print("Montant EUR : "); double m = sc.nextDouble();
                                    System.out.print("Devise (TND/USD/GBP) : ");
                                    System.out.printf("Resultat : %.2f%n", stub.conversion(m, sc.next())); }
                        case 8 -> { System.out.print("Capital : "); double c = sc.nextDouble();
                                    System.out.print("Taux annuel (%) : "); double t = sc.nextDouble();
                                    System.out.print("Duree (annees) : ");
                                    System.out.printf("Interets : %.2f%n", stub.calculerInterets(c, t, sc.nextInt())); }
                        case 9 -> { System.out.print("Montant du credit : "); double c = sc.nextDouble();
                                    System.out.print("Taux annuel (%) : "); double t = sc.nextDouble();
                                    System.out.print("Duree (mois) : ");
                                    System.out.printf("Mensualite : %.2f%n", stub.calculerMensualite(c, t, sc.nextInt())); }
                        case 10 -> { System.out.println("Date serveur : " + stub.GetServeurDate());
                                     System.out.println("Appels recus : " + stub.getNombreAppels()); }
                        default -> { }
                    }
                } catch (CompteInexistantException | SoldeInsuffisantException e) {
                    System.out.println("Erreur : " + e.getMessage());
                } catch (java.rmi.RemoteException e) {
                    System.out.println("Erreur serveur : " + e.getMessage());
                }
            } while (choix != 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
