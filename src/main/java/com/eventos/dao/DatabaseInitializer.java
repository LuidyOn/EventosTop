package com.eventos.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

           
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS eventos (
                            id SERIAL PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            descricao TEXT,
                            data DATE NOT NULL,
                            local VARCHAR(100) NOT NULL,
                            capacidade INT NOT NULL
                        )
                    """);

            
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS palestrantes (
                            id SERIAL PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            curriculo TEXT,
                            area_atuacao VARCHAR(50) NOT NULL
                        )
                    """);

            
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS participantes (
                            id SERIAL PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            email VARCHAR(100) UNIQUE NOT NULL
                        )
                    """);

            
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS evento_palestrante (
                            evento_id INT REFERENCES eventos(id) ON DELETE CASCADE,
                            palestrante_id INT REFERENCES palestrantes(id) ON DELETE CASCADE,
                            PRIMARY KEY (evento_id, palestrante_id)
                        )
                    """);

            
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS evento_participante (
                            evento_id INT REFERENCES eventos(id) ON DELETE CASCADE,
                            participante_id INT REFERENCES participantes(id) ON DELETE CASCADE,
                            PRIMARY KEY (evento_id, participante_id)
                        )
                    """);

            System.out.println(" Tabelas criadas com sucesso!");

        } catch (SQLException e) {
            System.err.println(" Erro ao criar tabelas: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
