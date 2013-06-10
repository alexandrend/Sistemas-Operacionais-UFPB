package br.ufpb.ci.so.projeto2;
import java.util.*;

/** Um pedido
 * 
 */
public class Pedido {
	/** Número de versão */
    private Map<Cana,Integer> quant;

    /** Cria um novo pedido com todos os valores em 0. */
    public Pedido() {
        quant = new EnumMap<Cana,Integer>(Cana.class);
        for (Cana g : Cana.values()) {
            quant.put(g, 0);
        }
    } 

    /** 
     * @return retorna uma versão legível deste pedido.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String sep = "[";
        for (Cana g : Cana.values()) {
            sb.append(String.format("%s%d %s", sep, quant.get(g), g));
            sep = ", ";
        }
        sb.append("]");
        return sb.toString();
    } // toString()

    /** Retorna a quantidade da cana g neste pedido.
     * @param g tipo de cana de açúcar
     * @return quantidade de cana dessa variedade
     */
    public int get(Cana g) {
        return quant.get(g).intValue();
    } 

    /** Seta a quantidade desejada cana de variedade g para n
     * @param g uma variedade de cana
     * @param n a quantidade solicitada dessa variedade
     */
    public void set(Cana g, int n) {
        quant.put(g, n);
    }

    /** Modifica o valor de "g" pelo valor "diff".
     * @param g uma variedade de cana
     * @param diff a diferença na quantidade de g
     */
    public void troca(Cana g, int diff) {
        quant.put(g, quant.get(g) + diff);
    } 

    /** Retorna uma cópia deste pedido.
     * @return uma cópia deste pedido.
     */
    public Pedido copia() {
        Pedido result = new Pedido();
        for (Cana g : Cana.values()) {
            result.quant.put(g, quant.get(g));
        }
        return result;
    } 
        
}
