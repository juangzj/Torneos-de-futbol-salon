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
public class PanelJugador extends JPanel {

    private Torneo torneo;
    private PanelSalida panelSalida;
    private JComboBox<String> comboEquipos;
    private JTextField txtNumeroIdentidad, txtNombre, txtPosicion, txtEdad;
    private JButton btnRegistrar, btnEditar, btnEliminar;

    public PanelJugador(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelSalida = panelSalida;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Gestión de Jugadores"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Equipo:"), gbc);
        gbc.gridx = 1;
        comboEquipos = new JComboBox<>();
        comboEquipos.setPreferredSize(new Dimension(200, 25));
        add(comboEquipos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Número Identidad:"), gbc);
        gbc.gridx = 1;
        txtNumeroIdentidad = new JTextField(15);
        add(txtNumeroIdentidad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Posición:"), gbc);
        gbc.gridx = 1;
        txtPosicion = new JTextField(15);
        add(txtPosicion, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1;
        txtEdad = new JTextField(5);
        add(txtEdad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        btnRegistrar = new JButton("Registrar Jugador");
        add(btnRegistrar, gbc);

        gbc.gridy = 6;
        btnEditar = new JButton("Editar Jugador");
        add(btnEditar, gbc);

        gbc.gridy = 7;
        btnEliminar = new JButton("Eliminar Jugador");
        add(btnEliminar, gbc);

        // Eventos
        btnRegistrar.addActionListener(e -> {
            String equipo = (String) comboEquipos.getSelectedItem();
            if (equipo == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un equipo");
                return;
            }
            String id = txtNumeroIdentidad.getText().trim();
            String nombre = txtNombre.getText().trim();
            String pos = txtPosicion.getText().trim();
            String edadStr = txtEdad.getText().trim();
            if (id.isEmpty() || nombre.isEmpty() || pos.isEmpty() || edadStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos");
                return;
            }
            try {
                int edad = Integer.parseInt(edadStr);
                boolean exito = torneo.registrarJugador(equipo, id, nombre, pos, edad);
                if (exito) {
                    panelSalida.agregarMensaje("✅ Jugador registrado: " + nombre + " en " + equipo);
                } else {
                    panelSalida.agregarMensaje("⚠️ Error: jugador duplicado o no válido");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Edad inválida");
            }
        });

        btnEliminar.addActionListener(e -> {
            String equipo = (String) comboEquipos.getSelectedItem();
            if (equipo == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un equipo");
                return;
            }
            String id = JOptionPane.showInputDialog("Número de identidad del jugador a eliminar:");
            if (id != null && !id.trim().isEmpty()) {
                boolean exito = torneo.eliminarJugador(equipo, id);
                panelSalida.agregarMensaje(exito ? "🗑️ Jugador eliminado" : "⚠️ No encontrado");
            }
        });

        btnEditar.addActionListener(e -> {
            String equipo = (String) comboEquipos.getSelectedItem();
            if (equipo == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un equipo");
                return;
            }
            String id = JOptionPane.showInputDialog("Número de identidad del jugador:");
            if (id != null && !id.trim().isEmpty()) {
                String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:");
                String nuevaPos = JOptionPane.showInputDialog("Nueva posición:");
                String edadStr = JOptionPane.showInputDialog("Nueva edad:");
                try {
                    int nuevaEdad = Integer.parseInt(edadStr);
                    boolean exito = torneo.editarJugador(equipo, id, nuevoNombre, nuevaPos, nuevaEdad);
                    panelSalida.agregarMensaje(exito ? "✏️ Jugador actualizado" : "⚠️ Jugador no encontrado");
                } catch (NumberFormatException ex2) {
                    JOptionPane.showMessageDialog(this, "Edad inválida");
                }
            }
        });
    }

    public void actualizarCombo(String equipo) {
        comboEquipos.addItem(equipo);
    }
}
