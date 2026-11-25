package model;

public class Designer extends Pessoa {
    private static final long serialVersionUID = 1L;
    private String especialidade;

    public Designer(String nome, String email, String especialidade) {
        super(nome, email);
        this.especialidade = especialidade;
    }

    public String getEspecialidade() { return especialidade; }

    @Override
    public String getResumo() {
        return getNome() + " - " + especialidade;
    }

    @Override
    public String toString() {
        return getNome() + " (" + especialidade + ")";
    }
}