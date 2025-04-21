package com.eventos.view;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.eventos.model.Palestrante;
import com.eventos.service.PalestranteService;


public class PalestranteView {
    private final PalestranteService palestranteService;
    private final Scanner scanner;

    public PalestranteView() {
        this.palestranteService = new PalestranteService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n Gerenciamento de Palestrantes");
            System.out.println("1. Cadastrar Palestrante");
            System.out.println("2. Atualizar Palestrante");
            System.out.println("3. Visualizar Palestrante");
            System.out.println("4. Listar Palestrantes");
            System.out.println("5. Excluir Palestrante");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcao) {
                case 1:
                    cadastrarPalestrante();
                    break;
                case 2:
                    atualizarPalestrante();
                    break;
                case 3:
                    visualizarPalestrante();
                    break;
                case 4:
                    listarPalestrantes();
                    break;
                case 5:
                    excluirPalestrante();
                    break;
                case 6:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void cadastrarPalestrante() {
        try {
            System.out.print("Nome do palestrante: ");
            String nome = scanner.nextLine();
            System.out.print("Currículo: ");
            String curriculo = scanner.nextLine();
            System.out.print("Área de atuação: ");
            String areaAtuacao = scanner.nextLine();

            Palestrante palestrante = palestranteService.criarPalestrante(nome, curriculo, areaAtuacao);
            System.out.println("Palestrante cadastrado com sucesso! ID: " + palestrante.getId());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void atualizarPalestrante() {
        try {
            System.out.print("ID do palestrante a atualizar: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Novo nome: ");
            String nome = scanner.nextLine();
            System.out.print("Novo currículo: ");
            String curriculo = scanner.nextLine();
            System.out.print("Nova área de atuação: ");
            String areaAtuacao = scanner.nextLine();

            Optional<Palestrante> palestrante = palestranteService.atualizarPalestrante(id, nome, curriculo, areaAtuacao);
            if (palestrante.isPresent()) {
                System.out.println("Palestrante atualizado com sucesso!");
            } else {
                System.out.println("Palestrante não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void visualizarPalestrante() {
        System.out.print("ID do palestrante: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Optional<Palestrante> palestrante = palestranteService.visualizarPalestrante(id);
        if (palestrante.isPresent()) {
            Palestrante p = palestrante.get();
            System.out.println("Palestrante: " + p.getNome());
            System.out.println("Currículo: " + p.getCurriculo());
            System.out.println("Área de atuação: " + p.getAreaAtuacao());
            System.out.println("Eventos: " + (p.getEventos().isEmpty() ? "Nenhum" : p.getEventos()));
        } else {
            System.out.println("Palestrante não encontrado.");
        }
    }

    private void listarPalestrantes() {
        List<Palestrante> palestrantes = palestranteService.listarPalestrantes();
        if (palestrantes.isEmpty()) {
            System.out.println("Nenhum palestrante cadastrado.");
        } else {
            for (Palestrante p : palestrantes) {
                System.out.println("ID: " + p.getId() + ", Nome: " + p.getNome() + ", Área: " + p.getAreaAtuacao());
            }
        }
    }

    private void excluirPalestrante() {
        System.out.print("ID do palestrante a excluir: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        if (palestranteService.excluirPalestrante(id)) {
            System.out.println("Palestrante excluído com sucesso!");
        } else {
            System.out.println("Palestrante não encontrado.");
        }
    }
}
