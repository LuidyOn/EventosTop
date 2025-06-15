package com.eventos.service;

import com.eventos.dao.EventoDao;
import com.eventos.model.Evento;
import com.eventos.model.Palestrante;
import com.eventos.model.Participante;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita a extensão Mockito para JUnit 5
@DisplayName("Testes para EventoService")
public class EventoServiceTest {

    @Mock // Cria um mock do EventoDao
    private EventoDao eventoDao;

    @InjectMocks // Injeta o mock de EventoDao no EventoService
    private EventoService eventoService;

    // Configuração antes de cada teste
    @BeforeEach
    void setUp() {
        // Nada a fazer aqui por enquanto, @Mock e @InjectMocks já cuidam da inicialização
    }

    @Test
    @DisplayName("Deve criar um evento com sucesso")
    void deveCriarEventoComSucesso() {
        // Dados de entrada
        String nome = "Conferência Tech";
        String descricao = "Maior evento de tecnologia";
        LocalDate data = LocalDate.now().plusDays(10); // Data futura
        String local = "Centro de Convenções";
        int capacidade = 500;

        // Simula o comportamento do DAO ao criar um evento
        // Quando eventoDao.criarEvento for chamado com qualquer Evento, ele deve retornar um novo Evento com ID 1
        when(eventoDao.criarEvento(any(Evento.class)))
            .thenAnswer(invocation -> {
                Evento eventoArgument = invocation.getArgument(0);
                return new Evento(1, eventoArgument.getNome(), eventoArgument.getDescricao(),
                                  eventoArgument.getData(), eventoArgument.getLocal(), eventoArgument.getCapacidade());
            });

        // Chama o método que queremos testar
        Evento eventoCriado = eventoService.criarEvento(nome, descricao, data, local, capacidade);

        // Verifica os resultados
        assertNotNull(eventoCriado);
        assertEquals(1, eventoCriado.getId());
        assertEquals(nome, eventoCriado.getNome());
        // Verifica se o método criarEvento do DAO foi chamado exatamente uma vez
        verify(eventoDao, times(1)).criarEvento(any(Evento.class));
    }

    @Test
    @DisplayName("Não deve criar evento com nome vazio")
    void naoDeveCriarEventoComNomeVazio() {
        String nome = "";
        String descricao = "Descrição";
        LocalDate data = LocalDate.now().plusDays(1);
        String local = "Local";
        int capacidade = 100;

        // Verifica se uma exceção é lançada
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            eventoService.criarEvento(nome, descricao, data, local, capacidade);
        });

        assertEquals("Nome do evento é obrigatório.", thrown.getMessage());
        // Verifica que o DAO nunca foi chamado, pois a validação ocorreu antes
        verify(eventoDao, never()).criarEvento(any(Evento.class));
    }

    @Test
    @DisplayName("Deve listar eventos existentes")
    void deveListarEventosExistentes() {
        // Cria uma lista de eventos mock para o DAO retornar
        List<Evento> eventosMock = Arrays.asList(
            new Evento(1, "Evento A", "Desc A", LocalDate.now(), "Local A", 100),
            new Evento(2, "Evento B", "Desc B", LocalDate.now(), "Local B", 200)
        );

        // Simula o comportamento do DAO
        when(eventoDao.listarEventos()).thenReturn(eventosMock);

        // Chama o método do Service
        List<Evento> eventos = eventoService.listarEventos();

        // Verifica os resultados
        assertNotNull(eventos);
        assertEquals(2, eventos.size());
        assertEquals("Evento A", eventos.get(0).getNome());
        verify(eventoDao, times(1)).listarEventos();
    }

    @Test
    @DisplayName("Deve atualizar evento com sucesso")
    void deveAtualizarEventoComSucesso() {
        // Dados para atualização
        int id = 1;
        String novoNome = "Novo Nome";
        String novaDescricao = "Nova Descrição";
        LocalDate novaData = LocalDate.now().plusDays(20);
        String novoLocal = "Novo Local";
        int novaCapacidade = 600;
        List<Palestrante> palestrantes = Collections.emptyList();
        List<Participante> participantes = Collections.emptyList();

        // Simula o comportamento do DAO
        when(eventoDao.atualizarEvento(any(Evento.class))).thenReturn(Optional.of(new Evento(id, novoNome, novaDescricao, novaData, novoLocal, novaCapacidade)));

        // Chama o método do Service
        Optional<Evento> eventoAtualizado = eventoService.atualizarEvento(id, novoNome, novaDescricao, novaData, novoLocal, novaCapacidade, palestrantes, participantes);

        // Verifica os resultados
        assertTrue(eventoAtualizado.isPresent());
        assertEquals(novoNome, eventoAtualizado.get().getNome());
        verify(eventoDao, times(1)).atualizarEvento(any(Evento.class));
    }

    @Test
    @DisplayName("Deve cancelar evento com sucesso")
    void deveCancelarEventoComSucesso() {
        int id = 1;
        // Simula o comportamento do DAO
        when(eventoDao.cancelarEvento(id)).thenReturn(true);

        // Chama o método do Service
        boolean cancelado = eventoService.cancelarEvento(id);

        // Verifica os resultados
        assertTrue(cancelado);
        verify(eventoDao, times(1)).cancelarEvento(id);
    }
}