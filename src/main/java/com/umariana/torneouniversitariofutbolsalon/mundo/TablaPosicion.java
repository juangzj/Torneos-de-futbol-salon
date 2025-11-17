package com.umariana.torneouniversitariofutbolsalon.mundo;

public class TablaPosicion {
    private int idEquipo;
    private String nombre;
    private int jugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    private int golesAFavor;
    private int golesEnContra;
    private int diferencia;
    private int puntos;

    public TablaPosicion(int idEquipo, String nombre) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
    }

    // getters y setters
    public int getIdEquipo() { return idEquipo; }
    public String getNombre() { return nombre; }
    public int getJugados() { return jugados; }
    public void setJugados(int jugados) { this.jugados = jugados; }
    public int getGanados() { return ganados; }
    public void setGanados(int ganados) { this.ganados = ganados; }
    public int getEmpatados() { return empatados; }
    public void setEmpatados(int empatados) { this.empatados = empatados; }
    public int getPerdidos() { return perdidos; }
    public void setPerdidos(int perdidos) { this.perdidos = perdidos; }
    public int getGolesAFavor() { return golesAFavor; }
    public void setGolesAFavor(int golesAFavor) { this.golesAFavor = golesAFavor; }
    public int getGolesEnContra() { return golesEnContra; }
    public void setGolesEnContra(int golesEnContra) { this.golesEnContra = golesEnContra; }
    public int getDiferencia() { return diferencia; }
    public void setDiferencia(int diferencia) { this.diferencia = diferencia; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    @Override
    public String toString() {
        return nombre + " | PJ: " + jugados + " W: " + ganados + " D: " + empatados + " L: " + perdidos +
                " GF: " + golesAFavor + " GC: " + golesEnContra + " DG: " + diferencia + " Pts: " + puntos;
    }
}
