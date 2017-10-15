package com.aigamelabs.swduel.enums

/**
 * Enumerates card groups. A card group is identified by the color behind the card or by its size (for wonders).
 * A card group does not reflect decks used during the game (e.g., third age and guilds card both go make up the third
 * age deck, there is a burned cards deck, etc)
 */
enum class CardGroup {
    FIRST_AGE,
    SECOND_AGE,
    THIRD_AGE,
    GUILDS,
    WONDERS
}