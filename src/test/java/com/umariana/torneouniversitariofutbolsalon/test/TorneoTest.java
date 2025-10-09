package com.umariana.torneouniversitariofutbolsalon.test;

import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import com.umariana.torneouniversitariofutbolsalon.mundo.Jugador;
import com.umariana.torneouniversitariofutbolsalon.mundo.ConexionBD;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class TorneoTest {

    private Torneo torneo;

    // ===================== CONFIGURACION GENERAL =====================
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
        limpiarBaseDatos();
        torneo = new Torneo();
        System.out.println("\n--- Nuevo test iniciado ---");
    }

    @AfterEach
    public void tearDown() {
        torneo = null;
        System.out.println("--- Test finalizado ---\n");
    }

    // Método auxiliar para limpiar las tablas antes de cada test
    private void limpiarBaseDatos() {
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM jugadores");
            st.executeUpdate("DELETE FROM equipos");
            System.out.println("Base de datos limpiada correctamente");
        } catch (Exception e) {
            System.out.println("Error al limpiar la BD: " + e.getMessage());
        }
    }

    // ===================== HU-1 =====================
    @Test
    public void testRegistrarEquipo() {
        System.out.println("[CP001] Registrar equipo nuevo");
        boolean resultado = torneo.registrarEquipo("Tigres FC");
        assertTrue(resultado, "El equipo debería registrarse correctamente");

        var equipos = torneo.listarEquipos();
        assertEquals(1, equipos.size());
        assertTrue(equipos.get(0).contains("Tigres FC"));
    }

    @Test
    public void testEquipoDuplicado() {
        torneo.registrarEquipo("Tigres FC");
        boolean resultado = torneo.registrarEquipo("Tigres FC");
        assertFalse(resultado, "No debe permitir equipos duplicados");
    }

    // ===================== HU-2 =====================
    @Test
    public void testRegistrarJugador() {
        torneo.registrarEquipo("Tigres FC");
        boolean resultado = torneo.registrarJugador("Tigres FC", "101", "Carlos Perez", "Delantero", 20);
        assertTrue(resultado, "El jugador debería registrarse correctamente");

        List<Jugador> jugadores = torneo.listarJugadoresPorEquipo("Tigres FC");
        assertEquals(1, jugadores.size());
        assertEquals("Carlos Perez", jugadores.get(0).getNombre());
    }

    @Test
    public void testRegistrarJugadorEquipoInexistente() {
        boolean resultado = torneo.registrarJugador("Fantasma FC", "102", "Juan Gomez", "Defensa", 22);
        assertFalse(resultado, "No debería registrar jugador en equipo inexistente");
    }

    @Test
    public void testJugadorDuplicadoEnOtroEquipo() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarEquipo("Leones FC");

        torneo.registrarJugador("Tigres FC", "103", "Carlos Perez", "Delantero", 20);
        boolean resultado = torneo.registrarJugador("Leones FC", "103", "Carlos Perez", "Defensa", 22);
        assertFalse(resultado, "No debería permitir el mismo número de identidad en dos equipos");
    }

    // ===================== HU-3 =====================
    @Test
    public void testListarEquipos() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarEquipo("Leones FC");

        var equipos = torneo.listarEquipos();
        assertEquals(2, equipos.size());
        assertTrue(equipos.stream().anyMatch(e -> e.contains("Leones FC")));
    }

    @Test
    public void testListarEquiposVacio() {
        var equipos = torneo.listarEquipos();
        assertTrue(equipos.isEmpty(), "La lista de equipos debería estar vacía");
    }

    // ===================== HU-4 =====================
    @Test
    public void testListarJugadoresPorEquipo() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarJugador("Tigres FC", "104", "Carlos Perez", "Delantero", 20);
        torneo.registrarJugador("Tigres FC", "105", "Juan Gomez", "Defensa", 22);

        var jugadores = torneo.listarJugadoresPorEquipo("Tigres FC");
        assertEquals(2, jugadores.size());
        assertTrue(jugadores.stream().anyMatch(j -> j.getNombre().equals("Carlos Perez")));
    }

    @Test
    public void testListarJugadoresEquipoVacio() {
        torneo.registrarEquipo("Leones FC");
        var jugadores = torneo.listarJugadoresPorEquipo("Leones FC");
        assertTrue(jugadores.isEmpty(), "El equipo no debería tener jugadores");
    }

    // ===================== HU-5 =====================
    @Test
    public void testEliminarJugador() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarJugador("Tigres FC", "106", "Carlos Perez", "Delantero", 20);

        boolean eliminado = torneo.eliminarJugador("Tigres FC", "106");
        assertTrue(eliminado, "El jugador debería eliminarse correctamente");

        var jugadores = torneo.listarJugadoresPorEquipo("Tigres FC");
        assertTrue(jugadores.isEmpty());
    }

    @Test
    public void testEliminarJugadorInexistente() {
        torneo.registrarEquipo("Tigres FC");
        boolean eliminado = torneo.eliminarJugador("Tigres FC", "999");
        assertFalse(eliminado, "No debería eliminar un jugador inexistente");
    }

    // ===================== HU-6 =====================
    @Test
    public void testEditarJugador() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarJugador("Tigres FC", "107", "Carlos Perez", "Delantero", 20);

        boolean editado = torneo.editarJugador("Tigres FC", "107", "Carlos Ramirez", "Mediocampista", 21);
        assertTrue(editado, "El jugador debería editarse correctamente");

        var jugadores = torneo.listarJugadoresPorEquipo("Tigres FC");
        Jugador j = jugadores.get(0);
        assertEquals("Carlos Ramirez", j.getNombre());
        assertEquals("Mediocampista", j.getPosicion());
        assertEquals(21, j.getEdad());
    }

    @Test
    public void testEditarJugadorInexistente() {
        torneo.registrarEquipo("Tigres FC");
        boolean editado = torneo.editarJugador("Tigres FC", "999", "Otro", "Defensa", 25);
        assertFalse(editado, "No debería editar un jugador inexistente");
    }

    // ===================== HU-7 =====================
    @Test
    public void testEliminarEquipoConJugadores() {
        torneo.registrarEquipo("Tigres FC");
        torneo.registrarJugador("Tigres FC", "108", "Carlos Perez", "Delantero", 20);
        torneo.registrarJugador("Tigres FC", "109", "Juan Gomez", "Defensa", 22);

        boolean eliminado = torneo.eliminarEquipo("Tigres FC");
        assertTrue(eliminado, "El equipo debería eliminarse correctamente");

        var equipos = torneo.listarEquipos();
        assertTrue(equipos.stream().noneMatch(e -> e.contains("Tigres FC")));
    }

    @Test
    public void testEliminarEquipoInexistente() {
        boolean eliminado = torneo.eliminarEquipo("Equipo Fantasma");
        assertFalse(eliminado, "No debería eliminar un equipo inexistente");
    }
}
