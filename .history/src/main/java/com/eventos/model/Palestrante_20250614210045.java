package com.eventos.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Palestrante {
    private int id;
    private String nome;
    private String curriculo;
    private String areaAtuacao;
    private List<Evento> eventos;

    public Palestrante(int id, String nome, String curriculo, String areaAtuacao) {
        this.id = id;
        this.nome = nome;
        this.curriculo = curriculo;
        this.areaAtuacao = areaAtuacao;
        this.eventos = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCurriculo() { return curriculo; }
    public void setCurriculo(String curriculo) { this.curriculo = curriculo; }
    public String getAreaAtuacao() { return areaAtuacao; }
    public void setAreaAtuacao(String areaAtuacao) { this.areaAtuacao = areaAtuacao; }
    public List<Evento> getEventos() { return new ArrayList<>(eventos); }
    public void adicionarEvento(Evento evento) { this.eventos.add(evento); }

    @Override
    public String toString() {
        return id + " - " + nome + " (" + areaAtuacao + ")"; // Formato útil para exibição na JList
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Palestrante that = (Palestrante) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
