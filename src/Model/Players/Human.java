package Model.Players;

import Model.Card;
import Model.Game;

import java.util.List;

import static View.ViewRequest.*;

/**
 * Classe des joueurs humains
 */
public class Human extends Player {
    /**
     * Crée un humain
     * @param name nom du joueur
     */
    public Human(String name) {super(name);}

    /**
     * Faire une offre à partir de 2 cartes
     * @param cards cartes
     */
    @Override
    public void makeOffer(List<Card> cards) {
        this.setChanged();
        this.notifyObservers(cards);
    }

    /**
     * Prend une carte parmi les offres disponibles
     */
    @Override
    public void takeCard() {
        Game.getInstance().getGameState().setPicking(this);
        this.setPlayed(true);
        this.setChanged();
        this.notifyObservers(PICKCARD);
    }
}
