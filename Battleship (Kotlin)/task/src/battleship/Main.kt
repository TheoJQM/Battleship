package battleship

enum class State(state: String) {
    GOOD("Good"),
    DESTROYED("Destroyed")
}

class Cell() {
    var x = 0
    var y = 0
    private var symbol = "~"

    constructor(x: Int, y: Int, symbol: String) : this() {
        this.x = x
        this.y = y
        this.symbol = symbol
    }

    override fun toString(): String {
        return this.symbol
    }
}

class Boat(val name: String, val size: Int) {
    private var state = State.GOOD

    val cells = List(size) { Cell() }
}

class Game {
    private val boats = listOf(Boat("Aircraft Carrier", 5),Boat("Battleship", 4),Boat("Cruiser", 3),
        Boat("Submarine", 3),Boat("Destroyer", 2)
    )

    private val board = List(10) { row ->
        List(10) { column ->
            Cell(row, column, "~")
        }
    }

    fun play() {
        printBoard()
        placeBoats()
    }

    private fun placeBoats() {
        for (boat in boats) {
            var boatPlaced = false
            var coordinates = println("Enter the coordinates of the ${boat.name} (${boat.size} cells):").run { readln() }
            while (!boatPlaced) {
                val validCoordinate = checkCoordinates(coordinates, boat)
                 if (validCoordinate) {
                     boatPlaced = validCoordinate
                 } else {
                     coordinates = println("Try again:").run { readln() }
                 }
            }

        }
    }

    private fun checkCoordinates(coordinate: String, boat: Boat): Boolean {
        val coordinatedRegex = Regex("""[A-J]([1-9|10]) [A-J]([1-9|10])""")
        val areCoordinateGood = coordinatedRegex.matches(coordinate.trim())

        val (startCell, endCell) = if (areCoordinateGood) createCoordinate(coordinate) else listOf(Cell(), Cell())

        return when {
            !areCoordinateGood -> {
                print("Error: coordinates must be between A1 and J10 and in the format \"$$ $$\". ")
                false
            }
            startCell.x != endCell.x && startCell.y != endCell.y -> {
                print("Error: the boat can only be placed vertically or horizontally. ")
                false
            }
            else -> {
                createBoatCoordinates(startCell, endCell)
                true
            }
        }
    }

    private fun createCoordinate(coordinate: String): List<Cell> {
        return coordinate.split(" ").map { Cell(it.first().code - 65, it.takeLast(1).toInt(), "O" ) }
    }

    private fun createBoatCoordinates(startCell: Cell, endCell: Cell) {
        if (startCell.x == endCell.x) {
            horizontalBoat(startCell, endCell)
        } else {
            println()
        }


    }

    private fun horizontalBoat(startCell: Cell, endCell: Cell): MutableList<Pair<Int, Int>> {
        val listCellCoordinates = mutableListOf<Pair<Int, Int>>()
        val yMin = minOf(startCell.y, endCell.y)
        val yMax = maxOf(startCell.y, endCell.y)
        val x = startCell.x

        for (y in yMin..yMax) listCellCoordinates.add(Pair(x, y))

        return listCellCoordinates
    }

    private fun verticalBoat(startCell: Cell, endCell: Cell): MutableList<Pair<Int, Int>> {
        val listCellCoordinates = mutableListOf<Pair<Int, Int>>()
        val xMin = minOf(startCell.x, endCell.x)
        val xMax = maxOf(startCell.x, endCell.x)
        val y = startCell.y

        for (x in xMin..xMax) listCellCoordinates.add(Pair(x, y))

        return listCellCoordinates
    }

    private fun printBoard() {
        println("  1 2 3 4 5 6 7 8 9 10")
        for (i in board.indices) {
            println("${(i + 65).toChar()} ${board[i].joinToString(" ")}")
        }
    }
}

fun main() {
    val myGame = Game()
    myGame.play()
}