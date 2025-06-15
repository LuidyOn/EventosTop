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
import com.eventos.model.Palestrante;
import com.eventos.model.Participante;


public class EventoDao {
    private final PalestranteDao palestranteDAO;
    private final ParticipanteDao participanteDAO;

    public EventoDao() {
        this.palestranteDAO = new PalestranteDao();
        this.participanteDAO = new ParticipanteDao();
    }

    
    public Evento criarEvento(Evento evento) {
        String sql = "INSERT INTO eventos (nome, descricao, data, local, capacidade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, java.sql.Date.valueOf(evento.getData()));
            stmt.setString(4, evento.getLocal());
            stmt.setInt(5, evento.getCapacidade());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Evento(id, evento.getNome(), evento.getDescricao(), evento.getData(),
                        evento.getLocal(), evento.getCapacidade());
            }
            throw new SQLException("Falha ao obter ID do evento criado.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar evento: " + e.getMessage());
        }
    }

    
    public Optional<Evento> atualizarEvento(Evento evento) {
        String sql = "UPDATE eventos SET nome = ?, descricao = ?, data = ?, local = ?, capacidade = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, java.sql.Date.valueOf(evento.getData()));
            stmt.setString(4, evento.getLocal());
            stmt.setInt(5, evento.getCapacidade());
            stmt.setInt(6, evento.getId());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Atualizar associações de palestrantes
                removerPalestrantesDoEvento(evento.getId());
                for (Palestrante p : evento.getPalestrantes()) {
                    associarPalestrante(evento.getId(), p.getId());
                }
                // Atualizar associações de participantes
                removerParticipantesDoEvento(evento.getId());
                for (Participante p : evento.getParticipantesInscritos()) {
                    associarParticipante(evento.getId(), p.getId());
                }
                return Optional.of(evento);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar evento: " + e.getMessage());
        }
    }

    public Optional<Evento> buscarEventoPorId(int id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Evento evento = new Evento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        LocalDate.parse(rs.getString("data")),
                        rs.getString("local"),
                        rs.getInt("capacidade")
                );

                List<Palestrante> palestrantes = palestranteDAO.buscarPalestrantesPorEvento(id);
                palestrantes.forEach(evento::adicionarPalestrante);
                
                List<Participante> participantes = participanteDAO.buscarParticipantesPorEvento(id);
                participantes.forEach(evento::adicionarParticipante);

                return Optional.of(evento);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar evento: " + e.getMessage());
        }
    }

    public List<Evento> listarEventos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Evento evento = new Evento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        LocalDate.parse(rs.getString("data")),
                        rs.getString("local"),
                        rs.getInt("capacidade")
                );
    
                List<Palestrante> palestrantes = palestranteDAO.buscarPalestrantesPorEvento(evento.getId());
                palestrantes.forEach(evento::adicionarPalestrante);
                
                List<Participante> participantes = participanteDAO.buscarParticipantesPorEvento(evento.getId());
                participantes.forEach(evento::adicionarParticipante);

                eventos.add(evento);
            }
            return eventos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos: " + e.getMessage());
        }
    }

    
    public boolean cancelarEvento(int id) {
        removerPalestrantesDoEvento(id);
        removerParticipantesDoEvento(id);

        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar evento: " + e.getMessage());
        }
    }

   
    public void associarPalestrante(int eventoId, int palestranteId) {
        String sql = "INSERT INTO evento_palestrante (evento_id, palestrante_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.setInt(2, palestranteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Palestrante já associado ao evento. Ignorando.");
            } else {
                throw new RuntimeException("Erro ao associar palestrante: " + e.getMessage());
            }
        }
    }

    
    public void removerPalestrantesDoEvento(int eventoId) {
        String sql = "DELETE FROM evento_palestrante WHERE evento_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover palestrantes: " + e.getMessage());
        }
    }

    public void removerPalestranteDoEvento(int eventoId, int palestranteId) {
        String sql = "DELETE FROM evento_palestrante WHERE evento_id = ? AND palestrante_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.setInt(2, palestranteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover palestrante do evento: " + e.getMessage());
        }
    }

    public void associarParticipante(int eventoId, int participanteId) {
        String sql = "INSERT INTO evento_participante (evento_id, participante_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.setInt(2, participanteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Participante já inscrito no evento. Ignorando.");
            } else {
                throw new RuntimeException("Erro ao associar participante ao evento: " + e.getMessage());
            }
        }
    }

    public void removerParticipantesDoEvento(int eventoId) {
        String sql = "DELETE FROM evento_participante WHERE evento_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover participantes do evento: " + e.getMessage());
        }
    }

    public void removerParticipanteDoEvento(int eventoId, int participanteId) {
        String sql = "DELETE FROM evento_participante WHERE evento_id = ? AND participante_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.setInt(2, participanteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover participante do evento: " + e.getMessage());
        }
    }
}
