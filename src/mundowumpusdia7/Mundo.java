package mundowumpusdia7;

import java.util.Random;

/**
 * A classe Mundo cria e mostra o mapa.
 * Cada posição da matriz possui uma linha e uma coluna.
 */
public class Mundo {

    public static  int TAMANHO ;

    public static final char VAZIO = '.';
    public static final char POCO1 = 'P';
    public static final char POCO2 = 'P';
    public static final char POCO3 = 'P';
    public static final char POCO4 = 'P';
    public static final char POCO5 = 'P';
    public static final char WUMPUS = 'W';
    public static final char OURO = 'O';

    private final char[][] mapa;
    private final boolean[][] visitado;

    public Mundo() {
        mapa = new char[TAMANHO][TAMANHO];
        visitado = new boolean[TAMANHO][TAMANHO];
        criarMapa();
        visitado[0][0] = true;
    }

    /** Preenche a matriz e posiciona os elementos da fase. */
    private void criarMapa() {
        for (int linha = 0; linha < TAMANHO; linha++) {
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                mapa[linha][coluna] = VAZIO;
            }
        }

        Random sorteador = new Random();
       if (TAMANHO == 5) {   
            
        posicionarAleatoriamente(sorteador, POCO1);
        posicionarAleatoriamente(sorteador, POCO2);
            
        } 
       if (TAMANHO== 7) {
              posicionarAleatoriamente(sorteador, POCO1);
        posicionarAleatoriamente(sorteador, POCO2);
                posicionarAleatoriamente(sorteador, POCO3);
        
            
        }
          if (TAMANHO== 10) {
              posicionarAleatoriamente(sorteador, POCO1);
        posicionarAleatoriamente(sorteador, POCO2);
                posicionarAleatoriamente(sorteador, POCO3);
            posicionarAleatoriamente(sorteador, POCO4);
              posicionarAleatoriamente(sorteador, POCO5);
            
        }
 
 
                
           
                
                
        
       
        
 
        
        posicionarAleatoriamente(sorteador, WUMPUS);
        posicionarAleatoriamente(sorteador, OURO);
    }

    /**
     * Sorteia uma casa vazia e diferente da casa inicial (0,0) para o
     * elemento, evitando que dois elementos caiam na mesma posição.
     */
    private void posicionarAleatoriamente(Random sorteador, char elemento) {
        int linha;
        int coluna;
        do {
            linha = sorteador.nextInt(TAMANHO);
            coluna = sorteador.nextInt(TAMANHO);
        } while (mapa[linha][coluna] != VAZIO || (linha == 0 && coluna == 0));
        mapa[linha][coluna] = elemento;
    }

    public boolean estaDentroDoMapa(int linha, int coluna) {
        return linha >= 0 && linha < TAMANHO
                && coluna >= 0 && coluna < TAMANHO;
    }

    public char getElemento(int linha, int coluna) {
        return mapa[linha][coluna];
    }

    public void removerElemento(int linha, int coluna) {
        mapa[linha][coluna] = VAZIO;
    }

    /**
     * Faz a flecha percorrer uma linha reta até sair do mapa.
     * Retorna true quando o Wumpus estava no caminho.
     */
    public boolean atirarFlecha(int linha, int coluna, char direcao) {
        int variacaoLinha = 0;
        int variacaoColuna = 0;

        switch (direcao) {
            case 'W':
                variacaoLinha = -1;
                break;
            case 'S':
                variacaoLinha = 1;
                break;
            case 'A':
                variacaoColuna = -1;
                break;
            case 'D':
                variacaoColuna = 1;
                break;
            default:
                return false;
        }

        int linhaDaFlecha = linha + variacaoLinha;
        int colunaDaFlecha = coluna + variacaoColuna;

        while (estaDentroDoMapa(linhaDaFlecha, colunaDaFlecha)) {
            if (mapa[linhaDaFlecha][colunaDaFlecha] == WUMPUS) {
                removerElemento(linhaDaFlecha, colunaDaFlecha);
                return true;
            }

            linhaDaFlecha = linhaDaFlecha + variacaoLinha;
            colunaDaFlecha = colunaDaFlecha + variacaoColuna;
        }

        return false;
    }

    /** Registra uma posição na memória visual do mapa. */
    public void marcarVisitada(int linha, int coluna) {
        visitado[linha][coluna] = true;
    }

    /** Diz se essa posição já foi visitada pelo agente. */
    public boolean isVisitado(int linha, int coluna) {
        return visitado[linha][coluna];
    }

    /** Procura um elemento nas quatro posições vizinhas. */
    private boolean existeVizinho(int linha, int coluna, char procurado) {
        int[][] direcoes = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
        };

        for (int[] direcao : direcoes) {
            int linhaVizinha = linha + direcao[0];
            int colunaVizinha = coluna + direcao[1];

            if (estaDentroDoMapa(linhaVizinha, colunaVizinha)
                    && mapa[linhaVizinha][colunaVizinha] == procurado) {
                return true;
            }
        }

        return false;
    }

    /** Métodos usados pelo agente para consultar suas percepções atuais. */
    public boolean temBrisa(int linha, int coluna) {
        return existeVizinho(linha, coluna, POCO1);
    }

    public boolean temFedor(int linha, int coluna) {
        return existeVizinho(linha, coluna, WUMPUS);
    }

    /** Mostra os sinais existentes ao redor do agente. */
    public void mostrarPercepcoes(AgenteInteligente agente) {
        int linha = agente.getLinha();
        int coluna = agente.getColuna();
        boolean percebeuAlgo = false;

        System.out.print("Percepções: ");

        if (temBrisa(linha, coluna)) {
            System.out.print("BRISA  ");
            percebeuAlgo = true;
        }

        if (temFedor(linha, coluna)) {
            System.out.print("FEDOR  ");
            percebeuAlgo = true;
        }

        if (mapa[linha][coluna] == OURO) {
            System.out.print("BRILHO  ");
            percebeuAlgo = true;
        }

        if (!percebeuAlgo) {
            System.out.print("NENHUMA");
        }

        System.out.println();
    }

    /**
     * Durante o jogo, ? representa uma posição desconhecida e + uma posição
     * visitada. No final, revelarTudo permite discutir o mapa real com a turma.
     */
    public void mostrar(AgenteInteligente agente, boolean revelarTudo) {
        System.out.print("      ");
        for (int coluna = 0; coluna < TAMANHO; coluna++) {
            System.out.print(coluna + "   ");
        }
        System.out.println("  COLUNAS");

        for (int linha = 0; linha < TAMANHO; linha++) {
            System.out.print("  " + linha + "  ");

            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                if (linha == agente.getLinha()
                        && coluna == agente.getColuna()) {
                    System.out.print(agente.estaVivo() ? "[A] " : "[X] ");
                } else if (revelarTudo) {
                    System.out.print("[" + mapa[linha][coluna] + "] ");
                } else if (visitado[linha][coluna]) {
                    System.out.print("[+] ");
                } else {
                    System.out.print("[?] ");
                }
            }

            System.out.println();
        }
        System.out.println("LINHAS");
    }
}
