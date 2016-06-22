package testes;

import battleship.FilaJogadores;
import battleship.Navio;
import battleship.Tabuleiro;
import battleship.TiroEnum;
import java.util.ArrayList;

/**
 *
 * @author elder
 */
public class Teste {
    
    public static void main(String[] args) throws Exception {
        Tabuleiro t = new Tabuleiro();
        
        System.out.println(t.desenhaTabuleiro());
        
        if(t.fimDeJogo())
        {
            //fim do jogo
            t= new Tabuleiro();
        }
           
     
       
       
        for( int i=0; i<10; i++ )
        {
            for(int j=0; j<10;j++)
            { 
                t.atirar(i, j);
                if(t.fimDeJogo()){
                    System.out.println("FIM");
                    j=10;
                    i=10;
                }
            }
        }
                
        System.out.println(t.desenhaTabuleiro() );
      /*  for (Navio n : t.getNavios()) {
            System.out.println(n.toString());
            
        }*/
        
        
        
    }
    
}
