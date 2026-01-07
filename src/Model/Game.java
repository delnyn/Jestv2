package Model;

import Controllers.Controller;
import Model.Players.Player;
import Model.RuleSets.ChaosVisitor;
import Model.RuleSets.Visitor;
import View.Console;
import View.GUI;

import java.io.Serializable;
import java.util.*;

import static Model.Suit.*;
import static View.ViewRequest.*;

/**
 * Classe de la Partie - singleton
 */
public final class Game extends Observable implements Serializable {
    private static Game instance;
    private List<Player> players;
    private List<Card> deck;
    private List<Card> trophies;
    private Visitor visitor;
    private GameState gameState;
    private transient List<Observer> _observers; // TODO: put back new observers when loading game
    private static final long serialVersionUID = 1L;

    /**
     * Crée un objet Game, y ajoute des View et lance le début de partie
     */
    static void main() {
        Game game = Game.getInstance();
        game._observers = new ArrayList<>();

        Console console = new Console(new Controller());
        Thread consoleThread = new Thread(console);
        consoleThread.start();

        GUI gui = new GUI(new Controller());
        Thread guiThread = new Thread(gui);
        guiThread.start();

        game._addObserver(console);
        game._addObserver(gui);
        game.setChanged();
        game.notifyObservers(MENU);
    }

    /**
     * Avoir le visiteur propre aux règles de la partie
     * @return le visiteur
     */
    public Visitor getVisitor() {
        return this.visitor;
    }

    /**
     * Fixer les règles
     * @param visitor visiteur propre aux règles
     */
    public void setRules(Visitor visitor) {
        this.visitor = visitor;
        if (visitor instanceof ChaosVisitor) {
            this.setChanged();
            this.notifyObservers(SHOWCARDORDER);
        }
        this.setChanged();
        this.notifyObservers(CHOOSEEXTENSION);
    }

    /**
     * Avoir les trophées de la partie
     * @return les cartes trophée
     */
    public List<Card> getTrophies() {
        return trophies;
    }

    /**
     * Ajouter une View et la stocker dans _observers
     * @param observer
     */
    public void _addObserver(Observer observer) {
        if (this._observers == null) {
            this._observers = new ArrayList<>();
        }
        this._observers.add(observer);
        this.addObserver(observer);
    }

    /**
     * Obtenir la liste des View
     * @return liste des View
     */
    public List<Observer> _getObservers() {
        return this._observers;
    }

