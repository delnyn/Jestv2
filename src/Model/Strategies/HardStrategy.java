package Model.Strategies;

import Model.Card;
import Model.Hand;
import Model.Players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie de bot difficile
 */
public class HardStrategy implements Strategy {

    /**
     * Fait une main en fonction des cartes distribuées
     * @param cards cartes distribuées
     * @return Hand créée
     */
    @Override
    public Hand makeOffer(List<Card> cards) {
        Hand hand = new Hand();
        List<Integer> scores = new ArrayList<>();
        for (Card card : cards) {
            switch (card.getSuit().getValue()) {
                case 4, 3 -> scores.add(card.getRank() * 2);
                case 2 -> scores.add(card.getRank() * -3);
                case 1 -> scores.add(-1);
                case 0 -> scores.add(4); // joker
            }
        }
        if (scores.getFirst() > scores.getLast()) {
            hand.setHand(cards.get(1), cards.get(0));
        } else {
            hand.setHand(cards.get(0), cards.get(1));
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
        // assume hidden is always best
        // check visible cards
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
        if (best < 0) { // if best visible sucks, assume hidden is better
            return players.get(bestIndex).getHand().getHidden();
        } else {
            return players.get(bestIndex).getHand().getVisible();
        }
    }
}
