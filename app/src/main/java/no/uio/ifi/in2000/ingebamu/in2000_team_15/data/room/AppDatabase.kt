package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteQuery

@Entity(primaryKeys = ["stationID", "timeReference"]) //The table in which weather data is stored
data class WeatherData(
    @ColumnInfo(name = "stationID") val stationID: String, //This column along with the one below it makes each row in the table distinct
    @ColumnInfo(name = "timeReference") val timeReference: String,
    @ColumnInfo(name = "air") val airTemperature: Double?,
    @ColumnInfo(name = "snow") val snow: Double?,
    @ColumnInfo(name = "cloud") val cloud: Double?,
    @ColumnInfo(name = "stationName") val stationName: String?,
    @ColumnInfo(name = "timeOfFetch") val timeOfFetch: String?,
    @ColumnInfo(name = "coordinate") val coordinate: String?,
)

@Dao //A selection of SQL-queries that can be used on the weather-table by using its corresponding function in Kotlin
interface WeatherDao {
    //Checks if the weather station in the parameter already exists in the table
    @RawQuery
    fun doesDataExist(query: SupportSQLiteQuery): Boolean

    //Returns all rows in the table that matches the string parameter
    @RawQuery
    fun getDataRows(query: SupportSQLiteQuery): List<WeatherData>

    //Returns all rows with timeOfFetch that matches the string parameter
    @RawQuery
    fun getTimeOfFetch(query: SupportSQLiteQuery): List<WeatherData>

    //Deletes all rows with this coordinate
    @Query("DELETE FROM WeatherData " +
            "WHERE coordinate = :thisCoordinate")
    fun deleteOldDataWithCoordinate(thisCoordinate : String)

    //Deletes all rows with this stationID
    @Query ("DELETE FROM WeatherData " +
            "WHERE stationID = :thisStationID")
    fun deleteOldDataWithStationID(thisStationID : String)

    //Inserts data for a new coordinate and month into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewObservationData(weatherData: WeatherData)
}

@Database(entities = [WeatherData::class], version = 1) //The app's access point to the database
abstract class AppDatabase : RoomDatabase() {
    abstract fun WeatherDao(): WeatherDao
}