/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umariana.torneouniversitariofutbolsalon.interfaz;

import java.awt.*;
import javax.swing.*;
/**
 *
 * @author Juan Goyes
 */
public class PanelSalida extends JPanel {

    private JTextArea txtSalida;

    public PanelSalida() {
        setLayout(new BorderLayout());
        txtSalida = new JTextArea(10, 60);
        txtSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtSalida);
        scroll.setBorder(BorderFactory.createTitledBorder("Registro de Actividades"));
        add(scroll, BorderLayout.CENTER);
    }

    public void agregarMensaje(String mensaje) {
        txtSalida.append(mensaje + "\n");
    }
}
