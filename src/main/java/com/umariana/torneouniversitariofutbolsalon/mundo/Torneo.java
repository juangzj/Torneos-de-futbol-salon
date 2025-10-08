/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umariana.torneouniversitariofutbolsalon.mundo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juan Goyes
 */
public class Torneo {

    // ===================== ATRIBUTOS =====================
    private List<Equipo> equipos;
    private int contadorEquipos = 1;
    private int contadorJugadores = 1;

    // ===================== CONSTRUCTOR =====================
    public Torneo() {
        this.equipos = new ArrayList<>();
    }

    // ===================== MÉTODOS PRIVADOS PARA GENERAR IDs =====================
    private int generarIdEquipo() {
        return contadorEquipos++;
    }

    private int generarIdJugador() {
        return contadorJugadores++;
    }

    // ===================== HU-1: REGISTRAR EQUIPO =====================
    public boolean registrarEquipo(String nombreEquipo) {
        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
            return false;
        }
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nombreEquipo.trim())) {
                return false; // nombre duplicado
            }
        }
        equipos.add(new Equipo(generarIdEquipo(), nombreEquipo.trim()));
        return true;
    }

    // ===================== HU-2: REGISTRAR JUGADOR =====================
    public boolean registrarJugador(String nombreEquipo, String numeroIdentidad, String nombreJugador, String posicion, int edad) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nombreEquipo)) {
                // validar duplicado dentro del mismo equipo
                for (Jugador j : e.getJugadores()) {
                    if (j.getNumeroIdentidad().equals(numeroIdentidad)) {
                        return false;
                    }
                }
                // validar si ya pertenece a otro equipo
                for (Equipo otro : equipos) {
                    for (Jugador j : otro.getJugadores()) {
                        if (j.getNumeroIdentidad().equals(numeroIdentidad)) {
                            return false;
                        }
                    }
                }
                e.agregarJugador(new Jugador(generarIdJugador(), numeroIdentidad, nombreJugador, posicion, edad));
                return true;
            }
        }
        return false; // equipo no existe
    }

    // ===================== HU-3: LISTAR EQUIPOS =====================
    public List<String> listarEquipos() {
        List<String> lista = new ArrayList<>();
        for (Equipo e : equipos) {
            lista.add("ID: " + e.getId() + " - " + e.getNombre() + " (Jugadores: " + e.getJugadores().size() + ")");
        }
        return lista;
    }

    // ===================== HU-4: LISTAR JUGADORES POR EQUIPO =====================
    public List<Jugador> listarJugadoresPorEquipo(String nombreEquipo) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nombreEquipo)) {
                return e.getJugadores();
            }
        }
        return new ArrayList<>();
    }

    // ===================== HU-5: ELIMINAR JUGADOR =====================
    public boolean eliminarJugador(String nombreEquipo, String numeroIdentidad) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nombreEquipo)) {
                return e.getJugadores().removeIf(j -> j.getNumeroIdentidad().equals(numeroIdentidad));
            }
        }
        return false;
    }

    // ===================== HU-6: EDITAR JUGADOR =====================
    public boolean editarJugador(String nombreEquipo, String numeroIdentidad, String nuevoNombre, String nuevaPosicion, int nuevaEdad) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nombreEquipo)) {
                for (Jugador j : e.getJugadores()) {
                    if (j.getNumeroIdentidad().equals(numeroIdentidad)) {
                        j.setNombre(nuevoNombre);
                        j.setPosicion(nuevaPosicion);
                        j.setEdad(nuevaEdad);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ===================== MÉTODO OPCIONAL: OBTENER TODOS LOS EQUIPOS =====================
    public List<Equipo> obtenerEquipos() {
        return equipos;
    }
}
