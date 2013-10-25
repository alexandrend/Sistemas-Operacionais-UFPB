package br.ufpb.ci.so.p20131.projeto3;
/**
 * Uma requisição de um Alambique para o Deposito
 * 
 * Uma Requisição registra as quantidades de cada cana requisitadas e atendidas até o momento, 
 * e serve como um ponto de espera para o atendimento de um pedido.
 */
public class Requisicao {
   

    /** Raiz da sequência de números */
    private static int proximoSeq= 0;

    /** A quantidade requisitada originalmente. */
    private Pedido requisitado;

    /** Id do consumidor */
    private int id;

    /** Número de sequência deste pedido  */
    public final int seq;

    /** Total alocado até o momento */
    private Pedido alocado;

    /** Flag que indica se a requisição foi completamente atendida */
    private boolean concluida = false;

    /** Cria uma nova requisição
     * Nota: Este método só deve ser invocado dentre de um método sincronizado
     * @param id do consumidor
     * @param quantidade requisitada de cana grão
     */
    public Requisicao(int id, Pedido requisicao) {
        this.id = id;
        this.requisitado = requisicao.copia();
        this.seq = ++proximoSeq;
        alocado = new Pedido();
    } 

    /** Checa se a quantidade não atendida nesta requisição é menor ou igual ao disponível no
     * fornecedor
     * @param a quantidade disponível no fornecedor
     * @return true se a requisição pode ser completamente atendida
     */
    public synchronized boolean menorOuIgual(Pedido estoque) {
        for (Cana c : Cana.values()) {
            if (requisitado.get(c) - alocado.get(c) > estoque.get(c)) {
                return false;
            }
        }
        return true;
    } 

    /** Aloca alguma quantidade de cana para esta requsição
     * A quantidade de cana cana entregue é o mínimo entre a quantidade em estoque
     * e a quantidade necessária para completar o pedido. No entanto, se o limite for positivo
     * não mais do que o limite será entregue
     * @param estoque de cana
     * @param se o limite for maior que zero, não mais do que esta quantidade de cada cana
     * será entegue
     * @return a quantidade total de cana entregue
     */
    public synchronized int entrega(Pedido estoque, int limite) {
        int resultado = 0;
        for (Cana c : Cana.values()) {
            int quant = requisitado.get(c) - alocado.get(c);
            if (quant > estoque.get(c)) {
                quant = estoque.get(c);
            }
            if (limite > 0 && quant > limite) {
                quant = limite;
            }
            alocado.troca(c, quant);
            estoque.troca(c, -quant);
            resultado += quant;
        }
        return resultado;
    } 

    /** Checa se esta requisição foi completamente atendida
     * @return true se a requisição foi atendida.
     */
    public synchronized boolean satisfeita() {
        for (Cana c : Cana.values()) {
            if (alocado.get(c) != requisitado.get(c)) {
                return false;
            }
        }
        return true;
    } 

    /** Retorna o total requisitado
     * @return soma de todas as quantidades
     */
    public synchronized int total() {
        int soma = 0;
        for (Cana c : Cana.values()) {
            soma += requisitado.get(c);
        }
        return soma;
    } 

    /** Retorna as quantidades requisitadas restantes
     * @return soma do que foi solicitado e não atendido
     */
    public synchronized int restante() {
        int soma = 0;
        for (Cana c : Cana.values()) {
            soma += requisitado.get(c) - alocado.get(c);
        }
        return soma;
    } 

    /** Sinaliza a conclusão da requisição
     */
    public synchronized void completa() {
        concluida = true;
        notify();
    } 

    /** Aguarda a conclusão desta requisição
     * @throws InterruptedException se o thread for interrompido enquanto aguarda 
     * o atendimento da requisição.
     */
    public synchronized void await() throws InterruptedException {
        while (!concluida) {
            wait();
        }
    } 

    /** Retorna a quantidade já alocada desta requisição
     *
     * @return a quantidade alocada
     */
    public synchronized Pedido getAlocado() {
        return alocado;
    } // getAlloc()

    /** toString
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(seq + ":" + id);
        char sep = '['; 
        
        for (Cana c : Cana.values()) {
            sb.append(String.format(
                "%c%d/%d %s", sep, alocado.get(c), requisitado.get(c), c));
            sep = ',';
        }
        sb.append(']');
        return sb.toString();
    }
} 
