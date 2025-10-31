package com.umariana.torneouniversitariofutbolsalon.mundo;

import java.sql.Date;

/**
 * Modelo que representa un partido entre dos equipos
 */
public class Partido {

    private int id;
    private int idEquipoLocal;
    private String nombreEquipoLocal;
    private int idEquipoVisitante;
    private String nombreEquipoVisitante;
    private Date fecha; // puede ser null

    public Partido(int id, int idEquipoLocal, String nombreEquipoLocal, int idEquipoVisitante, String nombreEquipoVisitante, Date fecha) {
        this.id = id;
        this.idEquipoLocal = idEquipoLocal;
        this.nombreEquipoLocal = nombreEquipoLocal;
        this.idEquipoVisitante = idEquipoVisitante;
        this.nombreEquipoVisitante = nombreEquipoVisitante;
        this.fecha = fecha;
    }

    // getters y setters
    public int getId() {
        return id;
    }

    public int getIdEquipoLocal() {
        return idEquipoLocal;
    }

    public String getNombreEquipoLocal() {
        return nombreEquipoLocal;
    }

    public int getIdEquipoVisitante() {
        return idEquipoVisitante;
    }

    public String getNombreEquipoVisitante() {
        return nombreEquipoVisitante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        String fechaStr = (fecha == null) ? "sin fecha" : fecha.toString();
        return "ID: " + id + " - " + nombreEquipoLocal + " vs " + nombreEquipoVisitante + " (" + fechaStr + ")";
    }
}