/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umariana.torneouniversitariofutbolsalon.interfaz;
import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import com.umariana.torneouniversitariofutbolsalon.mundo.Jugador;
import java.awt.*;
import javax.swing.*;
import java.util.List;
/**
 *
 * @author Juan Goyes
 */
public class PanelListados extends JPanel {

    private Torneo torneo;
    private PanelSalida panelSalida;
    private JComboBox<String> comboEquipos;
    private JButton btnListarEquipos, btnListarJugadores;

    public PanelListados(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelSalida = panelSalida;
        initUI();
    }

    private void initUI() {
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("Listados"));
        comboEquipos = new JComboBox<>();
        btnListarEquipos = new JButton("Listar Equipos");
        btnListarJugadores = new JButton("Listar Jugadores");

        add(btnListarEquipos);
        add(comboEquipos);
        add(btnListarJugadores);

        btnListarEquipos.addActionListener(e -> {
            List<String> lista = torneo.listarEquipos();
            panelSalida.agregarMensaje("📋 Lista de equipos:");
            for (String eq : lista)
                panelSalida.agregarMensaje(" - " + eq);
        });

        btnListarJugadores.addActionListener(e -> {
            String equipo = (String) comboEquipos.getSelectedItem();
            if (equipo == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un equipo");
                return;
            }
            List<Jugador> jugadores = torneo.listarJugadoresPorEquipo(equipo);
            panelSalida.agregarMensaje("👥 Jugadores de " + equipo + ":");
            for (Jugador j : jugadores)
                panelSalida.agregarMensaje(" - " + j.getNombre() + " | " + j.getPosicion() + " | Edad: " + j.getEdad());
        });
    }

    public void actualizarCombo(String equipo) {
        comboEquipos.addItem(equipo);
    }
}