package com.eventos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.eventos.dao.EventoDao;
import com.eventos.dao.ParticipanteDao;
import com.eventos.model.Evento;
import com.eventos.model.Participante;

public class ParticipanteService {
    private final ParticipanteDao participanteDAO;
    private final EventoDao eventoDAO;

    public ParticipanteService() {
        this.participanteDAO = new ParticipanteDao();
        this.eventoDAO = new EventoDao();
    }

    
    public Participante criarParticipante(String nome, String email) {
        validarCampos(nome, email);
        Participante participante = new Participante(0, nome, email);
        return participanteDAO.criarParticipante(participante);
    }

    
    public Optional<Participante> atualizarParticipante(int id, String nome, String email) {
        validarCampos(nome, email);
        Participante participante = new Participante(id, nome, email);
        return participanteDAO.atualizarParticipante(participante);
    }

    
    public void inscreverEvento(int participanteId, int eventoId) {
        Optional<Participante> participanteOpt = participanteDAO.buscarParticipantePorId(participanteId);
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);

        if (!participanteOpt.isPresent()) {
            throw new IllegalArgumentException("Participante não encontrado com ID: " + participanteId);
        }
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }

        Evento evento = eventoOpt.get();

        // Mantenha esta validação
        if (evento.getData().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Não é possível inscrever-se em um evento passado.");
        }

        participanteDAO.inscreverEvento(participanteId, eventoId);
    }

    public void cancelarInscricao(int participanteId, int eventoId) {
        Optional<Participante> participanteOpt = participanteDAO.buscarParticipantePorId(participanteId);
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);

        if (!participanteOpt.isPresent()) {
            throw new IllegalArgumentException("Participante não encontrado com ID: " + participanteId);
        }
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }

        participanteDAO.cancelarInscricao(participanteId, eventoId);
    }

    
    public String emitirCertificado(int participanteId, int eventoId) {
        Optional<Participante> participanteOpt = participanteDAO.buscarParticipantePorId(participanteId);
        Optional<Evento> eventoOpt = eventoDAO.buscarEventoPorId(eventoId);

        if (!participanteOpt.isPresent()) {
            throw new IllegalArgumentException("Participante não encontrado com ID: " + participanteId);
        }
        if (!eventoOpt.isPresent()) {
            throw new IllegalArgumentException("Evento não encontrado com ID: " + eventoId);
        }

        Participante participante = participanteOpt.get();
        Evento evento = eventoOpt.get();

        
        boolean inscrito = participante.getEventosInscritos().stream()
                .anyMatch(e -> e.getId() == eventoId);
        if (!inscrito) {
            throw new IllegalStateException("Participante não está inscrito no evento.");
        }
        if (evento.getData().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Evento ainda não ocorreu.");
        }

        return "Certificado de Participação\n" +
                "Participante: " + participante.getNome() + "\n" +
                "Evento: " + evento.getNome() + "\n" +
                "Data: " + evento.getData() + "\n" +
                "Local: " + evento.getLocal();
    }

    
    public Optional<Participante> visualizarParticipante(int id) {
        return participanteDAO.buscarParticipantePorId(id);
    }

    
    public List<Participante> listarParticipantes() {
        return participanteDAO.listarParticipantes();
    }

    
    private void validarCampos(String nome, String email) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do participante é obrigatório.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido.");
        }
    }
}
