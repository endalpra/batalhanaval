package servidor;

import battleship.FilaJogadores;
import battleship.Navio;
import battleship.Tabuleiro;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    private ServerSocket serverSocket;
    private ArrayList<TarefaCliente> clientes;
    private FilaJogadores<TarefaCliente> fila;
    private Tabuleiro tabuleiro;
    private static Estados estado;

    public Servidor() {
        clientes = new ArrayList<>();
        //Criação da fila
        fila = new FilaJogadores<>();
        System.out.println("Instaciando FilaJogadores");
        novoTabuleiro();
    }

    public void addCliente(TarefaCliente c) {
        clientes.add(c);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* 1 - Criar o servidor de conexões
         2 -Esperar um pedido de conexão;
         2.1 e criar uma nova conexão;
         3 - Criar streams de entrada e saída;
         4 - Tratar a conversação entre cliente e 
         servidor (tratar protocolo);
           
         5 - voltar para o passo 2;
         */

        Servidor servidor = new Servidor();
        System.out.println("Criando Servidor...");

////        //Criação de tabuleiro
//        servidor.novoTabuleiro();       
        try {
            //1
            servidor.criaServidor(5555);
        } catch (IOException ex) {
            System.out.println("Erro: " + ex.getMessage());
        }

        try {
            while (true) {

                System.out.println("Esperando conexão...");
                //2
                //2.1
                Socket socket = servidor.esperaConexao();

                System.out.println("Conexão aceita.");
                //3
                //4
                //servidor.trataConexao(socket);
                //cria uma trhead para a tarefaCliente
                //inicia a thread

                if (servidor.getFila().tamanho() == 0) {
                    servidor.setEstado(Estados.VEZJOGAR);
                }
                TarefaCliente tarefaCliente = new TarefaCliente(socket, servidor, estado);
                Thread t = new Thread(tarefaCliente);
                t.start();
                System.out.println("Thread criada" + t.getName());
                servidor.addCliente(tarefaCliente);
                System.out.println("Fila: " + servidor.getFila().tamanho());

                //fim
            }
        } catch (IOException ex) {
            System.out.println("Erro: " + ex.getMessage());

        } finally {
            servidor.fechaConexao();
        }

    }

    public void criaServidor(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);

    }

    public Socket esperaConexao() throws IOException {
        Socket socket = serverSocket.accept();
        return socket;
    }

    public void fechaConexao() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<TarefaCliente> fila() {
        return fila.getArray();
        //return clientes;
    }

    public TarefaCliente pegaProximoJogador() throws IOException {
        TarefaCliente tf = fila.proximo();
        tf.estado = Estados.VEZJOGAR;
        tf.output.writeUTF("\n" + tabuleiro.desenhaTabuleiro() +"\n$ Seus pontos: "+tf.pontos+ "\n$ VezJogar");
        return tf;
    }

    public void novoTabuleiro() {
        tabuleiro = new Tabuleiro();
        System.out.println("Criando novo tabuleiro");
        System.out.println(tabuleiro.MostraPosicaoNavios());
    }

    public String rankink() {
        return new Ranking().ordena(clientes);
    }

    public FilaJogadores<TarefaCliente> getFila() {
        return fila;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setFila(FilaJogadores<TarefaCliente> fila) {
        this.fila = fila;
    }

    public void setTabuleiro(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

}
