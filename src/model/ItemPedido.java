package model;

import java.io.Serializable;

public class ItemPedido implements Serializable {
    private static final long serialVersionUID = 1L;
    private Servico servico;
    private int quantidade;

    public ItemPedido(Servico servico, int quantidade) {
        this.servico = servico;
        this.quantidade = quantidade;
    }

    public Servico getServico() { return servico; }
    public int getQuantidade() { return quantidade; }

    public double getSubtotal() {
        return servico.calcularValorFinal() * quantidade;
    }
}