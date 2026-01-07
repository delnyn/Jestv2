package Model.RuleSets;

import Model.Card;
import Model.Players.Player;

import java.util.ArrayList;
import java.util.List;

import static Model.Suit.*;

/**
 * Visiteur pour les règles inversées
 */
public class InvertedVisitor extends Visitor {

    /**
     * Crée un visiteur inverse et inverse l'ordre des couleurs
     */
    public InvertedVisitor() {
        SPADE.setValue(2);
        CLUB.setValue(2);
        DIAMOND.setValue(4);
    }

    /**
     * Calcule un score en fonction des règles inversées
     * @param player joueur dont on évalue le jest
     * @return score calculé
     */
    @Override
    public int calculateScore(Player player) {
        int score = 0;
        List<Integer> spades = new ArrayList<>();
        List<Integer> clubs = new ArrayList<>();
        List<Integer> hearts = new ArrayList<>();
        this.lonelyAces(player);
        for (Card card : player.getJest()) {
            if (card.getRank() <= 4){  // extension cards are excluded from this count
                switch (card.getSuit()) {
                    case SPADE:
                        score -= card.getRank();
                        spades.add(card.getRank());
                        if (clubs.contains(card.getRank())) {
                            score -= 2;
                        }
                        break;
                    case CLUB:
                        score -= card.getRank();
                        clubs.add(card.getRank());
                        if (spades.contains(card.getRank())) {
                            score -= 2;
                        }
                        break;
                    case DIAMOND:
                        score += card.getRank();
                        break;
                    case HEART:
                        hearts.add(card.getRank());
                        break;
                    default:
                        break;
                }
            }
        }
        score += this.jokerPoints(player, hearts);
        score = this.scoreWithExtension(player, score);
        return score;
    }
}
