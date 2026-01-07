package Model.RuleSets;

import Model.Card;
import Model.Players.Player;
import Model.Suit;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static Model.Suit.*;

/**
 * Classe abstraite des visiteurs
 */
public abstract  class Visitor implements Serializable {
    /**
     * Calcule un score en fonction d'un ensemble de règles
     * @param player joueur dont on évalue le jest
     * @return score calculé
     */
    public abstract int calculateScore(Player player);


    /**
     * Transforme les As seuls de leur couleur dans un jest en 5 de leur couleur
     * @param player joueur dont on évalue le jest
     */
    protected void lonelyAces (Player player) {
        List<Suit> suits = Arrays.asList(SPADE, CLUB, DIAMOND, HEART);
        int cardCount = 0;
        for (Suit suit : suits) {
            if (player.getJest().contains(new Card(1,  suit))) {
                for (int i = 2; i < 5; i++) {
                    if (player.getJest().contains(new Card(i,  suit))) {
                        cardCount += 1;
                    }
                }
                if (cardCount == 0) {
                    player.getJest().remove(new Card(1,  suit));
                    player.getJest().add(new Card(5,  suit));
                }
            }
        }
    }

    /**
     * Calcule les points entre les cœurs et le joker
     * @param player joueur dont on évalue le jest
     * @param hearts liste des rangs de cartes cœurs que le joueur possède
     * @return points bonus gagnés
     */
    protected int jokerPoints(Player player, List<Integer> hearts) {
        int score = 0;
        if (player.getJest().contains(new Card(0, JOKER))) {
            switch (hearts.size()) {
                case 0:
                    score += 4;
                    break;
                case 1,2,3:
                    if (!player.getJest().contains(new Card(6, HEART))) { // if 6 of hearts didn't cancel
                        for (int faceValue : hearts) {
                            score -= faceValue;
                        }
                    }
                    break;
                case 4:
                    score += 10;
                    break;
            }
        }
        return score;
    }

    /**
     * Calcule le score final du joueur en fonction des cartes d'extension
     * @param player joueur dont on évalue le jest
     * @param score score sans les cartes d'extension
     * @return score avec les cartes d'extension
     */
    protected int scoreWithExtension(Player player, int score) {
        if (player.getJest().contains(new Card(6, SPADE))) {
            score *= 2;
        } if (player.getJest().contains(new Card(6, CLUB))) {
            score += 6;
        } if (player.getJest().contains(new Card(6, DIAMOND))) {
            score -= 10;
        }
        return score;
    }
}
