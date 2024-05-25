package br.com.tdso.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerChannelSelector {

    public static void main(String[] args) {
        try(ServerSocketChannel serverChannel = ServerSocketChannel.open()){

            serverChannel.bind(new InetSocketAddress(5000));
            serverChannel.configureBlocking(false);

            // obtenho um selector
            Selector selector = Selector.open();
            // registro que o canal serverChannel que ser notificado sobre evento ACCEPT
            // isso cria uma chave de selecao - token de registro -
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true){

                // recupera um conjunto de chaves cujos canais estão prontos para operação de E/S

                selector.select(); // metodo de bloqueio - pode fornecer um intervalo de tempo limite

                // se algum evento ocorrer nos canais e nós o tivermos registrado ele será retornado quando
                // invocarmos o método selected keys do selector
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()){
                        SocketChannel clientChannel = serverChannel.accept();
                        System.out.println("Client connected " + clientChannel.getRemoteAddress());
                        // garanto que cada canal seja non blocking
                        clientChannel.configureBlocking(false);
                        // registro o selector e a chave do evento que desejo ouvir
                        clientChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()){  // se o client fez uma solicitacao
                        echoData(key);
                    }
                }
            }

        } catch(IOException io){
            System.out.println(io.getMessage());
        }

    }

    private static void echoData(SelectionKey key) throws IOException{
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead > 0){
            // se houver dados significa que o buffer foi escrito/gravadp,vamos virá-lo para lê-lo
            buffer.flip();
            // aloca area para ler os dados do buffer
            byte[] data = new byte[buffer.remaining()];
            // le os dados do buffer
            buffer.get(data);
            String message = "Echo: " + new String(data);
            // grava os dados no canal do cliente fazendo wrap do ByteBuffer
            clientChannel.write(ByteBuffer.wrap(message.getBytes()));
        } else if (bytesRead == -1){
            System.out.println("Client disconnected : " + clientChannel.getRemoteAddress());
            // se o client desconectou limpo a chave associdada a ele
            key.cancel();
            // fecha a conexao do canal do cliente
            clientChannel.close();
        }
    }
}
