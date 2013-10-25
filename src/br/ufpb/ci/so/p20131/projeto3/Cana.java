package br.ufpb.ci.so.p20131.projeto3;
/** Variedades de cana de açúcar
 *
 */
public enum Cana {
    CAIANA, CANINHA, PRATA, RAINHA;
    static Cana randChoice() {
        Cana[] v = values();
        int n = Projeto3.randInt(0, v.length - 1);
        return v[n];
    }
};
