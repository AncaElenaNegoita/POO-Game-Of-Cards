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
     * @param attacker
     * @param rowCards
     * @param index
     * @return
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
     * @param attacker
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
     * @param attacker
     */
    public void miraj(final Minion attacker) {
        int copyHealth = health;
        health = attacker.getHealth();
        attacker.setHealth(copyHealth);
        attacker.setAttackedAnotherCard(true);
    }

    /**
     *
     * @param attacker
     * @return
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
     *
     */
    public void disciple() {
        health += 2;
        this.setAttackedAnotherCard(true);
    }
}
