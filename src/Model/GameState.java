package Model;

import Model.Players.Player;

import java.io.Serializable;
import java.util.List;

/**
 * Objet de l'état de la partie - sauvegarde
 */
public class GameState implements Serializable {
    enum State {
        NONE, FLOATING, INBETWEEN, PICKING
    }

    private State state;
    private Player player;
    private List<Card> futureHand;

    /**
     * Avoir l'état de la partie
     * @return enum State état de la partie
     */
    public State getState() {
        return state;
    }

    /**
     * Donne le joueur qui doit définir son offre ou tirer celle des autres
     * @return joueur qui doit jouer
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Fixe le joueur qui doit définir son offre ou tirer celle des autres
     * @param player joueur qui doit jouer
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Avoir les cartes avec lesquelles le joueur flottant doit formuler son offre
     * @return liste de 2 cartes
     */
    public List<Card> getFutureHand() {
        return futureHand;
    }

    /**
     * Fixer les cartes avec lesquelles le joueur flottant doit formuler son offre
     * @param futureHand
     */
    public void setFutureHand(List<Card> futureHand) {
        this.futureHand = futureHand;
    }

    /**
     * Mettre l'état de la partie à rien (tour fini)
     */
    public void setNone () {
        this.state = State.NONE;
        this.player = null;
        this.futureHand = null;
    }

    /**
     * Fixer l'état de la partie à 'flottant' (le joueur est en train de formuler son offre)
     * @param player joueur qui doit formuler son offre
     * @param futureHand cartes avec lesquelles le joueur flottant doit formuler son offre
     */
    public void setFloating (Player player, List<Card> futureHand) {
        this.state = State.FLOATING;
        this.player = player;
        this.futureHand = futureHand;
    }

    /**
     * Fixer l'état de la partie à 'prenant' (le joueur est en train de prendre une carte)
     * @param player joueur qui doit encore prendre une carte
     */
    public void setPicking (Player player) {
        this.state = State.PICKING;
        this.player = player;
        this.futureHand = null;
    }

    /**
     * Fixer l'état de la partie à 'entre-deux' (les joueurs ont fait leur offre et doivent tous prendre une carte)
     */
    public void setInbetween () {
        this.state = State.INBETWEEN;
        this.player = null;
        this.futureHand = null;
    }
}
