package br.ufpb.ci.so.p20131.projeto2;
/** Um consumidor de cana de açúcar.
 *
 */
public class Alambique implements Runnable {
	/** Total consumido até agora. */
    private Pedido consumido = new Pedido();

    /**
     * @return total consumido até o momento.
     */
    public synchronized Pedido getConsumo() {
        return consumido;
    }

    /** Consome o total indicado de cana de açúcar
     * @param um vetor de resultados, um para cada tipo de cana.
     */
    private synchronized void consome(Pedido pedido) {
        for (Cana g : Cana.values()) {
            consumido.troca(g, pedido.get(g));
        }
    } 

    /** Loop principal
     * Gera pedidos aleatórios para fornecedores aleatórios
     */
    public void run() {
        Pedido pedido = new Pedido();
        for (;;) {
            try {
                Thread.sleep(Projeto2.randInt(500));
            } catch (InterruptedException e) {
                Projeto2.setVerbose(true);
                Projeto2.debug("interrompido enquanto dormia");
                return;
            }

            for (Cana g : Cana.values()) {
                pedido.set(g, Projeto2.randInt(1,10));
            }
            Cana g = Cana.randChoice();
            Projeto2.debug("solicitando %s ao atravessador de cana %s", pedido, g);
            try {
                Projeto2.especialista(g).get(pedido);
            } catch (InterruptedException e) {
                Projeto2.setVerbose(true);
                Projeto2.debug("interrompido durante solicitação%n"
                        + "     %s do fornecedor de cana %s",
                    pedido, g);
                return;
            }
            Projeto2.debug("recebi %s do fornecedor de cana %s", pedido, g);
            consome(pedido);
        }
    } // run()
}
