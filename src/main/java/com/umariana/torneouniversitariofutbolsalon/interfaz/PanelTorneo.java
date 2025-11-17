package com.umariana.torneouniversitariofutbolsalon.interfaz;

import com.umariana.torneouniversitariofutbolsalon.mundo.TablaPosicion;
import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelTorneo extends JPanel {

    private Torneo torneo;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnTablaPosiciones;
    private JButton btnReiniciar;

    // Referencias a los demas paneles para refrescar
    private PanelEquipo panelEquipo;
    private PanelJugador panelJugador;
    private PanelListados panelListados;
    private PanelPartidos panelPartidos;
    private PanelSalida panelSalida;

    public PanelTorneo(Torneo torneo, PanelEquipo panelEquipo, PanelJugador panelJugador, PanelListados panelListados, PanelPartidos panelPartidos, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelEquipo = panelEquipo;
        this.panelJugador = panelJugador;
        this.panelListados = panelListados;
        this.panelPartidos = panelPartidos;
        this.panelSalida = panelSalida;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelBotones = new JPanel();
        btnTablaPosiciones = new JButton("Calcular Tabla de Posiciones");
        btnTablaPosiciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnTablaPosiciones.addActionListener(e -> calcularTabla());

        btnReiniciar = new JButton("Reiniciar Torneo");
        btnReiniciar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnReiniciar.addActionListener(e -> reiniciarTorneo());

        panelBotones.add(btnTablaPosiciones);
        panelBotones.add(btnReiniciar);

        add(panelBotones, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new Object[]{"Pos", "Equipo", "PJ", "G", "E", "P", "GF", "GC", "DG", "Pts"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void calcularTabla() {
        List<TablaPosicion> lista = torneo.calcularTablaPosiciones();

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay partidos con resultados registrados");
            return;
        }

        modelo.setRowCount(0);
        int pos = 1;
        for (TablaPosicion t : lista) {
            modelo.addRow(new Object[]{
                pos++,
                t.getNombre(),
                t.getJugados(),
                t.getGanados(),
                t.getEmpatados(),
                t.getPerdidos(),
                t.getGolesAFavor(),
                t.getGolesEnContra(),
                t.getDiferencia(),
                t.getPuntos()
            });
        }

        JOptionPane.showMessageDialog(this, "Tabla de posiciones calculada correctamente");
    }

    private void reiniciarTorneo() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas reiniciar el torneo?\nSe eliminarán TODOS los partidos y resultados",
                "Confirmar reinicio",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = torneo.reiniciarTorneo();

            if (exito) {
                // Limpiar la tabla de posiciones de este panel
                modelo.setRowCount(0);

                // Refrescar paneles que muestran datos
                if (panelEquipo != null) {
                    panelEquipo.refrescar();
                }

                // PanelJugador y PanelListados en muchos diseños ya exponen cargarEquipos,
                // usamos cargarEquipos si existe para sincronizar combos/listas
                try {
                    if (panelJugador != null) {
                        panelJugador.cargarEquipos();
                    }
                } catch (Throwable ignored) {
                }

                try {
                    if (panelListados != null) {
                        panelListados.cargarEquipos();
                    }
                } catch (Throwable ignored) {
                }

                if (panelPartidos != null) {
                    panelPartidos.refrescar();
                }

                // Limpiar mensajes en PanelSalida usando su metodo mostrado en el codigo original
                if (panelSalida != null) {
                    try {
                        panelSalida.mostrarMensaje("");
                    } catch (Throwable ignored) {
                    }
                }

                JOptionPane.showMessageDialog(this, "Torneo reiniciado correctamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al reiniciar el torneo");
            }
        }
    }
}
