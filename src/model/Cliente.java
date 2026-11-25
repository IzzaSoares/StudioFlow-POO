package model;

public class Cliente extends Pessoa {
    private static final long serialVersionUID = 1L;
    private String empresa;

    public Cliente(String nome, String email, String empresa) {
        super(nome, email);
        this.empresa = empresa;
    }

    public String getEmpresa() { return empresa; }

    @Override
    public String getResumo() {
        return getNome() + " (" + empresa + ")";
    }
}