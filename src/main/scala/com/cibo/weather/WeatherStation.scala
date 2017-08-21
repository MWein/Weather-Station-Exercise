/*
 * Copyright © CiBO Technologies 2016
 */
package com.cibo.weather

import java.nio.file.Path

/**
  * Methods for creating weather stations
  */
trait WeatherStationFactory {
  /**
    * Constructs WeatherStations that serve the provided data.
    *
    * @param headerFile Path of a PRISM header file `*.hdr`.
    * @param dataFile   Path of a corresponding data file `*.bil`.
    * @return a new weather station
    */
  def newWeatherStation(headerFile: Path, dataFile: Path): WeatherStation
}

/**
  * Provides point and mean-area temperatures based on lat/lon coordinates
  */
trait WeatherStation {
  /**
    * Provides the temperature in Celsius, if available.
    *
    * If the location requested is not within the bounds of the weather station, an Exception is thrown.
    *
    * @param latitude  The latitude.
    * @param longitude The longitude.
    * @return the temperature if one is available, or None if the temperature is missing.
    */
  def temperature(latitude: Double, longitude: Double): Option[Double]

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
  def averageTemperature(lat0: Double, lon0: Double, lat1: Double, lon1: Double): Option[Double]
}
