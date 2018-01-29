package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.CardGroup

class FaceDownCard(override val cardGroup: CardGroup) : CardPlaceholder(cardGroup) {
    override fun toString(): String {
        return "Face down card"
    }
}