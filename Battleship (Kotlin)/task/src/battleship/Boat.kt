package battleship

class Boat(val name: String, val size: Int) {
    var state = State.GOOD
    var cells = List(size) { Cell() }
}