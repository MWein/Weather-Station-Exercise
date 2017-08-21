import java.nio.file.{Path, Paths}

import com.cibo.weather.WeatherStation

/**
  * Created by MikeW on 8/20/2017.
  */
object Driver {

  def main(args: Array[String]): Unit = {

    val DataHeaderPath: String = "C:\\sample_data.hdr"
    val DataPath: String = "C:\\sample_data.bil"

    val myWeatherStation = W_WeatherStationFactory.newWeatherStation(Paths.get(DataHeaderPath), Paths.get(DataPath))


    val testtemp = myWeatherStation.temperature(40.0, -92.0).get

    System.out.println(testtemp)
  }


}
