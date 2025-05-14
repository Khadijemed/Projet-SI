// RegisterWindow.java avec hachage SHA-256 et style
package prisonersdilemma;

import javax.swing.*;
import java.awt.*;

public class RegisterWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterWindow() {
        setTitle("Créer un compte");
        setSize(600, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        JLabel passwordLabel = new JLabel("Mot de passe:");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton registerButton = new JButton("S'inscrire");
        JButton backButton = new JButton("Retour");

        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(244, 67, 54));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));

        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        gbc.gridy = 3;
        panel.add(backButton, gbc);

        add(panel);
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hashedPassword = LoginWindow.hashPassword(password);

        if (SQLiteHelper.register(username, hashedPassword)) {
            JOptionPane.showMessageDialog(this, "Inscription réussie. Vous pouvez vous connecter.");
            dispose();
            new LoginWindow().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Nom d'utilisateur déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
