package com.eventos.service;

import com.eventos.dao.PalestranteDao;
import com.eventos.model.Palestrante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para PalestranteService")
public class PalestranteServiceTest {

    @Mock
    private PalestranteDao palestranteDao;

    @InjectMocks
    private PalestranteService palestranteService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test if needed, though @Mock handles this often
    }

    @Test
    @DisplayName("Deve criar um palestrante com sucesso")
    void deveCriarPalestranteComSucesso() {
        String nome = "Maria Silva";
        String curriculo = "Doutora em IA";
        String areaAtuacao = "Inteligência Artificial";

        when(palestranteDao.criarPalestrante(any(Palestrante.class)))
            .thenAnswer(invocation -> {
                Palestrante p = invocation.getArgument(0);
                return new Palestrante(1, p.getNome(), p.getCurriculo(), p.getAreaAtuacao());
            });

        Palestrante palestranteCriado = palestranteService.criarPalestrante(nome, curriculo, areaAtuacao);

        assertNotNull(palestranteCriado);
        assertEquals(1, palestranteCriado.getId());
        assertEquals(nome, palestranteCriado.getNome());
        verify(palestranteDao, times(1)).criarPalestrante(any(Palestrante.class));
    }

    @Test
    @DisplayName("Não deve criar palestrante com nome vazio")
    void naoDeveCriarPalestranteComNomeVazio() {
        String nome = "";
        String curriculo = "Curriculo Teste";
        String areaAtuacao = "Area Teste";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            palestranteService.criarPalestrante(nome, curriculo, areaAtuacao);
        });

        assertEquals("Nome do palestrante é obrigatório.", thrown.getMessage());
        verify(palestranteDao, never()).criarPalestrante(any(Palestrante.class));
    }

    @Test
    @DisplayName("Deve listar palestrantes existentes")
    void deveListarPalestrantesExistentes() {
        List<Palestrante> palestrantesMock = Arrays.asList(
            new Palestrante(1, "João", "Bio1", "Dev"),
            new Palestrante(2, "Ana", "Bio2", "Design")
        );

        when(palestranteDao.listarPalestrantes()).thenReturn(palestrantesMock);

        List<Palestrante> palestrantes = palestranteService.listarPalestrantes();

        assertNotNull(palestrantes);
        assertEquals(2, palestrantes.size());
        assertEquals("João", palestrantes.get(0).getNome());
        verify(palestranteDao, times(1)).listarPalestrantes();
    }

    @Test
    @DisplayName("Deve atualizar palestrante com sucesso")
    void deveAtualizarPalestranteComSucesso() {
        int id = 1;
        String novoNome = "João Atualizado";
        String novoCurriculo = "Currículo Atualizado";
        String novaArea = "Nova Área";

        when(palestranteDao.atualizarPalestrante(any(Palestrante.class))).thenReturn(Optional.of(new Palestrante(id, novoNome, novoCurriculo, novaArea)));

        Optional<Palestrante> palestranteAtualizado = palestranteService.atualizarPalestrante(id, novoNome, novoCurriculo, novaArea);

        assertTrue(palestranteAtualizado.isPresent());
        assertEquals(novoNome, palestranteAtualizado.get().getNome());
        verify(palestranteDao, times(1)).atualizarPalestrante(any(Palestrante.class));
    }

    @Test
    @DisplayName("Deve excluir palestrante com sucesso")
    void deveExcluirPalestranteComSucesso() {
        int id = 1;
        when(palestranteDao.excluirPalestrante(id)).thenReturn(true);

        boolean excluido = palestranteService.excluirPalestrante(id);

        assertTrue(excluido);
        verify(palestranteDao, times(1)).excluirPalestrante(id);
    }
}
