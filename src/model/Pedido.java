package model;

import exceptions.OrcamentoInvalidoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Orcavel, Serializable {
    private static final long serialVersionUID = 1L;
    private int numero;
    private Cliente cliente;        // Agregação
    private Designer designer;      // Agregação
    private List<ItemPedido> itens; // Composição

    public Pedido(int numero, Cliente cliente, Designer designer) {
        this.numero = numero;
        this.cliente = cliente;
        this.designer = designer;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(Servico servico, int quantidade) throws OrcamentoInvalidoException {
        if (quantidade <= 0) {
            throw new OrcamentoInvalidoException("A quantidade do serviço deve ser maior que zero.");
        }
        this.itens.add(new ItemPedido(servico, quantidade));
    }

    public void validarPedido() throws OrcamentoInvalidoException {
        if (itens.isEmpty()) {
            throw new OrcamentoInvalidoException("O pedido deve conter pelo menos um serviço.");
        }
    }

    @Override
    public double calcularValorFinal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public Designer getDesigner() { return designer; }
    public List<ItemPedido> getItens() { return itens; }
}
