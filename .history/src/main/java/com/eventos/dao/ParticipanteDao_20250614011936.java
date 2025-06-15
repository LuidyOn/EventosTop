package com.eventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.eventos.model.Evento;
import com.eventos.model.Participante;



public class ParticipanteDao {
   
    public Participante criarParticipante(Participante participante) {
        String sql = "INSERT INTO participantes (nome, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, participante.getNome());
            stmt.setString(2, participante.getEmail());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Participante(id, participante.getNome(), participante.getEmail());
            }
            throw new SQLException("Falha ao obter ID do participante criado.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar participante: " + e.getMessage());
        }
    }

    
    public Optional<Participante> atualizarParticipante(Participante participante) {
        String sql = "UPDATE participantes SET nome = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, participante.getNome());
            stmt.setString(2, participante.getEmail());
            stmt.setInt(3, participante.getId());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Manter as associações existentes de eventos para este participante
                // Sua lógica atual já remove e recria, o que é um jeito de lidar
                // com isso se o formulário for "fonte da verdade" para eventos inscritos
                // No entanto, para Participantes, a inscrição é feita à parte,
                // então o atualizarParticipante não precisa remover eventos aqui.
                // A remoção de eventos inscritos deve ser feita pelo método cancelarInscricao.
                // A parte abaixo é sobre como o formulário de participante lidaria com a lista.
                // Mas como o form de participante não gerencia eventos, podemos deixar a lista vazia
                // ou simplesmente remover essa parte aqui se a regra de negócio for que
                // eventos inscritos não são atualizados via form de participante.
                // removerEventosDoParticipante(participante.getId());
                // for (Evento e : participante.getEventosInscritos()) {
                //     inscreverEvento(participante.getId(), e.getId());
                // }
                return Optional.of(participante);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar participante: " + e.getMessage());
        }
    }

    
    public Optional<Participante> buscarParticipantePorId(int id) {
        String sql = "SELECT * FROM participantes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Participante participante = new Participante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email")
                );
               
                List<Evento> eventos = buscarEventosPorParticipante(id);
                eventos.forEach(participante::inscreverEvento);
                return Optional.of(participante);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar participante: " + e.getMessage());
        }
    }

    
    public List<Participante> listarParticipantes() {
        List<Participante> participantes = new ArrayList<>();
        String sql = "SELECT * FROM participantes";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Participante participante = new Participante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email")
                );
               
                List<Evento> eventos = buscarEventosPorParticipante(participante.getId());
                eventos.forEach(participante::inscreverEvento);
                participantes.add(participante);
            }
            return participantes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar participantes: " + e.getMessage());
        }
    }

    public void inscreverEvento(int participanteId, int eventoId) {
        String sql = "INSERT INTO evento_participante (evento_id, participante_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.setInt(2, participanteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                throw new RuntimeException("Erro: Este participante já está inscrito neste evento.");
            }
            throw new RuntimeException("Erro ao inscrever participante: " + e.getMessage());
        }
    }

    public void cancelarInscricao(int participanteId, int eventoId) {
        String sql = "DELETE FROM evento_participante WHERE evento_id = ? AND participante_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.setInt(2, participanteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar inscrição: " + e.getMessage());
        }
    }

    // NOVO MÉTODO: Buscar participantes por evento (para o EventoDao)
    public List<Participante> buscarParticipantesPorEvento(int eventoId) {
        List<Participante> participantes = new ArrayList<>();
        String sql = """
            SELECT p.* FROM participantes p
            JOIN evento_participante ep ON p.id = ep.participante_id
            WHERE ep.evento_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Participante participante = new Participante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email")
                );
                participantes.add(participante);
            }
            return participantes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar participantes por evento: " + e.getMessage());
        }
    }

    public List<Evento> buscarEventosPorParticipante(int participanteId) {
        List<Evento> eventos = new ArrayList<>();
        String sql = """
            SELECT e.* FROM eventos e
            JOIN evento_participante ep ON e.id = ep.evento_id
            WHERE ep.participante_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, participanteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Evento evento = new Evento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        LocalDate.parse(rs.getString("data")),
                        rs.getString("local"),
                        rs.getInt("capacidade")
                );
                eventos.add(evento);
            }
            return eventos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar eventos por participante: " + e.getMessage());
        }
    }

    private] void removerEventosDoParticipante(int participanteId) {
        String sql = "DELETE FROM evento_participante WHERE participante_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, participanteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover eventos: " + e.getMessage());
        }
    }
}
