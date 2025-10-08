/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.umariana.torneouniversitariofutbolsalon.mundo;

/**
 *
 * @author Juan Goyes
 */
public class Jugador {

    private int id;
    private String numeroIdentidad;
    private String nombre;
    private String posicion;
    private int edad;

    public Jugador(int id, String numeroIdentidad, String nombre, String posicion, int edad) {
        this.id = id;
        this.numeroIdentidad = numeroIdentidad;
        this.nombre = nombre;
        this.posicion = posicion;
        this.edad = edad;
    }

    public int getId() {
        return id;
    }

    public String getNumeroIdentidad() {
        return numeroIdentidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public int getEdad() {
        return edad;
    }

    // ==== NUEVOS SETTERS PARA PODER EDITAR ====
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}
