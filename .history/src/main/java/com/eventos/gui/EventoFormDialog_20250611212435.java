package com.eventos.gui;

import com.eventos.model.Evento;
import com.eventos.model.Palestrante;
import com.eventos.service.EventoService;
import com.eventos.service.PalestranteService; // Adicionado para buscar palestrantes

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Para facilitar a manipulação de listas

public class EventoFormDialog extends JDialog {

    private JTextField idField, nomeField, descricaoField, dataField, localField, capacidadeField;
    private JButton salvarButton, cancelarButton;

    // Componentes para gerenciamento de palestrantes
    private JList<Palestrante> palestrantesDisponiveisList;
    private DefaultListModel<Palestrante> palestrantesDisponiveisModel;
    private JList<Palestrante> palestrantesEventoList;
    private DefaultListModel<Palestrante> palestrantesEventoModel;
    private JButton adicionarPalestranteButton, removerPalestranteButton;

    private EventoService eventoService;
    private PalestranteService palestranteService; // Novo serviço
    private Evento eventoParaEditar;
    private boolean salvo = false;

    public EventoFormDialog(JFrame parent, Evento evento) {
        super(parent, true); // Modal dialog
        this.eventoService = new EventoService();
        this.palestranteService = new PalestranteService(); // Inicializar serviço
        this.eventoParaEditar = evento;

        setTitle(evento == null ? "Novo Evento" : "Editar Evento");
        setSize(800, 500); // Aumentei o tamanho para as listas
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10)); // Adiciona espaçamento

        // --- Painel Principal de Formulário ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        idField = new JTextField();
        idField.setEditable(false);

        nomeField = new JTextField();
        descricaoField = new JTextField();
        dataField = new JTextField("dd/MM/yyyy");
        localField = new JTextField();
        capacidadeField = new JTextField();

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Nome:"));
        formPanel.add(nomeField);
        formPanel.add(new JLabel("Descrição:"));
        formPanel.add(descricaoField);
        formPanel.add(new JLabel("Data (dd/MM/yyyy):"));
        formPanel.add(dataField);
        formPanel.add(new JLabel("Local:"));
        formPanel.add(localField);
        formPanel.add(new JLabel("Capacidade:"));
        formPanel.add(capacidadeField);

        // --- Painel de Gerenciamento de Palestrantes ---
        JPanel palestrantesManagementPanel = new JPanel(new BorderLayout(5, 5));
        palestrantesManagementPanel.setBorder(BorderFactory.createTitledBorder("Gerenciar Palestrantes"));

        // Lista de Palestrantes Disponíveis
        palestrantesDisponiveisModel = new DefaultListModel<>();
        palestrantesDisponiveisList = new JList<>(palestrantesDisponiveisModel);
        JScrollPane scrollPaneDisponiveis = new JScrollPane(palestrantesDisponiveisList);
        palestrantesManagementPanel.add(new JLabel("Palestrantes Disponíveis:"), BorderLayout.NORTH);
        palestrantesManagementPanel.add(scrollPaneDisponiveis, BorderLayout.WEST);

        // Botões de Adicionar/Remover
        JPanel moveButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        adicionarPalestranteButton = new JButton(">> Adicionar");
        removerPalestranteButton = new JButton("<< Remover");
        moveButtonPanel.add(adicionarPalestranteButton);
        moveButtonPanel.add(removerPalestranteButton);
        palestrantesManagementPanel.add(moveButtonPanel, BorderLayout.CENTER);

        // Lista de Palestrantes do Evento
        palestrantesEventoModel = new DefaultListModel<>();
        palestrantesEventoList = new JList<>(palestrantesEventoModel);
        JScrollPane scrollPaneEvento = new JScrollPane(palestrantesEventoList);
        palestrantesManagementPanel.add(new JLabel("Palestrantes do Evento:"), BorderLayout.NORTH);
        palestrantesManagementPanel.add(scrollPaneEvento, BorderLayout.EAST);

        // --- Adicionar painéis à janela principal ---
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Divide em 2 colunas
        contentPanel.add(formPanel);
        contentPanel.add(palestrantesManagementPanel);
        add(contentPanel, BorderLayout.CENTER);


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
        carregarListasDePalestrantes(); // Carrega as listas de palestrantes (disponíveis e do evento)

        // Listeners dos botões
        salvarButton.addActionListener(e -> salvarEvento());
        cancelarButton.addActionListener(e -> dispose());
        adicionarPalestranteButton.addActionListener(e -> adicionarPalestranteAoEvento());
        removerPalestranteButton.addActionListener(e -> removerPalestranteDoEvento());
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
            // Separa os palestrantes já associados do evento
            List<Palestrante> palestrantesAssociados = eventoParaEditar.getPalestrantes();
            
            for (Palestrante p : todosPalestrantes) {
                if (palestrantesAssociados.contains(p)) {
                    palestrantesEventoModel.addElement(p);
                } else {
                    palestrantesDisponiveisModel.addElement(p);
                }
            }
        } else {
            // Se for um novo evento, todos os palestrantes estão disponíveis inicialmente
            for (Palestrante p : todosPalestrantes) {
                palestrantesDisponiveisModel.addElement(p);
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

    private void salvarEvento() {
        try {
            String nome = nomeField.getText();
            String descricao = descricaoField.getText();
            LocalDate data = LocalDate.parse(dataField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String local = localField.getText();
            int capacidade = Integer.parseInt(capacidadeField.getText());

            // Coleta a lista final de palestrantes do evento
            List<Palestrante> palestrantesFinais = new ArrayList<>();
            for (int i = 0; i < palestrantesEventoModel.size(); i++) {
                palestrantesFinais.add(palestrantesEventoModel.getElementAt(i));
            }

            if (eventoParaEditar == null) { // Modo de criação
                Evento novoEvento = eventoService.criarEvento(nome, descricao, data, local, capacidade);
                // Após criar, associar os palestrantes
                for (Palestrante p : palestrantesFinais) {
                    eventoService.associarPalestrante(novoEvento.getId(), p);
                }
                JOptionPane.showMessageDialog(this, "Evento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Modo de edição
                // O método atualizarEvento no service agora lida com a lista completa
                eventoService.atualizarEvento(eventoParaEditar.getId(), nome, descricao, data, local, capacidade, palestrantesFinais);
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
