package com.eventos.gui;

import com.eventos.model.Participante;
import com.eventos.service.ParticipanteService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class ParticipanteFormDialog extends JDialog {

    private JTextField idField, nomeField, emailField;
    private JButton salvarButton, cancelarButton;
    private ParticipanteService participanteService;
    private Participante participanteParaEditar;
    private boolean salvo = false;

    public ParticipanteFormDialog(JFrame parent, Participante participante) {
        super(parent, true); // Modal dialog
        this.participanteService = new ParticipanteService();
        this.participanteParaEditar = participante;

        setTitle(participante == null ? "Novo Participante" : "Editar Participante");
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        idField = new JTextField();
        idField.setEditable(false);

        nomeField = new JTextField();
        emailField = new JTextField();

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Nome:"));
        formPanel.add(nomeField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (participanteParaEditar != null) {
            preencherCampos();
        }

        salvarButton.addActionListener(e -> salvarParticipante());
        cancelarButton.addActionListener(e -> dispose());
    }

    private void preencherCampos() {
        idField.setText(String.valueOf(participanteParaEditar.getId()));
        nomeField.setText(participanteParaEditar.getNome());
        emailField.setText(participanteParaEditar.getEmail());
    }

    private void salvarParticipante() {
        try {
            String nome = nomeField.getText();
            String email = emailField.getText();

            if (participanteParaEditar == null) { // Modo de criação
                participanteService.criarParticipante(nome, email);
                JOptionPane.showMessageDialog(this, "Participante cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Modo de edição
                participanteService.atualizarParticipante(participanteParaEditar.getId(), nome, email);
                JOptionPane.showMessageDialog(this, "Dados do participante atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            salvo = true;
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro de validação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSalvo() {
        return salvo;
    }
}
