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
    private JTable tabla;
    private DefaultTableModel modelo;
    private PanelSalida panelSalida;

    public PanelPartidos(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelSalida = panelSalida;
        initUI();
        cargarPartidos();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con botones
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

        if (torneo.isTorneoIniciado()) {
            btnIniciar.setVisible(false);
        }

        panelBotones.add(btnIniciar);
        panelBotones.add(btnAsignarFecha);
        panelBotones.add(btnEliminarFecha);

        add(panelBotones, BorderLayout.NORTH);

        // Configurar tabla
        modelo = new DefaultTableModel(new Object[]{"ID", "Local", "Visitante", "Fecha"}, 0) {
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
            panelSalida.mostrarMensaje("Torneo iniciado y partidos generados correctamente");
            btnIniciar.setVisible(false);
            cargarPartidos();
        } else {
            List<String> equipos = torneo.listarEquipos();
            if (equipos.size() % 2 != 0) {
                panelSalida.mostrarMensaje("El número de equipos es impar. No se puede iniciar el torneo");
            } else {
                panelSalida.mostrarMensaje("El torneo ya fue iniciado o ocurrió un error");
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
                fecha
            });
        }
    }

    /**
     * Asigna una fecha al partido seleccionado
     */
    private void asignarFecha() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            panelSalida.mostrarMensaje("Selecciona un partido para asignar la fecha");
            return;
        }

        int idPartido = (int) modelo.getValueAt(filaSeleccionada, 0);

        // Mostrar calendario
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
                panelSalida.mostrarMensaje("Fecha asignada correctamente");
            }
        }
    }

    /**
     * Elimina la fecha del partido seleccionado
     */
    private void eliminarFecha() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            panelSalida.mostrarMensaje("Selecciona un partido para eliminar la fecha");
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
            panelSalida.mostrarMensaje("Fecha eliminada correctamente");
        }
    }
}
