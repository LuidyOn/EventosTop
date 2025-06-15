// src/main/java/com/eventos/gui/EventoFormDialog.java
package com.eventos.gui;

import com.eventos.model.Evento;
import com.eventos.service.EventoService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList; // Para a lista de palestrantes, embora ainda não implementada no form
import java.util.List;

public class EventoFormDialog extends JDialog {

    private JTextField idField, nomeField, descricaoField, dataField, localField, capacidadeField;
    private JButton salvarButton, cancelarButton;
    private EventoService eventoService;
    private Evento eventoParaEditar; // Armazena o evento se estivermos em modo de edição
    private boolean salvo = false;

    public EventoFormDialog(JFrame parent, Evento evento) {
        super(parent, true); // Modal dialog
        this.eventoService = new EventoService();
        this.eventoParaEditar = evento;

        setTitle(evento == null ? "Novo Evento" : "Editar Evento");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5)); // 6 linhas, 2 colunas, espaçamento
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margem interna

        // Componentes do formulário
        idField = new JTextField();
        idField.setEditable(false); // ID não é editável

        nomeField = new JTextField();
        descricaoField = new JTextField();
        dataField = new JTextField("dd/MM/yyyy"); // Hint para o formato
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

        add(formPanel, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Preenche os campos se estivermos editando um evento
        if (eventoParaEditar != null) {
            preencherCampos();
        }

        // Listeners dos botões
        salvarButton.addActionListener(e -> salvarEvento());
        cancelarButton.addActionListener(e -> dispose()); // Fecha o diálogo
    }

    private void preencherCampos() {
        idField.setText(String.valueOf(eventoParaEditar.getId()));
        nomeField.setText(eventoParaEditar.getNome());
        descricaoField.setText(eventoParaEditar.getDescricao());
        dataField.setText(eventoParaEditar.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        localField.setText(eventoParaEditar.getLocal());
        capacidadeField.setText(String.valueOf(eventoParaEditar.getCapacidade()));
        // Note: A associação de palestrantes seria um pouco mais complexa aqui, talvez com um JList ou JComboBox
    }

    private void salvarEvento() {
        try {
            String nome = nomeField.getText();
            String descricao = descricaoField.getText();
            LocalDate data = LocalDate.parse(dataField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String local = localField.getText();
            int capacidade = Integer.parseInt(capacidadeField.getText());

            if (eventoParaEditar == null) { // Modo de criação
                eventoService.criarEvento(nome, descricao, data, local, capacidade);
                JOptionPane.showMessageDialog(this, "Evento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Modo de edição
                // Para simplificar, não estamos gerenciando palestrantes diretamente neste formulário
                // Você precisaria de um componente para isso, como um JList com uma lista de palestrantes disponíveis
                // e botões para adicionar/remover.
                // Por enquanto, passamos uma lista vazia, o que removeria associações existentes
                // ou você precisaria recuperar os palestrantes atuais e passá-los
                List<com.eventos.model.Palestrante> palestrantes = new ArrayList<>(); // TODO: Implementar seleção de palestrantes
                eventoService.atualizarEvento(eventoParaEditar.getId(), nome, descricao, data, local, capacidade, palestrantes);
                JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            salvo = true; // Indica que a operação foi bem-sucedida
            dispose(); // Fecha o diálogo
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