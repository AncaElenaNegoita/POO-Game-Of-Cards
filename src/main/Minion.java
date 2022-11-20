package main;

import fileio.CardInput;

import java.util.ArrayList;

public final class Minion extends CardInput {
    Boolean stunnedMinion = false;
    Boolean attackedAnotherCard = false;

    public Minion() { }
    public Minion(final CardInput card) {
        this.name = card.getName();
        this.attackDamage = card.getAttackDamage();
        this.colors = card.getColors();
        this.mana = card.getMana();
        this.description = card.getDescription();
        this.health = card.getHealth();
    }

    /**
     *
     * @param attacker -the card from the table selected to attack another card
     * @param rowCards -the row of cards where the attacked card is
     * @param index -the position (y) where the attacked card is on the game table
     * @return -returns if the attacked card was eliminated from the table
     */
    public boolean gotAttacked(final Minion attacker, final ArrayList<Minion> rowCards,
                               final int index) {
        health -= attacker.getAttackDamage();
        attacker.setAttackedAnotherCard(true);
        if (health <= 0) {
            rowCards.remove(index);
            return true;
        }
        return false;
    }

    public void setAttackedAnotherCard(final Boolean attackedAnotherCard) {
        this.attackedAnotherCard = attackedAnotherCard;
    }

    /**
     *
     * @param attacker -the card from the table selected to attack another card
     */
    public void theRipper(final Minion attacker) {
        attackDamage -= 2;
        if (attackDamage < 0) {
            attackDamage = 0;
        }
        attacker.setAttackedAnotherCard(true);
    }

    /**
     *
     * @param attacker -the card from the table selected to attack another card
     */
    public void miraj(final Minion attacker) {
        int copyHealth = health;
        health = attacker.getHealth();
        attacker.setHealth(copyHealth);
        attacker.setAttackedAnotherCard(true);
    }

    /**
     *
     * @param attacker -the card from the table selected to attack another card
     * @return -returns if the attacked card has 0 health in order to be eliminated
     */
    public Boolean theCursedOne(final Minion attacker) {
        int copyElem = health;
        health = attackDamage;
        attackDamage = copyElem;
        attacker.setAttackedAnotherCard(true);
        if (health == 0) {
            return true;
        }
        return false;
    }

    /**
     * function that gives a chosen card 2 more health
     */
    public void disciple() {
        health += 2;
        this.setAttackedAnotherCard(true);
    }
}
