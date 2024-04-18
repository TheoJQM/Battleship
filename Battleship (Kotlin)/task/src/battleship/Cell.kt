package battleship

enum class State {
    GOOD,
    DESTROYED
}

enum class Symbol(val value: String, val message: String) {
    Empty("~", ""),
    Full("O", ""),
    Missed("M", "\nYou missed!"),
    Touched("X", "\nYou hit a ship!")

}

class Cell() {
    var x = 0
    var y = 0
    var symbol = Symbol.Empty
    var hiddenSymbol = Symbol.Empty

    constructor(x: Int, y: Int, symbol: Symbol) : this() {
        this.x = x
        this.y = y
        this.symbol = symbol
        this.hiddenSymbol = symbol
    }

    override fun toString() = this.symbol.value
}