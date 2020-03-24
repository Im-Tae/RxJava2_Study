package common

class CommonUtils {
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