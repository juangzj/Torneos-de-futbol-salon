package com.umariana.torneouniversitariofutbolsalon.interfaz;

import com.umariana.torneouniversitariofutbolsalon.mundo.Partido;
import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import com.toedter.calendar.JCalendar;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelPartidos extends JPanel {

    private Torneo torneo;
    private JButton btnIniciar;
    private JButton btnAsignarFecha;
    private JButton btnEliminarFecha;
    private JButton btnGuardarResultado; // nuevo boton unico
    private JTable tabla;
    private DefaultTableModel modelo;

    /**
     * @param torneo
     * @param panelSalida 
     */
    public PanelPartidos(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        initUI();
        cargarPartidos();
    }
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        btnIniciar = new JButton("Iniciar Torneo");
        btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIniciar.addActionListener(e -> iniciarTorneo());

        btnAsignarFecha = new JButton("Asignar Fecha");
        btnAsignarFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnAsignarFecha.addActionListener(e -> asignarFecha());

        btnEliminarFecha = new JButton("Eliminar Fecha");
        btnEliminarFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnEliminarFecha.addActionListener(e -> eliminarFecha());

        btnGuardarResultado = new JButton("Guardar Resultado");
        btnGuardarResultado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnGuardarResultado.addActionListener(e -> guardarResultado());

        if (torneo.isTorneoIniciado()) {
            btnIniciar.setVisible(false);
        }

        panelBotones.add(btnIniciar);
        panelBotones.add(btnAsignarFecha);
        panelBotones.add(btnEliminarFecha);
        panelBotones.add(btnGuardarResultado);

        add(panelBotones, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID", "Local", "Visitante", "Fecha", "GL", "GV"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);
    }

    private void iniciarTorneo() {
        boolean exito = torneo.iniciarTorneo();

        if (exito) {
            JOptionPane.showMessageDialog(this, "Torneo iniciado y partidos generados correctamente");
            btnIniciar.setVisible(false);
            cargarPartidos();
        } else {
            List<String> equipos = torneo.listarEquipos();
            if (equipos.size() % 2 != 0) {
                JOptionPane.showMessageDialog(this, "El número de equipos es impar. No se puede iniciar el torneo");
            } else {
                JOptionPane.showMessageDialog(this, "El torneo ya fue iniciado o ocurrió un error");
            }
        }
    }

    private void cargarPartidos() {
        modelo.setRowCount(0);
        List<Partido> partidos = torneo.listarPartidos();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Partido p : partidos) {
            Object fecha = (p.getFecha() == null) ? "Sin fecha" : sdf.format(p.getFecha());
            modelo.addRow(new Object[]{
                p.getId(),
                p.getNombreEquipoLocal(),
                p.getNombreEquipoVisitante(),
                fecha,
                p.getGolesLocal(),
                p.getGolesVisitante()
            });
        }
    }

    private void asignarFecha() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un partido para asignar la fecha");
            return;
        }

        int idPartido = (int) modelo.getValueAt(filaSeleccionada, 0);

        JCalendar calendario = new JCalendar();
        calendario.setWeekOfYearVisible(false);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                calendario,
                "Selecciona la fecha del partido",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion == JOptionPane.OK_OPTION) {
            java.util.Date fechaSeleccionada = calendario.getDate();
            if (fechaSeleccionada != null) {
                Date sqlDate = new Date(fechaSeleccionada.getTime());
                torneo.asignarFechaPartido(idPartido, sqlDate);
                cargarPartidos();
                JOptionPane.showMessageDialog(this, "Fecha asignada correctamente");
            }
        }
    }

    private void eliminarFecha() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un partido para eliminar la fecha");
            return;
        }

        int idPartido = (int) modelo.getValueAt(filaSeleccionada, 0);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar la fecha de este partido?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            torneo.asignarFechaPartido(idPartido, null);
            cargarPartidos();
            JOptionPane.showMessageDialog(this, "Fecha eliminada correctamente");
        }
    }

    /**
     * Guarda o edita el resultado del partido seleccionado
     */
    private void guardarResultado() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un partido para registrar o editar el resultado");
            return;
        }

        int idPartido = (int) modelo.getValueAt(filaSeleccionada, 0);

        Object gl = modelo.getValueAt(filaSeleccionada, 4);
        Object gv = modelo.getValueAt(filaSeleccionada, 5);

        JTextField txtGolesLocal = new JTextField(gl == null ? "" : gl.toString());
        JTextField txtGolesVisitante = new JTextField(gv == null ? "" : gv.toString());

        Object[] mensaje = {
            "Goles equipo local:", txtGolesLocal,
            "Goles equipo visitante:", txtGolesVisitante
        };

        int opcion = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Guardar Resultado",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                int golesLocal = Integer.parseInt(txtGolesLocal.getText());
                int golesVisitante = Integer.parseInt(txtGolesVisitante.getText());

                if (golesLocal < 0 || golesVisitante < 0) {
                    JOptionPane.showMessageDialog(this, "Los goles no pueden ser negativos");
                    return;
                }

                boolean exito = torneo.registrarResultado(idPartido, golesLocal, golesVisitante);

                if (exito) {
                    cargarPartidos();
                    JOptionPane.showMessageDialog(this, "Resultado guardado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar el resultado");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Debes ingresar solo números enteros");
            }
        }
    }

    // Metodo publico para que otros componentes puedan refrescar este panel despues de reinicio
    public void refrescar() {
        // Vaciar tabla
        if (modelo != null) {
            modelo.setRowCount(0);
        }

        // Volver a cargar partidos (mostrara vacio si BD esta vacia)
        cargarPartidos();

        // Mostrar boton iniciar otra vez si el torneo no esta iniciado
        if (btnIniciar != null) {
            btnIniciar.setVisible(!torneo.isTorneoIniciado());
        }
    }
}
