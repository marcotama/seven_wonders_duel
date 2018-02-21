Game and MCTS-based AIs
===

This is a Kotlin implementation of the board game "Seven Wonders Duel", along with some automatic players based on Monte Carlo Tree Search.
The repository contains the complete mechanics of the game, to our best understanding, and a variety of bots that can play the game more or less intelligently.

Motivation
---
This is part of a research project in the context of Dynamic Difficulty Adjustment. One of the bots, in fact, implements a technique recently proposed by us in a [CIG paper](http://www.cig2017.com/wp-content/uploads/2017/08/paper_73.pdf) and more extensively described in this [thesis](https://researchbank.rmit.edu.au/view/rmit:162286).
We wished to study this technique in different domains, so we turned to board games, which differ from the fighting game originally used.

Playability
---
At this time, the game is not easy to play by humans, as no graphical user interface (GUI) has been implemented yet (feel free to contact us if you would like to contribute a GUI). However, a simple command-line interface (CLI) allows human players to play.
While technically it is possible to play solely with the CLI, we recommend that you setup the physical game on your desk as well, as no information is shown about the cards other than their name.

How to use
---
The software takes parameters via the command-line:
*   -P1 and -P2, followed by one of MCTS, MCTS_Civilian, MCTS_Science, MCTS_Military, DDA, Human, Random, sets the controller for the two players;
*   -L followed by a path on the file system specifies where the logs of the game are to be saved;
*   \[optional\] -S followed by the location of a JSON file, specifies a file with a game state to be used as initial game state.

Available controllers are:
*   MCTS: just aims at winning, using Monte Carlo Tree Search running for a certain amount of seconds on all but one of your CPUs;
*   MCTS_Civilian: aims at winning via civilian victory (i.e., all cards are drawn but no player achieved neither military nor science supremacy)
*   MCTS_Science: aims at winning via science supremacy;
*   MCTS_Military: aims at winning via military supremacy;
*   DDA: always chooses the action that has a chance of victory closest to 50%;
*   Human: queries a player via command-line for actions to take (can optionally run MCTS for the player and show the chance of victory for each action)
*   Random: chooses its actions at random.

Example:
> java com.aigamelabs.swduel.Main -P1 MCTS_Military -P2 Random -L /home/marco/seven_wonders_duel_logs/

The software saves log files for each game:
*   <game_time>_game.json stores a JSON array of alternating game states (in the form of JSON objects) and decisions (in the form of strings)
*   <game_time>_game.log stores the options and choices of both players in a human-readable format

MCTS-based bots also save log files:
*   <game_time>\_player\_<bot_name>.log: stores information about how many times UCT is run for each action as well as errors;
*   <game_time>\_player\_<bot_name>.json: stores the result of MCTS analysis for every decision; namely, the chance of victory for every action as per MCTS assessment.

To create a JSON file to load as a starting game state, the best way is to open the JSON log of a game and copy-paste the state you are interested in in a new JSON file.

References
---
*   S. Demediuk, M. Tamassia, W. L. Raffe, F. Zambetta, X. Li, and F. F. Mueller.
Monte carlo tree search based algorithms for dynamic difficulty adjustment. In
Computational Intelligence and Games (CIG), 2017 IEEE Conference on. IEEE, 2017.
*   M. Tamassia. Artificial intelligence techniques towards adaptive digital games, 2017.


TODOs
---
*   GUIs: this would be a great plus for obvious reasons
*   Other games: the code provides interfaces to implement additional games for which MCTS bots can be easily written by reusing code
*   Tests: our test coverage is far from complete