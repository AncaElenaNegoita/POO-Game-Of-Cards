package main;

import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
    ArrayList<CardInput> deck = new ArrayList<>();
    Hero hero;
    int mana;
    ArrayList<CardInput> handCards = new ArrayList<>();

    public Player(int playerIndex, int deckIndex, Input input, GameInput game) {
        mana = 1;
        ArrayList<CardInput> deckCopy = new ArrayList<>();
        if (playerIndex == 1) {
            deckCopy = input.getPlayerOneDecks().getDecks().get(deckIndex);
            hero = new Hero(game.getStartGame().getPlayerOneHero());
            hero.setHealth(30);
        } else {
            deckCopy = input.getPlayerTwoDecks().getDecks().get(deckIndex);
            hero = new Hero(game.getStartGame().getPlayerTwoHero());
            hero.setHealth(30);
        }

        for (CardInput card: deckCopy) {
            if (card.getHealth() == 0)
                deck.add(new Environment(card));
            else
                deck.add(new Minion(card));
        }
        Collections.shuffle(deck, new Random(game.getStartGame().getShuffleSeed()));
        handCards.add(deck.get(0));
        deck.remove(0);
    }

    public ArrayList<Environment> handEnvironmentCards() {
        ArrayList<Environment> copyEnvironment = new ArrayList<>();
        for (CardInput card : handCards)
            if (card.getHealth() == 0)
                copyEnvironment.add((Environment)card);
        return copyEnvironment;
    }
}
