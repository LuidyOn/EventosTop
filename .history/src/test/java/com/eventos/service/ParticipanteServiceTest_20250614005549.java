package com.eventos.service;

import com.eventos.dao.EventoDao;
import com.eventos.dao.ParticipanteDao;
import com.eventos.model.Evento;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ParticipanteService")
public class ParticipanteServiceTest {

    @Mock
    private ParticipanteDao participanteDao;

    @Mock
    private EventoDao eventoDao; // Mock do EventoDao também, pois ParticipanteService o utiliza

    @InjectMocks
    private ParticipanteService participanteService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test if needed
    }

    @Test
    @DisplayName("Deve criar um participante com sucesso")
    void deveCriarParticipanteComSucesso() {
        String nome = "Carlos Souza";
        String email = "carlos@email.com";

        when(participanteDao.criarParticipante(any(Participante.class)))
            .thenAnswer(invocation -> {
                Participante p = invocation.getArgument(0);
                return new Participante(1, p.getNome(), p.getEmail());
            });

        Participante participanteCriado = participanteService.criarParticipante(nome, email);

        assertNotNull(participanteCriado);
        assertEquals(1, participanteCriado.getId());
        assertEquals(nome, participanteCriado.getNome());
        verify(participanteDao, times(1)).criarParticipante(any(Participante.class));
    }

    @Test
    @DisplayName("Não deve criar participante com email inválido")
    void naoDeveCriarParticipanteComEmailInvalido() {
        String nome = "Testador";
        String email = "emailinvalido"; // Sem @

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            participanteService.criarParticipante(nome, email);
        });

        assertEquals("Email inválido.", thrown.getMessage());
        verify(participanteDao, never()).criarParticipante(any(Participante.class));
    }

    @Test
    @DisplayName("Deve listar participantes existentes")
    void deveListarParticipantesExistentes() {
        List<Participante> participantesMock = Arrays.asList(
            new Participante(1, "Pedro", "pedro@email.com"),
            new Participante(2, "Julia", "julia@email.com")
        );

        when(participanteDao.listarParticipantes()).thenReturn(participantesMock);
        // O Service buscará os eventos inscritos, então precisamos mockar essa chamada também
        when(participanteDao.buscarEventosPorParticipante(anyInt())).thenReturn(Collections.emptyList());


        List<Participante> participantes = participanteService.listarParticipantes();

        assertNotNull(participantes);
        assertEquals(2, participantes.size());
        assertEquals("Pedro", participantes.get(0).getNome());
        verify(participanteDao, times(1)).listarParticipantes();
    }

    @Test
    @DisplayName("Deve inscrever participante em evento futuro com sucesso")
    void deveInscreverParticipanteEmEventoFuturoComSucesso() {
        int participanteId = 1;
        int eventoId = 101;
        
        Participante participanteMock = new Participante(participanteId, "João", "joao@example.com");
        Evento eventoMock = new Evento(eventoId, "Evento Futuro", "Descrição", LocalDate.now().plusDays(5), "Local", 100);

        when(participanteDao.buscarParticipantePorId(participanteId)).thenReturn(Optional.of(participanteMock));
        when(eventoDao.buscarEventoPorId(eventoId)).thenReturn(Optional.of(eventoMock));
        
        // Não retorna nada (void), apenas verifica se foi chamado
        doNothing().when(participanteDao).inscreverEvento(participanteId, eventoId);

        assertDoesNotThrow(() -> participanteService.inscreverEvento(participanteId, eventoId));

        verify(participanteDao, times(1)).inscreverEvento(participanteId, eventoId);
    }

    @Test
    @DisplayName("Não deve inscrever participante em evento passado")
    void naoDeveInscreverParticipanteEmEventoPassado() {
        int participanteId = 1;
        int eventoId = 102;

        Participante participanteMock = new Participante(participanteId, "Maria", "maria@example.com");
        Evento eventoPassadoMock = new Evento(eventoId, "Evento Passado", "Descrição", LocalDate.now().minusDays(5), "Local", 100);

        when(participanteDao.buscarParticipantePorId(participanteId)).thenReturn(Optional.of(participanteMock));
        when(eventoDao.buscarEventoPorId(eventoId)).thenReturn(Optional.of(eventoPassadoMock));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            participanteService.inscreverEvento(participanteId, eventoId);
        });

        assertEquals("Não é possível inscrever-se em um evento passado.", thrown.getMessage());
        verify(participanteDao, never()).inscreverEvento(anyInt(), anyInt()); // Verifica que não tentou inscrever
    }
}
