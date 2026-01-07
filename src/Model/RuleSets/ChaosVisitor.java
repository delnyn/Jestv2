package Model.RuleSets;

import Model.Card;
import Model.Players.Player;
import Model.Suit;

import java.util.*;

import static Model.Suit.*;

/**
 * Visiteur pour les règles chaos - couleurs aléatoires, paire pique/trèfle +5, joker toujours +4
 */
public class ChaosVisitor extends Visitor {

    /**
     * Crée le visiteur chaos et fixe l'ordre aléatoire des cartes
     */
    public ChaosVisitor() {
        List<Suit> suitOrder = Arrays.asList(SPADE, CLUB, DIAMOND, HEART);
        Collections.shuffle(suitOrder);
        for (Suit suit : suitOrder) {
            suit.setValue(suitOrder.indexOf(suit) + 1);
        }
    }

    /**
     * Calcule un score selon les règles chaos
     * @param player joueur dont on évalue le jest
     * @return score calculé
     */
    @Override
    public int calculateScore(Player player) {
        int score = 0;
        List<Integer> spades = new ArrayList<>();
        List<Integer> clubs = new ArrayList<>();
        this.lonelyAces(player);
        for (Card card : player.getJest()) {
            if (card.getRank() <= 4) {  // extension cards are excluded from this count
                switch (card.getSuit()) {
                    case SPADE:
                        spades.add(card.getRank());
                        if (clubs.contains(card.getRank())) {
                            score += 5;
                        }
                        break;
                    case CLUB:
                        clubs.add(card.getRank());
                        if (spades.contains(card.getRank())) {
                            score += 5;
                        }
                        break;
                    case JOKER:
                        score += 4;
                        break;
                }
                switch (card.getSuit().getValue()) {
                    case 4, 3 -> score += card.getRank();
                    case 2, 1 -> score -= card.getRank();
                }
            }
        }
        score = this.scoreWithExtension(player, score);
        return score;
    }
}
