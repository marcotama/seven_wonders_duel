package com.aigamelabs.myfish.utils.printers

import com.aigamelabs.myfish.utils.HexOrientation

class LargePointyAsciiHexPrinter : AsciiHexPrinter() {

    private val width = 14
    private val height = 9
    private val sideWidth = 6  // Size from center to left/right border
    private val sideHeight = 3 // Size from top to left/right border
    private val bordersLength = 2 // Size of the combined left/right borders

    override fun getHexOrientation(): HexOrientation {
        return HexOrientation.POINTY
    }


    override fun getHex(line1: String, line2: String,line3: String, fillerChar: Char): String {
        var line1 = line1
        var line2 = line2
        var line3 = line3
        var hex = TEMPLATE

        line1 = restrictToLength(line1, 7)
        line2 = restrictToLength(line2, 7)
        line3 = restrictToLength(line3, 7)

        hex = hex.replace("XXXXXXX", line1)
        hex = hex.replace("YYYYYYY", line2)
        hex = hex.replace("ZZZZZZZ", line3)

        return hex.replace('#', fillerChar)
    }

    override fun mapHexCoordsToCharCoords(q: Int, r: Int): IntArray {
        val result = IntArray(2)
        result[0] = (width - bordersLength) * q + r % 2 * (height - sideHeight)
        result[1] = (height - sideHeight) * r

        return result
    }

    override fun getMapSizeInChars(hexWidth: Int, hexHeight: Int): IntArray {
        val widthInChars = hexWidth * width + sideWidth
        val heightInChars = hexHeight * (height - sideHeight) + sideHeight
        return intArrayOf(widthInChars, heightInChars)
    }

    companion object {

        const val TEMPLATE = (
                "     /#\\     \n"  // 0 - 13

                        + "   /# # #\\   \n"
                        + " /# # # # #\\ \n"
                        + "|# XXXXXXX #|\n"
                        + "|# YYYYYYY #|\n"
                        + "|# ZZZZZZZ #|\n"
                        + " \\# # # # #/ \n"
                        + "   \\# # #/   \n"
                        + "     \\#/     \n")
    }
}
