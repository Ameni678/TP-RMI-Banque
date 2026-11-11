package service;

import java.awt.*;
import java.rmi.Naming;
import javax.swing.*;

/** Client avec interface graphique Swing. */
public class ClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final EBanqueRemote stub;
    private final JTextField tfCompte = new JTextField(10);
    private final JTextField tfDest = new JTextField(10);
    private final JTextField tfMontant = new JTextField(10);
    private final JTextField tfTaux = new JTextField("5", 10);
    private final JTextField tfDuree = new JTextField("12", 10);
    private final JComboBox<String> cbDevise = new JComboBox<>(new String[]{"TND", "USD", "GBP"});
    private final JTextArea sortie = new JTextArea(12, 50);

    public ClientGUI(EBanqueRemote stub) {
        super("Banque RMI - Client");
        this.stub = stub;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel champs = new JPanel(new GridLayout(0, 2, 4, 4));
        champs.setBorder(BorderFactory.createTitledBorder("Parametres"));
        champs.add(new JLabel("N compte :"));          champs.add(tfCompte);
        champs.add(new JLabel("Compte destination :")); champs.add(tfDest);
        champs.add(new JLabel("Montant :"));           champs.add(tfMontant);
        champs.add(new JLabel("Taux annuel (%) :"));   champs.add(tfTaux);
        champs.add(new JLabel("Duree (annees / mois) :")); champs.add(tfDuree);
        champs.add(new JLabel("Devise :"));            champs.add(cbDevise);

        JPanel boutons = new JPanel(new GridLayout(0, 3, 4, 4));
        boutons.setBorder(BorderFactory.createTitledBorder("Operations"));
        ajouter(boutons, "Creer compte", () -> { stub.creerCompte(tfCompte.getText(), montant()); return "Compte cree."; });
        ajouter(boutons, "Depot", () -> { stub.deposer(tfCompte.getText(), montant()); return "Depot effectue."; });
        ajouter(boutons, "Retrait", () -> { stub.retirer(tfCompte.getText(), montant()); return "Retrait effectue."; });
        ajouter(boutons, "Virement", () -> { stub.virement(tfCompte.getText(), tfDest.getText(), montant()); return "Virement effectue."; });
        ajouter(boutons, "Solde", () -> String.format("Solde : %.2f", stub.getSolde(tfCompte.getText())));
        ajouter(boutons, "Historique", () -> String.join("\n", stub.getHistorique(tfCompte.getText())));
        ajouter(boutons, "Conversion", () -> String.format("%.2f EUR = %.2f %s", montant(),
                stub.conversion(montant(), (String) cbDevise.getSelectedItem()), cbDevise.getSelectedItem()));
        ajouter(boutons, "Interets", () -> String.format("Interets : %.2f",
                stub.calculerInterets(montant(), Double.parseDouble(tfTaux.getText()), Integer.parseInt(tfDuree.getText()))));
        ajouter(boutons, "Mensualite", () -> String.format("Mensualite : %.2f",
                stub.calculerMensualite(montant(), Double.parseDouble(tfTaux.getText()), Integer.parseInt(tfDuree.getText()))));
        ajouter(boutons, "Infos serveur", () -> "Date serveur : " + stub.GetServeurDate()
                + "\nAppels recus : " + stub.getNombreAppels());

        sortie.setEditable(false);
        sortie.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel haut = new JPanel(new BorderLayout(8, 8));
        haut.add(champs, BorderLayout.WEST);
        haut.add(boutons, BorderLayout.CENTER);
        add(haut, BorderLayout.NORTH);
        add(new JScrollPane(sortie), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private double montant() { return Double.parseDouble(tfMontant.getText()); }

    private interface Action { String run() throws Exception; }

    private void ajouter(JPanel p, String titre, Action a) {
        JButton b = new JButton(titre);
        b.addActionListener(e -> {
            try {
                sortie.append(a.run() + "\n");
            } catch (NumberFormatException ex) {
                sortie.append("Erreur : valeur numerique invalide\n");
            } catch (Exception ex) {
                sortie.append("Erreur : " + ex.getMessage() + "\n");
            }
        });
        p.add(b);
    }

    public static void main(String[] args) {
        String hote = (args.length > 0) ? args[0] : "localhost";
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : 1099;
        try {
            EBanqueRemote stub = (EBanqueRemote) Naming.lookup("rmi://" + hote + ":" + port + "/BK");
            SwingUtilities.invokeLater(() -> new ClientGUI(stub).setVisible(true));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Impossible de joindre le serveur : " + e.getMessage());
        }
    }
}
