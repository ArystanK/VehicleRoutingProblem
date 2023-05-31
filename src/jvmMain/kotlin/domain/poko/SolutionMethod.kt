package domain.poko

enum class SolutionMethod {
    LP,
    GA,
    GNN
}

fun String.toSolutionMethod(): SolutionMethod {
    return when (this) {
        "LP" -> SolutionMethod.LP
        "GA" -> SolutionMethod.GA
        "GNN" -> SolutionMethod.GNN
        else -> throw IllegalArgumentException("")
    }
}