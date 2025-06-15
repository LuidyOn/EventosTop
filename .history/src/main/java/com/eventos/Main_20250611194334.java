package com.eventos;

import java.util.Scanner;

import com.eventos.dao.DatabaseInitializer;
import com.eventos.view.EventoView;
import com.eventos.view.PalestranteView;
import com.eventos.view.ParticipanteView;


public class Main {
  public static void main(String[] args) {
    DatabaseInitializer.initialize();

    Scanner scanner = new Scanner(System.in);
    EventoView eventoView = new EventoView();
    PalestranteView palestranteView = new PalestranteView();
    ParticipanteView participanteView = new ParticipanteView();

    while (true) {
      System.out.println("\n Sistema de Gerenciamento de Eventos");S
      System.out.println("1. Gerenciar Eventos");
      System.out.println("2. Gerenciar Palestrantes");
      System.out.println("3. Gerenciar Participantes");
      System.out.println("4. Sair");
      System.out.print("Escolha uma opção: ");

      int opcao = scanner.nextInt();
      scanner.nextLine(); 

      switch (opcao) {
        case 1:
          eventoView.iniciar();
          break;
        case 2:
          palestranteView.iniciar();
          break;
        case 3:
          participanteView.iniciar();
          break;
        case 4:
          System.out.println("Saindo...");
          scanner.close();
          return;
        default:
          System.out.println("Opção inválida!");
      }
    }
  }
}
