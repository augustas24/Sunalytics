package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.utils

import com.mapbox.geojson.Point
import kotlin.math.abs

// Converts a point (from Mapbox) to string that displays the coordinates in DNS format.
// Unused, but kept because it theoretically could've been useful in the future.
fun Point.toDNS(
    northSpecifier: String = "N",
    southSpecifier: String = "S",
    eastSpecifier: String = "E",
    westSpecifier: String = "W",
    between: String = " "
) = buildString {
    val lat = latitude()
    append(toDNS(lat))
    when {
        lat > 0.0 -> append(northSpecifier)
        lat < 0.0 -> append(southSpecifier)
    }

    append(between)

    val lon = longitude()
    append(toDNS(lon))
    when {
        lon > 0.0 -> append(eastSpecifier)
        lon < 0.0 -> append(westSpecifier)
    }
}


private fun toDNS(decimalCoord: Double) = buildString {
    var coord = abs(decimalCoord)
    var mod = coord % 1
    var intPart = coord.toInt()

    val degrees = intPart
    append("$degrees°")

    coord = mod * 60
    mod = coord % 1
    intPart = coord.toInt()

    val minutes = intPart
    append("$minutes'")

    coord = mod * 60
    intPart = coord.toInt()

    val seconds = intPart
    append("$seconds\"")
}