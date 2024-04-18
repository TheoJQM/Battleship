package battleship

class Player(val name: String) : GamerInterface {
    private val boats = listOf(
        Boat("Aircraft Carrier", 5), Boat("Battleship", 4), Boat("Submarine", 3),
        Boat("Cruiser", 3), Boat("Destroyer", 2)
    )

    val board = List(10) { row ->
        List(10) { column ->
            Cell(row, column, Symbol.Empty)
        }
    }

    init {
        println("$name, place your ships on the game field")
        printBoard()
        for (boat in boats) {
            var boatPlaced = false
            var coordinates = println("Enter the coordinates of the ${boat.name} (${boat.size} cells):\n").run { readln() }
            while (!boatPlaced) {
                val validCoordinate = checkCoordinates(coordinates, boat)
                if (validCoordinate) {
                    boatPlaced = true
                } else {
                    coordinates = println("Try again:\n").run { readln() }
                }
            }
        }

        hideBoats()
        clearScreen()
    }

    private fun checkCoordinates(coordinate: String, boat: Boat): Boolean {
        val coordinatedRegex = Regex("""[A-J]([1-9]|10) [A-J]([1-9]|10)""")
        val areCoordinateGood = coordinatedRegex.matches(coordinate.trim())

        val (startCell, endCell) = if (areCoordinateGood) createCoordinate(coordinate) else listOf(Cell(), Cell())

        return when {
            !areCoordinateGood -> {
                print("Error: coordinates must be between A1 and J10 and in the format \"$$ $$\". ").let { false }
            }

            startCell.x != endCell.x && startCell.y != endCell.y -> {
                print("Error: the boat can only be placed vertically or horizontally. ").let { false }
            }

            else -> {
                val boatCells = createBoatCoordinates(startCell, endCell)
                when {
                    !checkBoatCoordinateAreEmpty(boatCells) -> false
                    boatCells.size != boat.size -> {
                        print("Error: wrong length of the ${boat.name}!").let { false }
                    }

                    else -> placeBoat(boat, boatCells).let { true }
                }
            }
        }
    }

    private fun createBoatCoordinates(startCell: Cell, endCell: Cell): MutableList<Pair<Int, Int>> {
        return if (startCell.x == endCell.x) createBoat(startCell, endCell, true)
        else createBoat(startCell, endCell, false)
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

    private fun checkBoatCoordinateAreEmpty(coordinates: MutableList<Pair<Int, Int>>): Boolean {
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
        coordinates.forEach {
            board[it.first][it.second].symbol = Symbol.Full
            board[it.first][it.second].hiddenSymbol = Symbol.Full
        }
        printBoard()
    }

    private fun hideBoats() {
        board.flatten().forEach { it.symbol = Symbol.Empty }
    }

    fun updateBoatCells(cell: Cell): Boolean{
        val index = boats.indexOfFirst { boat -> boat.cells.any { it.x == cell.x && it.y == cell.y } }
        boats[index].cells.first { it.x == cell.x && it.y == cell.y }.hiddenSymbol = Symbol.Touched
        return checkBoatState(index)
    }

    private fun checkBoatState(index: Int): Boolean {
        val isDestroyed = boats[index].cells.all { it.hiddenSymbol == Symbol.Touched }
        val boatsAlive = boats.count { it.state == State.GOOD }

        return when {
            isDestroyed && boatsAlive == 1 -> true

            boats[index].state == State.GOOD && isDestroyed && boatsAlive > 1 -> {
                boats[index].state = State.DESTROYED
                println("You sank a ship!").let { false }
            }

            isDestroyed -> {
                boats[index].state = State.DESTROYED
                println(Symbol.Touched.message).let { false }
            }
            else -> println(Symbol.Touched.message).let { false }
        }
    }



    private fun printBoard() {
        println()
        showBoard(true)
        println()
    }

    fun showBoard(hidden: Boolean ) {
        println("  1 2 3 4 5 6 7 8 9 10")
        for (i in board.indices) {
            println("${(i + 65).toChar()} ${board[i].joinToString(" ") { 
                if (hidden) it.hiddenSymbol.value else it.symbol.value
            }}")
        }
    }
}