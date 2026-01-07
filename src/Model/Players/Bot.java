package Model.Players;

import Model.Card;
import Model.Game;
import Model.Strategies.Strategy;

import java.util.List;

import static View.ViewRequest.*;

/**
 * Classe des joueurs bot
 */
public class Bot extends Player{
    private Strategy strategy;

    /**
     * Crée un bot
     * @param name nom du bot
     */
    public Bot(String name) {super(name);}

    /**
     * Crée une Hand à partir de deux cartes
     * @param cards cartes
     */
    @Override
    public void makeOffer(List<Card> cards) {
        this.setChanged();
        this.notifyObservers(MAKEHAND);
        this.setHand(this.strategy.makeOffer(cards));
    }

    /**
     * Prendre une carte parmi celles disponibles
     */
    @Override
    public void takeCard() {
        Game.getInstance().getGameState().setPicking(this);
        this.setPlayed(true);
        this.setChanged();
        this.notifyObservers(PICKCARD);
    }

    /**
     * Choisir une stratégie pour le bot
     * @param strategy la stratégie à adopter
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Renvoie la stratégie du bot
     * @return la stratégie utilisée
     */
    public Strategy getStrategy() {
        return this.strategy;
    }
}
