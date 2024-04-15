package battleship

enum class State(state: String) {
    GOOD("Good"),
    DESTROYED("Destroyed")
}

class Cell() {
    var x = 0
    var y = 0
    var symbol = "~"

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

    var cells = List(size) { Cell() }
}

class Game {
    private val boats = listOf(Boat("Aircraft Carrier", 5),Boat("Battleship", 4),Boat("Submarine", 3),
        Boat("Cruiser", 3),Boat("Destroyer", 2)
    )

    private val board = List(10) { row ->
        List(10) { column ->
            Cell(row, column, "~")
        }
    }

    init {
        printBoard()
        for (boat in boats) {
            var boatPlaced = false
            var coordinates = println("Enter the coordinates of the ${boat.name} (${boat.size} cells):\n").run { readln() }
            println()
            while (!boatPlaced) {
                val validCoordinate = checkCoordinates(coordinates, boat)
                if (validCoordinate) {
                    boatPlaced = true
                } else {
                    coordinates = println("Try again:\n").run { readln() }
                }
            }
        }

    }

    private fun checkCoordinates(coordinate: String, boat: Boat): Boolean {
        val coordinatedRegex = Regex("""[A-J]([1-9]|10) [A-J]([1-9]|10)""")
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
                val boatCells = createBoatCoordinates(startCell, endCell)
                when {
                    !checkBoatCoordinateAreEmpty(boatCells) -> false
                    boatCells.size != boat.size -> {
                        print("Error: wrong length of the ${boat.name}!")
                        false
                    }
                    else -> {
                        placeBoat(boat, boatCells)
                        true
                    }
                }
            }
        }
    }

    private fun createCoordinate(coordinate: String): List<Cell> {
        return coordinate.split(" ").map { Cell(it.first().code - 65, it.drop(1).toInt() - 1, "O" ) }
    }

    private fun createBoatCoordinates(startCell: Cell, endCell: Cell): MutableList<Pair<Int, Int>> {
        return if (startCell.x == endCell.x) createBoat(startCell, endCell, true) else createBoat(startCell, endCell, false)
    }

    private fun createBoat(startCell: Cell, endCell: Cell, isHorizontal: Boolean): MutableList<Pair<Int, Int>> {
        val listCellCoordinates = mutableListOf<Pair<Int, Int>>()
        val min = if (isHorizontal) minOf(startCell.y, endCell.y) else minOf(startCell.x, endCell.x)
        val max = if (isHorizontal) maxOf(startCell.y, endCell.y) else maxOf(startCell.x, endCell.x)
        val startX = startCell.x
        val startY = startCell.y

        if (isHorizontal) {
            for (y in min..max) listCellCoordinates.add(Pair(startX, y))
        } else {
            for (x in min..max) listCellCoordinates.add(Pair(x, startY))
        }

        return listCellCoordinates
    }

    private fun checkBoatCoordinateAreEmpty(coordinates: MutableList<Pair<Int, Int>> ): Boolean {
        for (coordinate in coordinates) {
            for (i in -1..1) {
                for (j in -1..1) {
                    try {
                        if (board[coordinate.first + i][coordinate.second + j].symbol != "~") {
                            print("Error: the boat can’t be on or near an another boat!")
                            return false
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }
        return true
    }

    private fun placeBoat(boat: Boat, coordinates: MutableList<Pair<Int, Int>>) {
        val index = boats.indexOf(boat)
        boats[index].cells = coordinates.toList().map { Cell(it.first, it.second, "O") }
        for (coordinate in coordinates) {
            board[coordinate.first][coordinate.second].symbol = "O"
        }
        println()
        printBoard()
    }



    private fun printBoard() {
        println("  1 2 3 4 5 6 7 8 9 10")
        for (i in board.indices) {
            println("${(i + 65).toChar()} ${board[i].joinToString(" ")}")
        }
        println()
    }
}

fun main() {
    Game()
}