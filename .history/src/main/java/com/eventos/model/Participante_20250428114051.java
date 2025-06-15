package com.eventos.model;

import java.util.ArrayList;
import java.util.List;

public class Participante {
    private int id;
    private String nome;
    private String email;
    private List<Evento> eventosInscritos;

    public Participante(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.eventosInscritos = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Evento> getEventosInscritos() { return new ArrayList<>(eventosInscritos); }

    public void inscreverEvento(Evento evento) {
        if (evento != null && !eventosInscritos.contains(evento)) {
            this.eventosInscritos.add(evento);
        }
    }

    public void cancelarInscricao(Evento evento) {
        if (evento != null) {
            this.eventosInscritos.remove(evento);
        }
    }

    @Override
    public String toString() {
        return "Participante{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }
}
