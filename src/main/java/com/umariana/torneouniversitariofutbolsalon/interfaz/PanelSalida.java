package com.umariana.torneouniversitariofutbolsalon.interfaz;

import javax.swing.*;
import java.awt.*;

public class PanelSalida extends JPanel {
    private JTextArea areaSalida;

    public PanelSalida() {
        setLayout(new BorderLayout());
        areaSalida = new JTextArea(3, 50);
        areaSalida.setEditable(false);
        add(new JScrollPane(areaSalida), BorderLayout.CENTER);
    }

    public void mostrarMensaje(String mensaje) {
        areaSalida.append(mensaje + "\n");
    }
}
