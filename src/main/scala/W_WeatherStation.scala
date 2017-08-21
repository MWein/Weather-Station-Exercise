import com.cibo.weather.WeatherStation

/**
  * Created by Mike Weinberg on 4/22/2017.
  */


//
case class LatLongException(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)


// Custom exception
case class LatLongException(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)


class W_WeatherStation(tMap: Array[Array[Float]], nRows: Int, nCols: Int, ULXMap: Double, ULYMap: Double, XDim: Double, YDim: Double, noData: Double) extends WeatherStation {


  /*** CONSTRUCTOR
    */
  val tempMap: Array[Array[Float]] = tMap
  val NROWS: Int = nRows
  val NCOLS: Double = nCols
  val ULXMAP: Double = ULXMap
  val ULYMAP: Double = ULYMap
  val XDIM: Double = XDim
  val YDIM: Double = YDim
  val NODATA: Double = noData

  // Calculate the lower right bounds of the data
  val LRXMAP: Double = ULXMAP + XDIM * nCols
  val LRYMAP: Double = ULYMAP + YDIM * nRows

  /** CONSTRUCTOR
    ***/




  /**
    * Verifies that the passed lat-long coordinate is within the bounds of the data
    *
    * @param Latitude The latitude
    * @param longitude The longitude
    * @return
    */
  def checkBounds(latitude: Double, longitude: Double): Boolean = {
    // Initially set the return value for true
    var withinBounds = true

    // Check longitude
    if (longitude > LRXMAP || longitude < ULXMAP) withinBounds = false

    // Check latitude
    if (latitude > ULYMAP || latitude < LRXMAP) withinBounds = false

    // Return the result
    withinBounds
  }


  /**
    * Converts a given longitude to its corresponding X in the 2D array
    *
    * Assuming Longitude is always a negative number
    * "For this study, we are only concerned with data in the Continental US."
    *
    * @param longitude The longitude
    * @return
    */
  def convertLongitudeToX(longitude: Double): Int = {
    // Assuming Longitude is always a negative number
    // "For this study, we are only concerned with data in the Continental US."
    math.floor(((ULXMAP - longitude) * -1) / XDIM).toInt
  }


  /**
    * Converts a given latitude to its corresponding Y in the 2D array
    *
    * Assuming Latitude is always a positive number
    * "For this study, we are only concerned with data in the Continental US."
    *
    * @param latitude The latitude
    * @return
    */
  def convertLatitudeToY(latitude: Double): Int = {
    math.floor((ULYMAP - latitude) / YDIM).toInt
  }




  /**
    * Provides the temperature in Celsius, if available.
    *
    * If the location requested is not within the bounds of the weather station, an Exception is thrown.
    *
    * @param latitude  The latitude.
    * @param longitude The longitude.
    * @return the temperature if one is available, or None if the temperature is missing.
    */
  override def temperature(latitude: Double, longitude: Double): Option[Double] = {
    // Make sure the point is within bounds
    if (!checkBounds(latitude, longitude)) throw LatLongException("Lat/Long Out of Bounds")

    // Calculate position X
    val posX = convertLongitudeToX(longitude)

    // Calculate position Y
    val posY = convertLatitudeToY(latitude)

    // Return the values from the 2D array
    Some(tempMap(posX)(posY))
  }


  /**
    * Calculates the average temperature in the rectangular region specified.
    *
    * If any portion of the area is not within the bounds of the weather station, an Exception is thrown.
    *
    * @param lat0 The Northern latitude bound.
    * @param lng0 The Western longitude bound.
    * @param lat1 The Southern latitude bound.
    * @param lng1 The Eastern longitude bound.
    * @return
    */
  override def averageTemperature(lat0: Double, lon0: Double, lat1: Double, lon1: Double): Option[Double] = {

    // Make sure lat0 lon0 is above and to the left of lat1 lon1
    if (lat0 < lat1 || lon0 > lon1) throw LatLongException("Invalid Entry. lat0/long0 must be North-West of lat1/long1")

    // Make sure both points are within bounds
    if (!checkBounds(lat0, lon0) || !checkBounds(lat1, lon1)) throw LatLongException("Lat/Long Out of Bounds")

    // Determine the bounds in array position
    val minX = convertLongitudeToX(lon0)
    val maxX = convertLongitudeToX(lon1)
    val minY = convertLatitudeToY(lat0)
    val maxY = convertLatitudeToY(lat1)

    // Trackers for the currently accessed value in the 2D array
    var curX = minX
    var curY = minY

    // Average temperature variable
    var averageTemp = 0.0

    // Calculate number of values for loop and later calculation
    val numOfValues = ((maxX - minX) + 1) * ((maxY - minY) + 1)

    // Counter for every NoData placeholder encountered
    var noValueCount = 0

    for (_ <- 0 until numOfValues) {

      // Value of the currently accessed data cell
      var currentTemp = tempMap(curX)(curY)

      // If the current data cell yields a no data placeholder,
      // Set currentTemp to zero and increment noValueCount to ignore it
      if (currentTemp == NODATA) {
        currentTemp = 0
        noValueCount += 1
      }

      // Add the current temperature to the current average
      averageTemp += currentTemp

      // Increment current X position
      curX += 1

      // If we're out of bounds, move to the beginning of the next row
      if (curX > maxX) {
        curX = minX
        curY += 1
      }

    }

    // Calculate the average temperature
    // Divide the total temperature by the number of values
    // NoData should not factor in
    averageTemp = averageTemp / (numOfValues - noValueCount)

    // And send it
    Some(averageTemp)
  }
}
