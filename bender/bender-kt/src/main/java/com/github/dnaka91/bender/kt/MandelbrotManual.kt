package com.github.dnaka91.bender.kt

internal object MandelbrotManual {
    fun render(width: Int, height: Int): ByteArray {
        val pixels = ByteArray(width * height)
        val upperLeft = Pair(-1.2, 0.35)
        val lowerRight = Pair(-1.0, 0.2)

        for (row in 0 until height) {
            for (column in 0 until width) {
                val point = pxToPoint(width, height, column, row, upperLeft, lowerRight)

                pixels[row * width + column] = escape(point, 255).let { 255 - it }.toByte()
            }
        }

        return pixels
    }

    private fun escape(c: Pair<Double, Double>, limit: Int): Int {
        val cr = c.first
        val ci = c.second
        var zr = 0.0
        var zi = 0.0

        for (i in 0..limit) {
            val ac = zr * zr
            val bd = zi * zi
            val ad = zr * zi
            val bc = zi * zr

            zr = ac - bd + cr
            zi = ad + bc + ci

            if (zr * zr + zi * zi > 4.0) {
                return i
            }
        }

        return 255
    }

    private fun pxToPoint(
        bounds_w: Int,
        bounds_h: Int,
        x: Int,
        y: Int,
        upperLeft: Pair<Double, Double>,
        lowerRight: Pair<Double, Double>
    ): Pair<Double, Double> {
        val width = lowerRight.first - upperLeft.first
        val height = upperLeft.second - lowerRight.second

        return Pair(
            upperLeft.first + x.toDouble() * width / bounds_w.toDouble(),
            upperLeft.second - y.toDouble() * height / bounds_h.toDouble()
        )
    }
}