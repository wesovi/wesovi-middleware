package com.wesovi.middleware.user.application.database

/**
 * Created by ivan on 11/1/15.
 */


import java.util.Date


import com.wesovi.middleware.user.application.database.PersonRepositoryMessage._
import com.wesovi.middleware.common.application.database.{BaseRepository, DatabaseConnection, DatabaseDriver}

import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.meta.MTable


object PersonRepositoryMessage{
  case class NewPersonMessage(userId:Option[Long],name:Option[String],gender:Option[String],birthday:Option[Date])
  case class UpdatePersonMessage(person:Person)
  case class ByUserIdMessage(userId:Long)
  case class CreateTable()
  case class DropTable()
}

case class Person(
                   id:Option[Long],
                   userId:Option[Long],
                   var name:Option[String],
                   gender:Option[String],
                   birthday:Option[Date],
                   city:Option[String],
                   state:Option[String],
                   country:Option[String],
                   zip:Option[String]
                   )

class PersonRepository(val profile:JdbcProfile) extends BaseRepository with DatabaseDriver{this: DatabaseDriver =>
  import profile.simple._

  implicit val DateTimeColumnType = MappedColumnType.base[java.util.Date, java.sql.Timestamp](

  { d => java.sql.Timestamp.from(d.toInstant())}, { t => java.util.Date.from(t.toInstant())}
  )

  val db = new DatabaseConnection(profile=profile).dbObject

  protected class PersonTable(tag:Tag) extends Table[Person](tag,"person") {

    def id = column[Long]("_id", O.PrimaryKey, O.AutoInc)
    def userId = column[Option[Long]]("_userId", O.Nullable)
    def name = column[Option[String]]("_name", O.Nullable)
    def gender = column[Option[String]]("_gender, O.Nullable")
    def birthday = column[Option[Date]]("_birthday, O.Nullable")
    def city = column[Option[String]]("_city, O.Nullable")
    def state = column[Option[String]]("_state, O.Nullable")
    def country = column[Option[String]]("_country, O.Nullable")
    def zip = column[Option[String]]("_zip, O.Nullable")
    def * = (id.?, userId,name,gender,birthday,city,state,country,zip) <> (Person.tupled, Person.unapply)
  }

  val personTableQuery = TableQuery[PersonTable]

  def createTable=db.withSession { implicit ss: Session =>
    if (MTable.getTables("person").list.isEmpty) personTableQuery.ddl.create
  }

  def dropTable=db.withSession { implicit ss: Session =>
    personTableQuery.ddl.drop
  }

  def insert(person:Person):Long= db.withSession { implicit ss: Session =>
    personTableQuery returning personTableQuery.map(_.id) += person
  }

  def update(person:Person)= db.withSession { implicit ss: Session =>
    personTableQuery.filter(_.id === person.id).update(person)
  }

  def byId(id:Long):Option[Person]=db.withSession { implicit ss: Session =>
    val query = for{
      p <- personTableQuery
      if p.id === id
    } yield p
    query.firstOption
  }

  def byUserId(userId:Long):Option[Person]=db.withSession { implicit ss: Session =>
    val query = for{
      p <- personTableQuery
      if p.userId === userId
    } yield p
    query.firstOption
  }

  def all():Seq[Person]= db.withSession { implicit ss: Session =>
    val query = for{
      p <- personTableQuery
    } yield p
    query.list
  }

  def receive = {
    case NewPersonMessage(userId,name,gender,birthday) => {
      sender() ! insert(Person(None, userId,name,gender,birthday,None,None,None,None))
    }
    case CreateTable=>{
      createTable
      sender ! true
    }
    case UpdatePersonMessage(person)=>{
      update(person)
    }
    case DropTable=>{
      dropTable
      sender ! true
    }
    case ByUserIdMessage(userId)=>{
      sender ! byUserId(userId)
    }
  }

}
