package View;

import Controllers.Controller;
import Model.Card;
import Model.Game;
import Model.Hand;
import Model.Players.Bot;
import Model.Players.Human;
import Model.Players.Player;
import Model.RuleSets.BaseVisitor;
import Model.RuleSets.ChaosVisitor;
import Model.RuleSets.InvertedVisitor;
import Model.Strategies.EasyStrategy;
import Model.Strategies.HardStrategy;
import Model.Suit;

import java.io.File;
import java.util.*;

import static Model.Suit.*;
import static View.ViewRequest.*;

public class  Console implements Observer, Runnable {
    private Scanner scan;
    private Controller controller;
    private boolean saveable = false;

    public Console (Controller controller) {
        this.controller = controller;
        this.scan = new Scanner(System.in);
    }

    public void run() {
    }

    private void say(String string) {
        System.out.println(string);
    }

    private int ask(List<String> strings) {
        System.out.println("\n" + strings.getFirst());
        for (int i = 1; i < strings.size(); i++) {
            System.out.println(i + ". " + strings.get(i));
        }
        System.out.print("> ");
        int answer = 0;
        String quit = scan.nextLine();
        if (quit.equals("q")) {
            if (this.ask(Arrays.asList("Quitter la partie?", "Non", "Oui")) == 2) {
                if (saveable) {
                    if (this.ask(Arrays.asList("Sauvegarder?", "Oui", "Non")) == 1) {
                        this.controller.saveGame();
                        this.controller.quit();
                    }
                } else {
                    this.controller.quit();
                }
            }
        } else {
            try {
                answer = Integer.parseInt(quit);
            } catch (NumberFormatException e) {
                System.out.println("Entrée incorrecte");
                this.ask(strings);
            }
        }
        if (answer < 1 || answer > strings.size() - 1) {
            System.out.println("Entrée incorrecte");
            this.ask(strings);
        }
        return answer;
    }



    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof List<?>) {
            this.makeHand((Player) o, (List<Card>) arg);
        } else {
            switch (arg) {
                case MENU -> this.menu();
                case CHOOSERULES -> this.chooseRules();
                case SHOWCARDORDER -> this.showCardOrder();
                case CHOOSEEXTENSION -> this.chooseExtension();
                case POPULATE -> this.populate();
                case SHOWTROPHIES -> this.showTrophies();
                case MAKEHAND -> this.say("[ " + ((Bot) o).getName() + " a fait son offre ]");
                case PICKCARD -> {
                    if (o instanceof Human) {
                        this.pickCard((Human) o);
                    } else {
                        this.say("[ " + ((Bot) o).getName() + " a pris une carte ]");
                        this.controller.takeCard((Bot) o, ((Bot) o).getStrategy().takeCard(Game.getInstance().getPickables((Bot) o)));
                    }
                }
                case TROPHIESGIVEN -> this.trophiesGiven();
                case WINNER -> this.winner();
                default -> throw new RuntimeException("/!\\ ViewRequest de Game irrecevable : " + arg);
            }
        }
    }

    private void menu() {
        this.say("\n\n[ JEST ]");
        switch (this.ask(Arrays.asList("", "Nouvelle partie", "Charger une partie sauvegardée"))) {
            case 1 -> this.controller.newGame();
            case 2 -> {
                File saveDir = new File("saves");
                if (saveDir.exists() && saveDir.isDirectory() && saveDir.listFiles().length > 0) {
                    List<String> fileNames = new ArrayList<>();
                    fileNames.add("Quelle sauvegarde charger?");
                    for (File file : saveDir.listFiles()) {
                        fileNames.add(file.getName());
                    }
                    this.controller.loadSave(fileNames.get(this.ask(fileNames)));
                }
            }
        }
    }

    private void chooseRules() {
        switch (this.ask(Arrays.asList("Avec quelles règles jouer?", "Règles de base", "Règles inversées (Piques et" +
                " Trèfles [-], Carreaux [+], paire noire [-2])", "Règles chaotiques (couleurs aléatoires, Joker toujours" +
                " +4, paires noires +5"))){
            case 1 -> this.controller.setRules(new BaseVisitor());
            case 2 -> this.controller.setRules(new InvertedVisitor());
            case 3 -> this.controller.setRules(new ChaosVisitor());
        }
    }

    private void showCardOrder() {
        if (Game.getInstance().getVisitor() instanceof ChaosVisitor) {
            List<Suit> suits = Arrays.asList(SPADE, CLUB, DIAMOND, HEART);
            System.out.print("\nRègles chaos, notez bien: ");
            for (Suit suit : suits) {
                System.out.print(suit);
                if (suit.getValue() > 2) {
                    System.out.print(" [+] ");
                } else  {
                    System.out.print(" [-] ");
                }
            }
        }
    }

    private void chooseExtension() {
        switch (this.ask(Arrays.asList("Jouer avec des cartes d'extension? \n(6 de pique: score ×2, 6 de trèfle: " +
                "score +6, 6 de carreau: score -10, 6 de coeur: pas de pénalité possible avec un joker)",
                "Non", "Oui"))) {
            case 1 -> this.controller.addExtension(false);
            case 2 -> this.controller.addExtension(true);
        }
    }

    private void populate() {
        int nbPlayers = 3;
        if (this.ask(Arrays.asList("Combien de joueurs (3 ou 4)?", "Trois joueurs", "Quatre joueurs")) == 2) {
            nbPlayers ++;
        }
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < nbPlayers; i++) {
            this.say("\n[ Joueur " + (i + 1) + " ]");
            System.out.print("Nom: ");
            String name = this.scan.nextLine();
            switch (this.ask(Arrays.asList("Quel type de joueur?", "Humain", "Bot"))) {
                case 1 -> players.add(new Human(name));
                case 2 -> {
                    players.add(new Bot(name));
                    switch (this.ask(Arrays.asList("Quelle stratégie utilise le bot?", "Simple", "Complexe"))) {
                        case 1 ->((Bot) players.get(i)).setStrategy(new EasyStrategy());
                        case 2 ->((Bot) players.get(i)).setStrategy(new HardStrategy());
                    }
                }
            }
        }
        this.controller.setPlayers(players);
    }

    private void showTrophies() {
        this.say("Trophées: " + Game.getInstance().getTrophies().getFirst() + " [" +
                Game.getInstance().getTrophies().getFirst().getTrophy() + "], " +
                Game.getInstance().getTrophies().getLast() + " [" +
                Game.getInstance().getTrophies().getLast().getTrophy() + "]");
        this.saveable = true;
    }

    private void makeHand(Player player, List<Card> cards) {
        Hand hand = new Hand();
        this.say("\n[ " + player.getName() + ", faites votre offre ]");
        switch (this.ask(Arrays.asList("Quelle carte garder visible?", cards.getFirst().toString(),
                cards.getLast().toString()))) {
            case 1 -> hand.setHand(cards.getFirst(), cards.getLast());
            case 2 -> hand.setHand(cards.getLast(), cards.getFirst());
        }
        this.controller.makeHand(player, hand);
    }

    private void pickCard(Player player) {
        this.say("\n[ " + player.getName() + ", prenez une carte ]");
        List<String> options = new ArrayList<>();
        List<Card> offers = new ArrayList<>();
        options.add("Quelle carte prendre?");
        for (Player pickable : Game.getInstance().getPickables(player)) {
            options.add(pickable.getName() + ": " + pickable.getHand().getVisible());
            options.add(pickable.getName() + ": carte cachée");

            offers.add(pickable.getHand().getVisible());
            offers.add(pickable.getHand().getHidden());
        }
        Card chosenCard = offers.get(this.ask(options) - 1);
        this.say("Vous avez pris un " + chosenCard);
        this.controller.takeCard(player, chosenCard);
    }

    private void trophiesGiven() {
        for (Card trophyCard : Game.getInstance().getTrophies()) {
            boolean given = false;
            for (Player player : Game.getInstance().getPlayers()) {
                if (player.getJest().contains(trophyCard)) {
                    this.say("[ Le trophée " + trophyCard + " (" + trophyCard.getTrophy() + ") a été distribué à " +
                            player.getName() + " ]");
                    given = true;
                    break;
                }
            }
            if (!given) {
                this.say("[ Le trophée " + trophyCard + " (" + trophyCard.getTrophy() + ") n'a pas été distribué ]");
            }
        }
    }

    private void winner() {
        int best = -999;
        Player bestPlayer = null;
        for (Player player : Game.getInstance().getPlayers()) {
            this.say(player.getName() + " a marqué " + player.getScore() + " points");
            if (player.getScore() > best) {
                best = player.getScore();
                bestPlayer = player;
            }
        }
        this.say(bestPlayer.getName() + " a gagné !");
    }
}
