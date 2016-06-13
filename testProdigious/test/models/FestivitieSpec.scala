package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import org.joda.time.{DateTime}


class FestivitieSpec extends Specification {

  // initialize JDBC driver & connection pool
  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton("jdbc:postgresql://localhost/prodi", "nescool", "")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

  "Festivitie" should {

    val fe = Festivitie.syntax("fe")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Festivitie.find(123)(session)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Festivitie.findBy(sqls.eq(fe.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Festivitie.findAll()(session)
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Festivitie.countAll()(session)
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Festivitie.findAllBy(sqls.eq(fe.id, 123))(session)
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Festivitie.countBy(sqls.eq(fe.id, 123))(session)
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Festivitie.create(startTime = DateTime.now, endTime = DateTime.now, id = 123, description = "MyString")(session)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Festivitie.findAll()(session).head
      // TODO modify something
      val modified = entity.copy(projectId = 4)
      val updated = Festivitie.save(modified)(session)
      updated should not equalTo(entity)
    }
  }

}
