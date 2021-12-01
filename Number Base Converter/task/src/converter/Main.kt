package converter

import java.math.BigDecimal
import java.math.BigInteger

private val digits = listOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
    'u', 'v', 'w', 'x', 'y', 'z'
)

fun main() = entryMenu()

private fun entryMenu() {
    var run = true

    do {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")

        val read = readLine()!!.split(" ").map { it.lowercase() }
        when {
            read[0] == "/exit" -> run = false
            read.size >= 2 -> convertMenu(read[0].toInt(), read[1].toInt())
            else -> println("Command not recognized")
        }
    } while (run)
}

private fun convertMenu(source: Int, target: Int) {
    var run = true

    do {
        println("Enter number in base $source to convert to base $target (To go back type /back)")

        when (val line = readLine()!!){
            "/back" -> run = false
            else -> {
                println("Conversion result: ${
                    (if (line.contains('.'))::changeBaseDec else ::changeBase)(line, source, target)
                }")
            }
        }
    } while (run)
}

private fun changeBase(number: String, from: Int, to: Int): String {
    return if (to == from) number
    else if (from == 10) changeToBase(number.toBigInteger(), to)
    else if (to == 10) changeFromBase(number, from).toString()
    else changeToBase(changeFromBase(number, from), to)
}

private fun changeBaseDec(number: String, from: Int, to: Int): String {
    return if (to == from) number
    else if (from == 10) changeToBaseDec(number.toBigDecimal(), to)
    else if (to == 10) changeFromBaseDec(number, from).toString()
    else changeToBaseDec(changeFromBaseDec(number, from), to)
}

private fun changeFromBaseDec(value: String, base: Int): BigDecimal {
    val baseBI = base.toBigDecimal()
    val intValue = changeFromBase(value.substringBefore('.'), base).toBigDecimal()
    var decValue = BigDecimal.ZERO
    var curr = BigDecimal.ONE.setScale(11)

    for (c in value.substringAfter('.')) {
        curr /= baseBI
        decValue += digits.indexOf(c).toBigDecimal() * curr
    }

    return intValue + decValue
}

private fun changeToBaseDec(value: BigDecimal, base: Int): String {
    val sb = StringBuilder(changeToBase(value.toBigInteger(), base)).append('.')
    val baseBI = base.toBigDecimal()
    var remaining = value.rem(BigDecimal.ONE)
    var curr = BigDecimal.ONE.setScale(11)
    val coef = IntArray(6)
    for (count in coef.indices) {
        curr /= baseBI
        val (digit, rem) = remaining.divideAndRemainder(curr)
        coef[count] = digit.toInt()
        remaining = rem
    }

    for (count in 0 until coef.size - 1) {
        sb.append(digits[coef[count]])
    }

    return sb.toString()
}

private fun changeFromBase(value: String, base: Int): BigInteger {
    if (value == "0") return BigInteger.ZERO
    if (base == 10) return value.toBigInteger()

    val baseBI = base.toBigInteger()
    var retValue = BigInteger.ZERO

    for (c in value) {
        retValue *= baseBI
        retValue += digits.indexOf(c).toBigInteger()
    }

    return retValue
}

private fun changeToBase(value: BigInteger, base: Int): String {
    if (value == BigInteger.ZERO) return "0"
    if (base == 10) return value.toString()

    val sb = StringBuilder()
    val baseBI = base.toBigInteger()
    var div = value
    while (div != BigInteger.ZERO) {
        val rem = div.rem(baseBI).toInt()
        div /= baseBI
        sb.append(digits[rem])
    }
    return sb.reverse().toString()
}
