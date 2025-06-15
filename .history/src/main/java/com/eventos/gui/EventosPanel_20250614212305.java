package com.eventos.gui;

import com.eventos.model.Evento;
import com.eventos.service.EventoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventosPanel extends JPanel {

    private EventoService eventoService;
    private JTable eventosTable;
    private DefaultTableModel tableModel;
    private JButton adicionarButton, editarButton, visualizarButton, excluirButton;

    public EventosPanel() {
        eventoService = new EventoService();
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nome", "Data", "Local", "Capacidade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(eventosTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        adicionarButton = new JButton("Adicionar Evento");
        editarButton = new JButton("Editar Evento");
        visualizarButton = new JButton("Visualizar Evento");
        excluirButton = new JButton("Excluir Evento");

        buttonPanel.add(adicionarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(visualizarButton);
        buttonPanel.add(excluirButton);
        add(buttonPanel, BorderLayout.SOUTH);

        adicionarButton.addActionListener(e -> adicionarEvento());
        editarButton.addActionListener(e -> editarEvento());
        visualizarButton.addActionListener(e -> visualizarEvento());
        excluirButton.addActionListener(e -> excluirEvento());

        carregarEventos();
    }

    private void carregarEventos() {
        tableModel.setRowCount(0);
        List<Evento> eventos = eventoService.listarEventos();
        for (Evento evento : eventos) {
            Object[] rowData = {
                    evento.getId(),
                    evento.getNome(),
                    evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    evento.getLocal(),
                    evento.getCapacidade()
            };
            tableModel.addRow(rowData);
        }
    }

    private void adicionarEvento() {
        EventoFormDialog formDialog = new EventoFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        formDialog.setVisible(true);

        if (formDialog.isSalvo()) {
            carregarEventos();
        }
    }

    private void editarEvento() {
        int selectedRow = eventosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventoId = (int) tableModel.getValueAt(selectedRow, 0);
        Optional<Evento> eventoOpt = eventoService.visualizarEvento(eventoId);

        eventoOpt.ifPresent(evento -> {
            EventoFormDialog formDialog = new EventoFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), evento);
            formDialog.setVisible(true);

            if (formDialog.isSalvo()) {
                carregarEventos();
            }
        });
    }

    private void visualizarEvento() {
        int selectedRow = eventosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para visualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventoId = (int) tableModel.getValueAt(selectedRow, 0);
        Optional<Evento> eventoOpt = eventoService.visualizarEvento(eventoId);

        eventoOpt.ifPresent(evento -> {
            String palestrantesStr = evento.getPalestrantes().isEmpty() ? "Nenhum" : 
                evento.getPalestrantes().stream()
                      .map(p -> p.getNome() + " (" + p.getAreaAtuacao() + ")")
                      .collect(Collectors.joining(", "));

            String participantesStr = evento.getParticipantesInscritos().isEmpty() ? "Nenhum" :
                evento.getParticipantesInscritos().stream()
                      .map(p -> p.getNome() + " (" + p.getEmail() + ")")
                      .collect(Collectors.joining(", "));

            String detalhes = "ID: " + evento.getId() + "\n" +
                              "Nome: " + evento.getNome() + "\n" +
                              "Descrição: " + evento.getDescricao() + "\n" +
                              "Data: " + evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                              "Local: " + evento.getLocal() + "\n" +
                              "Capacidade: " + evento.getCapacidade() + "\n" +
                              "Palestrantes: " + palestrantesStr + "\n" +
                              "Participantes Inscritos: " + participantesStr;
            JOptionPane.showMessageDialog(this, detalhes, "Detalhes do Evento", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void excluirEvento() {
        int selectedRow = eventosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este evento?\nTodas as associações com palestrantes e participantes serão removidas.", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int eventoId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                if (eventoService.cancelarEvento(eventoId)) {
                    JOptionPane.showMessageDialog(this, "Evento excluído com sucesso!");
                    carregarEventos();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir evento.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
