package Model.Strategies;

import Model.Card;
import Model.Hand;
import Model.Players.Player;

import java.io.Serializable;
import java.util.List;

/**
 * Patron de conception stratégie
 */
public interface Strategy extends Serializable {
    /**
     * Fait une main en fonction des cartes distribuées
     * @param cards cartes distribuées
     * @return Hand créée
     */
    Hand makeOffer(List<Card> cards);

    /**
     * Prend une carte parmi les offres des joueurs
     * @param players joueurs dont on peut prendre l'offre
     * @return carte choisie
     */
    Card takeCard(List<Player> players);
}
