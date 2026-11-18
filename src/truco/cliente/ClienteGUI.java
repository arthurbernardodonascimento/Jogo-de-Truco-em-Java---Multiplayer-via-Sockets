package truco.cliente;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteGUI {
    private JFrame frame;
    private JTextArea areaTexto;
    private JTextField campoEntrada;
    private JButton botaoEnviar;
    private JPanel painelMao;
    private JPanel painelMesa;
    private JLabel labelVira;
    private JLabel labelCartaJogada;

    // Novos botões para o Truco fixos na tela
    private JButton btnTrucoSim;
    private JButton btnTrucoNao;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    // Ajuste para o IP do seu servidor
    private final String IP_SERVIDOR = "192.168.0.109";
    private final int PORTA = 12345;

    public ClienteGUI() {
        configurarInterface();
    }

    private void configurarInterface() {
        frame = new JFrame("Truco Java - Interface Visual");
        frame.setSize(700, 750); // Aumentei um pouco a largura para acomodar os botões ao lado
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(34, 139, 34));

        // --- MESA ---
        painelMesa = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        painelMesa.setBackground(new Color(34, 139, 34));
        painelMesa.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Mesa", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        // Container para o VIRA
        JPanel painelVira = new JPanel(new BorderLayout(0, 5));
        painelVira.setOpaque(false);
        JLabel tituloVira = new JLabel("CARTA VIRA", SwingConstants.CENTER);
        tituloVira.setForeground(Color.WHITE);
        tituloVira.setFont(new Font("Arial", Font.BOLD, 14));
        labelVira = criarLabelCartaVazia("VIRA");
        painelVira.add(tituloVira, BorderLayout.NORTH);
        painelVira.add(labelVira, BorderLayout.CENTER);

        // Container para a CARTA NA MESA
        JPanel painelJogada = new JPanel(new BorderLayout(0, 5));
        painelJogada.setOpaque(false);
        JLabel tituloJogada = new JLabel("CARTA NA MESA", SwingConstants.CENTER);
        tituloJogada.setForeground(Color.WHITE);
        tituloJogada.setFont(new Font("Arial", Font.BOLD, 14));
        labelCartaJogada = criarLabelCartaVazia("JOGADA");
        painelJogada.add(tituloJogada, BorderLayout.NORTH);
        painelJogada.add(labelCartaJogada, BorderLayout.CENTER);

        painelMesa.add(painelVira);
        painelMesa.add(painelJogada);
        frame.add(painelMesa, BorderLayout.NORTH);

        // --- LOGS ---
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaTexto.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        frame.add(scrollPane, BorderLayout.CENTER);

        // --- CONTROLES INFERIORES ---
        JPanel painelInferior = new JPanel(new BorderLayout());

        // --- 1. Cartas (Centro) ---
        painelMao = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelMao.setBackground(new Color(34, 139, 34));
        painelMao.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Sua Mão", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        painelInferior.add(painelMao, BorderLayout.CENTER);

        // --- 2. Painel de Truco (Direita das cartas) ---
        JPanel painelTruco = new JPanel(new GridLayout(2, 1, 5, 5));
        painelTruco.setBackground(new Color(34, 139, 34));
        painelTruco.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Pedir Truco?", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        btnTrucoSim = new JButton("SIM (Truco)");
        btnTrucoSim.setBackground(new Color(255, 69, 0)); // Laranja forte
        btnTrucoSim.setForeground(Color.WHITE);
        btnTrucoSim.setFont(new Font("Arial", Font.BOLD, 14));
        btnTrucoSim.setEnabled(false);

        btnTrucoNao = new JButton("NÃO (Jogar)");
        btnTrucoNao.setBackground(Color.DARK_GRAY);
        btnTrucoNao.setForeground(Color.WHITE);
        btnTrucoNao.setFont(new Font("Arial", Font.BOLD, 14));
        btnTrucoNao.setEnabled(false);

        painelTruco.add(btnTrucoSim);
        painelTruco.add(btnTrucoNao);

        // Adiciona ações aos botões de truco
        btnTrucoSim.addActionListener(e -> responderTruco("s"));
        btnTrucoNao.addActionListener(e -> responderTruco("n"));

        painelInferior.add(painelTruco, BorderLayout.EAST);

        // --- 3. Chat / Entradas (Base) ---
        JPanel painelControles = new JPanel(new BorderLayout());
        campoEntrada = new JTextField();
        campoEntrada.setFont(new Font("SansSerif", Font.PLAIN, 16));
        campoEntrada.setEnabled(false);
        botaoEnviar = new JButton("Enviar Texto");
        botaoEnviar.setEnabled(false);

        painelControles.add(campoEntrada, BorderLayout.CENTER);
        painelControles.add(botaoEnviar, BorderLayout.EAST);
        painelInferior.add(painelControles, BorderLayout.SOUTH);

        frame.add(painelInferior, BorderLayout.SOUTH);

        ActionListener acaoEnviar = e -> enviarTexto();
        botaoEnviar.addActionListener(acaoEnviar);
        campoEntrada.addActionListener(acaoEnviar);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void responderTruco(String resposta) {
        enviarProServidor(resposta);
        btnTrucoSim.setEnabled(false);
        btnTrucoNao.setEnabled(false);

        if (resposta.equals("n")) {
            // Se ele não pediu truco, o próximo passo é jogar a carta.
            // Então liberamos as cartas imediatamente.
            habilitarCartas(true);
        }
    }

    private JLabel criarLabelCartaVazia(String titulo) {
        JLabel label = new JLabel(titulo, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, 140));
        label.setOpaque(true);
        label.setBackground(new Color(200, 200, 200));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return label;
    }

    private void enviarTexto() {
        String texto = campoEntrada.getText().trim();
        if (!texto.isEmpty()) {
            enviarProServidor(texto);
            areaTexto.append("\nVocê: " + texto + "\n");
            campoEntrada.setText("");
            campoEntrada.setEnabled(false);
            botaoEnviar.setEnabled(false);
        }
    }

    private void enviarProServidor(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException ex) {
            areaTexto.append("\nErro ao enviar.\n");
        }
    }

    public void iniciarConexao() {
        try {
            socket = new Socket(IP_SERVIDOR, PORTA);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            areaTexto.append("Conectado ao jogo!\n");

            new Thread(() -> {
                while (true) {
                    try {
                        String msg = in.readUTF();
                        SwingUtilities.invokeLater(() -> {
                            areaTexto.append(msg + "\n");
                            areaTexto.setCaretPosition(areaTexto.getDocument().getLength());

                            // Atualiza a tela (cartas e mesa) primeiro
                            processarMensagemVisual(msg);

                            // Lógica do Truco (Ativa os botões fixos em vez de abrir janela)
                            if (msg.contains("Deseja TRUCO? (s/n)")) {
                                btnTrucoSim.setEnabled(true);
                                btnTrucoNao.setEnabled(true);
                                habilitarCartas(false); // Bloqueia as cartas até decidir o truco
                            }

                            // Libera digitação manual caso seja pergunta de nome ou outra coisa
                            String msgL = msg.toLowerCase();
                            if ((msgL.contains("?") || msgL.contains("digite")) && !msg.contains("Deseja TRUCO?")) {
                                campoEntrada.setEnabled(true);
                                botaoEnviar.setEnabled(true);
                                campoEntrada.requestFocus();
                            }
                        });
                    } catch (IOException e) {
                        break;
                    }
                }
            }).start();
        } catch (Exception e) {
            areaTexto.append("Erro: " + e.getMessage());
        }
    }

    private void processarMensagemVisual(String msg) {
        if (msg.contains("=== VIRA:")) {
            formatarCartaVisual(labelVira, extrairTrecho(msg, "=== VIRA: ", " ==="));
            formatarCartaVisual(labelCartaJogada, "");
        }
        if (msg.contains("Mesa: [")) {
            formatarCartaVisual(labelCartaJogada, extrairTrecho(msg, "Mesa: [", "]"));
        }
        if (msg.contains("Vencedor da rodada") || msg.contains("Empate")) {
            formatarCartaVisual(labelCartaJogada, "");
        }

        // Desenha a mão de cartas
        if (msg.contains("mão:\n") || msg.contains("MÃO:\n") || msg.contains("Mão:\n") || msg.matches(".*\\d+: .*\\| Força:.*")) {
            painelMao.removeAll();
            String[] linhas = msg.split("\n");
            for (String linha : linhas) {
                if (linha.matches("\\d+: .*")) {
                    String indiceStr = linha.split(":")[0].trim();
                    String cartaDesc = linha.substring(linha.indexOf(":") + 1).trim();
                    painelMao.add(criarBotaoCarta(cartaDesc, indiceStr));
                }
            }
            painelMao.revalidate();
            painelMao.repaint();
        }

        // Habilita as cartas SE for a vez E não for o momento da pergunta de truco
        if (msg.contains("Sua vez!") && !msg.contains("Deseja TRUCO?")) {
            habilitarCartas(true);
        }
    }

    private String extrairTrecho(String texto, String inicio, String fim) {
        try {
            int start = texto.indexOf(inicio) + inicio.length();
            int end = texto.indexOf(fim, start);
            return (end == -1) ? texto.substring(start).trim() : texto.substring(start, end).trim();
        } catch (Exception e) { return ""; }
    }

    private JButton criarBotaoCarta(String descricao, String indice) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(100, 140));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        formatarComponenteCarta(btn, descricao);
        btn.addActionListener(e -> {
            enviarProServidor(indice);
            habilitarCartas(false);
            painelMao.remove(btn);
            painelMao.revalidate();
            painelMao.repaint();
        });
        return btn;
    }

    private void formatarCartaVisual(JLabel label, String desc) {
        if (desc.isEmpty()) {
            label.setText("VAZIO");
            label.setBackground(new Color(200, 200, 200));
            return;
        }
        label.setBackground(Color.WHITE);
        formatarComponenteCarta(label, desc);
    }

    private void formatarComponenteCarta(JComponent comp, String desc) {
        try {
            String valor = desc.split(" de ")[0].trim();
            String naipe = desc.split(" de ")[1].split(" \\|")[0].trim();
            String simbolo = ""; String cor = "#000000";
            switch (naipe) {
                case "Ouros": simbolo = "♦"; cor = "#FF0000"; break;
                case "Copas": simbolo = "♥"; cor = "#FF0000"; break;
                case "Espadas": simbolo = "♠"; break;
                case "Paus": simbolo = "♣"; break;
            }
            String html = "<html><center><span style='font-size:18px; color:" + cor + "'>" + valor + "</span><br>" +
                          "<span style='font-size:30px; color:" + cor + "'>" + simbolo + "</span></center></html>";
            if (comp instanceof JButton) ((JButton) comp).setText(html);
            else if (comp instanceof JLabel) ((JLabel) comp).setText(html);
        } catch (Exception e) { }
    }

    private void habilitarCartas(boolean h) {
        for (Component c : painelMao.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(h);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteGUI gui = new ClienteGUI();
            new Thread(gui::iniciarConexao).start();
        });
    }
}
