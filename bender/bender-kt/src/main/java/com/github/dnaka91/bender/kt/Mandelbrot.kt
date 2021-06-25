package com.github.dnaka91.bender.kt

import org.apache.commons.math3.complex.Complex
import kotlin.math.pow

internal object Mandelbrot {
    fun render(width: Int, height: Int): ByteArray {
        val pixels = ByteArray(width * height)
        val upperLeft = Complex(-1.2, 0.35)
        val lowerRight = Complex(-1.0, 0.2)

        for (row in 0 until height) {
            for (column in 0 until width) {
                val point = pxToPoint(width, height, column, row, upperLeft, lowerRight)

                pixels[row * width + column] = escape(point, 255).let { 255 - it }.toByte()
            }
        }

        return pixels
    }

    private fun escape(c: Complex, limit: Int): Int {
        var z = Complex(0.0, 0.0)
        for (i in 0..limit) {
            z = z.multiply(z).add(c)
            if (z.real.pow(2) + z.imaginary.pow(2) > 4.0) {
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
        upperLeft: Complex,
        lowerRight: Complex
    ): Complex {
        val width = lowerRight.real - upperLeft.real
        val height = upperLeft.imaginary - lowerRight.imaginary

        return Complex(
            upperLeft.real + x.toDouble() * width / bounds_w.toDouble(),
            upperLeft.imaginary - y.toDouble() * height / bounds_h.toDouble()
        )
    }
}