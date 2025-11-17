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

    // NUEVOS CAMPOS
    private Integer golesLocal;      // puede ser null si no hay resultado
    private Integer golesVisitante;  // puede ser null si no hay resultado

    // Constructor original modificado para incluir goles (pero no rompe el código viejo si lo usas con null)
    public Partido(int id, int idEquipoLocal, String nombreEquipoLocal, int idEquipoVisitante, String nombreEquipoVisitante, Date fecha, Integer golesLocal, Integer golesVisitante) {
        this.id = id;
        this.idEquipoLocal = idEquipoLocal;
        this.nombreEquipoLocal = nombreEquipoLocal;
        this.idEquipoVisitante = idEquipoVisitante;
        this.nombreEquipoVisitante = nombreEquipoVisitante;
        this.fecha = fecha;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
    }

    // Si quieres conservar el constructor antiguo, puedes dejarlo opcionalmente:
    public Partido(int id, int idEquipoLocal, String nombreEquipoLocal, int idEquipoVisitante, String nombreEquipoVisitante, Date fecha) {
        this(id, idEquipoLocal, nombreEquipoLocal, idEquipoVisitante, nombreEquipoVisitante, fecha, null, null);
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

    // NUEVOS GETTERS Y SETTERS
    public Integer getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(Integer golesLocal) {
        this.golesLocal = golesLocal;
    }

    public Integer getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(Integer golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    @Override
    public String toString() {
        String fechaStr = (fecha == null) ? "sin fecha" : fecha.toString();
        String marcador;
        if (golesLocal == null || golesVisitante == null) {
            marcador = " - sin resultado";
        } else {
            marcador = " - " + golesLocal + " : " + golesVisitante;
        }
        return "ID: " + id + " - " + nombreEquipoLocal + " vs " + nombreEquipoVisitante + " (" + fechaStr + ")" + marcador;
    }
}
