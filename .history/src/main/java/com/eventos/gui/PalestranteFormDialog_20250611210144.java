package com.eventos.gui;

import com.eventos.model.Palestrante;
import com.eventos.service.PalestranteService;

import javax.swing.*;
import java.awt.*;

public class PalestranteFormDialog extends JDialog {

    private JTextField idField, nomeField, curriculoField, areaAtuacaoField;
    private JButton salvarButton, cancelarButton;
    private PalestranteService palestranteService;
    private Palestrante palestranteParaEditar;
    private boolean salvo = false;

    public PalestranteFormDialog(JFrame parent, Palestrante palestrante) {
        super(parent, true); // Modal dialog
        this.palestranteService = new PalestranteService();
        this.palestranteParaEditar = palestrante;

        setTitle(palestrante == null ? "Novo Palestrante" : "Editar Palestrante");
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        idField = new JTextField();
        idField.setEditable(false);

        nomeField = new JTextField();
        curriculoField = new JTextField();
        areaAtuacaoField = new JTextField();

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Nome:"));
        formPanel.add(nomeField);
        formPanel.add(new JLabel("Currículo:"));
        formPanel.add(curriculoField);
        formPanel.add(new JLabel("Área de Atuação:"));
        formPanel.add(areaAtuacaoField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (palestranteParaEditar != null) {
            preencherCampos();
        }

        salvarButton.addActionListener(e -> salvarPalestrante());
        cancelarButton.addActionListener(e -> dispose());
    }

    private void preencherCampos() {
        idField.setText(String.valueOf(palestranteParaEditar.getId()));
        nomeField.setText(palestranteParaEditar.getNome());
        curriculoField.setText(palestranteParaEditar.getCurriculo());
        areaAtuacaoField.setText(palestranteParaEditar.getAreaAtuacao());
    }

    private void salvarPalestrante() {
        try {
            String nome = nomeField.getText();
            String curriculo = curriculoField.getText();
            String areaAtuacao = areaAtuacaoField.getText();

            if (palestranteParaEditar == null) { // Modo de criação
                palestranteService.criarPalestrante(nome, curriculo, areaAtuacao);
                JOptionPane.showMessageDialog(this, "Palestrante cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Modo de edição
                palestranteService.atualizarPalestrante(palestranteParaEditar.getId(), nome, curriculo, areaAtuacao);
                JOptionPane.showMessageDialog(this, "Palestrante atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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