    /**
     * Crée la pioche et lance le choix des règles
     */
    public void newGame() {
        this.deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            if (suit != JOKER) {
                for (int i = 1; i < 5; i++) {
                    this.deck.add(new Card(i, suit));
                }
            }
        }
        this.deck.add(new Card(0, JOKER));
        Collections.shuffle(this.deck);
        this.setChanged();
        this.notifyObservers(CHOOSERULES);
    }

    /**
     * Ajoute ou non les cartes d'extension puis lance la création des joueurs
     * @param add true si extension, false sinon
     */
    public void addExtension(boolean add) {
        if (add) {
            this.deck.add(new Card(6, SPADE));
            this.deck.add(new Card(6, CLUB));
            this.deck.add(new Card(6, DIAMOND));
            this.deck.add(new Card(6, HEART));
            Collections.shuffle(this.deck);
        }
        this.setChanged();
        this.notifyObservers(POPULATE);
    }

    /**
     * Fixe la liste des joueurs et lance la partie
     * @param players nouveaux joueurs
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
        for (Player player : this.players) {
            for (Observer observer : this._getObservers()) {
                player.addObserver(observer);
            }
        }
        this.startGame();
    }

    /**
     * Tire les trophées et lance le premier tour
     */
    public void startGame() {
        this.trophies = Arrays.asList(this.deck.removeLast(), this.deck.removeLast());
        this.setChanged();
        this.notifyObservers(SHOWTROPHIES);
        this.nextTurn();
    }

    /**
     * Lance un tour en fonction de GameState et lance le suivant si possible
     */
    public void nextTurn() {
        if (this.gameState == null) {
            this.gameState = new GameState();
            this.gameState.setNone();
        }
        switch (this.gameState.getState()) {
            case NONE -> {
                for (Player player : this.players) { // every player makes their offer
                    player.setPlayed(false);
                    List<Card> newHand;
                    if (player.getHand() == null) {
                        newHand = Arrays.asList(this.deck.removeLast(), this.deck.removeLast()); // hand out 2 cards if Hand empty, only one otherwise
                    } else {
                        newHand = Arrays.asList(this.deck.removeLast(), player.getHand().resetRemainingCard());
                    }
                    this.gameState.setFloating(player, newHand);
                    player.makeOffer(newHand);
                }
                this.gameState.setInbetween();
            }
            case FLOATING ->  {
                List<Player> canPlay = new ArrayList<>();
                int i = 0;
                while (players.get(i) != this.gameState.getPlayer()) {
                    i++;
                }
                while (i < this.players.size()) {
                    canPlay.add(this.players.get(i));
                    i++;
                }

                this.gameState.getPlayer().setPlayed(false);
                this.gameState.getPlayer().makeOffer(this.gameState.getFutureHand());
                canPlay.remove(this.gameState.getPlayer());

                for (Player player : canPlay) { // every player makes their offer
                    player.setPlayed(false);
                    List<Card> newHand;
                    if (player.getHand() == null) {
                        newHand = Arrays.asList(this.deck.removeLast(), this.deck.removeLast()); // hand out 2 cards if Hand empty, only one otherwise
                    } else {
                        newHand = Arrays.asList(this.deck.removeLast(), player.getHand().resetRemainingCard());
                    }
                    this.gameState.setFloating(player, newHand);
                    player.makeOffer(newHand);
                }
                this.gameState.setInbetween();
            }
            case INBETWEEN -> {
                while (mustSomeonePlay()) {
                    this.whoPlaysNow().takeCard();
                }

                this.gameState.setNone();

                if (this.deck.size() < this.players.size()) {
                    for (Player player : this.players) {
                        player.addJest(player.getHand().resetRemainingCard());
                        System.out.println(player.getJest());
                    }
                    this.giveTrophies();
                    this.scoresAndWinner();
                } else  {
                    this.nextTurn();
                }
            }
            case PICKING -> {
                this.gameState.getPlayer().takeCard();

                while (mustSomeonePlay()) {
                    this.whoPlaysNow().takeCard();
                }

                this.gameState.setNone();

                if (this.deck.size() < this.players.size()) {
                    for (Player player : this.players) {
                        player.addJest(player.getHand().resetRemainingCard());
                        System.out.println(player.getJest());
                    }
                    this.giveTrophies();
                    this.scoresAndWinner();
                } else  {
                    this.nextTurn();
                }
            }
        }
        this.nextTurn();
    }

    /**
     * Distribue les trophées en fin de partie
     */
    private void giveTrophies() {
        for (Card trophyCard : this.trophies) {
            Player trophyWinner = trophyCard.getTrophy().handOut(this.players);
            if (trophyWinner != null) {
                trophyWinner.addJest(trophyCard);
            }
        }
        this.setChanged();
        this.notifyObservers(TROPHIESGIVEN);
    }

    /**
     * Savoir si quelqu'un doit encore prendre une carte pendant ce tour
     * @return true si quelqu'un doit prendre une carte, false sinon
     */
    private boolean mustSomeonePlay() {
        boolean mustSomeonePlay = false;
        for (Player player : this.players) {
            if (player.hasToPlay()) {
                mustSomeonePlay = true;
                break;
            }
        }
        return mustSomeonePlay;
    }

    /**
     * Avoir le joueur avec l'offre de rang plus élevé, appelé quand la personne dont on a pris l'offre a déjà pris sa carte
     * @return le joueur qui doit entamer/continuer la prise des cartes
     */
    private Player whoPlaysNow() {
        List<Player> canPlay = new ArrayList<>();
        for (Player player : this.players) {
            if (player.hasToPlay()) {
                canPlay.add(player);
            }
        }
        int highestRank = -1;
        Player bestVisible = null;
        for (Player player : canPlay) {
            if (player.getHand().getVisible().getRank() > highestRank) {
                highestRank = player.getHand().getVisible().getRank();
                bestVisible = player;
            } else if (player.getHand().getVisible().getRank() == highestRank) {
                if (player.getHand().getVisible().getSuit().getValue() > bestVisible.getHand().getVisible().getSuit().getValue()) {
                    bestVisible = player;
                }
            }
        }
        return bestVisible;
    }

    /**
     * Avoir les joueurs dont le 'pov' peut prendre une carte
     * @param pov joueur qui doit prendre une carte
     * @return liste des joueurs desquels il peut prendre
     */
    public List<Player> getPickables(Player pov) {
        List<Player> pickables = new ArrayList<>();
        for (Player player : this.players) {
            if (player.getHand().isPickable()) {
                pickables.add(player);
            }
        }
        if (pickables.size() > 1) {
            pickables.remove(pov);
        }
        return pickables;
    }

    /**
     * Avoir la liste des joueurs
     * @return liste des joueurs
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Attribue un score à chaque joueur et dit aux Views d'annoncer le gagnant
     */
    private void scoresAndWinner() {
        for (Player player : this.players) {
            player.setScore(this.visitor.calculateScore(player));
        }
        this.setChanged();
        this.notifyObservers(WINNER);
    }

    /**
     * Avoir l'état de la partie
     * @return GameState de la partie en cours
     */
    public GameState getGameState() {
        return this.gameState;
    }

    /**
     * Avoir l'instance de la partie - singleton
     * @return Game en cours
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /**
     * Fixer la partie en cours - charge d'une sauvegarde
     * @param instance objet Game désérialisé
     */
    public static void  setInstance(Game instance) {
        Game.instance = instance;
    }
}
