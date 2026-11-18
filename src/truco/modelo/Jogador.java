package truco.modelo;

import java.util.ArrayList;

public class Jogador {
    private String nome;
    private ArrayList<Carta> mao;
    private int pontos;
    private int id;

    public Jogador(String nome, int id) {
        this.nome = nome;
        this.pontos = 0;
        this.mao = new ArrayList<>();
        this.id = id;
    }

    public String getNome() { return nome; }
    public ArrayList<Carta> getMao() { return mao; }
    public int getPontos() { return pontos; }
    public int getId() { return id; }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public void darCarta(Carta carta) {
        mao.add(carta);
    }

    public String getMaoString() {
        String s = "";
        for (int i = 0; i < mao.size(); i++) {
            s += (i + 1) + ": " + mao.get(i) + "\n";
        }
        return s;
    }

    public Carta jogarCarta(int escolha) {
        if (mao.isEmpty()) return null;

        if (escolha >= 1 && escolha <= mao.size()) {
            return mao.remove(escolha - 1);
        } else {
            return mao.remove(0);
        }
    }

    public void limparMao() {
        mao.clear();
    }
}
