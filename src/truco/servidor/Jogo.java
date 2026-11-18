package truco.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import truco.modelo.Baralho;
import truco.modelo.Carta;
import truco.modelo.Jogador;

public class Jogo {
    private Baralho b;
    private int ponto;
    private Jogador jogador1, jogador2;
    private ServerSocket server;
    private Socket socket1, socket2;
    private DataInputStream in1, in2;
    private DataOutputStream out1, out2;

    public Jogo() throws Exception {
        this.b = new Baralho();
        this.ponto = 1;
        server = new ServerSocket(12345);
        System.out.println("Servidor aguardando no IP: 10.130.44.139");

        socket1 = server.accept();
        socket2 = server.accept();

        in1 = new DataInputStream(socket1.getInputStream());
        out1 = new DataOutputStream(socket1.getOutputStream());
        in2 = new DataInputStream(socket2.getInputStream());
        out2 = new DataOutputStream(socket2.getOutputStream());
    }

    public void iniciarJogo() throws Exception {
        out1.writeUTF("Digite seu nome:");
        out2.writeUTF("Digite seu nome:");
        jogador1 = new Jogador(in1.readUTF(), 1);
        jogador2 = new Jogador(in2.readUTF(), 2);
        enviarAmbos("Partida iniciada: " + jogador1.getNome() + " vs " + jogador2.getNome());
    }

