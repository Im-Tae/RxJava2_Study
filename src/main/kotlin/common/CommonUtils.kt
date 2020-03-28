package common

class CommonUtils {
    var ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun numberToAlphabet(x: Int) = ALPHABET[x % ALPHABET.length].toString()

    companion object {

        var startTime: Long? = null


        fun start() {
            startTime = System.currentTimeMillis()
        }

        fun sleep(millis: Long) {
            try {
                Thread.sleep(millis)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}