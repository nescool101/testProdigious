package models

import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scalikejdbc._
import org.joda.time.{DateTime}
import scalikejdbc.interpolation.SQLSyntax._

case class Festivitie(
  id: Int,
  startTime: DateTime,
  endTime: DateTime,
  place: Option[String] = None,
  desc: Option[String] = None) {

  def save()(implicit session: DBSession = Festivitie.autoSession): Festivitie = Festivitie.save(this)(session)

  def destroy()(implicit session: DBSession = Festivitie.autoSession): Unit = Festivitie.destroy(this)(session)

}


object Festivitie extends SQLSyntaxSupport[Festivitie] {

  override val tableName = "festivitie"

  override val columns = Seq("id", "start_time", "end_time", "place", "desc")

  def apply(fe: SyntaxProvider[Festivitie])(rs: WrappedResultSet): Festivitie = apply(fe.resultName)(rs)
  def apply(fe: ResultName[Festivitie])(rs: WrappedResultSet): Festivitie = new Festivitie(
    id = rs.get(fe.id),
    startTime = rs.get(fe.startTime),
    endTime = rs.get(fe.endTime),
    place = rs.get(fe.place),
    desc = rs.get(fe.desc)
  )

  val fe = Festivitie.syntax("fe")
  val e = Employee.syntax

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Festivitie] = {
    withSQL {
      select.from(Festivitie as te).where.eq(fe.id, id)
    }.map(Festivitie(fe.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Festivitie] = {
    withSQL(select.from(Festivitie as te)).map(Festivitie(fe.resultName)).list.apply()
  }

  def findFromAndTo(fromOpt: Option[DateTime], toOpt: Option[DateTime])(implicit session: DBSession = autoSession) = {
    if(fromOpt.isEmpty && toOpt.isEmpty) Left("No query parameters supplied")
    else Right(
      withSQL {
        select.from(Festivitie as te)
          .where(sqls.toAndConditionOpt(
          fromOpt.map(from => sqls.gt(fe.startTime, from)),
          toOpt.map(to => sqls.lt(fe.endTime, to))
        )).orderBy(fe.startTime desc)
      }.map(Festivitie(fe.resultName)).list.apply()
    )
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Festivitie as te)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Festivitie] = {
    withSQL {
      select.from(Festivitie as te).where.append(sqls"${where}")
    }.map(Festivitie(fe.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Festivitie] = {
    withSQL {
      select.from(Festivitie as te).where.append(sqls"${where}")
    }.map(Festivitie(fe.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Festivitie as te).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }

  def create(
    startTime: DateTime,
    endTime: DateTime,
    desc: Option[String] = None,
    place: Option[String] = None)(implicit session: DBSession = autoSession): Festivitie = {
    val generatedKey = withSQL {
      insert.into(Festivitie).columns(
        column.startTime,
        column.endTime,
        column.desc,
        column.place
      ).values(
        startTime,
        endTime,
        desc,
        place
      )
    }.updateAndReturnGeneratedKey.apply()

    Festivitie(
      id = generatedKey.toInt, 
      startTime = startTime,
      endTime = endTime,
      desc = desc,
      place = place)
  }

  def save(entity: Festivitie)(implicit session: DBSession = autoSession): Festivitie = {
    withSQL {
      update(Festivitie).set(
        column.id -> entity.id,
        column.startTime -> entity.startTime,
        column.endTime -> entity.endTime,
        column.place -> entity.place,
        column.desc -> entity.desc
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Festivitie)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(Festivitie).where.eq(column.id, entity.id) }.update.apply()
  }

}
