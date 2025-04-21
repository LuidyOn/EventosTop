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



public class PalestranteDao {
    
    public Palestrante criarPalestrante(Palestrante palestrante) {
        String sql = "INSERT INTO palestrantes (nome, curriculo, area_atuacao) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, palestrante.getNome());
            stmt.setString(2, palestrante.getCurriculo());
            stmt.setString(3, palestrante.getAreaAtuacao());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Palestrante(id, palestrante.getNome(), palestrante.getCurriculo(), palestrante.getAreaAtuacao());
            }
            throw new SQLException("Falha ao obter ID do palestrante criado.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar palestrante: " + e.getMessage());
        }
    }

   
    public Optional<Palestrante> atualizarPalestrante(Palestrante palestrante) {
        String sql = "UPDATE palestrantes SET nome = ?, curriculo = ?, area_atuacao = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, palestrante.getNome());
            stmt.setString(2, palestrante.getCurriculo());
            stmt.setString(3, palestrante.getAreaAtuacao());
            stmt.setInt(4, palestrante.getId());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
               
                removerEventosDoPalestrante(palestrante.getId());
                for (Evento e : palestrante.getEventos()) {
                    associarEvento(palestrante.getId(), e.getId());
                }
                return Optional.of(palestrante);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar palestrante: " + e.getMessage());
        }
    }

   
    public Optional<Palestrante> buscarPalestrantePorId(int id) {
        String sql = "SELECT * FROM palestrantes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Palestrante palestrante = new Palestrante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("curriculo"),
                        rs.getString("area_atuacao")
                );
                
                List<Evento> eventos = buscarEventosPorPalestrante(id);
                eventos.forEach(palestrante::adicionarEvento);
                return Optional.of(palestrante);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar palestrante: " + e.getMessage());
        }
    }

    
    public List<Palestrante> listarPalestrantes() {
        List<Palestrante> palestrantes = new ArrayList<>();
        String sql = "SELECT * FROM palestrantes";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Palestrante palestrante = new Palestrante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("curriculo"),
                        rs.getString("area_atuacao")
                );
                
                List<Evento> eventos = buscarEventosPorPalestrante(palestrante.getId());
                eventos.forEach(palestrante::adicionarEvento);
                palestrantes.add(palestrante);
            }
            return palestrantes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar palestrantes: " + e.getMessage());
        }
    }

    
    public boolean excluirPalestrante(int id) {
        String sql = "DELETE FROM palestrantes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir palestrante: " + e.getMessage());
        }
    }

    
    public List<Palestrante> buscarPalestrantesPorEvento(int eventoId) {
        List<Palestrante> palestrantes = new ArrayList<>();
        String sql = """
            SELECT p.* FROM palestrantes p
            JOIN evento_palestrante ep ON p.id = ep.palestrante_id
            WHERE ep.evento_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Palestrante palestrante = new Palestrante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("curriculo"),
                        rs.getString("area_atuacao")
                );
                palestrantes.add(palestrante);
            }
            return palestrantes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar palestrantes por evento: " + e.getMessage());
        }
    }

  
    private List<Evento> buscarEventosPorPalestrante(int palestranteId) {
        List<Evento> eventos = new ArrayList<>();
        String sql = """
            SELECT e.* FROM eventos e
            JOIN evento_palestrante ep ON e.id = ep.evento_id
            WHERE ep.palestrante_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, palestranteId);
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
            throw new RuntimeException("Erro ao buscar eventos por palestrante: " + e.getMessage());
        }
    }

    
    private void associarEvento(int palestranteId, int eventoId) {
        String sql = "INSERT INTO evento_palestrante (evento_id, palestrante_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            stmt.setInt(2, palestranteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao associar evento: " + e.getMessage());
        }
    }

    
    private void removerEventosDoPalestrante(int palestranteId) {
        String sql = "DELETE FROM evento_palestrante WHERE palestrante_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, palestranteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover eventos: " + e.getMessage());
        }
    }
}
