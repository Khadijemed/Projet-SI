// LoginWindow.java avec protection brute-force, hachage SHA-256 et vérification d'intégrité
package prisonersdilemma;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, goToRegisterButton;
    private int failedAttempts = 0;
    private Timer lockoutTimer;

    public LoginWindow() {
        setTitle("Connexion");
        setSize(600, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Nom d'utilisateur :");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        loginButton = new JButton("Se connecter");
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));

        goToRegisterButton = new JButton("S'inscrire");
        goToRegisterButton.setBackground(new Color(76, 175, 80));
        goToRegisterButton.setForeground(Color.WHITE);
        goToRegisterButton.setFont(new Font("Arial", Font.BOLD, 16));

        buttonPanel.add(loginButton);
        buttonPanel.add(goToRegisterButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String hashedPassword = hashPassword(password);

            if (!IntegrityChecker.verifyIntegrity()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Alerte : Le fichier game_log.txt semble modifié ou corrompu.",
                        "Alerte sécurité", JOptionPane.WARNING_MESSAGE);
            }

            if (SQLiteHelper.authenticate(username, hashedPassword)) {
                failedAttempts = 0;
                dispose();
                new PrisonersDilemmaGUI(username).setVisible(true);
            } else {
                failedAttempts++;
                if (failedAttempts >= 3) {
                    loginButton.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "🚫 Trop de tentatives. Attendez 30 secondes.", "Blocage", JOptionPane.WARNING_MESSAGE);
                    lockoutTimer = new Timer(30000, evt -> {
                        loginButton.setEnabled(true);
                        failedAttempts = 0;
                    });
                    lockoutTimer.setRepeats(false);
                    lockoutTimer.start();
                } else {
                    JOptionPane.showMessageDialog(this, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        goToRegisterButton.addActionListener(e -> {
            dispose();
            new RegisterWindow().setVisible(true);
        });
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SQLiteHelper.connect();
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}
