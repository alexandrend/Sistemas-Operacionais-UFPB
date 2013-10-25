package br.ufpb.ci.so.p20131.projeto3;

import static java.lang.System.out;

/**
 * Um fornecedor de cana de açúcar
 * 
 */
public class Fornecedor implements Runnable {
    
	// Parametros quie definem o comportamento do fornecedor

    /** Número de interações antes de finalizar */
    private int iteracoes;

    /** Tempo de espera médio entre iterações */
    private int esperaMedio;

    /** Quantidade máxima a ser fornecida em cada iteração */
    private Pedido entregaMaxima = new Pedido();

    /** total entregue até o momento*/
    private Pedido entregue = new Pedido();

    // Construtores

    /** Cria um novo fornecedor
     * @param número de iterações antes da finalização
     */
    public Fornecedor(int iteracoes) {
        this.iteracoes = iteracoes;

        // Configura os vários parâmetros da simulação
        // NOTA: Estes números foram escolhidas para que a taxa média de produção
        // case com a taxa média de consumo
        //
        // O Alambique 0 gera em média um pedido a cada 50 ms por uma quantidade média de 
        // 25 únidades de cada cana por pedido, o que gera um consumo médio de 25/50 = 0.50 unidades/ms.\
        // De forma similar, os demais consumidores consomem em uma taxa de 5/50 = 0.10 unidades/ms.
        // Portanto, a taxa média total de consiumo é de 0.50 + (N-1)*0.10, onde N é o número de consumidores
        // 
        //
        // Configuramo o fornecedor para fornecer 4 unidades por iteração
        // portanto se S é o tempo médio de espera e A = entregaMaxima/2 é quantidade
        // fornecida, temos a equação
        //     A / S = 0.50 + (N-1)*0.10
        // ou
        //     S = A / (0.50 + (N-1)*0.10)

        for (Cana c : Cana.values()) {
            entregaMaxima.set(c, 10);
        }

        esperaMedio
            = (int) Math.round(5 / (0.50 + (Projeto3.getAlambiqueN() - 1) * 0.10));

        Projeto3.debug(
            "Fornecendo uma média de 5 unidades e cana a cada " + esperaMedio + " ms");
    } 

    // Métodos

    /** Indica a quantidade de cana entregue
     */
    public synchronized Pedido getProducao() {
        return entregue;
    } 

    /** Loop principal
     */
    public void run() {
        Pedido quant = new Pedido();
        for (int i = 0; i < iteracoes; i++) {
            try {
                int slp = Projeto3.expo(esperaMedio);
                Thread.sleep(slp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Cana g : Cana.values()) {
                quant.set(g, Projeto3.randInt(entregaMaxima.get(g) + 1));
            }
            Projeto3.getDeposito().entrega(quant);
            entregue.troca(quant);
        }
        out.printf("Fornecedor%n");
        out.printf("   Iterações:       %d%n", iteracoes);
        out.printf("   Total fornecido: %s%n", entregue);
    } 
} 