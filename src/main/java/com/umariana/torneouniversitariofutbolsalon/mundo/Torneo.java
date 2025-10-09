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
        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
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
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
}
