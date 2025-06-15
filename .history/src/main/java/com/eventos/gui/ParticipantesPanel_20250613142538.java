package com.eventos.gui;

import com.eventos.model.Evento;
import com.eventos.model.Participante;
import com.eventos.service.ParticipanteService;
import com.eventos.service.EventoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Para facilitar a formatação de listas

public class ParticipantesPanel extends JPanel {

    private ParticipanteService participanteService;
    private EventoService eventoService;
    private JTable participantesTable;
    private DefaultTableModel tableModel;
    private JButton adicionarButton, editarButton, visualizarButton, inscreverButton, cancelarInscricaoButton, emitirCertificadoButton;

    public ParticipantesPanel() {
        participanteService = new ParticipanteService();
        eventoService = new EventoService();
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nome", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        participantesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(participantesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        adicionarButton = new JButton("Adicionar Participante");
        editarButton = new JButton("Editar Participante");
        visualizarButton = new JButton("Visualizar Participante");
        inscreverButton = new JButton("Inscrever em Evento");
        cancelarInscricaoButton = new JButton("Cancelar Inscrição");
        emitirCertificadoButton = new JButton("Emitir Certificado");

        buttonPanel.add(adicionarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(visualizarButton);
        buttonPanel.add(inscreverButton);
        buttonPanel.add(cancelarInscricaoButton);
        buttonPanel.add(emitirCertificadoButton);
        add(buttonPanel, BorderLayout.SOUTH);

        adicionarButton.addActionListener(e -> adicionarParticipante());
        editarButton.addActionListener(e -> editarParticipante());
        visualizarButton.addActionListener(e -> visualizarParticipante());
        inscreverButton.addActionListener(e -> inscreverEvento());
        cancelarInscricaoButton.addActionListener(e -> cancelarInscricao());
        emitirCertificadoButton.addActionListener(e -> emitirCertificado());

        carregarParticipantes();
    }

    private void carregarParticipantes() {
        tableModel.setRowCount(0);
        List<Participante> participantes = participanteService.listarParticipantes();
        for (Participante participante : participantes) {
            Object[] rowData = {
                    participante.getId(),
                    participante.getNome(),
                    participante.getEmail()
            };
            tableModel.addRow(rowData);
        }
    }

    private void adicionarParticipante() {
        ParticipanteFormDialog formDialog = new ParticipanteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        formDialog.setVisible(true);

        if (formDialog.isSalvo()) {
            carregarParticipantes();
        }
    }

    private void editarParticipante() {
        int selectedRow = participantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um participante para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int participanteId = (int) tableModel.getValueAt(selectedRow, 0);
        Optional<Participante> participanteOpt = participanteService.visualizarParticipante(participanteId);

        participanteOpt.ifPresent(participante -> {
            ParticipanteFormDialog formDialog = new ParticipanteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), participante);
            formDialog.setVisible(true);

            if (formDialog.isSalvo()) {
                carregarParticipantes();
            }
        });
    }

    private void visualizarParticipante() {
        int selectedRow = participantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um participante para visualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int participanteId = (int) tableModel.getValueAt(selectedRow, 0);
        Optional<Participante> participanteOpt = participanteService.visualizarParticipante(participanteId);

        participanteOpt.ifPresent(participante -> {
            // NOVO: Formatar eventos inscritos para exibição
            String eventosInscritosStr = participante.getEventosInscritos().isEmpty() ? "Nenhum" : 
                participante.getEventosInscritos().stream()
                          .map(e -> e.getNome() + " (" + e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")")
                          .collect(Collectors.joining(", "));

            String detalhes = "ID: " + participante.getId() + "\n" +
                              "Nome: " + participante.getNome() + "\n" +
                              "Email: " + participante.getEmail() + "\n" +
                              "Eventos Inscritos: " + eventosInscritosStr; // Agora formatado
            JOptionPane.showMessageDialog(this, detalhes, "Detalhes do Participante", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void inscreverEvento() {
        int selectedRow = participantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um participante para inscrever.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int participanteId = (int) tableModel.getValueAt(selectedRow, 0);
        
        List<Evento> eventosDisponiveis = eventoService.listarEventos();
        if (eventosDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há eventos disponíveis para inscrição.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opcoesEventos = eventosDisponiveis.stream()
                                    .map(e -> e.getId() + " - " + e.getNome() + " (" + e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")")
                                    .toArray(String[]::new);

        String eventoSelecionadoStr = (String) JOptionPane.showInputDialog(this,
                                "Selecione o evento para inscrição:",
                                "Inscrever em Evento",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                opcoesEventos,
                                opcoesEventos[0]);

        if (eventoSelecionadoStr != null) {
            try {
                int eventoId = Integer.parseInt(eventoSelecionadoStr.split(" ")[0]);
                participanteService.inscreverEvento(participanteId, eventoId);
                JOptionPane.showMessageDialog(this, "Inscrição realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarParticipantes(); 
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar ID do evento.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro ao inscrever: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void cancelarInscricao() {
        int selectedRow = participantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um participante para cancelar a inscrição.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int participanteId = (int) tableModel.getValueAt(selectedRow, 0);

        Optional<Participante> participanteOpt = participanteService.visualizarParticipante(participanteId);
        if (participanteOpt.isEmpty() || participanteOpt.get().getEventosInscritos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este participante não está inscrito em nenhum evento.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Evento> eventosInscritos = participanteOpt.get().getEventosInscritos();
        String[] opcoesEventosInscritos = eventosInscritos.stream()
                                            .map(e -> e.getId() + " - " + e.getNome() + " (" + e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")")
                                            .toArray(String[]::new);

        String eventoSelecionadoStr = (String) JOptionPane.showInputDialog(this,
                                "Selecione o evento para cancelar a inscrição:",
                                "Cancelar Inscrição",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                opcoesEventosInscritos,
                                opcoesEventosInscritos[0]);

        if (eventoSelecionadoStr != null) {
            try {
                int eventoId = Integer.parseInt(eventoSelecionadoStr.split(" ")[0]);
                participanteService.cancelarInscricao(participanteId, eventoId);
                JOptionPane.showMessageDialog(this, "Inscrição cancelada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarParticipantes();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar ID do evento.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro ao cancelar inscrição: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void emitirCertificado() {
        int selectedRow = participantesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um participante para emitir certificado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int participanteId = (int) tableModel.getValueAt(selectedRow, 0);

        Optional<Participante> participanteOpt = participanteService.visualizarParticipante(participanteId);
        if (participanteOpt.isEmpty() || participanteOpt.get().getEventosInscritos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este participante não está inscrito em nenhum evento.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Evento> eventosInscritos = participanteOpt.get().getEventosInscritos();
        String[] opcoesEventosInscritos = eventosInscritos.stream()
                                            .filter(e -> e.getData().isBefore(java.time.LocalDate.now()))
                                            .map(e -> e.getId() + " - " + e.getNome() + " (" + e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")")
                                            .toArray(String[]::new);
        
        if (opcoesEventosInscritos.length == 0) {
            JOptionPane.showMessageDialog(this, "Este participante não possui eventos passados para emitir certificado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String eventoSelecionadoStr = (String) JOptionPane.showInputDialog(this,
                                "Selecione o evento para emitir certificado:",
                                "Emitir Certificado",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                opcoesEventosInscritos,
                                opcoesEventosInscritos[0]);

        if (eventoSelecionadoStr != null) {
            try {
                int eventoId = Integer.parseInt(eventoSelecionadoStr.split(" ")[0]);
                String certificado = participanteService.emitirCertificado(participanteId, eventoId);
                JOptionPane.showMessageDialog(this, certificado, "Certificado", JOptionPane.PLAIN_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar ID do evento.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro ao emitir certificado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}