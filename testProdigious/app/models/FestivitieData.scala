package models

import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json._

/**
 * Clase para postear la data de las  Festivities
 */

case class FestivitieData(
                          startTime: DateTime,
                          endTime: DateTime,
                          place: Option[String],
                          desc: Option[String]) {

  def create: Festivitie = Festivitie.create(startTime, endTime, place, desc)

  def update: Int => Festivitie =
    id => Festivitie(id, startTime, endTime, employeeId, place, desc).save()

  def checkTime: Boolean = startTime.isBefore(endTime)

}

object FestivitieData {

  private lazy val ISODateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis
  private lazy val ISODateTimeParser = ISODateTimeFormat.dateTimeParser

  implicit val DateTimeFormatter = new Format[DateTime] {
    def reads(j: JsValue) = JsSuccess(ISODateTimeParser.parseDateTime(j.as[String]))
    def writes(o: DateTime): JsValue = JsString(ISODateTimeFormatter.print(o))
  }

  implicit val festivitieDataReads = Json.reads[FestivitieData]
  implicit val festivitieDataWrites = Json.writes[FestivitieData]

  def fromFestivitie(fe: Festivitie): FestivitieData =
    FestivitieData(fe.startTime, fe.endTime, fe.place, fe.desc)

}
