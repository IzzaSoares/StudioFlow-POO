package exceptions;

public class PersistenciaException extends Exception {
    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
