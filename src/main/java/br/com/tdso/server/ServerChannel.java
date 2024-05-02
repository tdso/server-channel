package br.com.tdso.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ServerChannel {

    public static void main(String[] args) {

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(5000));
            System.out.println("Server listening port " + serverSocketChannel.socket().getLocalPort() + " !!");

            while (true) {
                SocketChannel clientChannel = serverSocketChannel.accept();
                System.out.printf("Client %s connected ...", clientChannel.socket().getRemoteSocketAddress());
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                SocketChannel channel = clientChannel;
                int readBytes = channel.read(buffer);

                if (readBytes > 0) {
                    buffer.flip();
                    channel.write(ByteBuffer.wrap("Echo Server >  ".getBytes(StandardCharsets.UTF_8)));
                    channel.write(ByteBuffer.wrap("Transmiting : ".getBytes(StandardCharsets.UTF_8)));
                    System.out.println("waiting ...");
                    Thread.sleep(5000);
                    while (buffer.hasRemaining()) {
                        channel.write(buffer);
                    }
                    System.out.println("Capacity = " + buffer.capacity());
                    System.out.println("Limit = " + buffer.limit());
                    System.out.println("Position = " + buffer.position());
                    buffer.clear();
                } else if (readBytes == -1) {
                    System.out.printf("Connection client %s lost", clientChannel.getRemoteAddress());
                    clientChannel.close();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Erro > " + e.getMessage());
            System.out.println("Local > " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


}
