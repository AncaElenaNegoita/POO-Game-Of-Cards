package main;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fileio.CardInput;

import java.util.ArrayList;

@JsonIgnoreProperties({"attackDamage"})

public final class Hero extends CardInput {
    Boolean attackedAnotherCard = false;

    public Hero(final CardInput card) {
        this.name = card.getName();
        this.colors = card.getColors();
        this.mana = card.getMana();
        this.description = card.getDescription();
        this.health = card.getHealth();
    }

    /**
     *
     * @param attacker -the card from the table of the current player that wants
     *                 to attack a hero
     */
    public void attackHero(final Minion attacker) {
        health -= attacker.getAttackDamage();
        attacker.setAttackedAnotherCard(true);
    }

    /**
     *
     * @param rowCards -the row of cards where the card with the biggest attack
     *                 damage needs to be eliminated
     */
    public void lordRoyce(final ArrayList<Minion> rowCards) {
        int attackDamageY = 0;
        for (int i = 0; i < rowCards.size(); i++) {
            if (attackDamageY < rowCards.get(i).getAttackDamage()) {
                attackDamageY = i;
            }
        }
        rowCards.get(attackDamageY).stunnedMinion = true;
        attackedAnotherCard = true;
    }

    /**
     *
     * @param rowCards -the row of cards where the card with the biggest healt needs
     *                 to be eliminated
     */
    public void empressThorina(final ArrayList<Minion> rowCards) {
        int healthY = 0;
        for (int i = 0; i < rowCards.size(); i++) {
            if (healthY < rowCards.get(i).getHealth()) {
                healthY = i;
            }
        }
        rowCards.remove(healthY);
        attackedAnotherCard = true;
    }

    /**
     *
     * @param rowCards -the row of cards where all the cards receive +1 health
     */
    public void kingMudface(final ArrayList<Minion> rowCards) {
        for (int i = 0; i < rowCards.size(); i++) {
            int health = rowCards.get(i).getHealth();
            rowCards.get(i).setHealth(health + 1);
        }
        attackedAnotherCard = true;
    }

    /**
     *
     * @param rowCards -the row of cards where all cards receive +1 attack damage
     */
    public void generalKocioraw(final ArrayList<Minion> rowCards) {
        for (int i = 0; i < rowCards.size(); i++) {
            int attackDamage = rowCards.get(i).getAttackDamage();
            rowCards.get(i).setAttackDamage(attackDamage + 1);
        }
        attackedAnotherCard = true;
    }
}
