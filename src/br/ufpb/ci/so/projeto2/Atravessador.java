package br.ufpb.ci.so.projeto2;
/**
 * Interface a ser implementada pela classe by AtravessadorImpl
 *
 */
public interface Atravessador {

    /** Informa a quantidade total de cana de açúcar em posse deste atravessador
     * @return uma indicação das quantidades de cada variedade de cana de açúcar em posse deste atravessador
     */
    Pedido getEstoqueDisponivel();

    /** Um pedido de um Alambique
     * O objeto que invoca este método é bloqueado até que a requisição possa ser atendida por completo
     * @param pedido número de fardos necessários de cada variedade de cana de açúcar
     * @throws InterruptedException se o thread atual for interrompido enquanto
     *            espera que o pedido possa ser atendido por completo.
     */
    void get(Pedido pedido) throws InterruptedException;

    /** Responde a um pedido de troca de um outro atravessador.
     * Um outro atravessador invoca este método para requisitar uma troca  
     * de uma quantidade de uma determinada variedade de cana de açúcar pela variedade de cana 
     * de açúcar que é a especialidade deste atravessador. 
     * Bloqueia o objeto que invoca o método até que este atravessador possa completar a troca.
     * @param variedade a variedade de cana de açúcar que o outro atravessador deseja enviar para este atravessador.
     * @param quant número de fardos a serem trocados
     * @throws InterruptedException se o thread atual for interrompido enquanto aguarda que a remessa
     * seja finalizada
     */
    void troca(Cana variedade, int quant) throws InterruptedException;

    /** Recebe uma entrega de um fornecedor.
     * O fornecedor invoca este método para entregar uma quantidade de fardos da variedade de 
     * cana de açúcar que é especialidade deste atravessador.
     * 
     * @param quant a quantidade de fardos de cana de açúcar sendo entregues
     * 
     */
    void entrega(int quant);
} 
