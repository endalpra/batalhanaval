package cliente;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author elder
 */
public class ThreadReceiver implements Runnable{
    
    private DataInputStream input;
    private Boolean ligado;
    private Cliente c;

    public ThreadReceiver(DataInputStream input, Boolean ligado, Cliente c) {
        this.input = input;
        this.ligado = ligado;
        this.c = c;
    }

    @Override
    public void run() {
       
        while(ligado)
        {
            try{
                while(ligado)
                {
                    c.recebeuMensagem( input.readUTF() );
                }
            }catch(IOException e)
            {
                System.out.println(e.getMessage());
                
            }
        }
    }   
}