/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umariana.torneouniversitariofutbolsalon.mundo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Juan Goyes
 */
public class Torneo {

    // ===================== CONSTRUCTOR =====================
    public Torneo() {
        try (Connection conn = ConexionBD.getConnection()) {
            System.out.println("Conexion establecida correctamente");
        } catch (SQLException e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
    }

    // ===================== HU-1: REGISTRAR EQUIPO =====================
    public boolean registrarEquipo(String nombreEquipo) {
        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
            return false;
        }

        String verificar = "SELECT * FROM equipos WHERE nombre = ?";
        String insertar = "INSERT INTO equipos (nombre) VALUES (?)";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement psVerificar = conn.prepareStatement(verificar); PreparedStatement psInsertar = conn.prepareStatement(insertar)) {

            psVerificar.setString(1, nombreEquipo);
            ResultSet rs = psVerificar.executeQuery();

            if (rs.next()) {
                return false; // nombre duplicado
            }

            psInsertar.setString(1, nombreEquipo);
            psInsertar.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== HU-2: REGISTRAR JUGADOR =====================
    public boolean registrarJugador(String nombreEquipo, String numeroIdentidad, String nombreJugador, String posicion, int edad) {
        try (Connection conn = ConexionBD.getConnection()) {

            // Obtener ID del equipo
            String sqlEquipo = "SELECT id FROM equipos WHERE nombre = ?";
            PreparedStatement psEquipo = conn.prepareStatement(sqlEquipo);
            psEquipo.setString(1, nombreEquipo);
            ResultSet rsEquipo = psEquipo.executeQuery();

            if (!rsEquipo.next()) {
                return false; // equipo no existe
            }

            int idEquipo = rsEquipo.getInt("id");

            // Validar duplicado (en general)
            String sqlDup = "SELECT * FROM jugadores WHERE numero_identidad = ?";
            PreparedStatement psDup = conn.prepareStatement(sqlDup);
            psDup.setString(1, numeroIdentidad);
            ResultSet rsDup = psDup.executeQuery();

            if (rsDup.next()) {
                return false; // ya existe
            }

            // Insertar jugador
            String sqlInsert = "INSERT INTO jugadores (numero_identidad, nombre, posicion, edad, id_equipo) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setString(1, numeroIdentidad);
            psInsert.setString(2, nombreJugador);
            psInsert.setString(3, posicion);
            psInsert.setInt(4, edad);
            psInsert.setInt(5, idEquipo);
            psInsert.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== HU-3: LISTAR EQUIPOS =====================
    public List<String> listarEquipos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, COUNT(j.id) AS cantidad FROM equipos e LEFT JOIN jugadores j ON e.id = j.id_equipo GROUP BY e.id, e.nombre";

        try (Connection conn = ConexionBD.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add("ID: " + rs.getInt("id") + " - " + rs.getString("nombre") + " (Jugadores: " + rs.getInt("cantidad") + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ===================== HU-4: LISTAR JUGADORES POR EQUIPO =====================
    public List<Jugador> listarJugadoresPorEquipo(String nombreEquipo) {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT j.* FROM jugadores j INNER JOIN equipos e ON j.id_equipo = e.id WHERE e.nombre = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreEquipo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                jugadores.add(new Jugador(
                        rs.getInt("id"),
                        rs.getString("numero_identidad"),
                        rs.getString("nombre"),
                        rs.getString("posicion"),
                        rs.getInt("edad")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jugadores;
    }

    // ===================== HU-5: ELIMINAR JUGADOR =====================
    public boolean eliminarJugador(String nombreEquipo, String numeroIdentidad) {
        String sql = "DELETE j FROM jugadores j INNER JOIN equipos e ON j.id_equipo = e.id WHERE e.nombre = ? AND j.numero_identidad = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreEquipo);
            ps.setString(2, numeroIdentidad);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== HU-6: EDITAR JUGADOR =====================
    public boolean editarJugador(String nombreEquipo, String numeroIdentidad, String nuevoNombre, String nuevaPosicion, int nuevaEdad) {
        String sql = "UPDATE jugadores j INNER JOIN equipos e ON j.id_equipo = e.id SET j.nombre = ?, j.posicion = ?, j.edad = ? WHERE e.nombre = ? AND j.numero_identidad = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevaPosicion);
            ps.setInt(3, nuevaEdad);
            ps.setString(4, nombreEquipo);
            ps.setString(5, numeroIdentidad);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // ===================== HU-7: ELIMINAR EQUIPO =====================

    public boolean eliminarEquipo(String nombreEquipo) {
        // Verificar si el torneo está iniciado
        if (isTorneoIniciado()) {
            JOptionPane.showMessageDialog(null, "No se puede eliminar el equipo mientras el torneo está en curso",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre del equipo no puede estar vacío",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Primero obtenemos el ID del equipo
        String sqlObtenerId = "SELECT id FROM equipos WHERE nombre = ?";
        String sqlEliminarJugadores = "DELETE FROM jugadores WHERE id_equipo = ?";
        String sqlEliminarEquipo = "DELETE FROM equipos WHERE id = ?";

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            PreparedStatement psId = conn.prepareStatement(sqlObtenerId);
            psId.setString(1, nombreEquipo);
            ResultSet rs = psId.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "El equipo no existe",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false; // no existe el equipo
            }

            int idEquipo = rs.getInt("id");

            // Eliminar jugadores asociados primero
            PreparedStatement psJugadores = conn.prepareStatement(sqlEliminarJugadores);
            psJugadores.setInt(1, idEquipo);
            psJugadores.executeUpdate();

            // Luego eliminar el equipo
            PreparedStatement psEquipo = conn.prepareStatement(sqlEliminarEquipo);
            psEquipo.setInt(1, idEquipo);
            int filasAfectadas = psEquipo.executeUpdate();

            if (filasAfectadas > 0) {
                conn.commit(); // Confirmar transacción
                JOptionPane.showMessageDialog(null, "Equipo eliminado correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "No se pudo eliminar el equipo",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar el equipo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ===================== HU-8: INICIAR TORNEO Y GENERAR PARTIDOS =====================
    public boolean iniciarTorneo() {
        String sqlEquipos = "SELECT id, nombre FROM equipos";
        String sqlInsertPartido = "INSERT INTO partidos (id_equipo_local, id_equipo_visitante) VALUES (?, ?)";
        String sqlCheckEstado = "SELECT iniciado FROM estado_torneo WHERE id = 1";

        try (Connection conn = ConexionBD.getConnection()) {

            // Verificar si ya está iniciado
            PreparedStatement psEstado = conn.prepareStatement(sqlCheckEstado);
            ResultSet rsEstado = psEstado.executeQuery();
            if (rsEstado.next() && rsEstado.getBoolean("iniciado")) {
                JOptionPane.showMessageDialog(null, "El torneo ya fue iniciado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Obtener todos los equipos
            PreparedStatement psEquipos = conn.prepareStatement(sqlEquipos);
            ResultSet rs = psEquipos.executeQuery();

            List<Integer> equipos = new ArrayList<>();
            while (rs.next()) {
                equipos.add(rs.getInt("id"));
            }

            // Validar cantidad de equipos
            if (equipos.size() == 0) {
                JOptionPane.showMessageDialog(null, "No hay equipos registrados. No se puede iniciar el torneo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (equipos.size() % 2 != 0) {
                JOptionPane.showMessageDialog(null, "La cantidad de equipos es impar. No se puede iniciar el torneo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Generar partidos únicos
            PreparedStatement psInsert = conn.prepareStatement(sqlInsertPartido);

            for (int i = 0; i < equipos.size(); i++) {
                for (int j = i + 1; j < equipos.size(); j++) {
                    int local = equipos.get(i);
                    int visitante = equipos.get(j);

                    // Evitar duplicados
                    String verificar = "SELECT COUNT(*) FROM partidos WHERE (id_equipo_local = ? AND id_equipo_visitante = ?) OR (id_equipo_local = ? AND id_equipo_visitante = ?)";
                    PreparedStatement psVerif = conn.prepareStatement(verificar);
                    psVerif.setInt(1, local);
                    psVerif.setInt(2, visitante);
                    psVerif.setInt(3, visitante);
                    psVerif.setInt(4, local);
                    ResultSet rsVerif = psVerif.executeQuery();
                    rsVerif.next();

                    if (rsVerif.getInt(1) == 0) {
                        psInsert.setInt(1, local);
                        psInsert.setInt(2, visitante);
                        psInsert.executeUpdate();
                    }
                }
            }

            // Cambiar estado del torneo a iniciado
            if (actualizarEstadoTorneo(true)) {
                JOptionPane.showMessageDialog(null, "Torneo iniciado y partidos generados correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar el estado del torneo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ocurrió un error al iniciar el torneo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Actualiza el estado del torneo en la base de datos
     *
     * @param iniciado true si el torneo se inicia, false si se reinicia o
     * detiene
     * @return true si la actualización fue exitosa, false si ocurrió un error
     */
    public boolean actualizarEstadoTorneo(boolean iniciado) {
        String sql = "UPDATE estado_torneo SET iniciado = ? WHERE id = 1";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, iniciado);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar el estado del torneo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

// ===================== HU-9: LISTAR PARTIDOS =====================
    public List<Partido> listarPartidos() {
        List<Partido> lista = new ArrayList<>();
        String sql = "SELECT p.id, e1.nombre AS local, e2.nombre AS visitante, p.fecha, e1.id AS id_local, e2.id AS id_visitante "
                + "FROM partidos p "
                + "INNER JOIN equipos e1 ON p.id_equipo_local = e1.id "
                + "INNER JOIN equipos e2 ON p.id_equipo_visitante = e2.id "
                + "ORDER BY p.id";

        try (Connection conn = ConexionBD.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Partido(
                        rs.getInt("id"),
                        rs.getInt("id_local"),
                        rs.getString("local"),
                        rs.getInt("id_visitante"),
                        rs.getString("visitante"),
                        rs.getDate("fecha")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

// ===================== HU-9: ASIGNAR FECHA =====================
    public boolean asignarFechaPartido(int idPartido, java.sql.Date fecha) {
        String sql = "UPDATE partidos SET fecha = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            ps.setInt(2, idPartido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// ===================== HU-9: ELIMINAR FECHA =====================
    public boolean eliminarFechaPartido(int idPartido) {
        String sql = "UPDATE partidos SET fecha = NULL WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// ===================== CONSULTAR ESTADO DEL TORNEO =====================
    public boolean isTorneoIniciado() {
        String sql = "SELECT iniciado FROM estado_torneo WHERE id = 1";
        try (Connection conn = ConexionBD.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBoolean("iniciado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
