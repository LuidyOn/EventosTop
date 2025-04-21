package com.eventos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private int id;
    private String nome;
    private String descricao;
    private LocalDate data;
    private String local;
    private int capacidade;
    private List<Palestrante> palestrantes;

    public Evento(int id, String nome, String descricao, LocalDate data, String local, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.capacidade = capacidade;
        this.palestrantes = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDate getData() { return data; }
    public String getLocal() { return local; }
    public int getCapacidade() { return capacidade; }
    public List<Palestrante> getPalestrantes() { return palestrantes; }

    public void adicionarPalestrante(Palestrante palestrante) {
        if (palestrante != null && !palestrantes.contains(palestrante)) {
            palestrantes.add(palestrante);
        }
    }
}
