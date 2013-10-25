package br.ufpb.ci.so.p20131.projeto2;

import java.util.EnumMap;
import java.util.Map;

public class AtravessadorImpl implements Atravessador {

    /**
     * A cana que é a especialidade deste atravessador.
     */
    private Cana especialidade;
    /**
     * O estoque de cana deste atravessador em cache. Pra cada cana, um valor
     * inteiro associado que representa a quantidade de fardos em estoque.
     */
    private Map<Cana, Integer> estoqueCache;
    /**
     * O estoque real. Pra cada cana, um valor inteiro associado que representa
     * a quantidade de fardos em estoque.
     */
    private Map<Cana, Integer> estoqueReal;

    /**
     * Cria um objeto que implementa a interface Atravessador.
     *
     * @param g a especialidade deste Atravessador.
     */
    public AtravessadorImpl(Cana g) {
        especialidade = g;
        estoqueCache = new EnumMap<Cana, Integer>(Cana.class);
        estoqueReal = new EnumMap<Cana, Integer>(Cana.class);
        for (Cana c : Cana.values()) {
            estoqueCache.put(c, 0);
            estoqueReal.put(c, 0);
        }
    }


    public Pedido getEstoqueDisponivel() {
        Pedido p = new Pedido();
        for (Cana c : estoqueReal.keySet()) {
            p.set(c, estoqueReal.get(c));
        }
        return p;
    }

    /**
     * Verifica a quantidade de fardos da cana {@code c} em estoque cache.
     *
     * @param c o tipo da cana.
     * @return a quantidade da cana {@code c} em estoque.
     */
    private synchronized int verifica(Cana c) {
        return estoqueCache.get(c);
    }

    /**
     * Adiciona {@code qtde} fardos da cana {@code c} no estoque cache e acorda
     * todas as threads que estão esperando por entregas neste atravessador.
     *
     * @param c o tipo de Cana.
     * @param qtde a quantidade.
     * @param real {@code true} para atualizar também o estoque real.
     */
    private synchronized void adicionaCache(Cana c, int qtde) {
        estoqueCache.put(c, estoqueCache.get(c) + qtde);
        notifyAll();
    }

    /**
     * Entrega {@code qtde} de cana {@code c} em uma troca ou solicitação se, e
     * somente se, tiver {@code qtde} ou mais fardos em estoque cache.
     *
     * @param c o tipo de Cana.
     * @param qtde a quantidade.
     * @param real {@code true} para também atualizar o estoque real.
     * @return {@code true} caso consiga efetuar a entrega e {@code false} do
     * contrário.
     */
    private synchronized boolean removeCache(Cana c, int qtde) {
        if (verifica(c) < qtde) {
            return false;
        }
        estoqueCache.put(c, estoqueCache.get(c) - qtde);
        return true;
    }

    /**
     * Adiciona ao estoque real da cana desta especialidade a quantidade
     * {@code q}.
     *
     * @param q a quantidade a ser adicionada.
     */
    private synchronized void fornece(int q) {
        estoqueReal.put(especialidade, estoqueReal.get(especialidade) + q);
    }

    /**
     * Completa efetivamente uma entrega.
     *
     * @param p o pedido a ser entregue.
     */
    private synchronized void completaEntrega(Pedido p) {
        for (Cana c : Cana.values()) {
            estoqueReal.put(c, estoqueReal.get(c) - p.get(c));
        }
    }

    
    public void get(Pedido pedido) throws InterruptedException {
        /**
         * Pra cada tipo de cana, verifica se tem a quantidade pedida.
         */
        for (Cana c : Cana.values()) {
            /**
             * Enquanto não fizer a entrega da cana {@code c}, faça.
             */
            while (true) {
                System.out.println("-- VERIFICA DIF -- " + especialidade);
                int diferenca = pedido.get(c) - verifica(c);
                /**
                 * Se a quantidade pedida é maior que a quantidade disponível.
                 */
                if (diferenca > 0) {
                    /**
                     * Se a cana {@code c} é a especialidade deste atravessador,
                     * espera o fornecedor entregar. Se não, troca com o
                     * atravessador especialista desta cana.
                     */
                    if (c.equals(especialidade)) {
                        System.out.println("-- ESPERA ESP -- " + diferenca + " DE " + especialidade);
                        synchronized (this) {
                            wait();
                        }
                    } else {
                        //A troca obrigatoriamente ocorrerá.
                        System.out.println("-- TROCA -- " + diferenca + " DE " + c + " POR " + especialidade);
                        troca(c, diferenca);
                        Projeto2.especialista(c).troca(especialidade, diferenca);
                    }
                } else {
                    System.out.println("-- TENTA ENTREGAR -- " + pedido.get(c) + " DE " + c + " POR " + especialidade);
                    /**
                     * Se tem, entrega e quebra o while, passando pra próxima
                     * cana ou encerrando o for, se não, retorna do início do
                     * while. Não atualiza o estoque real.
                     */
                    if (removeCache(c, pedido.get(c))) {
                        System.out.println("--  ENTREGA -- " + pedido.get(c) + " DE " + c + " POR " + especialidade);
                        break;
                    }
                }
            }
        }
        completaEntrega(pedido);
    }

    
    public void troca(Cana variedade, int quant) throws InterruptedException {
        //Enquanto não tiver a quantidade da especialidade pra trocar, espera
        while (!removeCache(especialidade, quant)) {
            synchronized (this) {
                wait();
            }
        }
        adicionaCache(variedade, quant);
    }

    
    public void entrega(int quant) {
        //Atualiza os dois estoques (cache e real)
        fornece(quant);
    }
}