package battleship;

import java.util.ArrayList;

/**
 *
 * @author elder
 */
public class FilaJogadores<T> {

    ArrayList<T> array;

    public FilaJogadores() {
        array = new ArrayList<>();
    }
    
    public void enfilera( T elemento )
    {
        array.add(elemento);
    }
    
    public T desenfilera() throws Exception
    {
        if( array.isEmpty() ) throw new Exception( "FILA VAZIA!");
        return array.remove(0);
    }
    
    public T proximo()
    {
        return array.get(0);
    }
    
    public void limpa()
    {
        array = new ArrayList<>();
    }
    
    public Integer tamanho()
    {
        return array.size();
    }
    
    public Boolean removeElemento(T elemento)
    {
        if(array.contains(elemento))
        {
            array.remove(elemento);
            return true;
        }else
            return false;
    }

    
    public ArrayList<T> getArray() {
        return array;
    }

    
    
    @Override
    public String toString() {
        return "FilaJogadores{" + "array=" + array.get(0).toString() + '}';
    }  
    
    
    
    
    
}
