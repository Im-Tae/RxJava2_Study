package common

class CommonUtils {
    private var ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun numberToAlphabet(x: Int) = ALPHABET[x % ALPHABET.length].toString()

    companion object {

        var startTime: Long? = null
        var GITHUB_ROOT = "https://raw.githubusercontent.com/Im-Tae/RxJava2_Study/master"


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