package battleship

class Game : GamerInterface {
    private var isGameFinished = false
    private val players = Pair(Player("Player 1"), Player("Player 2"))
    private var currentPlayer = players.first
    private var enemy = players.second
    private lateinit var winner: String



    fun play() {
        while (!isGameFinished) {
            enemy.showBoard(false)
            println("---------------------")
            currentPlayer.showBoard(true)

            println("\n${currentPlayer.name}, it's your turn:\n")
            var target = readln()
            while (!shot(target)) target = readln()
        }
        println("$winner sank the last ship. $winner won. Congratulations!")
    }

    private fun shot(target: String): Boolean {
        val targetRegex = Regex("""[A-J]([1-9]|10)""")
        return when {
            !targetRegex.matches(target) -> {
                println("\nError: you entered the wrong coordinates!Try again:\n").let { false }
            }

            else -> {
                val cell = createCoordinate(target).first()
                val symbol = enemy.board[cell.x][cell.y].hiddenSymbol
                if (symbol == Symbol.Empty || symbol == Symbol.Missed) {
                    enemy.board[cell.x][cell.y].symbol = Symbol.Missed
                    enemy.board[cell.x][cell.y].hiddenSymbol = Symbol.Missed
                    println(enemy.board[cell.x][cell.y].symbol.message)
                    haveWinner(false)
                } else {
                    enemy.board[cell.x][cell.y].symbol = Symbol.Touched
                    enemy.board[cell.x][cell.y].hiddenSymbol = Symbol.Touched
                    val gameOver = enemy.updateBoatCells(enemy.board[cell.x][cell.y])
                    haveWinner(gameOver)
                }
                changeRole().let { true }
            }
        }
    }

    private fun changeRole() {
        currentPlayer = enemy.also { enemy = currentPlayer }
    }

    private fun haveWinner(gameOver: Boolean) {
        if (gameOver) {
            isGameFinished = true
            winner = currentPlayer.name
        } else {
            clearScreen()
        }
    }
}

fun main() {
    Game().play()
}