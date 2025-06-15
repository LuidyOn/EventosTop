package com.eventos.gui;

import com.eventos.model.Evento;
import com.eventos.model.Palestrante;
import com.eventos.model.Participante; // Importar Participante
import com.eventos.service.EventoService;
import com.eventos.service.PalestranteService;
import com.eventos.service.ParticipanteService; // Novo serviço

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EventoFormDialog extends JDialog {

    private JTextField idField, nomeField, descricaoField, dataField, localField, capacidadeField;
    private JButton salvarButton, cancelarButton;

    // Componentes para gerenciamento de palestrantes
    private JList<Palestrante> palestrantesDisponiveisList;
    private DefaultListModel<Palestrante> palestrantesDisponiveisModel;
    private JList<Palestrante> palestrantesEventoList;
    private DefaultListModel<Palestrante> palestrantesEventoModel;
    private JButton adicionarPalestranteButton, removerPalestranteButton;

    // NOVO: Componentes para gerenciamento de participantes
    private JList<Participante> participantesDisponiveisList;
    private DefaultListModel<Participante> participantesDisponiveisModel;
    private JList<Participante> participantesEventoList;
    private DefaultListModel<Participante> participantesEventoModel;
    private JButton adicionarParticipanteButton, removerParticipanteButton;

    private EventoService eventoService;
    private PalestranteService palestranteService;
    private ParticipanteService participanteService; // NOVO: Serviço de Participantes
    private Evento eventoParaEditar;
    private boolean salvo = false;

    public EventoFormDialog(JFrame parent, Evento evento) {
        super(parent, true);
        this.eventoService = new EventoService();
        this.palestranteService = new PalestranteService();
        this.participanteService = new ParticipanteService(); // Inicializar serviço
        this.eventoParaEditar = evento;

        setTitle(evento == null ? "Novo Evento" : "Editar Evento");
        setSize(1000, 700); // Aumentei ainda mais o tamanho
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
        
        JPanel moveButtonPanelPalestrantes = new JPanel(new GridLayout(2, 1, 5, 5));
        adicionarPalestranteButton = new JButton(">> Adicionar");
        removerPalestranteButton = new JButton("<< Remover");
        moveButtonPanelPalestrantes.add(adicionarPalestranteButton);
        moveButtonPanelPalestrantes.add(removerPalestranteButton);
        
        palestrantesEventoModel = new DefaultListModel<>();
        palestrantesEventoList = new JList<>(palestrantesEventoModel);
        JScrollPane scrollPaneEventoPalestrantes = new JScrollPane(palestrantesEventoList);
        
        palestrantesManagementPanel.add(new JLabel("Disponíveis:"), BorderLayout.WEST);
        palestrantesManagementPanel.add(scrollPaneDisponiveisPalestrantes, BorderLayout.WEST);
        palestrantesManagementPanel.add(moveButtonPanelPalestrantes, BorderLayout.CENTER);
        palestrantesManagementPanel.add(new JLabel("Associados:"), BorderLayout.EAST);
        palestrantesManagementPanel.add(scrollPaneEventoPalestrantes, BorderLayout.EAST);

        // Ajuste de layout para palestrantesManagementPanel
        JSplitPane splitPanePalestrantes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneDisponiveisPalestrantes, moveButtonPanelPalestrantes);
        splitPanePalestrantes.setDividerLocation(200); // Ajuste para separar as listas
        palestrantesManagementPanel.add(splitPanePalestrantes, BorderLayout.WEST);
        palestrantesManagementPanel.add(scrollPaneEventoPalestrantes, BorderLayout.CENTER); // Mudei para CENTER


        // --- NOVO: Painel de Gerenciamento de Participantes ---
        JPanel participantesManagementPanel = new JPanel(new BorderLayout(5, 5));
        participantesManagementPanel.setBorder(BorderFactory.createTitledBorder("Gerenciar Participantes Inscritos"));

        participantesDisponiveisModel = new DefaultListModel<>();
        participantesDisponiveisList = new JList<>(participantesDisponiveisModel);
        JScrollPane scrollPaneDisponiveisParticipantes = new JScrollPane(participantesDisponiveisList);

        JPanel moveButtonPanelParticipantes = new JPanel(new GridLayout(2, 1, 5, 5));
        adicionarParticipanteButton = new JButton(">> Inscrever");
        removerParticipanteButton = new JButton("<< Remover");
        moveButtonPanelParticipantes.add(adicionarParticipanteButton);
        moveButtonPanelParticipantes.add(removerParticipanteButton);

        participantesEventoModel = new DefaultListModel<>();
        participantesEventoList = new JList<>(participantesEventoModel);
        JScrollPane scrollPaneEventoParticipantes = new JScrollPane(participantesEventoList);

        participantesManagementPanel.add(new JLabel("Disponíveis:"), BorderLayout.WEST);
        participantesManagementPanel.add(scrollPaneDisponiveisParticipantes, BorderLayout.WEST);
        participantesManagementPanel.add(moveButtonPanelParticipantes, BorderLayout.CENTER);
        participantesManagementPanel.add(new JLabel("Inscritos:"), BorderLayout.EAST);
        participantesManagementPanel.add(scrollPaneEventoParticipantes, BorderLayout.EAST);

        // Ajuste de layout para participantesManagementPanel
        JSplitPane splitPaneParticipantes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneDisponiveisParticipantes, moveButtonPanelParticipantes);
        splitPaneParticipantes.setDividerLocation(200);
        participantesManagementPanel.add(splitPaneParticipantes, BorderLayout.WEST);
        participantesManagementPanel.add(scrollPaneEventoParticipantes, BorderLayout.CENTER); // Mudei para CENTER


        // --- Organização Geral dos Painéis ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10)); // Painel para dados básicos e palestrantes
        topPanel.add(basicFormPanel, BorderLayout.NORTH); // Formulário no topo
        topPanel.add(palestrantesManagementPanel, BorderLayout.CENTER); // Gerenciamento de palestrantes

        JPanel mainContentPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Divide a área central
        mainContentPanel.add(topPanel);
        mainContentPanel.add(participantesManagementPanel); // NOVO: Participantes na parte de baixo

        add(mainContentPanel, BorderLayout.CENTER); // Adiciona o painel principal ao centro

        // --- Painel de Botões Salvar/Cancelar ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Preenche os campos e as listas se estivermos editando um evento
        if (eventoParaEditar != null) {
            preencherCampos();
        }
        carregarListasDePalestrantes();
        carregarListasDeParticipantes(); // NOVO: Carregar lista de participantes

        // Listeners dos botões
        salvarButton.addActionListener(e -> salvarEvento());
        cancelarButton.addActionListener(e -> dispose());
        adicionarPalestranteButton.addActionListener(e -> adicionarPalestranteAoEvento());
        removerPalestranteButton.addActionListener(e -> removerPalestranteDoEvento());
        adicionarParticipanteButton.addActionListener(e -> adicionarParticipanteAoEvento()); // NOVO
        removerParticipanteButton.addActionListener(e -> removerParticipanteDoEvento()); // NOVO
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

    // NOVO: Método para carregar listas de participantes
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

    // NOVO: Adicionar participante ao evento
    private void adicionarParticipanteAoEvento() {
        List<Participante> selecionados = participantesDisponiveisList.getSelectedValuesList();
        for (Participante p : selecionados) {
            participantesDisponiveisModel.removeElement(p);
            participantesEventoModel.addElement(p);
        }
    }

    // NOVO: Remover participante do evento
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

            // NOVO: Coleta a lista final de participantes do evento
            List<Participante> participantesFinais = new ArrayList<>();
            for (int i = 0; i < participantesEventoModel.size(); i++) {
                participantesFinais.add(participantesEventoModel.getElementAt(i));
            }

            if (eventoParaEditar == null) { // Modo de criação
                Evento novoEvento = eventoService.criarEvento(nome, descricao, data, local, capacidade);
                for (Palestrante p : palestrantesFinais) {
                    eventoService.associarPalestrante(novoEvento.getId(), p);
                }
                for (Participante p : participantesFinais) { // NOVO
                    eventoService.associarParticipante(novoEvento.getId(), p); // NOVO
                }
                JOptionPane.showMessageDialog(this, "Evento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Modo de edição
                // Chama o atualizarEvento com a lista de palestrantes E participantes
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