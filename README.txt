The only modification needed to run my code is to change the path strings for the hdr and bil files. They can be found at the beginning of Driver.scala.

  val DataHeaderPath: String = "C:\\sample_data.hdr"
  val DataPath: String = "C:\\sample_data.bil"



My only concern is that this program was designed with the assumption that longitude is always negative and latitude is always positive. Since the considerations section says that the only concern is data within CONUS, I did not write it to accept other cases.

Suggesting use of QGIS or some other free GIS software to view sample_data could be helpful. I used QGIS to produce more test cases to verify that my program converted the lat long to the proper location in the 2D array used to store the data. It also helped to know where the NoData pixels were supposed to be to verify that I was copying the data to a 2D array correctly.