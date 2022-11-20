// @CopyRight Negoita Anca-Elena, 321CA

# Tema POO  - GwentStone

<div align="center"><img src="https://tenor.com/view/witcher3-gif-9340436.gif" width="500px"></div>

#### Assignment Link: [https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/tema](https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/tema)


## Skel Structure

* src/
  * checker/ - checker files
  * fileio/ - contains classes used to read data from the json files
  * main/
      * Main - the Main class runs the checker on your implementation. Add the entry point to your implementation in it. Run Main to test your implementation from the IDE or from command line.
      * Test - run the main method from Test class with the name of the input file from the command line and the result will be written
        to the out.txt file. Thus, you can compare this result with ref.
* input/ - contains the tests in JSON format
* ref/ - contains all reference output for the tests in JSON format

## Tests

1. test01_game_start - 3p
2. test02_place_card - 4p
3. test03_place_card_invalid - 4p
4. test04_use_env_card - 4p
5. test05_use_env_card_invalid - 4p
6. test06_attack_card - 4p
7. test07_attack_card_invalid - 4p
8. test08_use_card_ability - 4p
9. test09_use_card_ability_invalid -4p
10. test10_attack_hero - 4p
11. test11_attack_hero_invalid - 4p
12. test12_use_hero_ability_1 - 4p
13. test13_use_hero_ability_2 - 4p
14. test14_use_hero_ability_invalid_1 - 4p
15. test15_use_hero_ability_invalid_2 - 4p
16. test16_multiple_games_valid - 5p
17. test17_multiple_games_invalid - 6p
18. test18_big_game - 10p


<div align="center"><img src="https://tenor.com/view/homework-time-gif-24854817.gif" width="500px"></div>

## The Goal

The purpose of this project is to deepen the basic knowledge in Java through implementation
and the use of methods and classes necessary for each action.

A game contains several commands with different conditions depending on the current player and
the desired outcome. If a given command has conditions that cannot be fulfilled, then it
generates a specific error depending on the case. If it doesn't find any errors in the end,
it puts in the output node the position of a card, all cards on the game table, or nothing at
all. The goal is to win the game by attacking the enemy's hero and, in order to do so, it needs
a lot of thinking and strategies.

For example, the function "cardUsesAttack" is used by a player to attack with a card on the game 
table another card that is from the enemy. The coordinates are verified in order to really attack
the enemy card, and after that it is verified if the attacked card is a Tank (it must be situated
in the front row), or it is a Minion and there are no Tanks on the table. After that, if the player
has enough mana, it attacks the card and lowers its health. In this example, if it attacks the 
card, nothing will be shown in the output.

After a hero dies, the output displays a message where it specifies who won. In order to win the
game, the enemy's hero needs to be killed(have 0 health) and this can be done by attacking him
with the cards that are placed on the game table. A new game can be started and all the fields 
are reset.
