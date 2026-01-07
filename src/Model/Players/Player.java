package Model.Players;

import Model.Card;
import Model.Hand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Classe abstraite des joueurs
 */
public abstract class Player extends Observable implements Serializable {
    private String name;
    private List<Card> jest;
    private Hand hand;
    private boolean played;
    private int score;

    /**
     * Avoir le score du joueur
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Donner un score au joueur
     * @param score score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Crée un joueur et un jest vide
     * @param name nom du joueur
     */
    public Player(String name) {
        this.name = name;
        this.jest = new ArrayList<>();
    }

    /**
     * Fixer si le joueur a joué pendant ce tour
     * @param played true si oui, false sinon
     */
    public void setPlayed(boolean played) {
        this.played = played;
    }

    /**
     * Savoir si le joueur doit encore jouer
     * @return true si oui, false sinon
     */
    public boolean hasToPlay() {
        return !played;
    }

    /**
     * Construire une Hand à partir de 2 cartes
     * @param cards cartes
     */
    abstract public void makeOffer(List<Card> cards);

    /**
     * Prendre une carte parmi les offres disponibles
     */
    abstract public void takeCard();

    /**
     * Avoir le jest du joueur
     * @return son jest
     */
    public List<Card> getJest() {
        return this.jest;
    }

    /**
     * Avoir la main du joueur
     * @return sa main
     */
    public Hand getHand() {
        return this.hand;
    }

    /**
     * Avoir le nom du joueur
     * @return son nom
     */
    public String getName() {
        return this.name;
    }

    /**
     * Fixer la main du joueur
     * @param hand sa nouvelle main
     */
    public void setHand(Hand hand) {
        this.hand = hand;
    }

    /**
     * Ajouter une carte au jest du joueur
     * @param card la carte à ajouter
     */
    public void addJest(Card card) {
        this.jest.add(card);
    }
}
