// 🎮 Version améliorée du Dilemme du Prisonnier avec sécurité, graphique et audio
package prisonersdilemma;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class PrisonersDilemmaGUI extends JFrame {
    private JTextArea outputArea;
    private JButton cooperateButton, defectButton, replayButton, aiButton, logoutButton, historyButton, portScannerButton;
    private JLabel userLabel, aiLabel, roundLabel;
    private int round = 1, playerScore = 0, computerScore = 0;
    private String aiStrategy = "random";
    private String lastPlayerChoice = "COOPERATE";
    private int consecutiveDefectsByAI = 0;
    private boolean aiBlocked = false;
    private int totalDefectsByPlayer = 0;
    private final int MAX_ALLOWED_DEFECTS = 3;
    private boolean aiAlertShown = false;
    private int[] playerScoreHistory = new int[6];
    private int[] computerScoreHistory = new int[6];
    private int timeLimitSeconds = 10;

    public PrisonersDilemmaGUI(String username) {
        setTitle("🎮 Dilemme du prisonnier - Cybersécurité");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🔘 Demander la durée de la manche
        String input = JOptionPane.showInputDialog(this, "⏱ Entrez la durée (en secondes) de chaque manche :", "10");
        try {
            int val = Integer.parseInt(input);
            if (val > 0) timeLimitSeconds = val;
        } catch (Exception ignored) {}

        userLabel = new JLabel("👤 Joueur : " + username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));

        aiLabel = new JLabel("🤖 Adversaire : IA");
        aiLabel.setFont(new Font("Arial", Font.BOLD, 16));

        roundLabel = new JLabel("🧩 Manche : 1 sur 5");
        roundLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        topPanel.add(userLabel);
        topPanel.add(roundLabel);
        topPanel.add(aiLabel);
        add(topPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        cooperateButton = new JButton("🤝 Coopérer");
        defectButton = new JButton("❌ Trahir");
        aiButton = new JButton("🤖 Changer IA");
        replayButton = new JButton("🔁 Rejouer");
        logoutButton = new JButton("🔓 Déconnexion");
        historyButton = new JButton("📜 Historique");
        portScannerButton = new JButton("🛡️ Scanner les ports");
        replayButton.setVisible(false);

        cooperateButton.addActionListener(e -> playRound("COOPERATE"));
        defectButton.addActionListener(e -> playRound("DEFECT"));
        replayButton.addActionListener(e -> resetGame());
        aiButton.addActionListener(e -> switchAIStrategy());
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vous déconnecter ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginWindow().setVisible(true);
            }
        });
        historyButton.addActionListener(e -> showHistory());
        portScannerButton.addActionListener(e -> PortScannerGUI.showScannerWindow());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.add(cooperateButton);
        buttonPanel.add(defectButton);
        buttonPanel.add(replayButton);
        buttonPanel.add(aiButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(portScannerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        print("Bienvenue ! Choisissez une action.");
    }

    private void playRound(String playerChoice) {
        Timer timer = new Timer(timeLimitSeconds * 1000, e -> {
            if (cooperateButton.isEnabled() && defectButton.isEnabled()) {
                playRound("COOPERATE");
                print("⏱ Temps écoulé. Coopération choisie automatiquement.");
            }
        });
        timer.setRepeats(false);
        timer.start();

        if (aiBlocked) {
            print("IA bloquée pour comportement hostile.");
            replayButton.setVisible(true);
            return;
        }

        if (round > 5) {
            print("Fin de la partie. Cliquez sur Rejouer.");
            replayButton.setVisible(true);
            return;
        }

        if (playerChoice.equals("DEFECT")) {
            totalDefectsByPlayer++;
            if (totalDefectsByPlayer > MAX_ALLOWED_DEFECTS) {
                print("Trop de trahisons. Vous êtes bloqué.");
                JOptionPane.showMessageDialog(this, "Blocage activé suite à trop de trahisons.", "Firewall", JOptionPane.ERROR_MESSAGE);
                disableButtons();
                replayButton.setVisible(true);
                return;
            }
        }

        String computerChoice = getComputerChoice();
        roundLabel.setText("🧩 Manche : " + round + " sur 5");
        print("--- Manche " + round + " ---");
        print("Vous : " + (playerChoice.equals("COOPERATE") ? "Coopérer" : "Trahir"));
        print("IA : " + (computerChoice.equals("COOPERATE") ? "Coopérer" : "Trahir"));

        if (computerChoice.equals("DEFECT")) {
            consecutiveDefectsByAI++;
            if (consecutiveDefectsByAI >= 3 && !aiAlertShown) {
                aiAlertShown = true;
                print("🚨 IA trahit depuis 3 tours !");
                JOptionPane.showMessageDialog(this, "Comportement suspect détecté de l'IA.", "Alerte", JOptionPane.WARNING_MESSAGE);
                aiBlocked = true;
                disableButtons();
                replayButton.setVisible(true);
                return;
            }
        } else {
            consecutiveDefectsByAI = 0;
        }

        logEncryptedToFile(round, playerChoice, computerChoice);

        if (playerChoice.equals("COOPERATE") && computerChoice.equals("COOPERATE")) {
            playerScore += 3; computerScore += 3;
        } else if (playerChoice.equals("COOPERATE") && computerChoice.equals("DEFECT")) {
            playerScore += 0; computerScore += 5;
        } else if (playerChoice.equals("DEFECT") && computerChoice.equals("COOPERATE")) {
            playerScore += 5; computerScore += 0;
        } else {
            playerScore += 1; computerScore += 1;
        }
        

        playerScoreHistory[round] = playerScore;
        computerScoreHistory[round] = computerScore;

        round++;

        if (round > 5) {
            print("\n--- Résultats finaux ---");
            print("Joueur : " + playerScore);
            print("IA : " + computerScore);
            if (playerScore > computerScore) print("🎉 Vous avez gagné !");
            else if (playerScore < computerScore) print("😢 Vous avez perdu.");
            else print("🤝 Égalité.");
            replayButton.setVisible(true);
            showScoreChart();
        }
    }

    private void showScoreChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 1; i <= 5; i++) {
            dataset.addValue(playerScoreHistory[i], "Joueur", "Tour " + i);
            dataset.addValue(computerScoreHistory[i], "IA", "Tour " + i);
        }

        JFreeChart chart = ChartFactory.createLineChart(
            "Évolution des scores", "Tour", "Score",
            dataset, PlotOrientation.VERTICAL, true, true, false);

        ChartPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame("Statistiques");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setVisible(true);
    }

    private void showHistory() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("game_log.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            sb.append("Aucun historique disponible.");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, pane, "Historique des parties", JOptionPane.INFORMATION_MESSAGE);
    }

    private void disableButtons() {
        cooperateButton.setEnabled(false);
        defectButton.setEnabled(false);
    }

    private String getComputerChoice() {
        return switch (aiStrategy) {
            case "always-defect" -> "DEFECT";
            case "tit-for-tat" -> lastPlayerChoice;
            default -> new Random().nextBoolean() ? "COOPERATE" : "DEFECT";
        };
    }

    private void switchAIStrategy() {
        aiStrategy = switch (aiStrategy) {
            case "random" -> "tit-for-tat";
            case "tit-for-tat" -> "always-defect";
            default -> "random";
        };
        print("Stratégie de l'IA changée : " + aiStrategy);
    }

    private void logEncryptedToFile(int round, String playerChoice, String computerChoice) {
        String encryptedPlayer = EncryptionUtil.encrypt(playerChoice);
        String encryptedComputer = EncryptionUtil.encrypt(computerChoice);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("game_log.txt", true)))) {
            out.println("Tour " + round + " | Joueur: " + encryptedPlayer + " | IA: " + encryptedComputer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetGame() {
        round = 1;
        playerScore = 0;
        computerScore = 0;
        consecutiveDefectsByAI = 0;
        totalDefectsByPlayer = 0;
        lastPlayerChoice = "COOPERATE";
        aiAlertShown = false;
        aiBlocked = false;
        roundLabel.setText("🧩 Manche : 1 sur 5");
        outputArea.setText("Nouvelle partie lancée !\n\n");
        cooperateButton.setEnabled(true);
        defectButton.setEnabled(true);
        replayButton.setVisible(false);
    }

    private void print(String text) {
        outputArea.append(text + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrisonersDilemmaGUI("Invité").setVisible(true));
    }
}
