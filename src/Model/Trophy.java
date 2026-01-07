package Model;

import Model.Players.Player;

import java.io.Serializable;
import java.util.List;

import static Model.Suit.*;

/**
 * Objet trophée
 */
public class Trophy implements Serializable {
    enum Condition{
        HIGHEST, LOWEST, MAJORITY, HASJOKER, BESTJEST, BESTJESTNOJOKE, NONE;
    }

    private Condition condition;
    private Suit suit;
    private int rank;

    /**
     * Crée un trophée indépendant
     * @param condition condition: Joker, Meilleur Jest, Meilleur Jest sans Joker ou Aucun
     */
    public Trophy(Condition condition) {
        this.condition = condition;
    }

    /**
     * Crée un trophée dépendant d'une couleur
     * @param condition condition : Plus haut, Plus bas
     * @param suit couleur dont dépend la condition
     */
    public Trophy(Condition condition, Suit suit) {
        this.condition = condition;
        this.suit = suit;
    }

    /**
     * Crée un trophée dépendant d'un rang
     * @param condition condition : Majorité
     * @param rank rang dont dépend le trophée
     */
    public Trophy(Condition condition, int rank) {
        this.condition = condition;
        this.rank = rank;
    }

    /**
     * Donner le trophée à un joueur
     * @param players liste des joueurs
     * @return joueur qui a gagné ce trophée
     */
    public Player handOut(List<Player> players) {
        switch (this.condition) {
            case HIGHEST -> {
                int highest = 0;
                Player highestPlayer = null;
                for (Player player : players) {
                    for (Card card : player.getJest()) {
                        if (card.getSuit() == this.suit && card.getRank() > highest) {
                            highest = card.getRank();
                            highestPlayer = player;
                        }
                    }

                }
                return highestPlayer;
            }
            case LOWEST -> {
                int lowest = 7;
                Player lowestPlayer = null;
                for (Player player : players) {
                    for (Card card : player.getJest()) {
                        if (card.getSuit() == this.suit && card.getRank() < lowest) {
                            lowest = card.getRank();
                            lowestPlayer = player;
                        }
                    }
                }
                return lowestPlayer;
            }
            case MAJORITY -> {
                int majority = 0;
                Player majorityPlayer = null;
                for (Player player : players) {
                    int count = 0;
                    for (Card card : player.getJest()) {
                        if (card.getRank() == this.rank) {
                            count++;
                        }
                    }
                    if (count > majority) {
                        majority = count;
                        majorityPlayer = player;
                    }
                }
                return majorityPlayer;
            }
            case HASJOKER -> {
                for (Player player : players) {
                    if (player.getJest().contains(new Card(0, JOKER))) {
                        return player;
                    }
                }
                return null;
            }
            case BESTJEST -> {
                int bestJest = 0;
                Player bestJestPlayer = null;
                for (Player player : players) {
                    if (Game.getInstance().getVisitor().calculateScore(player) > bestJest) {
                        bestJest = Game.getInstance().getVisitor().calculateScore(player);
                        bestJestPlayer = player;
                    }
                }
                return bestJestPlayer;
            }
            case BESTJESTNOJOKE -> {
                int bestJest = 0;
                Player bestJestPlayer = null;
                for (Player player : players) {
                    boolean removed = false;
                    if (player.getJest().contains(new Card(0, JOKER))) {
                        removed = true;
                        player.getJest().remove(new Card(0, JOKER));
                    }
                    if (Game.getInstance().getVisitor().calculateScore(player) > bestJest) {
                        bestJest = Game.getInstance().getVisitor().calculateScore(player);
                        bestJestPlayer = player;
                    }
                    if (removed) {
                        player.getJest().add(new Card(0, JOKER));
                    }
                }
                return bestJestPlayer;
            }
            case NONE -> {return null;}
        }
        return null;
    }

    public String toString() {
        return switch (this.condition) {
            case HIGHEST -> "Plus haut " + this.suit;
            case LOWEST -> "Plus bas " + this.suit;
            case MAJORITY -> "Majorité de " + this.rank;
            case HASJOKER -> "Joker";
            case BESTJEST -> "Meilleur Jest";
            case BESTJESTNOJOKE -> "Meilleur Jest sans Joker";
            case NONE -> "Aucun (cartes d'extension)";
        };
    }
}
