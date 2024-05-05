package br.com.tdso.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost", 5000)) {

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            String request;
            String response;

            do {
                System.out.println("Enter with command: ");
                request = scanner.nextLine();


                if (!request.equals("exit")){
                    // envia comando para o servidor
                    output.println(request);

                    // receber a resposta do servidor
                    response = input.readLine();

                    // imprime no console a resposta server
                    System.out.println(response);
                }
            } while (!request.equals("exit"));

        } catch (IOException e) {
            System.out.println("Erro client >>> " + e.getMessage());
            System.out.println("Erro local mensage >>> " + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Client disconnected .....");
        }

    }
}
