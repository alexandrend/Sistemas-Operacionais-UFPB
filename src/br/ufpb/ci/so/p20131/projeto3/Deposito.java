package br.ufpb.ci.so.p20131.projeto3;
import java.util.*;
import static java.lang.System.out;

/**
 * Um provedor centralizado de cana de açucar
 * 
 * Esta classe substitui os atravessadores do projeto 2.
 * Um deposito aceita pedidos dos Alambiques e os atende o mais rápido possível
 * Se ele recebe múltiplos pedidos ele os atende de diversas formas diferentes,
 * de acordo com o algoritmo especificado no construtor
 */
public class Deposito implements Runnable {
    
	// Parametros que determinam o comportamento do Deposito

    /** Algoritmo a ser utilizado */
    private int algoritmo;

    /** Quantidade de algoritmos implementados */
    private static final int NUM_ALGORITMOS = 4;

    // Estado atual do deposito

    /** Fila de requisicoes*/
    private List<Requisicao> espera = new ArrayList<Requisicao>();

    /** Estoque disponivel. */
    private Pedido disponivel = new Pedido();

    /** Total recebido do fornecedor. */
    private Pedido recebido = new Pedido();

    /** Número de requisicoes atendidas. */
    private int atendidas = 0;

    /** Quantidade total entregue aos consumidores */
    private Pedido entregue = new Pedido();

    /** Cria um deposito
     * @param algoritmo a ser utilizado para escolher entre as requisições
     */
    public Deposito(int algoritmo) {
        if (algoritmo < 1 || algoritmo > NUM_ALGORITMOS) {
            throw new IllegalArgumentException(
                "O algorito deve estar entre  1 e " + NUM_ALGORITMOS);
        }
        this.algoritmo = algoritmo;
    } 

    /** Recebe uma carga do fornecedor
     * @param quantidade sendo entregue
     */
    public synchronized void entrega(Pedido quant) {
        disponivel.troca(quant);
        recebido.troca(quant);
        notify();
    } // deliver(Pedido)

    /** Recebe de volta a quantidade de um pedido que foi interrompido
     * @param quantidade sendo devolvida
     */
    public synchronized void desfaz(Pedido quant) {
        disponivel.troca(quant);
    } 

    /** Aceita um pedido do Alambique e o bloqueia até que o pedido possa ser atendido.
     *
     * @param id do Alambique
     * @param quantidade requisitida
     * @throws InterruptedException se o Alambique for interrompido enquanto aguarda a entrega
     */
    public void get(int id, Pedido quant) throws InterruptedException {
        Requisicao req = enfileira(id, quant);
        try {
            req.await();
            registraAtendimento(quant);
        } catch (InterruptedException e) {
            desfaz(req.getAlocado());
            throw e;
        }
    } 


    /** Cria uma requisição e a coloca na fila
     * @param id do cliente
     * @param quantidade requisita
     * @return o pedido
     */
    private synchronized Requisicao enfileira(int id, Pedido quant) {
        Requisicao req = new Requisicao(id, quant);
        espera.add(req);
        notify();
        return req;
    } // enqueue(Pedido)

    /** Loop principal */
    public synchronized void run() {
        for (;;) {
            switch (algoritmo) {
            case 1: algoritmo1(); break;
            case 2: algoritmo2(); break;
            case 3: algoritmo3(); break;
            case 4: algoritmo4(); break;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                Projeto3.debug("Deposito encerrando");
                break;
            }
        }
        out.printf("Deposito, algoritmo %d%n", algoritmo);
        out.printf("  Compras atendidas:   %d%n", atendidas);
        out.printf("  Cana recebida:       %s%n", recebido);
        out.printf("  Cana entregue:       %s%n", entregue);
        out.printf("  Estoque disponível:  %s%n", disponivel);
    } 

    /** Retorna o estoque disponivel
     * @return quantidade de cada cana disponível no depósito
     */
    public synchronized Pedido getDisponivel() {
        return disponivel.copia();
    } 

    /** Tenta satisfazer e liberar um ou mais consumidores
     * Usa o algoritmo 1
     * Deve ser chamado sempre que o estador mudar e apenas a partir de métodos sincronizados
     */
    private void algoritmo1() {
        while (!espera.isEmpty()) {
            Requisicao req = espera.get(0);
            if (req.entrega(disponivel, 0) == 0) {
                return;
            }
            if (req.satisfeita()) {
                espera.remove(0);
                req.completa();
            }
        }
    } 

    /** Tenta satisfazer e liberar um ou mais consumidores
     * Usa o algoritmo 2
     * Deve ser chamado sempre que o estador mudar e apenas a partir de métodos sincronizados
     */
    private void algoritmo2() {
        int quant;
        do {
            quant = 0;
            for (Iterator<Requisicao> i = espera.iterator(); i.hasNext(); ) {
                Requisicao req = i.next();
                quant += req.entrega(disponivel, 1);
                if (req.satisfeita()) {
                    i.remove();
                    req.completa();
                }
            }
        } while (quant > 0);
    } // algorithm2()

    /** Tenta satisfazer e liberar um ou mais consumidores
     * Usa o algoritmo 3
     * Deve ser chamado sempre que o estador mudar e apenas a partir de métodos sincronizados
     */
    private void algoritmo3() {
        while (!espera.isEmpty()) {
            Requisicao req;
            int min = Integer.MAX_VALUE;
            int mini = -1;
            for (int i = 0; i < espera.size(); i++) {
                req = espera.get(i);
                if (req.total() < min) {
                    min = req.total();
                    mini = i;
                }
            }
            req = espera.get(mini);
            if (req.entrega(disponivel, 0) == 0) {
                return;
            }
            if (req.satisfeita()) {
                espera.remove(mini);
                req.completa();
            }
        }
    } 

    /** Tenta satisfazer e liberar um ou mais consumidores
     * Usa o algoritmo 4
     * Deve ser chamado sempre que o estador mudar e apenas a partir de métodos sincronizados
     */
    private void algoritmo4() {
        while (!espera.isEmpty()) {
            Requisicao req;
            int min = Integer.MAX_VALUE;
            int mini = -1;
            for (int i = 0; i < espera.size(); i++) {
                req = espera.get(i);
                if (req.restante() < min) {
                    min = req.restante();
                    mini = i;
                }
            }
            req = espera.get(mini);
            if (req.entrega(disponivel, 0) == 0) {
                return;
            }
            if (req.satisfeita()) {
                espera.remove(mini);
                req.completa();
            }
        }
    } 

    /** Registra que um pedido foi atendido
     * @param quantidade do pedido
     */
    private synchronized void registraAtendimento(Pedido quant) {
        atendidas++;
        entregue.troca(quant);
    } 
} 
