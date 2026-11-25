package model;

public class IdentidadeVisual extends Servico {
    private static final long serialVersionUID = 1L;
    private int qtdAplicacoes;

    public IdentidadeVisual(String titulo, double valorBase, int qtdAplicacoes) {
        super(titulo, valorBase);
        this.qtdAplicacoes = qtdAplicacoes;
    }

    @Override
    public double calcularValorFinal() {
        return getValorBase() + (qtdAplicacoes * 80.0);
    }

    @Override
    public String toString() {
        return getTitulo() + " [ID Visual - " + qtdAplicacoes + " aplic.]";
    }
}