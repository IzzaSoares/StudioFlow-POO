package model;

import java.io.Serializable;

public abstract class Servico implements Orcavel, Serializable {
    private static final long serialVersionUID = 1L;
    private String titulo;
    private double valorBase;

    public Servico(String titulo, double valorBase) {
        this.titulo = titulo;
        this.valorBase = valorBase;
    }

    public String getTitulo() { return titulo; }
    public double getValorBase() { return valorBase; }

    @Override
    public abstract double calcularValorFinal();
}