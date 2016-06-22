package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elder
 */
public class Cliente {

    /**
     * @param args the command line arguments
     */
    Socket socket;
    Boolean ligado;

    public Cliente() {
        ligado = true;

    }

  

    public Socket criarConexao(String host, int porta) throws IOException {
        System.out.println("Esperando/criando conexão...");
        socket = new Socket(host, porta);

        return socket;
    }

    public void trataConexao(Socket socket) throws IOException {
        System.out.println("Criando streams...");
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        System.out.println("Tratando conexão...");
        String msg = "";
        ThreadReceiver run = new ThreadReceiver(input, ligado, this);
        new Thread(run).start();
        
        while (ligado) {

            System.out.print("$");
            Scanner scanner = new Scanner(System.in);
            msg = scanner.nextLine();
            msg = msg.toUpperCase();
            //enviei mensageml
            output.writeUTF(msg);
            //String msgServidor = input.readUTF();
            
        }

    }

    public void recebeuMensagem(String msg) {
        System.out.print(""+msg+ "\n$ ");
        if (msg.equals("EXITREPLY")) {
            ligado = false;
        }
    }

    public void fechaSocket() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

      public static void main(String[] args) {
        /*
         1 - Solicitar/criar conexão;
         2 - Criar input e output;
         2 - Tratar a conversação com o servidor;
    
         */
        Cliente cliente;
        cliente = new Cliente();

        try {
            //1
            Socket socket = cliente.criarConexao("localhost", 5555);
            //2
            cliente.trataConexao(socket);

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            cliente.fechaSocket();
        }
    }   
}
