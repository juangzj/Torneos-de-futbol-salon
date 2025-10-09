package com.umariana.torneouniversitariofutbolsalon.interfaz;

import com.umariana.torneouniversitariofutbolsalon.mundo.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class PanelListados extends JPanel {

    private Torneo torneo;
    private PanelSalida panelSalida;
    private JComboBox<String> comboEquipos;
    private JTextArea areaListado;
    private JButton btnVerJugadores;

    public PanelListados(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelSalida = panelSalida;
        initUI();
        cargarEquipos();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout());
        comboEquipos = new JComboBox<>();
        btnVerJugadores = new JButton("Ver jugadores del equipo");

        panelSuperior.add(new JLabel("Equipo:"));
        panelSuperior.add(comboEquipos);
        panelSuperior.add(btnVerJugadores);
        add(panelSuperior, BorderLayout.NORTH);

        areaListado = new JTextArea(15, 50);
        areaListado.setEditable(false);
        add(new JScrollPane(areaListado), BorderLayout.CENTER);

        btnVerJugadores.addActionListener(e -> mostrarJugadores());
    }

    public void cargarEquipos() {
        comboEquipos.removeAllItems();
        List<String> equipos = torneo.listarEquipos();
        for (String eq : equipos) {
            String nombre = eq.substring(eq.indexOf("-") + 2, eq.indexOf("(")).trim();
            comboEquipos.addItem(nombre);
        }
    }

    private void mostrarJugadores() {
        String equipo = (String) comboEquipos.getSelectedItem();
        List<Jugador> jugadores = torneo.listarJugadoresPorEquipo(equipo);
        areaListado.setText("Jugadores del equipo: " + equipo + "\n\n");
        for (Jugador j : jugadores) {
            areaListado.append(j.getNombre() + " - " + j.getPosicion() + " (" + j.getEdad() + " años)\n");
        }
    }
}
