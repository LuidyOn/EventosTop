package com.eventos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.eventos.dao.EventoDao;
import com.eventos.model.Evento;
import com.eventos.model.Palestrante;


public class EventoService {
    private final EventoDao eventoDAO;

    public EventoService() {
        this.eventoDAO = new EventoDao();
    }

    
    public Evento criarEvento(String nome, String descricao, LocalDate data, String local, int capacidade) {
        validarCampos(nome, descricao, data, local, capacidade);
        // O ID 0 é um placeholder, o DAO gerará o ID real
        Evento evento = new Evento(0, nome, descricao, data, local, capacidade); 
        return eventoDAO.criarEvento(evento);
    }

    
    public Optional<Evento> atualizarEvento(int id, String nome, String descricao, LocalDate data, 
                                            String local, int capacidade, List<Palestrante> palestrantes) {
        validarCampos(nome, descricao, data, local, capacidade);
        Evento evento = new Evento(id, nome, descricao, data, local, capacidade);
        // Garante que o objeto evento que será passado para o DAO tenha a lista de palestrantes
        palestrantes.forEach(evento::adicionarPalestrante); 
        return eventoDAO.atualizarEvento(evento);
    }

    
    public void associarPalestrante(int eventoId, Palestrante palestrante) {
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }
        if (palestrante == null || palestrante.getId() == 0) { // Garante que o palestrante tem ID válido
            throw new IllegalArgumentException("Palestrante inválido para associação.");
        }
        eventoDAO.associarPalestrante(eventoId, palestrante.getId());
    }

    // NOVO MÉTODO: Desassociar um único palestrante de um evento
    public void desassociarPalestrante(int eventoId, Palestrante palestrante) {
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }
        if (palestrante == null || palestrante.getId() == 0) {
            throw new IllegalArgumentException("Palestrante inválido para desassociação.");
        }
        eventoDAO.removerPalestranteDoEvento(eventoId, palestrante.getId());
    }
    
    // ... restante da classe EventoService (visualizarEvento, listarEventos, cancelarEvento, validarCampos)
    public Optional<Evento> visualizarEvento(int id) {
        return eventoDAO.buscarEventoPorId(id);
    }

    public List<Evento> listarEventos() {
        return eventoDAO.listarEventos();
    }

    public boolean cancelarEvento(int id) {
        return eventoDAO.cancelarEvento(id);
    }

    private void validarCampos(String nome, String descricao, LocalDate data, String local, int capacidade) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do evento é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do evento é obrigatória.");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data do evento é obrigatória.");
        }
        if (data.isBefore(LocalDate.now())) {
            // Depende da regra de negócio. Se puder criar evento passado, remova.
            // Para atualizar, talvez precise de uma validação diferente.
            // Por simplicidade, mantemos a validação para criação/atualização.
            // Se fosse um requisito, teríamos métodos de validação específicos para cada caso.
            // throw new IllegalArgumentException("Data do evento deve ser futura."); 
        }
        if (local == null || local.trim().isEmpty()) {
            throw new IllegalArgumentException("Local do evento é obrigatório.");
        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
    }
}
