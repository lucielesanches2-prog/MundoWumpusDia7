package mundowumpusdia7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class TelaJogo extends JFrame {

    // ============================================================
    // CAMPOS — estado do jogo (lógica) + referências de componentes
    // visuais (interface). Ficam juntos no topo, como é convenção em
    // Java; a separação de responsabilidades acontece nos MÉTODOS
    // abaixo, não na declaração dos campos.
    // ============================================================

    private Mundo mundo;
    private AgenteInteligente agente;
    private JLabel[][] celulas;

    private int quantidadeMovimentos = 0;
    private boolean jogoFinalizado = false;
    private Timer timerAutoJogo;

    private JLabel lblTituloNivel;
    private JLabel lblPontuacao;
    private JLabel lblMovimentos;
    private JLabel lblOuro;
    private JLabel lblFlecha;
    private JLabel lblPercepcoes;
    private JTextArea txtLog;
    private JPanel painelGridComIndices;

    private final Color COR_FUNDO_JANELA = new Color(18, 20, 24);
    private final Color COR_FUNDO_GRID = new Color(25, 27, 32);
    private final Color COR_CELULA_NAO_VISITADA = new Color(35, 37, 42);
    private final Color COR_CELULA_VISITADA = new Color(50, 53, 62);

    private final Color COR_AGENTE = new Color(180, 50, 50);
    private final Color COR_WUMPUS = new Color(195, 155, 35);
    private final Color COR_POCO = new Color(12, 12, 15);
    private final Color COR_OURO = new Color(235, 185, 20);

    // ============================================================
    // CONSTRUTOR — orquestra os dois lados: primeiro monta o modelo
    // (lógica), depois a tela (interface), e no fim liga o loop
    // automático (lógica de novo). Fica aqui no topo por convenção.
    // ============================================================

    public TelaJogo() {
        iniciarNovoJogo();            
        configurarJanela();           
        gerarGridComCoordenadas();    
        atualizarTela();              
        iniciarLoopAutomatico();  
        configurarTeclaEspaco();   
    }

    // ============================================================
    // ==================== INTERFACE (Laiz) ======================
  

    private void configurarJanela() {
        setTitle("Mundo de Wumpus — Jogando");
        setSize(850, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(COR_FUNDO_JANELA);

        // Barra Superior
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(COR_FUNDO_JANELA);
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        String nomeNivel = (Mundo.TAMANHO == 5) ? "Fácil (5x5)" : (Mundo.TAMANHO == 7) ? "Médio (7x7)" : "Difícil (10x10)";
        lblTituloNivel = new JLabel(nomeNivel, SwingConstants.LEFT);
        lblTituloNivel.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTituloNivel.setForeground(Color.WHITE);

        JPanel painelBotoesTopo = new JPanel(new GridLayout(1, 3, 10, 0));
        painelBotoesTopo.setBackground(COR_FUNDO_JANELA);

        JButton btnNovoMapa = new JButton("Novo mapa");
        JButton btnMenu = new JButton("Menu");

        btnNovoMapa.addActionListener(e -> reiniciarPartida());
        btnMenu.addActionListener(e -> voltarAoMenu());

        painelBotoesTopo.add(btnNovoMapa);
        painelBotoesTopo.add(btnMenu);

        painelTopo.add(lblTituloNivel, BorderLayout.WEST);
        painelTopo.add(painelBotoesTopo, BorderLayout.EAST);
        add(painelTopo, BorderLayout.NORTH);

        // Grid Central + Painel Lateral
        JPanel painelCentral = new JPanel(new BorderLayout(20, 0));
        painelCentral.setBackground(COR_FUNDO_JANELA);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        painelGridComIndices = new JPanel();
        painelGridComIndices.setBackground(COR_FUNDO_GRID);
        painelGridComIndices.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 43, 50), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        painelCentral.add(painelGridComIndices, BorderLayout.CENTER);

        JPanel painelDireita = new JPanel();
        painelDireita.setLayout(new BoxLayout(painelDireita, BoxLayout.Y_AXIS));
        painelDireita.setBackground(COR_FUNDO_JANELA);
        painelDireita.setPreferredSize(new Dimension(320, 0));

        lblPontuacao = criarRotuloStatus("Pontuação: 0", Color.WHITE);
        lblMovimentos = criarRotuloStatus("Movimentos: 0", Color.WHITE);
        lblOuro = criarRotuloStatus("Ouro: não", Color.WHITE);
        lblFlecha = criarRotuloStatus("Flecha: sim", Color.WHITE);
        lblPercepcoes = criarRotuloStatus("Percepções: nenhuma", COR_WUMPUS);

        painelDireita.add(lblPontuacao);
        painelDireita.add(Box.createRigidArea(new Dimension(0, 5)));
        painelDireita.add(lblMovimentos);
        painelDireita.add(Box.createRigidArea(new Dimension(0, 5)));
        painelDireita.add(lblOuro);
        painelDireita.add(Box.createRigidArea(new Dimension(0, 5)));
        painelDireita.add(lblFlecha);
        painelDireita.add(Box.createRigidArea(new Dimension(0, 8)));
        painelDireita.add(lblPercepcoes);
        painelDireita.add(Box.createRigidArea(new Dimension(0, 15)));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(COR_FUNDO_GRID);
        txtLog.setForeground(Color.WHITE);
        txtLog.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);

        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(50, 55, 65), 1));
        scrollLog.setPreferredSize(new Dimension(300, 200));

        painelDireita.add(scrollLog);
        painelCentral.add(painelDireita, BorderLayout.EAST);
        add(painelCentral, BorderLayout.CENTER);

        JLabel lblRodape = new JLabel("", SwingConstants.CENTER);
        lblRodape.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblRodape.setForeground(new Color(150, 155, 170));
        lblRodape.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        add(lblRodape, BorderLayout.SOUTH);

        adicionarLog("Mapa novo gerado. Boa sorte!");
    }

    private JLabel criarRotuloStatus(String texto, Color corTexto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(corTexto);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private void gerarGridComCoordenadas() {
        int tam = Mundo.TAMANHO;
        celulas = new JLabel[tam][tam];

        painelGridComIndices.setLayout(new GridLayout(tam + 1, tam + 1, 3, 3));
        painelGridComIndices.removeAll();

        int fonteTamanho = (tam <= 5) ? 16 : (tam <= 7) ? 14 : 11;

        for (int i = 0; i <= tam; i++) {
            for (int j = 0; j <= tam; j++) {
                if (i == 0 && j == 0) {
                    painelGridComIndices.add(new JLabel(""));
                } else if (i == 0) {
                    JLabel lblCol = new JLabel(String.valueOf(j - 1), SwingConstants.CENTER);
                    lblCol.setFont(new Font("SansSerif", Font.BOLD, 14));
                    lblCol.setForeground(Color.WHITE);
                    painelGridComIndices.add(lblCol);
                } else if (j == 0) {
                    JLabel lblLin = new JLabel(String.valueOf(i - 1), SwingConstants.CENTER);
                    lblLin.setFont(new Font("SansSerif", Font.BOLD, 14));
                    lblLin.setForeground(Color.WHITE);
                    painelGridComIndices.add(lblLin);
                } else {
                    // Tradução entre a posição visual (i, j) e a posição
                    // lógica do jogo (r, c) — ver o roteiro para a explicação
                    // completa desse trecho.
                    int r = i - 1;
                    int c = j - 1;

                    JLabel label = new JLabel("", SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setFont(new Font("SansSerif", Font.BOLD, fonteTamanho));
                    label.setBorder(BorderFactory.createLineBorder(new Color(45, 48, 55), 1));

                    celulas[r][c] = label;
                    painelGridComIndices.add(label);
                }
            }
        }

        painelGridComIndices.revalidate();
        painelGridComIndices.repaint();
    }

    private void adicionarLog(String mensagem) {
        txtLog.append(mensagem + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }



    private void voltarAoMenu() {
        if (timerAutoJogo != null) {
            timerAutoJogo.stop();
        }
        TelaEscolherFase telafase = new TelaEscolherFase();
        telafase.setVisible(true);
        this.dispose();
    }

    // ===============================================================
    // ==================  LÓGICA DO JOGO (Andrey e Luciele) =========

    private void iniciarNovoJogo() {
        this.mundo = new Mundo();
        this.agente = new AgenteInteligente();
        this.quantidadeMovimentos = 0;
        this.jogoFinalizado = false;

        if (mundo != null && agente != null) {
            mundo.marcarVisitada(agente.getLinha(), agente.getColuna());
        }
    }

    private void reiniciarPartida() {
        iniciarNovoJogo();
        txtLog.setText("");
        adicionarLog("Mapa novo gerado. Boa sorte!");
        atualizarTela();
        this.requestFocusInWindow();
        iniciarLoopAutomatico();
    }
    
    private void iniciarLoopAutomatico() {
        if (timerAutoJogo != null) {
            timerAutoJogo.stop();
        }
        timerAutoJogo = new Timer(500, e -> {
            
            if (jogoFinalizado || !agente.estaVivo()) {
                timerAutoJogo.stop();
                return;
            }
            moverAgente(' ');
            atualizarTela();
        });
        timerAutoJogo.setInitialDelay(500);
        timerAutoJogo.start();
    }

    /**
     * Calcula brisa/fedor/brilho da posição atual do agente. Reaproveitado
     * tanto em atualizarPainelStatus() (para exibir) quanto em
     * moverAgente() (para alimentar agente.observar(...)).
     */
    private boolean[] calcularPercepcoesAtuais() {
        int l = agente.getLinha();
        int c = agente.getColuna();

        boolean brisa = false, fedor = false, brilho = false;
        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : direcoes) {
            int nl = l + d[0];
            int nc = c + d[1];
            if (mundo.estaDentroDoMapa(nl, nc)) {
                char elem = mundo.getElemento(nl, nc);
                if (elem == Mundo.POCO1 || elem == Mundo.POCO2) brisa = true;
                if (elem == Mundo.WUMPUS) fedor = true;
                if (elem == Mundo.OURO) brilho = true;
            }
        }

        return new boolean[]{brisa, fedor, brilho};
    }

    private void moverAgente(char direcao) {
        // Antes de mover, o agente "observa" a casa em que está: calcula
        // brisa/fedor da posição atual e alimenta a memória de risco.
        boolean[] percepcoes = calcularPercepcoesAtuais();
        boolean brisa = percepcoes[0];
        boolean fedor = percepcoes[1];
        agente.observar(mundo, brisa, fedor);

        // Se sentir fedor e ainda tiver flecha, mira pela casa vizinha
        // desconhecida de maior risco (não é mais sorteio).
        if (agente.possuiFlecha() && fedor) {
            char direcaoFlecha = agente.escolherDirecaoDaFlecha(mundo);

            agente.usarFlecha();
            agente.alterarPontuacao(AgenteInteligente.CUSTO_FLECHA);
            boolean acertou = mundo.atirarFlecha(agente.getLinha(), agente.getColuna(), direcaoFlecha);
            if (acertou) {
                agente.alterarPontuacao(AgenteInteligente.BONUS_WUMPUS);
                adicionarLog("Sentiu o fedor, mirou pelo risco e atirou pra " + direcaoFlecha + " matando o Wumpus!");
            } else {
                adicionarLog("Sentiu o fedor, mirou pelo risco e atirou pra " + direcaoFlecha + ", mas errou.");
            }
        }

        // O agente decide para onde ir: se já tem o ouro, volta pelo
        // caminho conhecido; senão, explora avaliando o risco de cada
        // vizinho. A tecla/timer só pedem "mais um passo".
        String resultado = agente.possuiOuro()
                ? agente.retornarPeloCaminho()
                : agente.moverExplorando(mundo);
        adicionarLog(resultado);
        quantidadeMovimentos++;
        mundo.marcarVisitada(agente.getLinha(), agente.getColuna());
        adicionarLog("Agente moveu para (" + agente.getLinha() + ", " + agente.getColuna() + ")");

        // Coleta automática do ouro ao pisar
        if (mundo.getElemento(agente.getLinha(), agente.getColuna()) == Mundo.OURO) {
            agente.pegarOuro();
            agente.alterarPontuacao(AgenteInteligente.BONUS_OURO);
            mundo.removerElemento(agente.getLinha(), agente.getColuna());
            adicionarLog("Você pegou o Ouro!");
        }
    }

    private void verificarFimDeJogo() {
        if (jogoFinalizado) return;

        char elemAtual = mundo.getElemento(agente.getLinha(), agente.getColuna());

        if (elemAtual == Mundo.WUMPUS) {
            // Confronto corpo a corpo: chance de matar o Wumpus em vez de
            // morrer automaticamente.
            if (agente.tentarMatarWumpus()) {
                agente.alterarPontuacao(AgenteInteligente.BONUS_WUMPUS);
                mundo.removerElemento(agente.getLinha(), agente.getColuna());
                adicionarLog("Encontrou o Wumpus cara a cara e conseguiu matá-lo!");
                atualizarTela();
                return;
            }

            agente.morrer();
            agente.alterarPontuacao(AgenteInteligente.PENALIDADE_MORTE);
            jogoFinalizado = true;
            if (timerAutoJogo != null) {
                timerAutoJogo.stop();
            }
            atualizarTela();

            String msg = "GAME OVER! O Wumpus te devorou!";
            adicionarLog(msg);
            JOptionPane.showMessageDialog(this, msg);

        } else if (elemAtual == Mundo.POCO1 || elemAtual == Mundo.POCO2) {
            agente.morrer();
            agente.alterarPontuacao(AgenteInteligente.PENALIDADE_MORTE);
            jogoFinalizado = true;
            if (timerAutoJogo != null) {
                timerAutoJogo.stop();
            }
            atualizarTela();

            String msg = "GAME OVER! Você caiu em um poço!";
            adicionarLog(msg);
            JOptionPane.showMessageDialog(this, msg);

        } else if (agente.possuiOuro() && agente.getLinha() == 0 && agente.getColuna() == 0) {
            agente.alterarPontuacao(AgenteInteligente.BONUS_VITORIA);
            jogoFinalizado = true;
            if (timerAutoJogo != null) {
                timerAutoJogo.stop();
            }
            atualizarTela();
            adicionarLog("PARABÉNS! Você saiu da caverna com o ouro!");
            JOptionPane.showMessageDialog(this, "PARABÉNS! Você venceu o jogo!");
        }
    }

    public void atualizarTela() {
        //Método que redesenha o tabuleiro depois de cada ação do jogo. 
        int tam = Mundo.TAMANHO;

        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {

                boolean visivel = jogoFinalizado || espiandoMapa || mundo.isVisitado(i, j);

                if (agente.getLinha() == i && agente.getColuna() == j && agente.estaVivo()) {
                    celulas[i][j].setText("A");
                    celulas[i][j].setForeground(Color.WHITE);
                    celulas[i][j].setBackground(COR_AGENTE);
                }
                else if (visivel) {
                    char elem = mundo.getElemento(i, j);

                    if (elem == Mundo.VAZIO) {
                        celulas[i][j].setText("");
                        celulas[i][j].setBackground(COR_CELULA_VISITADA);
                    } else {
                        celulas[i][j].setText(String.valueOf(elem));

                        if (elem == Mundo.WUMPUS) {
                            celulas[i][j].setForeground(Color.BLACK);
                            celulas[i][j].setBackground(COR_WUMPUS);
                        }
                        else if (elem == Mundo.POCO1 || elem == Mundo.POCO2) {
                            celulas[i][j].setForeground(Color.WHITE);
                            celulas[i][j].setBackground(COR_POCO);
                        }
                        else if (elem == Mundo.OURO) {
                            celulas[i][j].setForeground(Color.BLACK);
                            celulas[i][j].setBackground(COR_OURO);
                        }
                    }
                }
                else {
                    celulas[i][j].setText("?");
                    celulas[i][j].setForeground(new Color(110, 115, 125));
                    celulas[i][j].setBackground(COR_CELULA_NAO_VISITADA);
                }
            }
        }

        atualizarPainelStatus();
        verificarFimDeJogo();
        
  
        
    }
    private boolean espiandoMapa =false;
    private javax.swing.Timer gameLoop;
    
          private void iniciarGameLoop() {
    // Atualiza o jogo a cada 50 milissegundos (~20 FPS)
    gameLoop = new javax.swing.Timer(50, e -> {
        // Coloque aqui lógicas contínuas (ex: tempo, movimento automático de inimigos)
        atualizarTela();
    });
    gameLoop.start(); // Inicia o loop
}

    private void atualizarPainelStatus() {
        lblPontuacao.setText("Pontuação: " + agente.getPontuacao());
        lblMovimentos.setText("Movimentos: " + quantidadeMovimentos);
        lblOuro.setText("Ouro: " + (agente.possuiOuro() ? "sim" : "não"));
        lblFlecha.setText("Flecha: " + (agente.possuiFlecha() ? "sim" : "não"));

        boolean[] percepcoes = calcularPercepcoesAtuais();
        boolean brisa = percepcoes[0];
        boolean fedor = percepcoes[1];
        boolean brilho = percepcoes[2];

        StringBuilder sb = new StringBuilder();
        if (brisa) sb.append("brisa ");
        if (fedor) sb.append("fedor ");
        if (brilho) sb.append("brilho ");

        String textoPercepcao = sb.length() > 0 ? sb.toString().trim() : "nenhuma";
        lblPercepcoes.setText("Percepções: " + textoPercepcao);
    }
    
private void configurarTeclaEspaco() {

    addKeyListener(new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                espiandoMapa = true;
                atualizarTela();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                espiandoMapa = false;
                atualizarTela();
            }
        }
    });

    setFocusable(true);
    requestFocusInWindow();
}
}
