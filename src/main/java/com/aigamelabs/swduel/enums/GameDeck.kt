package com.aigamelabs.swduel.enums

/**
 * Enumerates decks used in the game. A deck is a stack of cards that players draw from during the game.
 * A game deck does not identify a type of cards (e.g., the third age deck is made both from cards of the third age type
 * and cards of the guilds type - I know, poor naming!)
 */
enum class GameDeck {
    FIRST_AGE,
    SECOND_AGE,
    THIRD_AGE,
    BURNED,
    WONDERS_1,
    WONDERS_2
}