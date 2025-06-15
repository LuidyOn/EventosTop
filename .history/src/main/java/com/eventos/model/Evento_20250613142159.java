package com.eventos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Importar Objects

public class Evento {
    private int id;
    private String nome;
    private String descricao;
    private LocalDate data;
    private String local;
    private int capacidade;
    private List<Palestrante> palestrantes;
    private List<Participante> participantesInscritos; // NOVO: Lista de participantes

    public Evento(int id, String nome, String descricao, LocalDate data, String local, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.capacidade = capacidade;
        this.palestrantes = new ArrayList<>();
        this.participantesInscritos = new ArrayList<>(); // Inicializar a lista
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDate getData() { return data; }
    public String getLocal() { return local; }
    public int getCapacidade() { return capacidade; }
    public List<Palestrante> getPalestrantes() { return palestrantes; }
    public List<Participante> getParticipantesInscritos() { return participantesInscritos; } // NOVO GETTER

    public void adicionarPalestrante(Palestrante palestrante) {
        if (palestrante != null && !palestrantes.contains(palestrante)) {
            palestrantes.add(palestrante);
        }
    }

    // NOVO: Método para adicionar participante (usado pelo DAO)
    public void adicionarParticipante(Participante participante) {
        if (participante != null && !participantesInscritos.contains(participante)) {
            participantesInscritos.add(participante);
        }
    }

    @Override
    public String toString() {
        return id + " - " + nome + " (" + data + ")"; // Útil para JList de eventos
    }

    // IMPORTE: Adicione os métodos equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id == evento.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}