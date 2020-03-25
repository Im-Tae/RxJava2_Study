package common

class Shape {
    fun getShape(obj: String): String {
        if (obj == "") return "NO-SHAPE"
        if (obj.endsWith("-H")) return "HEXAGON"
        if (obj.endsWith("-O")) return "OCTAGON"
        if (obj.endsWith("-R")) return "RECTANGLE"
        if (obj.endsWith("-T")) return "TRIANGLE"
        if (obj.endsWith("◇")) return "DIAMOND"
        return "BALL"
    }
}