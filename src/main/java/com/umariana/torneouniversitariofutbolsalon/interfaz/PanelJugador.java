package com.umariana.torneouniversitariofutbolsalon.interfaz;

import com.umariana.torneouniversitariofutbolsalon.mundo.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelJugador extends JPanel {

    private Torneo torneo;
    private PanelSalida panelSalida;

    private JComboBox<String> comboEquipos;
    private JTable tablaJugadores;
    private DefaultTableModel modelo;

    private JTextField txtCedula, txtNombre, txtPosicion, txtEdad;
    private JButton btnRegistrar, btnEliminar, btnActualizar;

    public PanelJugador(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelSalida = panelSalida;
        initUI();
        cargarEquipos();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ======= PANEL SUPERIOR: SELECCIÓN DE EQUIPO =======
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        comboEquipos = new JComboBox<>();
        btnActualizar = new JButton("Actualizar jugadores");

        panelSuperior.add(new JLabel("Equipo:"));
        panelSuperior.add(comboEquipos);
        panelSuperior.add(btnActualizar);

        add(panelSuperior, BorderLayout.NORTH);

        // ======= TABLA CENTRAL: JUGADORES =======
        modelo = new DefaultTableModel(new String[]{"Cédula", "Nombre", "Posición", "Edad"}, 0);
        tablaJugadores = new JTable(modelo);
        tablaJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaJugadores.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tablaJugadores);
        add(scroll, BorderLayout.CENTER);

        // ======= PANEL INFERIOR: REGISTRO Y ACCIONES =======
        JPanel panelInferior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCedula = new JTextField(10);
        txtNombre = new JTextField(10);
        txtPosicion = new JTextField(10);
        txtEdad = new JTextField(5);

        btnRegistrar = new JButton("Registrar jugador");
        btnEliminar = new JButton("Eliminar jugador");

        // Fila de etiquetas
        gbc.gridx = 0; gbc.gridy = 0; panelInferior.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1; panelInferior.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 2; panelInferior.add(new JLabel("Posición:"), gbc);
        gbc.gridx = 3; panelInferior.add(new JLabel("Edad:"), gbc);

        // Fila de campos
        gbc.gridy = 1;
        gbc.gridx = 0; panelInferior.add(txtCedula, gbc);
        gbc.gridx = 1; panelInferior.add(txtNombre, gbc);
        gbc.gridx = 2; panelInferior.add(txtPosicion, gbc);
        gbc.gridx = 3; panelInferior.add(txtEdad, gbc);

        // Fila de botones
        gbc.gridy = 2;
        gbc.gridx = 0; gbc.gridwidth = 2;
        panelInferior.add(btnRegistrar, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2;
        panelInferior.add(btnEliminar, gbc);

        add(panelInferior, BorderLayout.SOUTH);

        // ======= EVENTOS =======
        comboEquipos.addActionListener(e -> cargarJugadores());
        btnRegistrar.addActionListener(e -> registrarJugador());
        btnEliminar.addActionListener(e -> eliminarJugador());
        btnActualizar.addActionListener(e -> cargarJugadores());
    }

    // ======= CARGAR EQUIPOS =======
    public void cargarEquipos() {
        comboEquipos.removeAllItems();
        List<String> equipos = torneo.listarEquipos();
        for (String eq : equipos) {
            // extrae solo el nombre sin el texto adicional
            if (eq.contains("-") && eq.contains("(")) {
                String nombre = eq.substring(eq.indexOf("-") + 2, eq.indexOf("(")).trim();
                comboEquipos.addItem(nombre);
            }
        }
        if (comboEquipos.getItemCount() > 0) cargarJugadores();
    }

    // ======= CARGAR JUGADORES =======
    private void cargarJugadores() {
        modelo.setRowCount(0);
        String equipoSeleccionado = (String) comboEquipos.getSelectedItem();
        if (equipoSeleccionado == null) return;

        List<Jugador> jugadores = torneo.listarJugadoresPorEquipo(equipoSeleccionado);
        for (Jugador j : jugadores) {
            modelo.addRow(new Object[]{
                j.getNumeroIdentidad(),
                j.getNombre(),
                j.getPosicion(),
                j.getEdad()
            });
        }
    }

    // ======= REGISTRAR JUGADOR =======
    private void registrarJugador() {
        String equipo = (String) comboEquipos.getSelectedItem();
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String posicion = txtPosicion.getText().trim();
        String edadTxt = txtEdad.getText().trim();

        if (equipo == null || cedula.isEmpty() || nombre.isEmpty() || posicion.isEmpty() || edadTxt.isEmpty()) {
            panelSalida.mostrarMensaje("Complete todos los campos");
            return;
        }

        try {
            int edad = Integer.parseInt(edadTxt);
            if (torneo.registrarJugador(equipo, cedula, nombre, posicion, edad)) {
                panelSalida.mostrarMensaje("Jugador registrado correctamente");
                txtCedula.setText("");
                txtNombre.setText("");
                txtPosicion.setText("");
                txtEdad.setText("");
                cargarJugadores();
            } else {
                panelSalida.mostrarMensaje("Error al registrar jugador (posible duplicado)");
            }
        } catch (NumberFormatException ex) {
            panelSalida.mostrarMensaje("Edad inválida");
        }
    }

    // ======= ELIMINAR JUGADOR =======
    private void eliminarJugador() {
        String equipo = (String) comboEquipos.getSelectedItem();
        int fila = tablaJugadores.getSelectedRow();
        if (fila == -1) {
            panelSalida.mostrarMensaje("Seleccione un jugador para eliminar");
            return;
        }

        String cedula = (String) modelo.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar al jugador con cédula " + cedula + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (torneo.eliminarJugador(equipo, cedula)) {
                panelSalida.mostrarMensaje("Jugador eliminado correctamente");
                cargarJugadores();
            } else {
                panelSalida.mostrarMensaje("Error al eliminar jugador");
            }
        }
    }
}
