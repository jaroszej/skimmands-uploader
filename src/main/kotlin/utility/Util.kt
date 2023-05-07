package utility

class Util {
    companion object {
        fun minutesToMS(minutes: Int): Long {
            return minutes.toLong() * 60000

        }
    }
}
