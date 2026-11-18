package truco.modelo;

import java.util.ArrayList;
import java.util.Collections;

public class Baralho {
    private ArrayList<Carta> baralho;
    private Carta cartaVirada;

    public Baralho() {
        this.baralho = new ArrayList<>();
        iniciaBaralho();
    }

    public void iniciaBaralho() {
        final String[] numeros = {"4", "5", "6", "7", "Q", "J", "K", "A", "2", "3"};
        final String[] naipes = {"Ouros", "Espadas", "Copas", "Paus"};
        int forca = 1;

        baralho.clear();

        for (String numero : numeros) {
            for (String naipe : naipes) {
                baralho.add(new Carta(numero, naipe, forca));
            }
            forca++;
        }

        embaralhar();
        criarManilha();
    }

    private void criarManilha() {
        cartaVirada = baralho.remove(0);
        System.out.println("\n\nCarta virada: " + cartaVirada+ "\n\n");

        int manilhaForca = (cartaVirada.getForca() % 10) + 1;

        for (Carta c : baralho) {
            if (c.getForca() == manilhaForca) {
                c.setManilha();
            }
        }
    }

    private void embaralhar() {
        Collections.shuffle(baralho);
    }

    public ArrayList<Carta> getBaralho() {
        embaralhar();
        return baralho;
    }

    public Carta darCarta() {
        if (!baralho.isEmpty()) {
            return baralho.remove(0);
        }
        return null;
    }

    public int tamanho() {
        return baralho.size();
    }

    public void verBaralho() {
        for (Carta c : baralho) {
            System.out.println(c);
        }
    }

    public void reiniciarBaralho() {
        iniciaBaralho();
    }

    public boolean estaVazio() {
        return baralho.isEmpty();
    }

    public Carta getCartaVirada() {
        return cartaVirada;
    }
}
