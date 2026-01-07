package Model;

import java.io.Serializable;

import static Model.Suit.*;
import static Model.Trophy.Condition.*;

/**
 * Classe Carte
 */
public class Card implements Serializable {
    private int rank;
    private Suit suit;

    /**
     * Créer une carte en fonction de son rang et sa couleur
     * @param rank son rang
     * @param suit sa couleur
     */
    public Card(int rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Avoir le trophée correspondant à la carte
     * @return le trophée propre à la carte
     */
    public Trophy getTrophy() {
        Trophy trophy = null;
        switch (this.suit) {
            case SPADE-> {
                switch (this.rank) {
                    case 1 -> trophy = new Trophy(HIGHEST, CLUB);
                    case 2 -> trophy = new Trophy(MAJORITY, 3);
                    case 3 -> trophy = new Trophy(MAJORITY, 2);
                    case 4 -> trophy = new Trophy(LOWEST, CLUB);
                    case 6 -> trophy = new Trophy(NONE);
                }
            }
            case CLUB -> {
                    switch (this.rank) {
                        case 1 -> trophy = new Trophy(HIGHEST, SPADE);
                        case 2 -> trophy = new Trophy(LOWEST, HEART);
                        case 3 -> trophy = new Trophy(HIGHEST, HEART);
                        case 4 -> trophy = new Trophy(LOWEST, SPADE);
                        case 6 -> trophy = new Trophy(NONE);
                    }
            }
            case DIAMOND-> {
                    switch (this.rank) {
                        case 1 -> trophy = new Trophy(MAJORITY, 4);
                        case 2 -> trophy = new Trophy(HIGHEST, DIAMOND);
                        case 3 -> trophy = new Trophy(LOWEST, DIAMOND);
                        case 4 -> trophy = new Trophy(BESTJESTNOJOKE);
                        case 6 -> trophy = new Trophy(NONE);
                    }
            }
            case HEART->  {
                if (this.rank == 6) {
                    trophy = new Trophy(NONE);
                } else {
                    trophy = new Trophy(HASJOKER);
                }
            }
            case JOKER-> trophy = new Trophy(BESTJEST);
        }
        return trophy;
    }

    /**
     * Avoir le rang de la carte
     * @return rang de la carte
     */
    public int getRank() {
        return rank;
    }

    /**
     * Avoir la couleur de la carte
     * @return couleur de la carte
     */
    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        if (this.suit == JOKER) {
            return "Joker";
        }
        return this.rank + " de " + this.suit;
    }
}
