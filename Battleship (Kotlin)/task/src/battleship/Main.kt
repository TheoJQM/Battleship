package battleship

enum class State(state: String) {
    GOOD("Good"),
    DESTROYED("Destroyed")
}

enum class Symbol(val value: String, val message: String) {
    Empty("~",""),
    Full("O", ""),
    Missed("M", "You missed!"),
    Touched("X", "You hit a ship!")
}

class Cell() {
    var x = 0
    var y = 0
    var symbol = Symbol.Empty
    var hiddenSymbol = Symbol.Empty

    constructor(x: Int, y: Int, symbol: Symbol) : this() {
        this.x = x
        this.y = y
        this.hiddenSymbol = symbol
    }

    override fun toString() = this.symbol.value
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
            Cell(row, column, Symbol.Empty)
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
        play()
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
        return coordinate.split(" ").map { Cell(it.first().code - 65, it.drop(1).toInt() - 1, Symbol.Full ) }
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
                        if (board[coordinate.first + i][coordinate.second + j].symbol != Symbol.Empty) {
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
        boats[index].cells = coordinates.toList().map { Cell(it.first, it.second, Symbol.Full) }
        for (coordinate in coordinates) {
            board[coordinate.first][coordinate.second].symbol = Symbol.Full
            board[coordinate.first][coordinate.second].hiddenSymbol = Symbol.Full
        }
        println()
        printBoard()
    }



    private fun play() {
        var shotTaken = false
        println("The game starts!\n")
        hideBoats()
        var target = println("Take a shot!\n").run { readln() }
        while (!shotTaken) {
            if (shot(target)) {
                shotTaken = true
            } else {
                target = println("Try again:\n").run { readln() }
            }
        }
    }

    private fun shot(target: String): Boolean {
        val targetRegex = Regex("""[A-J]([1-9]|10)""")
        return when {
            !targetRegex.matches(target) -> {
                print("\nError: you entered the wrong coordinates!")
                false
            }
            else -> {
                val cell = createCoordinate(target).first()
                val symbol = board[cell.x][cell.y].hiddenSymbol
                 if (symbol == Symbol.Empty) {
                     board[cell.x][cell.y].symbol =  Symbol.Missed
                     board[cell.x][cell.y].hiddenSymbol =  Symbol.Missed
                 } else {
                     board[cell.x][cell.y].symbol =  Symbol.Touched
                     board[cell.x][cell.y].hiddenSymbol =  Symbol.Touched
                 }
                printBoard()
                println(board[cell.x][cell.y].symbol.message)
                println()
                showBoats()
                true
            }
        }
    }

    private fun hideBoats() {
        board.flatten().forEach { it.symbol = Symbol.Empty }
        printBoard()
    }

    private fun showBoats() {
        board.flatten().forEach { it.symbol = it.hiddenSymbol }
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