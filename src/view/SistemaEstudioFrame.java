package view;

import exceptions.OrcamentoInvalidoException;
import exceptions.PersistenciaException;
import model.*;
import persistence.ArquivoRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class SistemaEstudioFrame extends JFrame {
    private List<Pedido> listaPedidos;
    private List<Designer> listaDesigners;
    private List<Servico> catalogoServicos;
    private Pedido pedidoAtual;
    private ArquivoRepository repository;

    // Componentes Aba 1: Novo Orçamento
    private JTextField txtClienteNome, txtClienteEmail, txtClienteEmpresa;
    private JComboBox<Designer> cbDesigner;
    private JComboBox<Servico> cbServico;
    private JSpinner spQuantidade;
    private DefaultTableModel modeloItens;
    private JLabel lblTotalAtual;

    // Componentes Aba 2: Pedidos & Faturamento
    private DefaultTableModel modeloPedidos;
    private JTable tabelaPedidos;
    private JLabel lblResumoFinanceiro;

    // Componentes Aba 4: Relatório
    private JComboBox<String> cbPedidosRelatorio;
    private JTextArea txtAreaRelatorio;

    public SistemaEstudioFrame() {
        repository = new ArquivoRepository();
        inicializarDadosBase();

        try {
            listaPedidos = repository.carregarPedidos();
        } catch (PersistenciaException e) {
            listaPedidos = new ArrayList<>();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        configurarJanela();
        criarMenuSuperior();
        inicializarAbas();
        atualizarTelasDependentes();
    }

    private void inicializarDadosBase() {
        listaDesigners = new ArrayList<>();
        listaDesigners.add(new Designer("Izza Maria", "izza@studioflow.com", "Ilustração & Identidade Visual"));
        listaDesigners.add(new Designer("Lucas Silva", "lucas@studioflow.com", "Motion Design & 3D"));
        listaDesigners.add(new Designer("Camila Rocha", "camila@studioflow.com", "UI/UX & Social Media"));

        catalogoServicos = new ArrayList<>();
        catalogoServicos.add(new ArteDigital("Ilustração de Personagem", 300.0, true));
        catalogoServicos.add(new ArteDigital("Banner Redes Sociais", 120.0, false));
        catalogoServicos.add(new IdentidadeVisual("Manual de Marca Completo", 900.0, 5));
        catalogoServicos.add(new IdentidadeVisual("Logotipo Essencial", 500.0, 2));
    }

    private void configurarJanela() {
        setTitle("StudioFlow - Sistema de Gestão de Estúdio Criativo");
        setSize(920, 620);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Salva automaticamente ao clicar no X da janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    repository.salvarPedidos(listaPedidos);
                } catch (PersistenciaException ex) {
                    System.err.println(ex.getMessage());
                }
                System.exit(0);
            }
        });
    }

    private void criarMenuSuperior() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSistema = new JMenu("StudioFlow");
        JMenuItem itemSobre = new JMenuItem("Sobre o Sistema");
        JMenuItem itemSair = new JMenuItem("Salvar e Sair");

        itemSobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "StudioFlow v2.0 - Gestão de Projetos Criativos\n" +
                "Desenvolvido por: Izza Maria Soares Silva\n" +
                "Disciplina: Programação Orientada a Objetos (UFU)",
                "Sobre o StudioFlow", JOptionPane.INFORMATION_MESSAGE));

        itemSair.addActionListener(e -> {
            try {
                repository.salvarPedidos(listaPedidos);
                JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!");
                System.exit(0);
            } catch (PersistenciaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        menuSistema.add(itemSobre);
        menuSistema.addSeparator();
        menuSistema.add(itemSair);
        menuBar.add(menuSistema);
        setJMenuBar(menuBar);
    }

    private void inicializarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("SansSerif", Font.BOLD, 13));

        abas.addTab("1. Novo Orçamento", criarPainelOrcamento());
        abas.addTab("2. Pedidos & Faturamento", criarPainelPedidos());
        abas.addTab("3. Equipe & Catálogo", criarPainelCatalogo());
        abas.addTab("4. Relatório do Pedido", criarPainelRelatorio());

        add(abas);
    }

    // ==================== ABA 1: NOVO ORÇAMENTO ====================
    private JPanel criarPainelOrcamento() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Formulário Cliente
        JPanel painelCliente = new JPanel(new GridLayout(4, 2, 8, 8));
        painelCliente.setBorder(BorderFactory.createTitledBorder("Dados do Cliente e Responsável"));

        txtClienteNome = new JTextField();
        txtClienteEmail = new JTextField();
        txtClienteEmpresa = new JTextField();
        cbDesigner = new JComboBox<>(listaDesigners.toArray(new Designer[0]));

        painelCliente.add(new JLabel("Nome do Cliente:"));
        painelCliente.add(txtClienteNome);
        painelCliente.add(new JLabel("E-mail de Contato:"));
        painelCliente.add(txtClienteEmail);
        painelCliente.add(new JLabel("Empresa / Marca:"));
        painelCliente.add(txtClienteEmpresa);
        painelCliente.add(new JLabel("Designer Responsável:"));
        painelCliente.add(cbDesigner);

        // Adição de Serviços
        JPanel painelServicos = new JPanel(new BorderLayout(8, 8));
        painelServicos.setBorder(BorderFactory.createTitledBorder("Itens do Orçamento (Cálculo Polimórfico)"));

        JPanel barraAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cbServico = new JComboBox<>(catalogoServicos.toArray(new Servico[0]));
        spQuantidade = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));
        JButton btnAdicionar = new JButton("Adicionar Serviço");
        lblTotalAtual = new JLabel("Total: R$ 0,00");
        lblTotalAtual.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalAtual.setForeground(new Color(0, 102, 51));

        barraAdd.add(new JLabel("Serviço:"));
        barraAdd.add(cbServico);
        barraAdd.add(new JLabel("Qtd:"));
        barraAdd.add(spQuantidade);
        barraAdd.add(btnAdicionar);
        barraAdd.add(lblTotalAtual);

        modeloItens = new DefaultTableModel(new String[]{"Serviço", "Tipo / Regra", "Valor Unit.", "Qtd", "Subtotal"}, 0);
        JTable tabelaItens = new JTable(modeloItens);

        painelServicos.add(barraAdd, BorderLayout.NORTH);
        painelServicos.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        // Botões inferiores
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnLimpar = new JButton("Limpar Formulário");
        JButton btnSalvar = new JButton("Finalizar e Salvar Pedido");
        btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 12));

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnSalvar);

        painel.add(painelCliente, BorderLayout.NORTH);
        painel.add(painelServicos, BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> adicionarServicoAoPedido());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarPedidoCompleto());

        return painel;
    }

    // ==================== ABA 2: PEDIDOS & FATURAMENTO ====================
    private JPanel criarPainelPedidos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        lblResumoFinanceiro = new JLabel("Pedidos Salvos: 0 | Faturamento Total: R$ 0,00");
        lblResumoFinanceiro.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblResumoFinanceiro.setBorder(BorderFactory.createTitledBorder("Resumo Geral do Estúdio"));

        modeloPedidos = new DefaultTableModel(new String[]{"Nº", "Cliente (Empresa)", "E-mail", "Designer", "Qtd Itens", "Valor Total"}, 0);
        tabelaPedidos = new JTable(modeloPedidos);

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExcluir = new JButton("Excluir Pedido Selecionado");
        painelRodape.add(btnExcluir);

        painel.add(lblResumoFinanceiro, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);
        painel.add(painelRodape, BorderLayout.SOUTH);

        btnExcluir.addActionListener(e -> excluirPedidoSelecionado());

        return painel;
    }

    // ==================== ABA 3: EQUIPE & CATÁLOGO ====================
    private JPanel criarPainelCatalogo() {
        JPanel painel = new JPanel(new GridLayout(2, 1, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Tabela Designers
        DefaultTableModel modDesigners = new DefaultTableModel(new String[]{"Nome", "E-mail", "Especialidade"}, 0);
        for (Designer d : listaDesigners) {
            modDesigners.addRow(new Object[]{d.getNome(), d.getEmail(), d.getEspecialidade()});
        }
        JPanel pDesigners = new JPanel(new BorderLayout());
        pDesigners.setBorder(BorderFactory.createTitledBorder("Designers da Equipe (Herança de Pessoa)"));
        pDesigners.add(new JScrollPane(new JTable(modDesigners)));

        // Tabela Serviços
        DefaultTableModel modServicos = new DefaultTableModel(new String[]{"Título do Serviço", "Categoria", "Valor Base", "Valor Final Calculado"}, 0);
        for (Servico s : catalogoServicos) {
            String tipo = (s instanceof ArteDigital) ? "Arte Digital" : "Identidade Visual";
            modServicos.addRow(new Object[]{
                s.getTitulo(),
                tipo,
                String.format("R$ %.2f", s.getValorBase()),
                String.format("R$ %.2f", s.calcularValorFinal())
            });
        }
        JPanel pServicos = new JPanel(new BorderLayout());
        pServicos.setBorder(BorderFactory.createTitledBorder("Catálogo de Serviços (Herança de Servico + Polimorfismo)"));
        pServicos.add(new JScrollPane(new JTable(modServicos)));

        painel.add(pDesigners);
        painel.add(pServicos);
        return painel;
    }

    // ==================== ABA 4: RELATÓRIO DETALHADO ====================
    private JPanel criarPainelRelatorio() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cbPedidosRelatorio = new JComboBox<>();
        JButton btnGerar = new JButton("Gerar Recibo Detalhado");
        topo.add(new JLabel("Selecione o Pedido:"));
        topo.add(cbPedidosRelatorio);
        topo.add(btnGerar);

        txtAreaRelatorio = new JTextArea();
        txtAreaRelatorio.setEditable(false);
        txtAreaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 13));

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(txtAreaRelatorio), BorderLayout.CENTER);

        btnGerar.addActionListener(e -> exibirRelatorioSelecionado());

        return painel;
    }

    // ==================== LÓGICA E EVENTOS ====================
    private void adicionarServicoAoPedido() {
        try {
            if (pedidoAtual == null) {
                String nome = txtClienteNome.getText().trim();
                String email = txtClienteEmail.getText().trim();
                String empresa = txtClienteEmpresa.getText().trim();
                if (nome.isEmpty() || email.isEmpty() || empresa.isEmpty()) {
                    throw new OrcamentoInvalidoException("Preencha Nome, E-mail e Empresa do cliente antes de adicionar serviços.");
                }
                Cliente cliente = new Cliente(nome, email, empresa);
                Designer designer = (Designer) cbDesigner.getSelectedItem();
                int proximoNum = listaPedidos.isEmpty() ? 1 : listaPedidos.get(listaPedidos.size() - 1).getNumero() + 1;
                pedidoAtual = new Pedido(proximoNum, cliente, designer);
            }

            Servico servico = (Servico) cbServico.getSelectedItem();
            int qtd = (int) spQuantidade.getValue();

            pedidoAtual.adicionarItem(servico, qtd);

            String tipo = (servico instanceof ArteDigital) ? "Arte Digital" : "Identidade Visual";
            modeloItens.addRow(new Object[]{
                servico.toString(),
                tipo,
                String.format("R$ %.2f", servico.calcularValorFinal()),
                qtd,
                String.format("R$ %.2f", servico.calcularValorFinal() * qtd)
            });

            lblTotalAtual.setText(String.format("Total: R$ %.2f", pedidoAtual.calcularValorFinal()));
        } catch (OrcamentoInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarPedidoCompleto() {
        try {
            if (pedidoAtual == null) {
                throw new OrcamentoInvalidoException("Nenhum pedido em andamento para salvar.");
            }
            pedidoAtual.validarPedido();
            listaPedidos.add(pedidoAtual);
            repository.salvarPedidos(listaPedidos);

            atualizarTelasDependentes();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Pedido salvo com sucesso! Veja os detalhes nas abas 2 e 4.");
        } catch (OrcamentoInvalidoException | PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPedidoSelecionado() {
        int linha = tabelaPedidos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            listaPedidos.remove(linha);
            repository.salvarPedidos(listaPedidos);
            atualizarTelasDependentes();
            JOptionPane.showMessageDialog(this, "Pedido excluído com sucesso!");
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirRelatorioSelecionado() {
        int idx = cbPedidosRelatorio.getSelectedIndex();
        if (idx == -1 || listaPedidos.isEmpty()) {
            txtAreaRelatorio.setText("Nenhum pedido disponível para relatório.");
            return;
        }
        Pedido p = listaPedidos.get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("           STUDIOFLOW - RECIBO DE ORÇAMENTO         \n");
        sb.append("====================================================\n");
        sb.append("Pedido Nº: ").append(p.getNumero()).append("\n");
        sb.append("Cliente:   ").append(p.getCliente().getResumo()).append("\n");
        sb.append("E-mail:    ").append(p.getCliente().getEmail()).append("\n");
        sb.append("Designer:  ").append(p.getDesigner().getResumo()).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append("SERVIÇOS CONTRATADOS:\n");
        for (ItemPedido item : p.getItens()) {
            sb.append(String.format(" - %s (x%d): R$ %.2f\n",
                    item.getServico().toString(), item.getQuantidade(), item.getSubtotal()));
        }
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("VALOR TOTAL DO ORÇAMENTO: R$ %.2f\n", p.calcularValorFinal()));
        sb.append("====================================================\n");
        txtAreaRelatorio.setText(sb.toString());
    }

    private void atualizarTelasDependentes() {
        modeloPedidos.setRowCount(0);
        cbPedidosRelatorio.removeAllItems();
        double faturamento = 0.0;

        for (Pedido p : listaPedidos) {
            modeloPedidos.addRow(new Object[]{
                p.getNumero(),
                p.getCliente().getResumo(),
                p.getCliente().getEmail(),
                p.getDesigner().getNome(),
                p.getItens().size(),
                String.format("R$ %.2f", p.calcularValorFinal())
            });
            cbPedidosRelatorio.addItem("Pedido #" + p.getNumero() + " - " + p.getCliente().getResumo());
            faturamento += p.calcularValorFinal();
        }

        lblResumoFinanceiro.setText(String.format("Pedidos Salvos: %d   |   Faturamento Total Acumulado: R$ %.2f",
                listaPedidos.size(), faturamento));
    }

    private void limparFormulario() {
        pedidoAtual = null;
        txtClienteNome.setText("");
        txtClienteEmail.setText("");
        txtClienteEmpresa.setText("");
        modeloItens.setRowCount(0);
        lblTotalAtual.setText("Total: R$ 0,00");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new SistemaEstudioFrame().setVisible(true));
    }
}