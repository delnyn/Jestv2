package Model.Strategies;

import Model.Card;
import Model.Hand;
import Model.Players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie de bot facile
 */
public class EasyStrategy implements Strategy {

    /**
     * Fait une main en fonction des cartes distribuées
     * @param cards cartes distribuées
     * @return Hand créée
     */
    @Override
    public Hand makeOffer(List<Card> cards) {
        Hand hand = new Hand(); // easy: always hide better card
        if (cards.get(0).getSuit().getValue() > cards.get(1).getSuit().getValue()) {
            hand.setHand(cards.get(1), cards.get(0));
        } else if (cards.get(0).getSuit().getValue() < cards.get(1).getSuit().getValue()) {
            hand.setHand(cards.get(0), cards.get(1));
        } else { // equal suit
            if (cards.get(0).getSuit().getValue() > 2) { // good suit: higher card is better
                if (cards.get(0).getRank() > cards.get(1).getRank()) {
                    hand.setHand(cards.get(1), cards.get(0));
                } else {
                    hand.setHand(cards.get(0), cards.get(1));
                }
            } else { // bad suit: lower is better
                if (cards.get(0).getRank() > cards.get(1).getRank()) {
                    hand.setHand(cards.get(0), cards.get(1));
                } else {
                    hand.setHand(cards.get(1), cards.get(0));
                }
            }
        }
        return hand;
    }

    /**
     * Prend une carte parmi les offres des joueurs
     * @param players joueurs dont on peut prendre l'offre
     * @return carte choisie
     */
    @Override
    public Card takeCard(List<Player> players) {
        // take the best visible card
        List<Integer> scores = new ArrayList<>();
        for (Player player : players) {
            switch (player.getHand().getVisible().getSuit().getValue()) {
                case 4, 3 -> scores.add(player.getHand().getVisible().getRank());
                case 1, 2 -> scores.add(-1 * player.getHand().getVisible().getRank());
                case 0 -> scores.add(0);
            }
        }
        int best = -100;
        int bestIndex = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (best < scores.get(i)) {
                best = scores.get(i);
                bestIndex = i;
            }
        }
        return players.get(bestIndex).getHand().getVisible();
    }
}
