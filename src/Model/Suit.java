package Model;

/**
 * enum des couleurs des cartes
 */
public enum Suit {
    SPADE(4),
    CLUB(3),
    DIAMOND(2),
    HEART(1),
    JOKER(0);

    private int value;

    /**
     * Crée une couleur et lui attribue une valeur selon les récompenses qu'elle donne
     * @param value valeur arbitraire de 0 à 4
     */
    Suit(int value){
        this.value = value;
    }

    /**
     * Avoir la valeur arbitraire d'une couleur
     * @return la valeur de la couleur
     */
    public int getValue() {return this.value;}

    /**
     * Fixer la valeur arbitraire d'une couleur
     * @param value nouvelle valeur de la couleur
     */
    public void setValue(int value) {this.value = value;}

    @Override
    public String toString() {
        String string = switch (this) {
            case SPADE -> "Pique";
            case CLUB -> "Trèfle";
            case DIAMOND -> "Carreau";
            case HEART -> "Cœur";
            case JOKER -> "Joker";
            default -> "";
        };
        return string;
    }
}
