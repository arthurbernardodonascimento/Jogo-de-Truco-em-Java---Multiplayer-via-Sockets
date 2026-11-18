package truco.modelo;

public class Carta {

    private String valor;
    private String naipe;
    private boolean manilha;
    private int forca;

    public Carta(String valor, String naipe, int forca) {
        this.valor = valor;
        this.naipe = naipe;
        this.forca = forca;
        this.manilha = false;
    }

    public String getValor() {
        return valor;
    }

    public String getNaipe() {
        return naipe;
    }

    public int getForca() {
        return forca;
    }

    public boolean isManilha() {
        return manilha;
    }

    public void setManilha() {
        switch (this.naipe) {
            case "Ouros":
                this.forca = 11;
                break;
            case "Espadas":
                this.forca = 12;
                break;
            case "Copas":
                this.forca = 13;
                break;
            case "Paus":
                this.forca = 14;
                break;
        }
        this.ativarManilha();
    }

    private void ativarManilha() {
        this.manilha = true;
    }

    @Override
    public String toString() {
        return valor + " de " + naipe + " | Força: " + forca + (manilha ? " [Manilha]" : "");
    }

    public void resetarManilha(int novaForca) {
        this.manilha = false;
        this.forca = novaForca;
    }
}
