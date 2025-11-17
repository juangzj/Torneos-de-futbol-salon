package com.umariana.torneouniversitariofutbolsalon.mundo;

import java.sql.Connection;
import java.sql.Date;
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

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement psVerificar = conn.prepareStatement(verificar);
             PreparedStatement psInsertar = conn.prepareStatement(insertar)) {

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
            try (PreparedStatement psEquipo = conn.prepareStatement(sqlEquipo)) {
                psEquipo.setString(1, nombreEquipo);
                try (ResultSet rsEquipo = psEquipo.executeQuery()) {
                    if (!rsEquipo.next()) {
                        return false; // equipo no existe
                    }
                    int idEquipo = rsEquipo.getInt("id");

                    // Validar duplicado (en general)
                    String sqlDup = "SELECT * FROM jugadores WHERE numero_identidad = ?";
                    try (PreparedStatement psDup = conn.prepareStatement(sqlDup)) {
                        psDup.setString(1, numeroIdentidad);
                        try (ResultSet rsDup = psDup.executeQuery()) {
                            if (rsDup.next()) {
                                return false; // ya existe
                            }
                        }
                    }

                    // Insertar jugador
                    String sqlInsert = "INSERT INTO jugadores (numero_identidad, nombre, posicion, edad, id_equipo) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                        psInsert.setString(1, numeroIdentidad);
                        psInsert.setString(2, nombreJugador);
                        psInsert.setString(3, posicion);
                        psInsert.setInt(4, edad);
                        psInsert.setInt(5, idEquipo);
                        psInsert.executeUpdate();
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== HU-3: LISTAR EQUIPOS =====================
    public List<String> listarEquipos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, COUNT(j.id) AS cantidad FROM equipos e LEFT JOIN jugadores j ON e.id = j.id_equipo GROUP BY e.id, e.nombre";

        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jugadores.add(new Jugador(
                            rs.getInt("id"),
                            rs.getString("numero_identidad"),
                            rs.getString("nombre"),
                            rs.getString("posicion"),
                            rs.getInt("edad")
                    ));
                }
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

        String sqlObtenerId = "SELECT id FROM equipos WHERE nombre = ?";
        String sqlEliminarJugadores = "DELETE FROM jugadores WHERE id_equipo = ?";
        String sqlEliminarPartidosEquipo = "DELETE FROM partidos WHERE id_equipo_local = ? OR id_equipo_visitante = ?";
        String sqlEliminarEquipo = "DELETE FROM equipos WHERE id = ?";

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psId = conn.prepareStatement(sqlObtenerId)) {
                psId.setString(1, nombreEquipo);
                try (ResultSet rs = psId.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(null, "El equipo no existe",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    int idEquipo = rs.getInt("id");

                    // Eliminar partidos relacionados al equipo (si existen)
                    try (PreparedStatement psPartidos = conn.prepareStatement(sqlEliminarPartidosEquipo)) {
                        psPartidos.setInt(1, idEquipo);
                        psPartidos.setInt(2, idEquipo);
                        psPartidos.executeUpdate();
                    }

                    // Eliminar jugadores asociados primero
                    try (PreparedStatement psJugadores = conn.prepareStatement(sqlEliminarJugadores)) {
                        psJugadores.setInt(1, idEquipo);
                        psJugadores.executeUpdate();
                    }

                    // Luego eliminar el equipo
                    try (PreparedStatement psEquipo = conn.prepareStatement(sqlEliminarEquipo)) {
                        psEquipo.setInt(1, idEquipo);
                        int filasAfectadas = psEquipo.executeUpdate();

                        if (filasAfectadas > 0) {
                            conn.commit();
                            JOptionPane.showMessageDialog(null, "Equipo eliminado correctamente",
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            return true;
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(null, "No se pudo eliminar el equipo",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
            } catch (SQLException exInner) {
                conn.rollback();
                throw exInner;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignore) {
                }
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
            try (PreparedStatement psEstado = conn.prepareStatement(sqlCheckEstado);
                 ResultSet rsEstado = psEstado.executeQuery()) {
                if (rsEstado.next() && rsEstado.getBoolean("iniciado")) {
                    JOptionPane.showMessageDialog(null, "El torneo ya fue iniciado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // Obtener todos los equipos
            List<Integer> equipos = new ArrayList<>();
            try (PreparedStatement psEquipos = conn.prepareStatement(sqlEquipos);
                 ResultSet rs = psEquipos.executeQuery()) {
                while (rs.next()) {
                    equipos.add(rs.getInt("id"));
                }
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
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertPartido)) {
                for (int i = 0; i < equipos.size(); i++) {
                    for (int j = i + 1; j < equipos.size(); j++) {
                        int local = equipos.get(i);
                        int visitante = equipos.get(j);

                        // Evitar duplicados
                        String verificar = "SELECT COUNT(*) FROM partidos WHERE (id_equipo_local = ? AND id_equipo_visitante = ?) OR (id_equipo_local = ? AND id_equipo_visitante = ?)";
                        try (PreparedStatement psVerif = conn.prepareStatement(verificar)) {
                            psVerif.setInt(1, local);
                            psVerif.setInt(2, visitante);
                            psVerif.setInt(3, visitante);
                            psVerif.setInt(4, local);
                            try (ResultSet rsVerif = psVerif.executeQuery()) {
                                rsVerif.next();
                                if (rsVerif.getInt(1) == 0) {
                                    psInsert.setInt(1, local);
                                    psInsert.setInt(2, visitante);
                                    psInsert.executeUpdate();
                                }
                            }
                        }
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

    // overload que usa la misma conexion (para transacciones)
    private boolean actualizarEstadoTorneo(boolean iniciado, Connection conn) throws SQLException {
        String sql = "UPDATE estado_torneo SET iniciado = ? WHERE id = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, iniciado);
            return ps.executeUpdate() > 0;
        }
    }

    // ===================== HU-9: LISTAR PARTIDOS =====================
    public List<Partido> listarPartidos() {
        List<Partido> lista = new ArrayList<>();
        String sql = "SELECT p.id, e1.nombre AS local, e2.nombre AS visitante, p.fecha, "
                + "e1.id AS id_local, e2.id AS id_visitante, p.goles_local, p.goles_visitante "
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
                        rs.getDate("fecha"),
                        (rs.getObject("goles_local") == null) ? null : rs.getInt("goles_local"),
                        (rs.getObject("goles_visitante") == null) ? null : rs.getInt("goles_visitante")
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

    // ===================== HU10: REGISTRAR / MODIFICAR RESULTADO =====================
    public boolean registrarResultado(int idPartido, int golesLocal, int golesVisitante) {
        if (golesLocal < 0 || golesVisitante < 0) {
            return false;
        }
        String sql = "UPDATE partidos SET goles_local = ?, goles_visitante = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, golesLocal);
            ps.setInt(2, golesVisitante);
            ps.setInt(3, idPartido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== HU11: CALCULAR TABLA DE POSICIONES =====================
    public List<TablaPosicion> calcularTablaPosiciones() {
        List<TablaPosicion> tabla = new ArrayList<>();
        String sqlEquipos = "SELECT id, nombre FROM equipos";
        String sqlPartidos = "SELECT id_equipo_local, id_equipo_visitante, goles_local, goles_visitante FROM partidos WHERE goles_local IS NOT NULL AND goles_visitante IS NOT NULL";

        try (Connection conn = ConexionBD.getConnection(); Statement stEquipos = conn.createStatement(); PreparedStatement psPartidos = conn.prepareStatement(sqlPartidos)) {

            try (ResultSet rsEquipos = stEquipos.executeQuery(sqlEquipos)) {
                while (rsEquipos.next()) {
                    TablaPosicion tp = new TablaPosicion(rsEquipos.getInt("id"), rsEquipos.getString("nombre"));
                    tabla.add(tp);
                }
            }

            java.util.Map<Integer, TablaPosicion> mapa = new java.util.HashMap<>();
            for (TablaPosicion t : tabla) {
                mapa.put(t.getIdEquipo(), t);
            }

            try (ResultSet rsPartidos = psPartidos.executeQuery()) {
                while (rsPartidos.next()) {
                    int local = rsPartidos.getInt("id_equipo_local");
                    int visitante = rsPartidos.getInt("id_equipo_visitante");
                    int gl = rsPartidos.getInt("goles_local");
                    int gv = rsPartidos.getInt("goles_visitante");

                    TablaPosicion tLocal = mapa.get(local);
                    TablaPosicion tVis = mapa.get(visitante);
                    if (tLocal == null || tVis == null) {
                        continue;
                    }

                    tLocal.setJugados(tLocal.getJugados() + 1);
                    tVis.setJugados(tVis.getJugados() + 1);

                    tLocal.setGolesAFavor(tLocal.getGolesAFavor() + gl);
                    tLocal.setGolesEnContra(tLocal.getGolesEnContra() + gv);
                    tVis.setGolesAFavor(tVis.getGolesAFavor() + gv);
                    tVis.setGolesEnContra(tVis.getGolesEnContra() + gl);

                    if (gl > gv) {
                        tLocal.setGanados(tLocal.getGanados() + 1);
                        tVis.setPerdidos(tVis.getPerdidos() + 1);
                        tLocal.setPuntos(tLocal.getPuntos() + 3);
                    } else if (gl < gv) {
                        tVis.setGanados(tVis.getGanados() + 1);
                        tLocal.setPerdidos(tLocal.getPerdidos() + 1);
                        tVis.setPuntos(tVis.getPuntos() + 3);
                    } else {
                        tLocal.setEmpatados(tLocal.getEmpatados() + 1);
                        tVis.setEmpatados(tVis.getEmpatados() + 1);
                        tLocal.setPuntos(tLocal.getPuntos() + 1);
                        tVis.setPuntos(tVis.getPuntos() + 1);
                    }
                }
            }

            for (TablaPosicion t : tabla) {
                t.setDiferencia(t.getGolesAFavor() - t.getGolesEnContra());
            }

            tabla.sort((a, b) -> {
                if (b.getPuntos() != a.getPuntos()) {
                    return b.getPuntos() - a.getPuntos();
                }
                if (b.getDiferencia() != a.getDiferencia()) {
                    return b.getDiferencia() - a.getDiferencia();
                }
                return b.getGolesAFavor() - a.getGolesAFavor();
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tabla;
    }

    // ===================== HU12: REINICIAR TORNEO (RESET COMPLETO) =====================
    /**
     * Reinicia completamente el torneo:
     * - elimina partidos, jugadores, equipos y tabla_posiciones (si existe)
     * - reinicia AUTO_INCREMENT en tablas afectadas 
     * - deja estado_torneo.iniciado = false
     */
    public boolean reiniciarTorneo() {
        String sqlEliminarPartidos = "DELETE FROM partidos";
        String sqlEliminarJugadores = "DELETE FROM jugadores";
        // tabla_posiciones puede o no existir en tu BD; se intenta eliminar si existe
        String sqlEliminarTablaPosiciones = "DELETE FROM tabla_posiciones";
        String sqlEliminarEquipos = "DELETE FROM equipos";

        String sqlResetPartidos = "ALTER TABLE partidos AUTO_INCREMENT = 1";
        String sqlResetJugadores = "ALTER TABLE jugadores AUTO_INCREMENT = 1";
        String sqlResetEquipos = "ALTER TABLE equipos AUTO_INCREMENT = 1";
        String sqlResetTablaPosiciones = "ALTER TABLE tabla_posiciones AUTO_INCREMENT = 1";

        try (Connection conn = ConexionBD.getConnection(); Statement st = conn.createStatement()) {
            conn.setAutoCommit(false);
            try {
                // eliminar partidos
                st.executeUpdate(sqlEliminarPartidos);
            } catch (SQLException ignore) {
                // si falla se continua, puede que tabla no exista
            }

            try {
                // eliminar tabla_posiciones si existe
                st.executeUpdate(sqlEliminarTablaPosiciones);
            } catch (SQLException ignore) {
            }

            try {
                // eliminar jugadores
                st.executeUpdate(sqlEliminarJugadores);
            } catch (SQLException ignore) {
            }

            try {
                // eliminar equipos
                st.executeUpdate(sqlEliminarEquipos);
            } catch (SQLException ignore) {
            }

            // intentar reiniciar autoincrement (no crítico)
            try {
                st.executeUpdate(sqlResetPartidos);
            } catch (SQLException ignore) {
            }
            try {
                st.executeUpdate(sqlResetJugadores);
            } catch (SQLException ignore) {
            }
            try {
                st.executeUpdate(sqlResetEquipos);
            } catch (SQLException ignore) {
            }
            try {
                st.executeUpdate(sqlResetTablaPosiciones);
            } catch (SQLException ignore) {
            }

            // actualizar estado usando la misma conexión para que quede consistente
            try {
                if (!actualizarEstadoTorneo(false, conn)) {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
