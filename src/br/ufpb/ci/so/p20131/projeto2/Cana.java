package br.ufpb.ci.so.p20131.projeto2;
/** Variedades de cana de açúcar
 *
 */
public enum Cana {
    CAIANA, CANINHA, PRATA, RAINHA;
    static Cana randChoice() {
        Cana[] v = values();
        int n = Projeto2.randInt(0, v.length - 1);
        return v[n];
    }
};
