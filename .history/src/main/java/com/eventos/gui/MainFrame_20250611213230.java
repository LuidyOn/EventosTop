package com.eventos.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private EventosPanel eventosPanel;
    private PalestrantesPanel palestrantesPanel;
    private ParticipantesPanel participantesPanel;

    public MainFrame() {
        // Tentar definir o Look and Feel do sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Não foi possível definir o Look and Feel do sistema. Usando o padrão Java.");
            // Opcional: e.printStackTrace(); para depuração
        }

        setTitle("Sistema de Gerenciamento de Eventos");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        eventosPanel = new EventosPanel();
        tabbedPane.addTab("Eventos", eventosPanel);

        palestrantesPanel = new PalestrantesPanel();
        tabbedPane.addTab("Palestrantes", palestrantesPanel);

        participantesPanel = new ParticipantesPanel();
        tabbedPane.addTab("Participantes", participantesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu arquivoMenu = new JMenu("Arquivo");
        JMenuItem sairMenuItem = new JMenuItem("Sair");
        sairMenuItem.addActionListener(e -> System.exit(0));
        arquivoMenu.add(sairMenuItem);
        menuBar.add(arquivoMenu);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        com.eventos.dao.DatabaseInitializer.initialize();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}