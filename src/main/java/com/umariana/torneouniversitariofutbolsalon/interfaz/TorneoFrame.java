/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umariana.torneouniversitariofutbolsalon.interfaz;

import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Juan Goyes
 */
public class TorneoFrame extends JFrame {

    private Torneo torneo;
    private PanelEquipo panelEquipo;
    private PanelJugador panelJugador;
    private PanelListados panelListados;
    private PanelSalida panelSalida;

    public TorneoFrame() {
        torneo = new Torneo();
        initUI();
    }

    private void initUI() {
        setTitle("Gestión de Torneos Universitarios - Fútbol de Salón");
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblTitulo = new JLabel("Gestión de Torneos Universitarios - Fútbol de Salón", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // Crear paneles
        panelSalida = new PanelSalida();
        panelEquipo = new PanelEquipo(torneo, panelSalida);
        panelJugador = new PanelJugador(torneo, panelSalida);
        panelListados = new PanelListados(torneo, panelSalida);
        PanelPartidos panelPartidos = new PanelPartidos(torneo, panelSalida);

        // Sincronizar combo de equipos
        panelEquipo.setPanelJugador(panelJugador);
        panelEquipo.setPanelListados(panelListados);

        // Panel central con pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Equipos", panelEquipo);
        tabs.addTab("Jugadores", panelJugador);
        tabs.addTab("Listados", panelListados);
        tabs.addTab("Partidos", panelPartidos);

        add(tabs, BorderLayout.CENTER);
        add(panelSalida, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TorneoFrame().setVisible(true));
    }
}
