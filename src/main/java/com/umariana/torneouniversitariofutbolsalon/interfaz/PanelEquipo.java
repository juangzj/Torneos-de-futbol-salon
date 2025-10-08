/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umariana.torneouniversitariofutbolsalon.interfaz;
import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import java.awt.*;
import javax.swing.*;


/**
 *
 * @author Juan Goyes
 */
public class PanelEquipo extends JPanel {

    private Torneo torneo;
    private PanelSalida panelSalida;
    private PanelJugador panelJugador;
    private PanelListados panelListados;
    private JTextField txtEquipo;
    private JButton btnRegistrarEquipo;
    private JComboBox<String> comboEquipos;

    public PanelEquipo(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelSalida = panelSalida;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Registrar Equipo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nombre del Equipo:"), gbc);
        gbc.gridx = 1;
        txtEquipo = new JTextField(15);
        add(txtEquipo, gbc);

        gbc.gridx = 2;
        btnRegistrarEquipo = new JButton("Registrar");
        add(btnRegistrarEquipo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        add(new JLabel("Equipos registrados:"), gbc);

        gbc.gridy = 2;
        comboEquipos = new JComboBox<>();
        comboEquipos.setPreferredSize(new Dimension(250, 25));
        add(comboEquipos, gbc);

        btnRegistrarEquipo.addActionListener(e -> {
            String nombre = txtEquipo.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre del equipo");
                return;
            }
            boolean exito = torneo.registrarEquipo(nombre);
            if (exito) {
                comboEquipos.addItem(nombre);
                if (panelJugador != null) panelJugador.actualizarCombo(nombre);
                if (panelListados != null) panelListados.actualizarCombo(nombre);
                panelSalida.agregarMensaje("✅ Equipo registrado: " + nombre);
            } else {
                panelSalida.agregarMensaje("⚠️ El equipo ya existe: " + nombre);
            }
            txtEquipo.setText("");
        });
    }

    public void setPanelJugador(PanelJugador panelJugador) {
        this.panelJugador = panelJugador;
    }

    public void setPanelListados(PanelListados panelListados) {
        this.panelListados = panelListados;
    }

    public JComboBox<String> getComboEquipos() {
        return comboEquipos;
    }
}