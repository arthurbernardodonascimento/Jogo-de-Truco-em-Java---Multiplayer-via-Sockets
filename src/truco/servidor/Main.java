package truco.servidor;

public class Main {
    public static void main(String[] args) {
        try {
            Jogo jogo = new Jogo();
            jogo.iniciarJogo();
            jogo.iniciarPartida();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
