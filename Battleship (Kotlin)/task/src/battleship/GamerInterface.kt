package battleship

interface GamerInterface {
    fun createCoordinate(coordinate: String): List<Cell> {
        return coordinate.split(" ").map { Cell(it.first().code - 65, it.drop(1).toInt() - 1, Symbol.Full) }
    }

    fun clearScreen() {
        println("Press Enter and pass the move to another player")
        if (System.`in`.read() != -1) repeat(10) { println("\u001B[0m" )}
    }
}