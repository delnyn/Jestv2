package Model;

import java.io.Serializable;

/**
 * Offre d'un joueur
 */
public class Hand implements Serializable {
    private Card visible;
    private Card hidden;

    /**
     * Fixer l'offre d'un joueur
     * @param visible carte visible
     * @param hidden carte cachéee
     */
    public void setHand (Card visible, Card hidden) {
        this.visible = visible;
        this.hidden = hidden;
    }

    /**
     * Avoir la carte visible
     * @return carte visible
     */
    public Card getVisible () {
        return this.visible;
    }

    /**
     * Avoir la carte cachée
     * @return carte cachée
     */
    public Card getHidden () {
        return this.hidden;
    }

    /**
     * Savoir si l'offre est complète
     * @return true si elle l'est, false sinon
     */
    public boolean isPickable() {
        return this.visible != null && this.hidden != null;
    }

    /**
     * Savoir si l'offre contient une carte donnée
     * @param card carte qu'on recherche
     * @return true si la carte est dans l'offre, false sinon
     */
    public boolean contains(Card card) {
        return this.visible == card || this.hidden == card;
    }

    /**
     * Retirer une carte donnée de l'offre
     * @param card carte à retirer
     */
    public void removeCard (Card card) {
        if (this.visible == card) {
            this.visible = null;
        }  else if (this.hidden == card) {
            this.hidden = null;
        } else throw new IllegalArgumentException(card + " n'est pas dans cette main");
    }

    /**
     * Retirer la carte restante du jest
     * @return la carte qui restait
     */
    public Card resetRemainingCard () {
        Card card;
        if (this.visible == null) {
            card = this.hidden;
            removeCard(this.hidden);
        } else if (this.hidden == null) {
            card = this.visible;
            removeCard(this.visible);
        }  else {
            throw new RuntimeException("Deux cartes restantes en main; impossible de la vider");
        }
        return card;
    }
}
