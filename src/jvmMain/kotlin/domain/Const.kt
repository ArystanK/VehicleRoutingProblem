package domain

import center.sciprog.maps.compose.WebMercatorSpace.Rectangle
import center.sciprog.maps.coordinates.Gmc
import space.kscience.kmath.geometry.Degrees


val AstanaArea = Rectangle(
    Gmc(Degrees(51.28193274061607), Degrees(71.19865356830648)),
    Gmc(Degrees(50.996389716773805), Degrees(71.65595628500706))
)
const val numberOfBusStopsInAstana = 320