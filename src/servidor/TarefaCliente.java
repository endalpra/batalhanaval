package servidor;

import battleship.FilaJogadores;
import battleship.Tabuleiro;
import battleship.TiroEnum;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elder
 */
public class TarefaCliente implements Runnable {

    String nome;
    int pontos;
    Socket socket;
    Servidor servidor;
    FilaJogadores fila;
    Tabuleiro tabuleiro;
    Estados estado;
    String resposta = "";
    DataOutputStream output;

    public TarefaCliente(Socket socket, Servidor servidor, Estados estado) {
        this.socket = socket;
        this.servidor = servidor;
        this.estado = estado;
        fila = servidor.getFila();
        tabuleiro = servidor.getTabuleiro();
        this.pontos = 0;
    }

    @Override
    public void run() {
        try {
            trataConexao();
        } catch (Exception ex) {
            Logger.getLogger(TarefaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void trataConexao() throws Exception {
        try {

            /* criando streams de entrada e saída para o socket*/
            System.out.println("Criando streams de entrada/saída...");
            DataInputStream input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            /*DEFINICAO DO ESTADO*/
            estado = Estados.CONECTADO;


            /*
             tratar o protocolo de comunicação entre o servidor
             e um cliente 
             */
            //recebendo msg do cliente
            System.out.println("Tratando conexão...");
            String msgCliente = "";
            Boolean ligado = true;
            while (ligado) {
                //Avisa cliente a sua vez de jogar
//                if (estado == Estados.VEZJOGAR) {
//                    output.writeUTF("\nVezJogar");
//                }
               
                 
                msgCliente = input.readUTF();
                System.out.println("Mensagem recebida do cliente " + socket.getInetAddress() + ": " + msgCliente);

                String[] protocolo = msgCliente.split("#");

                switch (estado) {
                    case CONECTADO:
                        switch (protocolo[0]) {
                            case "LOGIN":
                                try {
                                    if (protocolo[2].equals("IFSUL")) {
                                        resposta = "LOGINREPLY#OK";
                                        System.out.println("Usuario " + protocolo[1] + " logou-se.");
                                        estado = Estados.AUTENTICADO;
                                        //Setar nome ao jogador
                                        this.nome = protocolo[1];
                                        //Põe jogador no final da fila
                                        fila.enfilera(this);
                                        //Chama o primeiro jogador a se logar para jogar
                                        if (fila.tamanho() == 1) {
                                            servidor.pegaProximoJogador();
                                            //resposta += "\n" + tabuleiro.desenhaTabuleiro() + "\nVezJogar";
                                            //resposta += "\n" + tabuleiro.desenhaTabuleiro();
                                        }
                                    } else {
                                        resposta = "LOGINREPLY#FAIL";
                                    }

                                } catch (Exception e) {
                                    resposta = "ERRO#PARAMETROS INVALIDOS: " + e.getMessage();
                                }
                                break;
                            case "EXIT":
                                //tratar o exit
                                ligado = false;
                                resposta = "EXITREPLY";
                                estado = Estados.DESCONECTADO;
                                break;
                            default:
                                resposta = "ERRO#MENSAGEM INVALIDA OU NAO AUTORIZADA";
                                output.writeUTF("TESTESTESTE");
                        }
                        break;
                    case AUTENTICADO:
                        switch (protocolo[0])//sempre 1a posicao terá a operacao
                        {
                            case "RANKING":
                                for (TarefaCliente r : servidor.fila()) {
                                    resposta += "Jogador: " + r.nome + "->Pontos: " + r.pontos + "\n";
                                }
                                //resposta = String.valueOf(this.pontos);
                                break;
                            case "LOGOUT":
                                estado = Estados.CONECTADO;
                                resposta = "LOGOUTREPLY";
                                fila.removeElemento(this);
                                break;
                            default:
                                resposta = "ERRO#OPERACAO INVÁLIDA OU NÃO AUTORIZADA";
                                break;
                        }
                        break;
                    case VEZJOGAR:
                        TiroEnum enumerador = tabuleiro.atirar(Integer.parseInt(protocolo[0]), Integer.parseInt(protocolo[1]));
                        if (TiroEnum.AGUA == enumerador) {
                            this.pontos += -1;
                            resposta = "~";
                            //Retira da fila o jogador que jogou
                            fila.desenfilera();
                            //Coloca jogador no final da fila
                            fila.enfilera(this);
                            //Chama próximo jogador
                            estado = Estados.AUTENTICADO;
                            servidor.pegaProximoJogador();
                            //resposta += "\n" + tabuleiro.desenhaTabuleiro();

                        } else if (TiroEnum.DESCOBERTA == enumerador) {
                            this.pontos += 2;
                            resposta = "Descoberta";
                            resposta += "\n" + tabuleiro.desenhaTabuleiro() + "\nVezJogar";
                        } else if (TiroEnum.FOGO == enumerador) {
                            this.pontos += 1;
                            resposta = "X";
                            resposta += "\n" + tabuleiro.desenhaTabuleiro();
                        } else if (TiroEnum.AFUNDAR == enumerador) {
                            this.pontos += 2;
                            resposta = "-=";

                            //Se foram afundados todos os navios cria novo tabuleiro
                            if (tabuleiro.fimDeJogo()) {
                                System.out.println("FIM DE JOGO");
                                servidor.novoTabuleiro();
                                resposta += "Fim de jogo";
                            }
                            resposta += "\n" + tabuleiro.desenhaTabuleiro() + "\nVezJogar";
                        }
                        break;
                }

                //envia a resposta processada no switch
                output.writeUTF(resposta);
                System.out.println("Resposta: " + resposta);

                //Clientes que estão logados == estão na fila
                ArrayList<TarefaCliente> arrayClientes = servidor.fila();
                for (int i = 0; i < fila.tamanho(); i++) {
                    System.out.println("Jogador: " + arrayClientes.get(i).nome + "-> Pontuação: " + arrayClientes.get(i).pontos + " Estado: " + arrayClientes.get(i).estado);
                }
                System.out.print("Fila: " + fila.tamanho());
            }

        } catch (Exception e) {
            throw e;
        } finally {
            //ao fim de tudo, fecha a conexão
            socket.close();

        }
    }
}
