package com.eventos.gui;

import com.eventos.model.Palestrante;
import com.eventos.service.PalestranteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class PalestrantesPanel extends JPanel {

    private PalestranteService palestranteService;
    private JTable palestrantesTable;
    private DefaultTableModel tableModel;
    private JButton adicionarButton, editarButton, visualizarButton, excluirButton;

    public PalestrantesPanel() {
        palestranteService = new PalestranteService();
        setLayout(new BorderLayout());

        // Configuração da tabela
        String[] columnNames = {"ID", "Nome", "Currículo", "Área de Atuação"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        palestrantesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(palestrantesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        adicionarButton = new JButton("Adicionar Palestrante");
        editarButton = new JButton("Editar Palestrante");
        visualizarButton = new JButton("Visualizar Palestrante");
        excluirButton = new JButton("Excluir Palestrante");

        buttonPanel.add(adicionarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(visualizarButton);
        buttonPanel.add(excluirButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Adicionar Listeners aos botões
        adicionarButton.addActionListener(e -> adicionarPalestrante());
        editarButton.addActionListener(e -> editarPalestrante());
        visualizarButton.addActionListener(e -> visualizarPalestrante());
        excluirButton.addActionListener(e -> excluirPalestrante());

        carregarPalestrantes();
    }

    private void carregarPalestrantes() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Palestrante> palestrantes = palestranteService.listarPalestrantes();
        for (Palestrante palestrante : palestrantes) {
            Object[] rowData = {
                    palestrante.getId(),
                    palestrante.getNome(),
                    palestrante.getCurriculo(),
                    palestrante.getAreaAtuacao()
            };
            tableModel.addRow(rowData);
        }
    }

    private void adicionarPalestrante() {
        PalestranteFormDialog formDialog = new PalestranteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        formDialog.setVisible(true);

        if (formDialog.isSalvo()) {
            carregarPalestrantes();
        }
    }

    private void editarPalestrante() {
        int selectedRow = palestrantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um palestrante para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int palestranteId = (int) tableModel.getValueAt(selectedRow, 0);
        Optional<Palestrante> palestranteOpt = palestranteService.visualizarPalestrante(palestranteId);

        palestranteOpt.ifPresent(palestrante -> {
            PalestranteFormDialog formDialog = new PalestranteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), palestrante);
            formDialog.setVisible(true);

            if (formDialog.isSalvo()) {
                carregarPalestrantes();
            }
        });
    }

    private void visualizarPalestrante() {
        int selectedRow = palestrantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um palestrante para visualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int palestranteId = (int) tableModel.getValueAt(selectedRow, 0);
        Optional<Palestrante> palestranteOpt = palestranteService.visualizarPalestrante(palestranteId);

        palestranteOpt.ifPresent(palestrante -> {
            String detalhes = "ID: " + palestrante.getId() + "\n" +
                              "Nome: " + palestrante.getNome() + "\n" +
                              "Currículo: " + palestrante.getCurriculo() + "\n" +
                              "Área de Atuação: " + palestrante.getAreaAtuacao() + "\n" +
                              "Eventos: " + (palestrante.getEventos().isEmpty() ? "Nenhum" : palestrante.getEventos());
            JOptionPane.showMessageDialog(this, detalhes, "Detalhes do Palestrante", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void excluirPalestrante() {
        int selectedRow = palestrantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um palestrante para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este palestrante?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int palestranteId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                if (palestranteService.excluirPalestrante(palestranteId)) {
                    JOptionPane.showMessageDialog(this, "Palestrante excluído com sucesso!");
                    carregarPalestrantes();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir palestrante.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}