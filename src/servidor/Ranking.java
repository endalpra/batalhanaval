package servidor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Érico
 */
public class Ranking implements Comparable<Ranking> {
    
    private Integer pontos;
    private String nome;

    public Ranking(){
        
    }
    
    public Ranking(Integer pontos, String nome){
        this.pontos = pontos;
        this.nome = nome;
    }
    
    
    public String ordena(ArrayList<TarefaCliente> a) {
        Ranking[] ranking = new Ranking[a.size()];
        int x = 0;
        for (TarefaCliente t : a) {
            ranking[x] = new Ranking(t.pontos,t.nome);            
            x++;
        }
        Arrays.sort(ranking, Collections.reverseOrder());
        String s = "";
        int i = 0;
        for (Ranking r : ranking) {
            s += "\n"+ (i + 1) + "° lugar -> Nome: " + r.nome + " ->  Pontos: " + r.pontos;
            i++;
            //System.out.println("\nNome: "+r.nome+" - Pontos"+r.pontos);
            System.out.println("S: "+s);
        }
        return s;
    }

    @Override
    public int compareTo(Ranking o) {
        return this.pontos.compareTo(o.pontos);
    }

}
