package main;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fileio.CardInput;

import java.util.ArrayList;

@JsonIgnoreProperties({"health", "attackDamage"})
public final class Environment extends CardInput {
    static final int PLAYER1ROW = 3;

    public Environment(final CardInput card) {
        this.name = card.getName();
        this.colors = card.getColors();
        this.mana = card.getMana();
        this.description = card.getDescription();
    }

    public Environment() {

    }

    /**
     *
     * @param gameTable -the map/table that stores the placed cards that a player
     *                  can use
     * @param index -back row of the current player
     */
    public void firestorm(final ArrayList<ArrayList<Minion>> gameTable,
                          final int index) {
        int i;
        boolean wasEliminated;
        ArrayList<Minion> rowCards = gameTable.get(index);
        for (i = 0; i < gameTable.get(index).size(); i++) {
            Minion minion = new Minion();
            minion.setAttackDamage(1);
            wasEliminated = rowCards.get(i).gotAttacked(minion, rowCards, i);
            if (wasEliminated) {
                i--;
            }
        }
    }

    /**
     *
     * @param rowcCards -the row of cards of the attacked player that needs to be
     *                  stunned
     */
    public void winterfell(final ArrayList<Minion> rowcCards) {
        for (int i = 0; i < rowcCards.size(); i++) {
            rowcCards.get(i).stunnedMinion = true;
        }
    }

    /**
     *
     * @param gameTable -the map/table that stores the placed cards that a player
     *                  can use
     * @param index -the back row of the current player
     */
    public void heartHound(final ArrayList<ArrayList<Minion>> gameTable,
                           final int index) {
        ArrayList<Minion> player = gameTable.get(PLAYER1ROW - index);
        ArrayList<Minion> playerAttacked = gameTable.get(index);
        int maxHealth = 0;

        for (int i = 0; i < playerAttacked.size(); i++) {
            if (maxHealth < playerAttacked.get(i).getHealth()) {
                maxHealth = i;
            }
        }

        player.add(playerAttacked.get(maxHealth));
        playerAttacked.remove(maxHealth);
    }
}
