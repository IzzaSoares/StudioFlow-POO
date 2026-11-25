package model;

public class ArteDigital extends Servico {
    private static final long serialVersionUID = 1L;
    private boolean altaResolucao;

    public ArteDigital(String titulo, double valorBase, boolean altaResolucao) {
        super(titulo, valorBase);
        this.altaResolucao = altaResolucao;
    }

    @Override
    public double calcularValorFinal() {
        return altaResolucao ? getValorBase() * 1.25 : getValorBase();
    }

    @Override
    public String toString() {
        return getTitulo() + (altaResolucao ? " [Arte 4K]" : " [Arte Web]");
    }
}