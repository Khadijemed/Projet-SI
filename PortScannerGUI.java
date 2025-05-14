// 🔐 PortScanner avec interface graphique intégrée et lien depuis le jeu
package prisonersdilemma;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class PortScannerGUI extends JFrame {
    private JTextArea resultArea;
    private JButton scanButton;

    public PortScannerGUI() {
        setTitle("🔍 Scanner des ports ouverts");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scanButton = new JButton("📡 Lancer le scan");
        scanButton.addActionListener(e -> scanPorts());

        add(scrollPane, BorderLayout.CENTER);
        add(scanButton, BorderLayout.SOUTH);
    }

    private void scanPorts() {
        resultArea.setText("🔍 Début du scan sur localhost...\n");
        String host = "localhost";
        int startPort = 1;
        int endPort = 1024;

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (int port = startPort; port <= endPort; port++) {
                    try (Socket socket = new Socket(host, port)) {
                        publish("✅ Port ouvert : " + port);
                    } catch (IOException ignored) {
                        // Port fermé, ne rien faire
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String line : chunks) {
                    resultArea.append(line + "\n");
                }
            }

            @Override
            protected void done() {
                resultArea.append("\n✅ Scan terminé.\n");
            }
        };

        worker.execute();
    }

    // 👇 Méthode statique à appeler depuis PrisonersDilemmaGUI
    public static void showScannerWindow() {
        SwingUtilities.invokeLater(() -> new PortScannerGUI().setVisible(true));
    }

    public static void main(String[] args) {
        showScannerWindow();
    }
}
