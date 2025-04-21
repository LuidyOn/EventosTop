package com.eventos.service;

import java.util.List;
import java.util.Optional;

import com.eventos.dao.PalestranteDao;
import com.eventos.model.Evento;
import com.eventos.model.Palestrante;



public class PalestranteService {
    private final PalestranteDao palestranteDao;

    public PalestranteService() {
        this.palestranteDao = new PalestranteDao();
    }

    
    public Palestrante criarPalestrante(String nome, String curriculo, String areaAtuacao) {
        validarCampos(nome, curriculo, areaAtuacao);
        Palestrante palestrante = new Palestrante(0, nome, curriculo, areaAtuacao);
        return palestranteDao.criarPalestrante(palestrante);
    }

    
    public Optional<Palestrante> atualizarPalestrante(int id, String nome, String curriculo, String areaAtuacao) {
        validarCampos(nome, curriculo, areaAtuacao);
        Palestrante palestrante = new Palestrante(id, nome, curriculo, areaAtuacao);
        return palestranteDao.atualizarPalestrante(palestrante);
    }

    
    public void associarEvento(int palestranteId, Evento evento) {
        Optional<Palestrante> palestranteOpt = palestranteDao.buscarPalestrantePorId(palestranteId);
        if (!palestranteOpt.isPresent()) {
            throw new IllegalArgumentException("Palestrante não encontrado com ID: " + palestranteId);
        }
        if (evento == null) {
            throw new IllegalArgumentException("Evento não pode ser nulo.");
        }
        palestranteOpt.get().adicionarEvento(evento);
    }

    
    public Optional<Palestrante> visualizarPalestrante(int id) {
        return palestranteDao.buscarPalestrantePorId(id);
    }

    
    public List<Palestrante> listarPalestrantes() {
        return palestranteDao.listarPalestrantes();
    }

    
    public boolean excluirPalestrante(int id) {
        return palestranteDao.excluirPalestrante(id);
    }

    
    private void validarCampos(String nome, String curriculo, String areaAtuacao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do palestrante é obrigatório.");
        }
        if (curriculo == null || curriculo.trim().isEmpty()) {
            throw new IllegalArgumentException("Currículo do palestrante é obrigatório.");
        }
        if (areaAtuacao == null || areaAtuacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Área de atuação do palestrante é obrigatória.");
        }
    }
}
