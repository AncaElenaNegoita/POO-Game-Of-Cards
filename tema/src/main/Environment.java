package main;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fileio.CardInput;

import java.util.ArrayList;

@JsonIgnoreProperties({"health", "attackDamage"})
public class Environment extends CardInput {
    public Environment(CardInput card) {
        this.name = card.getName();
        this.colors = card.getColors();
        this.mana = card.getMana();
        this.description = card.getDescription();
    }

    public Environment() {

    }

    public void firestorm(ArrayList<ArrayList<Minion>> gameTable, int index) {
        int i;
        boolean wasEliminated;
        ArrayList<Minion> rowCards = gameTable.get(index);
        for (i = 0; i < gameTable.get(index).size(); i++) {
            Minion minion = new Minion();
            minion.setAttackDamage(1);
            wasEliminated = rowCards.get(i).gotAttacked(minion, rowCards, i);
            if (wasEliminated)
                i--;
        }
    }

    public void winterfell(ArrayList<Minion> rowcCards) {
        for (int i = 0; i < rowcCards.size(); i++) {
            rowcCards.get(i).stunnedMinion = true;
        }
    }

    public void heartHound(int cardIndex, ArrayList<ArrayList<Minion>> gameTable, int index) {
        ArrayList<Minion> player = gameTable.get(3 - index);
        ArrayList<Minion> playerAttacked = gameTable.get(index);
        int maxHealth = 0;

        for (int i = 0; i < playerAttacked.size(); i++)
            if (maxHealth < playerAttacked.get(i).getHealth())
                maxHealth = i;

        player.add(playerAttacked.get(maxHealth));
        playerAttacked.remove(maxHealth);
    }
}
