package com.eventos.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.eventos.model.Evento;
import com.eventos.model.Palestrante;
import com.eventos.service.EventoService;


public class EventoView {
    private final EventoService eventoService;
    private final Scanner scanner;

    public EventoView() {
        this.eventoService = new EventoService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        while (true) {
            System.out.println("\nSistema de Gerenciamento de Eventos");
            System.out.println("1. Criar Evento");
            System.out.println("2. Atualizar Evento");
            System.out.println("3. Visualizar Evento");
            System.out.println("4. Listar Eventos");
            System.out.println("5. Cancelar Evento");
            System.out.println("6. Associar Palestrante");
            System.out.println("7. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcao) {
                case 1:
                    criarEvento();
                    break;
                case 2:
                    atualizarEvento();
                    break;
                case 3:
                    visualizarEvento();
                    break;
                case 4:
                    listarEventos();
                    break;
                case 5:
                    cancelarEvento();
                    break;
                case 6:
                    associarPalestrante();
                    break;
                case 7:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void criarEvento() {
        try {
            System.out.print("Nome do evento: ");
            String nome = scanner.nextLine();
            System.out.print("Descrição: ");
            String descricao = scanner.nextLine();
            System.out.print("Data (dd/MM/yyyy): ");
            String dataStr = scanner.nextLine();
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.print("Local: ");
            String local = scanner.nextLine();
            System.out.print("Capacidade: ");
            int capacidade = scanner.nextInt();
            scanner.nextLine();

            Evento evento = eventoService.criarEvento(nome, descricao, data, local, capacidade);
            System.out.println("Evento criado com sucesso! ID: " + evento.getId());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void atualizarEvento() {
        try {
            System.out.print("ID do evento a atualizar: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Novo nome: ");
            String nome = scanner.nextLine();
            System.out.print("Nova descrição: ");
            String descricao = scanner.nextLine();
            System.out.print("Nova data (dd/MM/yyyy): ");
            String dataStr = scanner.nextLine();
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.print("Novo local: ");
            String local = scanner.nextLine();
            System.out.print("Nova capacidade: ");
            int capacidade = scanner.nextInt();
            scanner.nextLine();

            List<Palestrante> palestrantes = new ArrayList<>();
            System.out.print("Deseja adicionar palestrantes? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                while (true) {
                    System.out.print("ID do palestrante: ");
                    int palestranteId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nome do palestrante: ");
                    String nomePalestrante = scanner.nextLine();
                    System.out.print("Email do palestrante: ");
                    String emailPalestrante = scanner.nextLine();
                    palestrantes.add(new Palestrante(palestranteId, nomePalestrante, emailPalestrante, emailPalestrante));
                    System.out.print("Adicionar outro palestrante? (s/n): ");
                    if (!scanner.nextLine().equalsIgnoreCase("s")) {
                        break;
                    }
                }
            }

            Optional<Evento> evento = eventoService.atualizarEvento(id, nome, descricao, data, local, capacidade, palestrantes);
            if (evento.isPresent()) {
                System.out.println("Evento atualizado com sucesso!");
            } else {
                System.out.println("Evento não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void visualizarEvento() {
        System.out.print("ID do evento: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Optional<Evento> evento = eventoService.visualizarEvento(id);
        if (evento.isPresent()) {
            Evento e = evento.get();
            System.out.println("Evento: " + e.getNome());
            System.out.println("Descrição: " + e.getDescricao());
            System.out.println("Data: " + e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("Local: " + e.getLocal());
            System.out.println("Capacidade: " + e.getCapacidade());
            System.out.println("Palestrantes: " + (e.getPalestrantes().isEmpty() ? "Nenhum" : e.getPalestrantes()));
        } else {
            System.out.println("Evento não encontrado.");
        }
    }

    private void listarEventos() {
        List<Evento> eventos = eventoService.listarEventos();
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
        } else {
            for (Evento e : eventos) {
                System.out.println("ID: " + e.getId() + ", Nome: " + e.getNome() + ", Data: " + 
                                  e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }
    }

    private void cancelarEvento() {
        System.out.print("ID do evento a cancelar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        if (eventoService.cancelarEvento(id)) {
            System.out.println("Evento cancelado com sucesso!");
        } else {
            System.out.println("Evento não encontrado.");
        }
    }

    private void associarPalestrante() {
        try {
            System.out.print("ID do evento: ");
            int eventoId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("ID do palestrante: ");
            int palestranteId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Nome do palestrante: ");
            String nome = scanner.nextLine();
            System.out.print("Email do palestrante: ");
            String email = scanner.nextLine();

            Palestrante palestrante = new Palestrante(palestranteId, nome, email, email);
            eventoService.associarPalestrante(eventoId, palestrante);
            System.out.println("Palestrante associado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
