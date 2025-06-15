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
        Evento evento = new Evento(0, nome, descricao, data, local, capacidade);
        return eventoDAO.criarEvento(evento);
    }

    
    public Optional<Evento> atualizarEvento(int id, String nome, String descricao, LocalDate data, 
                                            String local, int capacidade, List<Palestrante> palestrantes) {
        validarCampos(nome, descricao, data, local, capacidade);
        Evento evento = new Evento(id, nome, descricao, data, local, capacidade);
        palestrantes.forEach(evento::adicionarPalestrante);
        return eventoDAO.atualizarEvento(evento);
    }

    
    public void associarPalestrante(int eventoId, Palestrante palestrante) {
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }
        if (palestrante == null) {
            throw new IllegalArgumentException("Palestrante não pode ser nulo.");
        }
        eventoDAO.associarPalestrante(eventoId, palestrante.getId());
    }

    
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
            throw new IllegalArgumentException("Data do evento deve ser futura.");
        }
        if (local == null || local.trim().isEmpty()) {
            throw new IllegalArgumentException("Local do evento é obrigatório.");
        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
    }
}
