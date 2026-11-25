package persistence;

import exceptions.PersistenciaException;
import model.Pedido;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoRepository {
    private static final String ARQUIVO = "estudio_dados.dat";

    public void salvarPedidos(List<Pedido> pedidos) throws PersistenciaException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(pedidos);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar os dados no arquivo.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Pedido> carregarPedidos() throws PersistenciaException {
        File file = new File(ARQUIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Pedido>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("Erro ao carregar os dados do arquivo.", e);
        }
    }
}