    public void iniciarPartida() throws Exception {
        boolean jogarNovamente = true;
        Jogador jogadorQueComecaAMao = jogador1; // Variável para controlar quem dá as cartas na mão

        // 1. Loop externo para múltiplas partidas (Revanche)
        while (jogarNovamente) {
            // Zera os pontos para a nova partida
            jogador1.setPontos(0);
            jogador2.setPontos(0);
            enviarAmbos("\n=====================================");
            enviarAmbos("   NOVA PARTIDA DE TRUCO INICIADA!   ");
            enviarAmbos("=====================================");

            // 2. Loop interno da partida até alguém fazer 12 pontos
            while (jogador1.getPontos() < 12 && jogador2.getPontos() < 12) {
                ponto = 1;
                b.iniciaBaralho();
                enviarAmbos("\n=== VIRA: " + b.getCartaVirada() + " ===");

                jogador1.limparMao();
                jogador2.limparMao();
                for (int i = 0; i < 3; i++) {
                    jogador1.darCarta(b.darCarta());
                    jogador2.darCarta(b.darCarta());
                }

                Jogador v1 = null, v2 = null, v3 = null;
                boolean correu = false;

                // Define quem é o primeiro a jogar nesta mão específica
                Jogador jogadorDaVez = jogadorQueComecaAMao;

                for (int r = 1; r <= 3; r++) {
                    Jogador oponente = (jogadorDaVez == jogador1) ? jogador2 : jogador1;
                    DataOutputStream outA = (jogadorDaVez == jogador1) ? out1 : out2;
                    DataInputStream inA = (jogadorDaVez == jogador1) ? in1 : in2;
                    DataOutputStream outB = (jogadorDaVez == jogador1) ? out2 : out1;
                    DataInputStream inB = (jogadorDaVez == jogador1) ? in2 : in1;

                    // Pergunta Truco para quem começa
                    outA.writeUTF("\nSua mão:\n" + jogadorDaVez.getMaoString() + "Deseja TRUCO? (s/n)");
                    outB.writeUTF("Aguardando " + jogadorDaVez.getNome() + "...");

                    if (inA.readUTF().equalsIgnoreCase("s")) {
                        outB.writeUTF("\nSUA MÃO:\n" + oponente.getMaoString() + "\nTRUCO solicitado! Aceita? (s/n)");
                        if (!inB.readUTF().equalsIgnoreCase("s")) {
                            jogadorDaVez.setPontos(jogadorDaVez.getPontos() + ponto);
                            enviarAmbos(oponente.getNome() + " correu!");
                            correu = true; break;
                        }
                        ponto = (ponto == 1) ? 3 : ponto + 3;
                        enviarAmbos("O jogo agora vale " + ponto + " pontos!");
                    }

                    // Jogada do Primeiro
                    outA.writeUTF("Sua vez! Escolha a carta (1-" + jogadorDaVez.getMao().size() + "):");
                    Carta c1 = jogadorDaVez.jogarCarta(lerEscolha(inA));
                    enviarAmbos(jogadorDaVez.getNome() + " jogou: " + c1);

                    // Jogada do Segundo (vendo a mesa)
                    outB.writeUTF("\nMão:\n" + oponente.getMaoString() + "Mesa: [" + c1 + "]\nSua vez! Escolha (1-" + oponente.getMao().size() + "):");
                    Carta c2 = oponente.jogarCarta(lerEscolha(inB));
                    enviarAmbos(oponente.getNome() + " jogou: " + c2);

                    Jogador vencedorRodada = comparar(c1, jogadorDaVez, c2, oponente);
                    if (r == 1) v1 = vencedorRodada; else if (r == 2) v2 = vencedorRodada; else v3 = vencedorRodada;

                    if (vencedorRodada != null) {
                        enviarAmbos("Vencedor da rodada: " + vencedorRodada.getNome());
                        jogadorDaVez = vencedorRodada; // Quem ganha o tombo joga primeiro a próxima carta
                    } else {
                        enviarAmbos("Empate na rodada!");
                    }

                    if (v1 != null && v1 == v2) break; // Ganhou as duas primeiras
                }

                if (!correu) {
                    Jogador vf = decidirVencedor(v1, v2, v3);
                    if (vf != null) {
                        vf.setPontos(vf.getPontos() + ponto);
                        enviarAmbos(">>> " + vf.getNome() + " venceu a mão!");
                    }
                }

                // 3. ALTERNA QUEM COMEÇA A PRÓXIMA MÃO DE CARTAS
                jogadorQueComecaAMao = (jogadorQueComecaAMao == jogador1) ? jogador2 : jogador1;

                enviarPlacar();
            }

            // --- FIM DA PARTIDA (Alguém bateu 12 pontos) ---
            Jogador campeao = (jogador1.getPontos() >= 12) ? jogador1 : jogador2;
            enviarAmbos("\n🏆 O CAMPEÃO DA PARTIDA É " + campeao.getNome() + "!!! 🏆\n");

            // Pergunta de revanche
            out1.writeUTF("Deseja jogar outra partida? (s/n)");
            out2.writeUTF("Deseja jogar outra partida? (s/n)");

            String resp1 = in1.readUTF();
            String resp2 = in2.readUTF();

            if (resp1.equalsIgnoreCase("s") && resp2.equalsIgnoreCase("s")) {
                enviarAmbos("Ambos aceitaram a revanche! Preparando as cartas...");
            } else {
                jogarNovamente = false;
                enviarAmbos("Um dos jogadores não quis revanche. Fim de jogo!");
            }
        }

        // Desconecta e finaliza tudo
        socket1.close();
        socket2.close();
        server.close();
    }

    private int lerEscolha(DataInputStream in) throws IOException {
        try { return Integer.parseInt(in.readUTF()); } catch (Exception e) { return 1; }
    }

    private Jogador comparar(Carta c1, Jogador j1, Carta c2, Jogador j2) {
        if (c1.getForca() > c2.getForca()) return j1;
        if (c2.getForca() > c1.getForca()) return j2;
        return null;
    }

    private Jogador decidirVencedor(Jogador v1, Jogador v2, Jogador v3) {
        if (v1 == v2 && v1 != null) return v1;
        if (v1 == null) return v2;
        if (v2 == null) return v1;
        return v3;
    }

    private void enviarAmbos(String msg) throws IOException {
        out1.writeUTF(msg); out2.writeUTF(msg);
    }

    private void enviarPlacar() throws IOException {
        enviarAmbos("\nPLACAR: " + jogador1.getNome() + " " + jogador1.getPontos() + " x " + jogador2.getPontos() + " " + jogador2.getNome());
    }
}
