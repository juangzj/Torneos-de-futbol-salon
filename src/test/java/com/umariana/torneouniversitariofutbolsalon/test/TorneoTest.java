package com.umariana.torneouniversitariofutbolsalon.test;

import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import com.umariana.torneouniversitariofutbolsalon.mundo.Jugador;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class TorneoTest {

    private Torneo torneo;

    @BeforeAll
    public static void setUpClass() {
        System.out.println("==== Iniciando pruebas de Torneo ====");
    }

    @AfterAll
    public static void tearDownClass() {
        System.out.println("==== Pruebas finalizadas ====");
    }

    @BeforeEach
    public void setUp() {
        torneo = new Torneo();
        System.out.println("\n--- Nuevo test iniciado ---");
    }

    @AfterEach
    public void tearDown() {
        torneo = null;
        System.out.println("--- Test finalizado ---\n");
    }

    // ================= HU-1 =================
    // CP001: Registrar equipo exitosamente
    @Test
    public void testRegistrarEquipo() {
        System.out.println("[CP001] Intentando registrar equipo: Tigres FC");
        boolean resultado = torneo.registrarEquipo("Tigres FC");
        assertTrue(resultado);

        int total = torneo.listarEquipos().size();
        assertEquals(1, total);
        System.out.println("✅ CP001 se realizo correctamente");
    }

    // CP002: Registrar equipo duplicado
    @Test
    public void testEquipoDuplicado() {
        torneo.registrarEquipo("Tigres FC");
        boolean resultado = torneo.registrarEquipo("Tigres FC");
        assertFalse(resultado);
        System.out.println("✅ CP002 se realizo correctamente");
    }

    // ================= HU-2 =================
    // CP003: Registrar jugador valido
    @Test
    public void testRegistrarJugador() {
        torneo.registrarEquipo("Tigres FC");
        boolean resultado = torneo.registrarJugador("Tigres FC", "101", "Carlos Perez", "Delantero", 20);
        assertTrue(resultado);

        List<Jugador> jugadores = torneo.listarJugadoresPorEquipo("Tigres FC");
        assertEquals(1, jugadores.size());
        System.out.println("✅ CP003 se realizo correctamente");
    }

    // CP004: Registrar jugador en equipo inexistente
    @Test
    public void testJugadorEquipoNull() {
        boolean resultado = torneo.registrarJugador("Fantasma FC", "102", "Juan Gomez", "Defensa", 22);
        assertFalse(resultado);
        System.out.println("✅ CP004 se realizo correctamente");
    }

    // CP005: Intentar asignar jugador a dos equipos
    @Test
    public void testJugadorDuplicado() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarEquipo("Leones FC");
        torneo.registrarJugador("Tigres FC", "103", "Carlos Perez", "Delantero", 20);
        boolean resultado = torneo.registrarJugador("Leones FC", "103", "Carlos Perez", "Defensa", 22);
        assertFalse(resultado);
        System.out.println("✅ CP005 se realizo correctamente");
    }

    // ================= HU-3 =================
    // CP006: Listar equipos con registros
    @Test
    public void testListarEquipos() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarEquipo("Leones FC");

        var equipos = torneo.listarEquipos();
        assertEquals(2, equipos.size());
        System.out.println("✅ CP006 se realizo correctamente");
    }

    // CP007: Listar equipos vacio
    @Test
    public void testListarEquiposVacio() {
        var equipos = torneo.listarEquipos();
        assertTrue(equipos.isEmpty());
        System.out.println("✅ CP007 se realizo correctamente");
    }

    // CP008: Listado actualizado tras registrar un nuevo equipo
    @Test
    public void testActualizarListado() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarEquipo("Aguilas FC");
        var equipos = torneo.listarEquipos();

        assertEquals(2, equipos.size());
        assertTrue(equipos.stream().anyMatch(e -> e.contains("Aguilas FC")));
        System.out.println("✅ CP008 se realizo correctamente");
    }

    // ================= HU-4 =================
    // CP009: Listar jugadores de un equipo con miembros
    @Test
    public void testListarJugadoresEquipo() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarJugador("Tigres FC", "104", "Carlos Perez", "Delantero", 20);
        torneo.registrarJugador("Tigres FC", "105", "Juan Gomez", "Defensa", 22);

        var jugadores = torneo.listarJugadoresPorEquipo("Tigres FC");
        assertEquals(2, jugadores.size());
        System.out.println("✅ CP009 se realizo correctamente");
    }

    // CP010: Listar jugadores de un equipo vacio
    @Test
    public void testListarJugadoresVacio() {
        torneo.registrarEquipo("Leones FC");
        var jugadores = torneo.listarJugadoresPorEquipo("Leones FC");
        assertTrue(jugadores.isEmpty());
        System.out.println("✅ CP010 se realizo correctamente");
    }

    // ================= HU-5 =================
    // CP011: Eliminar jugador de un equipo
    @Test
    public void testEliminarJugador() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarJugador("Tigres FC", "106", "Carlos Perez", "Delantero", 20);
        boolean eliminado = torneo.eliminarJugador("Tigres FC", "106");

        assertTrue(eliminado);
        assertTrue(torneo.listarJugadoresPorEquipo("Tigres FC").isEmpty());
        System.out.println("✅ CP011 se realizo correctamente");
    }

    // ================= HU-6 =================
    // CP012: Editar jugador correctamente
    @Test
    public void testEditarJugador() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarJugador("Tigres FC", "107", "Carlos Perez", "Delantero", 20);
        boolean editado = torneo.editarJugador("Tigres FC", "107", "Carlos Ramirez", "Mediocampista", 21);

        assertTrue(editado);
        var jugadores = torneo.listarJugadoresPorEquipo("Tigres FC");
        Jugador j = jugadores.get(0);
        assertEquals("Carlos Ramirez", j.getNombre());
        assertEquals("Mediocampista", j.getPosicion());
        assertEquals(21, j.getEdad());
        System.out.println("✅ CP012 se realizo correctamente");
    }

    // CP013: Intentar editar jugador inexistente
    @Test
    public void testEditarJugadorInexistente() {
        torneo.registrarEquipo("Tigres FC");
        boolean editado = torneo.editarJugador("Tigres FC", "999", "Otro", "Defensa", 25);
        assertFalse(editado);
        System.out.println("✅ CP013 se realizo correctamente");
    }
}
