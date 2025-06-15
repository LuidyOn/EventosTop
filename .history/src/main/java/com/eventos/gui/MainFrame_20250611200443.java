// src/main/java/com/eventos/gui/MainFrame.java
package com.eventos.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private EventosPanel eventosPanel;
    // Adicionar outros painéis conforme necessário (PalestrantesPanel, ParticipantesPanel)

    public MainFrame() {
        setTitle("Sistema de Gerenciamento de Eventos");
        setSize(800, 600); // Tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fechar a aplicação ao fechar a janela
        setLocationRelativeTo(null); // Centralizar a janela na tela

        // Criar um JTabbedPane para organizar as seções
        JTabbedPane tabbedPane = new JTabbedPane();

        // Painel para Eventos
        eventosPanel = new EventosPanel();
        tabbedPane.addTab("Eventos", eventosPanel);

        // TODO: Adicionar painéis para Palestrantes e Participantes aqui
        // PalestrantesPanel palestrantesPanel = new PalestrantesPanel();
        // tabbedPane.addTab("Palestrantes", palestrantesPanel);
        // ParticipantesPanel participantesPanel = new ParticipantesPanel();
        // tabbedPane.addTab("Participantes", participantesPanel);

        add(tabbedPane, BorderLayout.CENTER); // Adiciona o painel de abas ao centro da janela

        // Exemplo de um menu simples (opcional)
        JMenuBar menuBar = new JMenuBar();
        JMenu arquivoMenu = new JMenu("Arquivo");
        JMenuItem sairMenuItem = new JMenuItem("Sair");
        sairMenuItem.addActionListener(e -> System.exit(0)); // Ação para sair
        arquivoMenu.add(sairMenuItem);
        menuBar.add(arquivoMenu);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        // Inicializar o banco de dados (chamar uma vez ao iniciar a aplicação)
        com.eventos.dao.DatabaseInitializer.initialize();

        // Garante que a interface seja criada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}