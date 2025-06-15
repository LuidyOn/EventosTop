package com.eventos.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private EventosPanel eventosPanel;
    private PalestrantesPanel palestrantesPanel; // Novo
    private ParticipantesPanel participantesPanel; // Novo

    public MainFrame() {
        setTitle("Sistema de Gerenciamento de Eventos");
        setSize(900, 700); // Aumentei um pouco o tamanho para acomodar mais conteúdo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        eventosPanel = new EventosPanel();
        tabbedPane.addTab("Eventos", eventosPanel);

        palestrantesPanel = new PalestrantesPanel(); // Instanciar e adicionar
        tabbedPane.addTab("Palestrantes", palestrantesPanel);

        participantesPanel = new ParticipantesPanel(); // Instanciar e adicionar
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
