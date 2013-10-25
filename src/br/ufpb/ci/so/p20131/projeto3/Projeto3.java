package br.ufpb.ci.so.p20131.projeto3;


import java.util.*;
import static java.lang.System.out;

/** A classe principal do projeto 3
 * 
 */
public class Projeto3 {
   
    /** Número de Alambiques */
    private static int alambiqueN;

    /** O depósito único */
    private static Deposito deposito;

    /** Alambiques */
    private static Alambique[] alambiques;

    /**Threads dos Alambiques */
    private static Thread[] alambiqueThreads;

    /** O fornecedor único */
    private static Fornecedor fornecedor;

    /** Tempo de inicio */
    static private long startTime = System.currentTimeMillis();

    /** Controle de debug */
    private static boolean verbose = false;

    /** Liga ou desliga o debug
     * @param true para ligar, false para desligar
     */
    public static void setVerbose(boolean on) {
        verbose = on;
    } 

    /** Retorna o depósito
     *
     * @return deposito
     */
    public static Deposito getDeposito() {
        return deposito;
    }

    /** Imprime informação de depuração
     * @param mensagem para impressão
     */
    public static void debug(Object mensagem) {
        if (verbose) {
            out.printf("%6.3f %s: %s%n",
                tempo() / 1E3, Thread.currentThread().getName(), mensagem);
        }
    } // debug(Object)

    /** Imprime informação de depuração
     * @param formato
     * @param mensagem para impressão
     */
    public static void debug(String format, Object... args) {
        if (verbose) {
            String mensagem = String.format(format, args);
            out.printf("%6.3f %s: %s%n",
                tempo() / 1E3, Thread.currentThread().getName(), mensagem);
        }
    } 

    /** Calculo do tempo de execução
     * @return tempo desde o início da execução
     */
    static public int tempo() {
        return (int)(System.currentTimeMillis() - startTime);
    } 

    /** Número de alambiques ativos
     * @return número de alambiques
     */
    public static int getAlambiqueN() {
        return alambiqueN;
    } 

    /** gerador de números aleatórios */
    private static Random rand;
        
    /** função para geração de números aleatórios
     * @param maior valor
     * @return um número não negativo
     */
    public static int randInt(int max) {
        if (0 >= max) {
            throw new IllegalArgumentException("randInt");
        }
        return (rand.nextInt(max));
    } 

    /** função para geração de números aleatórios
     * @param maior valor
     * @param menor valor
     * @return um número não negativo entre maior e menor inclusive.
     */
    public static int randInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("randInt");
        }
        return min + rand.nextInt(max - min + 1);
    } 

    /** gera um número aleatório distribuído exponencialmente
     * @param media - a média da distribuição
     * @return o próximo valor da série
     */
    public static int expo(int media) {
        return (int) Math.round(-Math.log(rand.nextDouble()) * media);
    } 

    /** Imprime a forma de uso e encerra */
    private static void uso() {
        System.err.println(
            "uso: Projeto3 [-v][-r] algoritmo numeroDeAlambiques iterações");
        System.exit(1);
    } 

    /** Método principal
     * 
     */
    public static void main(String[] args) {
        // faz o parsing dos argumentos da linha de comnaod
        GetOpt options = new GetOpt("Projeto3", args, "vr");
        int opt;
        while ((opt = options.nextOpt()) != -1) {
            switch (opt) {
            default:
                uso();
                break;
            case 'v':
                verbose = true;
                break;
            case 'r':
                rand = new Random(0);
                break;
            }
        }
        if (rand == null) {
            rand = new Random();
        }
        if (options.optind != args.length - 3) {
            uso();
        }
        int algoritmo = Integer.parseInt(args[options.optind + 0]);
        alambiqueN = Integer.parseInt(args[options.optind + 1]);
        int iteracoes = Integer.parseInt(args[options.optind + 2]);

        // Cria o depósito
        deposito = new Deposito(algoritmo);
        Thread dthread = new Thread(deposito, "Deposito");

        // Cria o fornecedor
        fornecedor = new Fornecedor(iteracoes);
        Thread fThread = new Thread(fornecedor, "Fornecedor");

        // Cria os alambiques
        alambiques = new Alambique[alambiqueN];
        alambiqueThreads = new Thread[alambiqueN];
        for (int i = 0; i < alambiqueN; i++) {
            alambiques[i] = new Alambique(i);
            alambiqueThreads[i] = new Thread(alambiques[i], "Alambique" + i);
        }

        // Inicia os threads
       
        dthread.setPriority(Thread.NORM_PRIORITY - 1);
        dthread.start();
        fThread.setPriority(Thread.NORM_PRIORITY - 1);
        fThread.start();
        for (Thread t : alambiqueThreads) {
            t.setPriority(Thread.NORM_PRIORITY - 1);
            t.start();
        }

        //Espera até que todos os threads sejam encerrados
        try {
            // O thread do fornecedor se encerra quando ele completa todas as iterações
            fThread.join();

            // Aguarda 3 segundos para dar chance a todos de terminarem o que estão fazendo,
            // em seguida todos são interrompidos
            Thread.sleep(3000);

            // Mata o thread do depósito
            dthread.interrupt();
            dthread.join();

            // Mata os alambiques
            for (Thread t : alambiqueThreads) {
                t.interrupt();
                t.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // imprime o estado final
        out.printf("**** Programa terminando%n");

        Pedido quant;
        Pedido balanco = new Pedido();
        int produzido = 0;
        int noDeposito = 0;
        int consumido = 0;

        quant = fornecedor.getProducao();
        out.printf("Produzido %s%n", quant);

        balanco.troca(quant);
        produzido += quant.total();

        quant = deposito.getDisponivel();
        for (Cana c1 : Cana.values()) {
            int n = quant.get(c1);
            balanco.troca(c1, -n);
            noDeposito += n;
        }

        Pedido totalConsumido = new Pedido();
        int totalRequisitado = 0;
        int totalEspera = 0;
        for (int i = 0; i < alambiqueN; i++) {
            quant = alambiques[i].getConsumo();
            int requisitado = alambiques[i].requisicoesConcluidas();
            int espera = alambiques[i].tempoDeEspera();
            totalConsumido.troca(quant);
            for (Cana g : Cana.values()) {
                int n = quant.get(g);
                balanco.troca(g, -n);
                consumido += n;
            }
            out.printf("Alabique %d%n", i);
            out.printf("   Cana consumida:         %s%n", quant);
            out.printf("   Requisições atendidas:  %d%n", requisitado);
            out.printf("   Tempo total de espera:  %d ms%n", espera);
            if (requisitado > 0) {
                out.printf("   Tempo de espera médio:      %.2f ms%n",
                                espera / (double)requisitado);
            }
            totalRequisitado += requisitado;
            totalEspera += espera;
        }
        out.printf("Balanço (deficit) é %s%n", balanco);
        out.printf(
            "Total: produzido = %d, consumido = %d,"
                    + " restando no depósito = %d, líquido = %d%n",
            produzido, consumido, noDeposito,
            (produzido - consumido - noDeposito));
        out.printf(
            "Requisições concluídas: %d, tempo de espera médio %.2fms%n",
            totalRequisitado, totalEspera / (double) totalRequisitado);
    } 
} 
