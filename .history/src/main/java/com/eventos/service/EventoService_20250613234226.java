package com.eventos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.eventos.dao.EventoDao;
import com.eventos.model.Evento;
import com.eventos.model.Palestrante;
import com.eventos.model.Participante; // Importar Participante


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

    // Agora o atualizarEvento também aceita a lista de participantes
    public Optional<Evento> atualizarEvento(int id, String nome, String descricao, LocalDate data, 
                                            String local, int capacidade, 
                                            List<Palestrante> palestrantes, List<Participante> participantes) { // NOVO PARÂMETRO
        validarCampos(nome, descricao, data, local, capacidade);
        Evento evento = new Evento(id, nome, descricao, data, local, capacidade);
        
        // Adiciona palestrantes ao objeto Evento para que o DAO possa processá-los
        palestrantes.forEach(evento::adicionarPalestrante); 
        // NOVO: Adiciona participantes ao objeto Evento para que o DAO possa processá-los
        participantes.forEach(evento::adicionarParticipante);

        return eventoDAO.atualizarEvento(evento);
    }

    
    public void associarPalestrante(int eventoId, Palestrante palestrante) {
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }
        if (palestrante == null || palestrante.getId() == 0) {
            throw new IllegalArgumentException("Palestrante inválido para associação.");
        }
        eventoDAO.associarPalestrante(eventoId, palestrante.getId());
    }

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

    // NOVO: Método para associar um participante a um evento
    public void associarParticipante(int eventoId, Participante participante) {
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }
        if (participante == null || participante.getId() == 0) {
            throw new IllegalArgumentException("Participante inválido para associação.");
        }
        eventoDAO.associarParticipante(eventoId, participante.getId());
    }

    // NOVO: Método para desassociar um participante de um evento
    public void desassociarParticipante(int eventoId, Participante participante) {
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }
        if (participante == null || participante.getId() == 0) {
            throw new IllegalArgumentException("Participante inválido para desassociação.");
        }
        eventoDAO.removerParticipanteDoEvento(eventoId, participante.getId());
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
        // Se a data do evento deve ser futura, mantenha esta linha.
        // Se eventos passados puderem ser criados/atualizados, remova-a.
        // if (data.isBefore(LocalDate.now())) {
        //     throw new IllegalArgumentException("Data do evento deve ser futura.");
        // }
        if (local == null || local.trim().isEmpty()) {
            throw new IllegalArgumentException("Local do evento é obrigatório.");
        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
    }
}
