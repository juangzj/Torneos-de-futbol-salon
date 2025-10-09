package com.umariana.torneouniversitariofutbolsalon.interfaz;

import com.umariana.torneouniversitariofutbolsalon.mundo.Torneo;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class PanelEquipo extends JPanel {

    private Torneo torneo;
    private PanelSalida panelSalida;
    private PanelJugador panelJugador;
    private PanelListados panelListados;

    private JTextField txtNombreEquipo;
    private JButton btnRegistrar, btnActualizar, btnEliminar;
    private JList<String> listaEquipos;
    private DefaultListModel<String> modeloLista;

    public PanelEquipo(Torneo torneo, PanelSalida panelSalida) {
        this.torneo = torneo;
        this.panelSalida = panelSalida;
        initUI();
        cargarEquiposDesdeBD();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ======= Panel de registro (arriba) =======
        JPanel panelRegistro = new JPanel(new GridBagLayout());
        panelRegistro.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Registrar nuevo equipo",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNombre = new JLabel("Nombre del equipo:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelRegistro.add(lblNombre, gbc);

        txtNombreEquipo = new JTextField(18);
        gbc.gridx = 1;
        panelRegistro.add(txtNombreEquipo, gbc);

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(0, 102, 204));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        gbc.gridx = 2;
        panelRegistro.add(btnRegistrar, gbc);

        // ======= Panel central con la lista =======
        modeloLista = new DefaultListModel<>();
        listaEquipos = new JList<>(modeloLista);
        listaEquipos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaEquipos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Equipos registrados",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        JScrollPane scrollEquipos = new JScrollPane(listaEquipos);
        scrollEquipos.setPreferredSize(new Dimension(350, 250));

        // ======= Panel inferior =======
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnActualizar = new JButton("Actualizar lista");
        btnEliminar = new JButton("Eliminar equipo");
        btnEliminar.setBackground(new Color(204, 0, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnActualizar.setFocusPainted(false);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        // ======= Estructura general =======
        add(panelRegistro, BorderLayout.NORTH);
        add(scrollEquipos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // ======= Eventos =======
        btnRegistrar.addActionListener(e -> registrarEquipo());
        btnActualizar.addActionListener(e -> cargarEquiposDesdeBD());
        btnEliminar.addActionListener(e -> eliminarEquipo());
    }

    private void registrarEquipo() {
        String nombre = txtNombreEquipo.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre de equipo", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (torneo.registrarEquipo(nombre)) {
            panelSalida.mostrarMensaje("Equipo registrado correctamente");
            txtNombreEquipo.setText("");
            cargarEquiposDesdeBD();
            if (panelJugador != null) panelJugador.cargarEquipos();
            if (panelListados != null) panelListados.cargarEquipos();
        } else {
            panelSalida.mostrarMensaje("Error al registrar equipo (posible duplicado)");
        }
    }

    private void eliminarEquipo() {
        String equipoSeleccionado = listaEquipos.getSelectedValue();
        if (equipoSeleccionado == null || equipoSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un equipo para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extraer el nombre del equipo (sin el texto extra)
        String nombreEquipo = equipoSeleccionado;
        if (nombreEquipo.contains("-")) {
            nombreEquipo = nombreEquipo.substring(nombreEquipo.indexOf("-") + 2, nombreEquipo.indexOf("(")).trim();
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar el equipo '" + nombreEquipo + "'?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (torneo.eliminarEquipo(nombreEquipo)) {
                panelSalida.mostrarMensaje("Equipo eliminado correctamente");
                cargarEquiposDesdeBD();
                if (panelJugador != null) panelJugador.cargarEquipos();
                if (panelListados != null) panelListados.cargarEquipos();
            } else {
                panelSalida.mostrarMensaje("Error al eliminar el equipo");
            }
        }
    }

    public void cargarEquiposDesdeBD() {
        modeloLista.clear();
        List<String> equipos = torneo.listarEquipos();
        if (equipos.isEmpty()) {
            modeloLista.addElement("No hay equipos registrados");
        } else {
            for (String eq : equipos) {
                modeloLista.addElement(eq);
            }
        }
    }

    // ======= Métodos de sincronización =======
    public void setPanelJugador(PanelJugador panelJugador) {
        this.panelJugador = panelJugador;
    }

    public void setPanelListados(PanelListados panelListados) {
        this.panelListados = panelListados;
    }
}
