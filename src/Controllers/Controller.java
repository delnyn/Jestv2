package Controllers;

import Model.Card;
import Model.Game;
import Model.Hand;
import Model.Players.Player;
import Model.RuleSets.Visitor;
import View.Console;
import View.GUI;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Observer;

/**
 * Reçoit les instructions des Views et les transmet au Model
 */
public class Controller {
    private Game game;

    public Controller() {
        this.game = Game.getInstance();
    }

    /**
     * Crée une nouvelle partie
     */
    public void newGame() {
        this.game.newGame();
    }

    /**
     * Sauvegarde la partie en cours
     */
    public void saveGame() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");

        String formatted = time.format(timeFormatter);
        File saveDir = new File("saves");
        File save = new File("saves" + File.separator + "jestgame_" + formatted + ".ser");
        try {
            saveDir.mkdir();
            save.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            FileOutputStream fos = new FileOutputStream (save);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.game);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Termine le processus
     */
    public void quit() {
        System.exit(0);
    }

    /**
     * Charge une sauvegarde
     * @param fileName nom du fichier de la sauvegarde à charger
     */
    public void loadSave(String fileName) {
        File save = new File("saves" + File.separator + fileName);

        try {
            FileInputStream fis = new FileInputStream(save);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Game.setInstance((Game)ois.readObject());
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.game = Game.getInstance();
        this.game._addObserver(new Console(this));
        this.game._addObserver(new GUI(this));
        for (Player player : this.game.getPlayers()) {
            for (Observer observer : this.game._getObservers()) {
                player.addObserver(observer);
            }
        }
        this.game.nextTurn();
    }

    /**
     * Définit les règles de Game
     * @param visitor le visiteur propre aux règles
     */
    public void setRules(Visitor visitor) {
        this.game.setRules(visitor);
    }

    /**
     * Ajoute ou non les cartes d'extension
     * @param add true si il faut les ajouter, false sinon
     */
    public void addExtension(boolean add) {
        this.game.addExtension(add);
    }

    /**
     * Passe les joueurs à Game
     * @param players liste des joueurs de la partie
     */
    public void setPlayers (List<Player> players) {
        this.game.setPlayers(players);
    }

    /**
     * Crée la main d'un joueur
     * @param player joueur à qui donner la main
     * @param hand main à donner
     */
    public void makeHand(Player player, Hand hand) {
        player.setHand(hand);
    }

    /**
     * Prend la carte de l'offre d'un joueur et lance le tour de ce dernier s'il peut jouer
     * @param player joueur prenant la carte
     * @param chosenCard carte choisie
     */
    public void takeCard(Player player, Card chosenCard) {
        player.addJest(chosenCard);
        for (Player pickable : Game.getInstance().getPickables(player)) {
            if (pickable.getHand().contains(chosenCard)) {
                pickable.getHand().removeCard(chosenCard);
                if (pickable.hasToPlay()) {
                    pickable.takeCard();
                }
                break;
            }
        }
    }
}
