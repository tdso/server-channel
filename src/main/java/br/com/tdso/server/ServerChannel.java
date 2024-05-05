package br.com.tdso.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServerChannel {

    public static void main(String[] args) {

        List<SocketChannel> channels = new ArrayList<>();

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(5000));
            serverSocketChannel.configureBlocking(false); // non block

            System.out.println("Server listening port " + serverSocketChannel.socket().getLocalPort() + " !!");

            while (true) {
                // now no block
                SocketChannel clientChannel = serverSocketChannel.accept();

                if (clientChannel != null) {
                    System.out.printf("Client %s connected ...", clientChannel.socket().getRemoteSocketAddress());
                    clientChannel.configureBlocking(false);
                    channels.add(clientChannel);
                }
                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    for (int i = 0; i < channels.size(); i++) {

                        SocketChannel channel = channels.get(i);

                        int readBytes = channel.read(buffer);

                        if (readBytes > 0) {
                            buffer.flip();
                            channel.write(ByteBuffer.wrap("Echo Server >  ".getBytes(StandardCharsets.UTF_8)));
                            channel.write(ByteBuffer.wrap("Transmiting : ".getBytes(StandardCharsets.UTF_8)));
                            while (buffer.hasRemaining()) {
                                channel.write(buffer);
                            }
                            buffer.clear();
                        } else if (readBytes == -1) {
                            System.out.printf("Connection client %s lost", channel.getRemoteAddress());
                            channels.remove(i);
                            channel.close();
                        }
                    }

            }
        } catch (IOException e)  {
            System.out.println("Erro > " + e.getMessage());
            System.out.println("Local > " + e.getLocalizedMessage());
            System.out.println(" ");
            e.printStackTrace();
        }
    }


}
