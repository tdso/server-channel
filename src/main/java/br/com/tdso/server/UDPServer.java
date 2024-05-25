package br.com.tdso.server;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class UDPServer {

    private static final int PORT = 5000;
    private static  final int PACKET_SIZE = 1024;

    public static void main(String[] args) {

        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {

            byte [] buffer = new byte[PACKET_SIZE];

            System.out.println("Waiting connection client ....");

            DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(clientPacket); // bloqueia

            String audioFileName = new String(buffer, 0, clientPacket.getLength());
            System.out.println("Client requested to listen to: " + audioFileName);

            try {
                File audioFile = new File("src/main/resources/" + audioFileName);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                System.out.println(audioInputStream.getFormat());
            } catch (UnsupportedAudioFileException e){
                System.out.println("Format audio no supported !!");
            }
            // enviar o conteudo do arquivo para o client
            sendDataToClient(audioFileName, serverSocket, clientPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendDataToClient(String file,
                                         DatagramSocket serverSocket,
                                         DatagramPacket clientPacket){

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);

        Path path = Paths.get("src/main/resources/" + file);

        try(FileChannel fileChannel = FileChannel.open(path,
                StandardOpenOption.READ)){

            InetAddress clientIP = clientPacket.getAddress();
            int clientPort = clientPacket.getPort();

            while(true) {

                buffer.clear();

                if (fileChannel.read(buffer) == -1) {
                    break;
                }

                buffer.flip();

                while (buffer.hasRemaining()) {
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    DatagramPacket packet = new DatagramPacket(
                            data, data.length, clientIP, clientPort);
                    serverSocket.send(packet);
                }
                // add um little interval
                try {
                    TimeUnit.MILLISECONDS.sleep(22);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());

        }
    }
}
