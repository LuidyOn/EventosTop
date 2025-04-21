package com.eventos.view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.eventos.model.Evento;
import com.eventos.model.Participante;
import com.eventos.service.EventoService;
import com.eventos.service.ParticipanteService;


public class ParticipanteView {
    private final ParticipanteService participanteService;
    private final EventoService eventoService;
    private final Scanner scanner;

    public ParticipanteView() {
        this.participanteService = new ParticipanteService();
        this.eventoService = new EventoService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n Sistema para Participantes ");
            System.out.println("1. Cadastrar Participante");
            System.out.println("2. Atualizar Dados");
            System.out.println("3. Visualizar Dados");
            System.out.println("4. Inscrever-se em Evento");
            System.out.println("5. Cancelar Inscrição");
            System.out.println("6. Emitir Certificado");
            System.out.println("7. Listar Eventos Disponíveis");
            System.out.println("8. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcao) {
                case 1:
                    cadastrarParticipante();
                    break;
                case 2:
                    atualizarParticipante();
                    break;
                case 3:
                    visualizarParticipante();
                    break;
                case 4:
                    inscreverEvento();
                    break;
                case 5:
                    cancelarInscricao();
                    break;
                case 6:
                    emitirCertificado();
                    break;
                case 7:
                    listarEventosDisponiveis();
                    break;
                case 8:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void cadastrarParticipante() {
        try {
            System.out.print("Nome do participante: ");
            String nome = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();

            Participante participante = participanteService.criarParticipante(nome, email);
            System.out.println("Participante cadastrado com sucesso! ID: " + participante.getId());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void atualizarParticipante() {
        try {
            System.out.print("ID do participante: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Novo nome: ");
            String nome = scanner.nextLine();
            System.out.print("Novo email: ");
            String email = scanner.nextLine();

            Optional<Participante> participante = participanteService.atualizarParticipante(id, nome, email);
            if (participante.isPresent()) {
                System.out.println("Dados atualizados com sucesso!");
            } else {
                System.out.println("Participante não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void visualizarParticipante() {
        System.out.print("ID do participante: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Optional<Participante> participante = participanteService.visualizarParticipante(id);
        if (participante.isPresent()) {
            Participante p = participante.get();
            System.out.println("Participante: " + p.getNome());
            System.out.println("Email: " + p.getEmail());
            System.out.println("Eventos inscritos: " + (p.getEventosInscritos().isEmpty() ? "Nenhum" : p.getEventosInscritos()));
        } else {
            System.out.println("Participante não encontrado.");
        }
    }

    private void inscreverEvento() {
        try {
            System.out.print("ID do participante: ");
            int participanteId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("ID do evento: ");
            int eventoId = scanner.nextInt();
            scanner.nextLine();

            participanteService.inscreverEvento(participanteId, eventoId);
            System.out.println("Inscrição realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void cancelarInscricao() {
        try {
            System.out.print("ID do participante: ");
            int participanteId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("ID do evento: ");
            int eventoId = scanner.nextInt();
            scanner.nextLine();

            participanteService.cancelarInscricao(participanteId, eventoId);
            System.out.println("Inscrição cancelada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void emitirCertificado() {
        try {
            System.out.print("ID do participante: ");
            int participanteId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("ID do evento: ");
            int eventoId = scanner.nextInt();
            scanner.nextLine();

            String certificado = participanteService.emitirCertificado(participanteId, eventoId);
            System.out.println(certificado);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarEventosDisponiveis() {
        List<Evento> eventos = eventoService.listarEventos();
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento disponível.");
        } else {
            for (Evento e : eventos) {
                System.out.println("ID: " + e.getId() + ", Nome: " + e.getNome() + ", Data: " + 
                                  e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }
    }
}
