package truco.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws Exception {
        // Altere para o IP do Host (10.130.44.139) no PC do amigo
        String ipServidor = "192.168.0.109";
        Socket socket = new Socket(ipServidor, 12345);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        Scanner sc = new Scanner(System.in);

        System.out.println("Conectado ao servidor de Truco!");

        while (true) {
            try {
                String msg = in.readUTF();
                System.out.println(msg);

                // Libera o teclado se a mensagem for uma pergunta ou pedir escolha
                String msgLower = msg.toLowerCase();
                if (msgLower.contains("?") || msgLower.contains("escolha") || msgLower.contains("digite")) {
                    System.out.print("> ");
                    String input = sc.nextLine();
                    out.writeUTF(input);
                }

            } catch (IOException e) {
                System.out.println("\nConexão encerrada.");
                break;
            }
        }
        sc.close();
    }
}
