package com.eventos.gui;

import com.eventos.model.Evento;
import com.eventos.model.Palestrante;
import com.eventos.model.Participante;
import com.eventos.service.EventoService;
import com.eventos.service.PalestranteService;
import com.eventos.service.ParticipanteService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventoFormDialog extends JDialog {

    private JTextField idField, nomeField, descricaoField, dataField, localField, capacidadeField;
    private JButton salvarButton, cancelarButton;

    private JList<Palestrante> palestrantesDisponiveisList;
    private DefaultListModel<Palestrante> palestrantesDisponiveisModel;
    private JList<Palestrante> palestrantesEventoList;
    private DefaultListModel<Palestrante> palestrantesEventoModel;
    private JButton adicionarPalestranteButton, removerPalestranteButton;

    private JList<Participante> participantesDisponiveisList;
    private DefaultListModel<Participante> participantesDisponiveisModel;
    private JList<Participante> participantesEventoList;
    private DefaultListModel<Participante> participantesEventoModel;
    private JButton adicionarParticipanteButton, removerParticipanteButton;

    private EventoService eventoService;
    private PalestranteService palestranteService;
    private ParticipanteService participanteService;
    private Evento eventoParaEditar;
    private boolean salvo = false;

    public EventoFormDialog(JFrame parent, Evento evento) {
        super(parent, true);
        this.eventoService = new EventoService();
        this.palestranteService = new PalestranteService();
        this.participanteService = new ParticipanteService();
        this.eventoParaEditar = evento;

        setTitle(evento == null ? "Novo Evento" : "Editar Evento");
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Painel Principal de Formulário de Dados Básicos ---
        JPanel basicFormPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        basicFormPanel.setBorder(BorderFactory.createTitledBorder("Dados do Evento"));

        idField = new JTextField();
        idField.setEditable(false);
        nomeField = new JTextField();
        descricaoField = new JTextField();
        dataField = new JTextField("dd/MM/yyyy");
        localField = new JTextField();
        capacidadeField = new JTextField();

        basicFormPanel.add(new JLabel("ID:"));
        basicFormPanel.add(idField);
        basicFormPanel.add(new JLabel("Nome:"));
        basicFormPanel.add(nomeField);
        basicFormPanel.add(new JLabel("Descrição:"));
        basicFormPanel.add(descricaoField);
        basicFormPanel.add(new JLabel("Data (dd/MM/yyyy):"));
        basicFormPanel.add(dataField);
        basicFormPanel.add(new JLabel("Local:"));
        basicFormPanel.add(localField);
        basicFormPanel.add(new JLabel("Capacidade:"));
        basicFormPanel.add(capacidadeField);

        // --- Painel de Gerenciamento de Palestrantes ---
        JPanel palestrantesManagementPanel = new JPanel(new BorderLayout(5, 5));
        palestrantesManagementPanel.setBorder(BorderFactory.createTitledBorder("Gerenciar Palestrantes"));

        palestrantesDisponiveisModel = new DefaultListModel<>();
        palestrantesDisponiveisList = new JList<>(palestrantesDisponiveisModel);
        JScrollPane scrollPaneDisponiveisPalestrantes = new JScrollPane(palestrantesDisponiveisList);
        
        JPanel moveButtonContainerPalestrantes = new JPanel();
        moveButtonContainerPalestrantes.setLayout(new BoxLayout(moveButtonContainerPalestrantes, BoxLayout.Y_AXIS));
        moveButtonContainerPalestrantes.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Margem interna
        
        adicionarPalestranteButton = new JButton(">> Adicionar");
        removerPalestranteButton = new JButton("<< Remover");
        
        adicionarPalestranteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removerPalestranteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        moveButtonContainerPalestrantes.add(Box.createVerticalGlue());
        moveButtonContainerPalestrantes.add(adicionarPalestranteButton);
        moveButtonContainerPalestrantes.add(Box.createVerticalStrut(10));
        moveButtonContainerPalestrantes.add(removerPalestranteButton);
        moveButtonContainerPalestrantes.add(Box.createVerticalGlue());

        palestrantesEventoModel = new DefaultListModel<>();
        palestrantesEventoList = new JList<>(palestrantesEventoModel);
        JScrollPane scrollPaneEventoPalestrantes = new JScrollPane(palestrantesEventoList);
        
        JSplitPane splitPanePalestrantesLists = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneDisponiveisPalestrantes, scrollPaneEventoPalestrantes);
        splitPanePalestrantesLists.setResizeWeight(0.5); // Divide o espaço igualmente entre as listas
        splitPanePalestrantesLists.setDividerSize(5);

        // Um novo JSplitPane para incluir os botões entre as listas
        JSplitPane finalSplitPalestrantes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPanePalestrantesLists, moveButtonContainerPalestrantes);
        finalSplitPalestrantes.setResizeWeight(0.8); // Dá mais espaço para as listas em relação aos botões
        finalSplitPalestrantes.setDividerSize(5);

        palestrantesManagementPanel.add(finalSplitPalestrantes, BorderLayout.CENTER);


        // --- Painel de Gerenciamento de Participantes ---
        JPanel participantesManagementPanel = new JPanel(new BorderLayout(5, 5));
        participantesManagementPanel.setBorder(BorderFactory.createTitledBorder("Gerenciar Participantes Inscritos"));

        participantesDisponiveisModel = new DefaultListModel<>();
        participantesDisponiveisList = new JList<>(participantesDisponiveisModel);
        JScrollPane scrollPaneDisponiveisParticipantes = new JScrollPane(participantesDisponiveisList);

        JPanel moveButtonContainerParticipantes = new JPanel();
        moveButtonContainerParticipantes.setLayout(new BoxLayout(moveButtonContainerParticipantes, BoxLayout.Y_AXIS));
        moveButtonContainerParticipantes.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        adicionarParticipanteButton = new JButton(">> Inscrever");
        removerParticipanteButton = new JButton("<< Remover");

        adicionarParticipanteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removerParticipanteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        moveButtonContainerParticipantes.add(Box.createVerticalGlue());
        moveButtonContainerParticipantes.add(adicionarParticipanteButton);
        moveButtonContainerParticipantes.add(Box.createVerticalStrut(10));
        moveButtonContainerParticipantes.add(removerParticipanteButton);
        moveButtonContainerParticipantes.add(Box.createVerticalGlue());

        participantesEventoModel = new DefaultListModel<>();
        participantesEventoList = new JList<>(participantesEventoModel);
        JScrollPane scrollPaneEventoParticipantes = new JScrollPane(participantesEventoList);

        JSplitPane splitPaneParticipantesLists = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneDisponiveisParticipantes, scrollPaneEventoParticipantes);
        splitPaneParticipantesLists.setResizeWeight(0.5);
        splitPaneParticipantesLists.setDividerSize(5);

        JSplitPane finalSplitParticipantes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPaneParticipantesLists, moveButtonContainerParticipantes);
        finalSplitParticipantes.setResizeWeight(0.8);
        finalSplitParticipantes.setDividerSize(5);
        
        participantesManagementPanel.add(finalSplitParticipantes, BorderLayout.CENTER);


        // --- Organização Geral dos Painéis ---
        // Criar um JSplitPane principal para dividir verticalmente entre o formulário + palestrantes e participantes
        JSplitPane mainVerticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainVerticalSplitPane.setDividerSize(10); // Espaço entre os painéis
        mainVerticalSplitPane.setResizeWeight(0.5); // Dividir o espaço igualmente no início

        JPanel topContentPanel = new JPanel(new BorderLayout(10, 10));
        topContentPanel.add(basicFormPanel, BorderLayout.NORTH);
        topContentPanel.add(palestrantesManagementPanel, BorderLayout.CENTER);

        mainVerticalSplitPane.setTopComponent(topContentPanel);
        mainVerticalSplitPane.setBottomComponent(participantesManagementPanel);
        
        add(mainVerticalSplitPane, BorderLayout.CENTER);

        // --- Painel de Botões Salvar/Cancelar ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (eventoParaEditar != null) {
            preencherCampos();
        }
        carregarListasDePalestrantes();
        carregarListasDeParticipantes();

        salvarButton.addActionListener(e -> salvarEvento());
        cancelarButton.addActionListener(e -> dispose());
        adicionarPalestranteButton.addActionListener(e -> adicionarPalestranteAoEvento());
        removerPalestranteButton.addActionListener(e -> removerPalestranteDoEvento());
        adicionarParticipanteButton.addActionListener(e -> adicionarParticipanteAoEvento());
        removerParticipanteButton.addActionListener(e -> removerParticipanteDoEvento());
    }

    private void preencherCampos() {
        idField.setText(String.valueOf(eventoParaEditar.getId()));
        nomeField.setText(eventoParaEditar.getNome());
        descricaoField.setText(eventoParaEditar.getDescricao());
        dataField.setText(eventoParaEditar.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        localField.setText(eventoParaEditar.getLocal());
        capacidadeField.setText(String.valueOf(eventoParaEditar.getCapacidade()));
    }

    private void carregarListasDePalestrantes() {
        List<Palestrante> todosPalestrantes = palestranteService.listarPalestrantes();
        
        palestrantesDisponiveisModel.clear();
        palestrantesEventoModel.clear();

        if (eventoParaEditar != null) {
            List<Palestrante> palestrantesAssociados = eventoParaEditar.getPalestrantes();
            for (Palestrante p : todosPalestrantes) {
                if (palestrantesAssociados.contains(p)) {
                    palestrantesEventoModel.addElement(p);
                } else {
                    palestrantesDisponiveisModel.addElement(p);
                }
            }
        } else {
            for (Palestrante p : todosPalestrantes) {
                palestrantesDisponiveisModel.addElement(p);
            }
        }
    }

    private void carregarListasDeParticipantes() {
        List<Participante> todosParticipantes = participanteService.listarParticipantes();
        
        participantesDisponiveisModel.clear();
        participantesEventoModel.clear();

        if (eventoParaEditar != null) {
            List<Participante> participantesAssociados = eventoParaEditar.getParticipantesInscritos();
            for (Participante p : todosParticipantes) {
                if (participantesAssociados.contains(p)) {
                    participantesEventoModel.addElement(p);
                } else {
                    participantesDisponiveisModel.addElement(p);
                }
            }
        } else {
            for (Participante p : todosParticipantes) {
                participantesDisponiveisModel.addElement(p);
            }
        }
    }

    private void adicionarPalestranteAoEvento() {
        List<Palestrante> selecionados = palestrantesDisponiveisList.getSelectedValuesList();
        for (Palestrante p : selecionados) {
            palestrantesDisponiveisModel.removeElement(p);
            palestrantesEventoModel.addElement(p);
        }
    }

    private void removerPalestranteDoEvento() {
        List<Palestrante> selecionados = palestrantesEventoList.getSelectedValuesList();
        for (Palestrante p : selecionados) {
            palestrantesEventoModel.removeElement(p);
            palestrantesDisponiveisModel.addElement(p);
        }
    }

    private void adicionarParticipanteAoEvento() {
        List<Participante> selecionados = participantesDisponiveisList.getSelectedValuesList();
        for (Participante p : selecionados) {
            participantesDisponiveisModel.removeElement(p);
            participantesEventoModel.addElement(p);
        }
    }

    private void removerParticipanteDoEvento() {
        List<Participante> selecionados = participantesEventoList.getSelectedValuesList();
        for (Participante p : selecionados) {
            participantesEventoModel.removeElement(p);
            participantesDisponiveisModel.addElement(p);
        }
    }

    private void salvarEvento() {
        try {
            String nome = nomeField.getText();
            String descricao = descricaoField.getText();
            LocalDate data = LocalDate.parse(dataField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String local = localField.getText();
            int capacidade = Integer.parseInt(capacidadeField.getText());

            List<Palestrante> palestrantesFinais = new ArrayList<>();
            for (int i = 0; i < palestrantesEventoModel.size(); i++) {
                palestrantesFinais.add(palestrantesEventoModel.getElementAt(i));
            }

            List<Participante> participantesFinais = new ArrayList<>();
            for (int i = 0; i < participantesEventoModel.size(); i++) {
                participantesFinais.add(participantesEventoModel.getElementAt(i));
            }

            if (eventoParaEditar == null) {
                Evento novoEvento = eventoService.criarEvento(nome, descricao, data, local, capacidade);
                for (Palestrante p : palestrantesFinais) {
                    eventoService.associarPalestrante(novoEvento.getId(), p);
                }
                for (Participante p : participantesFinais) {
                    eventoService.associarParticipante(novoEvento.getId(), p);
                }
                JOptionPane.showMessageDialog(this, "Evento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                eventoService.atualizarEvento(eventoParaEditar.getId(), nome, descricao, data, local, capacidade, palestrantesFinais, participantesFinais);
                JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            salvo = true;
            dispose();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacidade deve ser um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSalvo() {
        return salvo;
    }
}
