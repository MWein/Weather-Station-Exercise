import java.io.{BufferedInputStream, FileInputStream, FileNotFoundException}
import java.nio.ByteBuffer

import com.cibo.weather.{WeatherStation, WeatherStationFactory}
import java.nio.file.{Files, Path}

import scala.io.Source

/**
  * Created by Mike Weinberg on 4/22/2017.
  */
object W_WeatherStationFactory extends WeatherStationFactory {
  /**
    * Constructs WeatherStations that serve the provided data.
    *
    * @param headerFile Path of a PRISM header file `*.hdr`.
    * @param dataFile   Path of a corresponding data file `*.bil`.
    * @return a new weather station
    */
  override def newWeatherStation(headerFile: Path, dataFile: Path): WeatherStation = {

    // Throw an exception if either of the files don't exist
    if (!Files.exists(headerFile) || !Files.exists(dataFile)) {
      throw new FileNotFoundException
    }

    // OBTAIN META DATA

    // Declare meta data variables
    var NROWS = 0
    var NCOLS = 0
    var ULXMAP = 0.0
    var ULYMAP = 0.0
    var XDIM = 0.0
    var YDIM = 0.0
    var NODATA = 0.0

    // Loop through each line of the header file
    for (line <- Source.fromFile(headerFile.toString).getLines()) {

      // Split the current line into an array
      val keyAndVal = line.split(" ")

      // The first value in the array is the key
      val key = keyAndVal(0)

      // The last value in the array is the value
      val value = keyAndVal(keyAndVal.size - 1)

      // Assign values to the variables based on the key
      if (key == "NROWS") NROWS = value.toInt
      else if (key == "NCOLS") NCOLS = value.toInt
      else if (key == "ULXMAP") ULXMAP = value.toDouble
      else if (key == "ULYMAP") ULYMAP = value.toDouble
      else if (key == "XDIM") XDIM = value.toDouble
      else if (key == "YDIM") YDIM = value.toDouble
      else if (key == "NODATA") NODATA = value.toDouble
    }


    // LOAD BIL TO 2D ARRAY

    // Establish buffered input stream
    val bis = new BufferedInputStream(new FileInputStream(dataFile.toString))

    // Store bytes into array
    val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray

    // Declare variables that keep track of rows and columns
    var colCounter = 0
    var rowCounter = 0

    // Declare a 2D array to store data
    val tempMap = Array.ofDim[Float](NCOLS, NROWS)


    // Loop through 4 bytes at a time (for 32 bits)
    for (i <- 0 until ((byteArray.length / 4) - 1)) {

      // Byte location in increments of 4
      val y = i * 4

      // Retrieve bytes from array
      val byte1 = byteArray(y)
      val byte2 = byteArray(y + 1)
      val byte3 = byteArray(y + 2)
      val byte4 = byteArray(y + 3)

      // Store the bytes into another array
      val my32BitArray = Array[Byte](byte1, byte2, byte3, byte4)

      // Add the parsed float value, derived from 32BitArray, to the temperature map
      tempMap(colCounter)(rowCounter) = parseAsLittleEndianFloat(toInt(my32BitArray, 0))

      // Keep track of rows and columns
      // Once we reach the end of the row, start at the beginning of the next row
      colCounter += 1
      if (colCounter >= NCOLS) {
        colCounter = 0
        rowCounter += 1
      }
    }

    // Pass all of the aquired info onto a new station
    val newStation = new W_WeatherStation(tempMap, NROWS, NCOLS, ULXMAP, ULYMAP, XDIM, YDIM, NODATA)

    // Return the new station
    newStation
  }



  // Provided method
  def parseAsLittleEndianFloat(i: Int): Float = {
    val bb = ByteBuffer.allocate(4).putInt(
      (i >>> 24) |
        ((i >> 8) & 0xFF00) |
        ((i << 8) & 0xFF0000) |
        (i << 24))
    bb.rewind()
    bb.getFloat()
  }



  // Grabbed the Java version of this from stack overflow, IntelliJ Scalafied it
  def toInt(bytes: Array[Byte], offset: Int): Int = {
    var ret = 0
    var i = 0
    while ( {
      i < 4 && i + offset < bytes.length
    }) {
      ret <<= 8
      ret |= bytes(i).toInt & 0xFF

      {
        i += 1; i - 1
      }
    }
    ret
  }


}
