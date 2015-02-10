package com.wesovi.middleware.user.application.database


import com.wesovi.middleware.user.application.database.UserRepositoryMessage._
import com.wesovi.middleware.common.application.database.{DatabaseConnection, DatabaseDriver, BaseRepository}

import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.meta.MTable


/**
 * Created by ivan on 13/1/15.
 */

object UserStatus{
  val CREATED:String="CR"
  val CANCELED:String="CA"
}

object UserRepositoryMessage{
  case class NewUserMessage(email:String,password:Option[String])
  case class FindUserByEmail(email:String)
  case class FindUserById(id:Long)
  case class UserAuthentication(email:String,password:String)
  case class UpdateUserMessage(user:User)
  case class CreateTable()
  case class DropTable()
}

case class User(
  id:Option[Long],
  email:String,
  var password:Option[String],
  var status:String
)

class UserRepository(val profile:JdbcProfile) extends BaseRepository with DatabaseDriver{this: DatabaseDriver =>
  import profile.simple._



  val db = new DatabaseConnection(profile=profile).dbObject

  class UserTable(tag:Tag)  extends Table[User](tag,"user") {

    def id = column[Long]("_id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("_email", O.NotNull)
    def password = column[Option[String]]("_password",O.Nullable)
    def status = column[String]("_status", O.NotNull,O.Default("CR"))

    def * = (id.?, email, password,status) <> (User.tupled, User.unapply)
    def userUniquerIndex = index("user_email_unique", email, unique = true)
  }

  val userTableQuery = TableQuery[UserTable]

  def createTable=db.withSession { implicit ss: Session =>
    if (MTable.getTables("user").list.isEmpty) userTableQuery.ddl.create
  }

  def dropTable=db.withSession { implicit ss: Session =>
    userTableQuery.ddl.drop
  }

  def insert(user:User):Long= db.withSession { implicit ss: Session =>
    userTableQuery returning userTableQuery.map(_.id) += user
  }

  def update(user:User)= db.withSession { implicit ss: Session =>
    userTableQuery.filter(_.id === user.id).update(user)
  }

  def byId(id:Long):Option[User]=db.withSession { implicit ss: Session =>
    val query = for{
      u <- userTableQuery
      if u.id === id &&  u.status === "CR"
    } yield u
    query.firstOption
  }

  def byEmailAndPassword(email:String,password:String):Option[User]=db.withSession { implicit ss: Session =>
    val query = for{
      u <- userTableQuery
      if u.email === email && u.password === password
    } yield u
    query.firstOption
  }

  def byEmail(email:String):Option[User]=db.withSession { implicit ss: Session =>
    val query = for{
      u <- userTableQuery
      if u.email === email
    } yield u
    query.firstOption
  }

  def all():Seq[User]= db.withSession { implicit ss: Session =>
    val query = for{
      u <- userTableQuery
    } yield u
    query.list
  }


  override def receive: Receive = {
    case NewUserMessage(email:String,password:Option[String]) => {
      sender ! insert(User(None, email,password,UserStatus.CREATED))
    }
    case FindUserByEmail(email)=>{
      sender ! byEmail(email)
    }
    case CreateTable=>{
      createTable
      sender ! true
    }
    case DropTable=>{
      dropTable
      sender ! true
    }
    case FindUserById(id)=>{
      sender ! byId(id)
    }
    case UserAuthentication(email,password)=>{
      sender ! byEmailAndPassword(email,password)
    }
    case UpdateUserMessage(user)=>{
      update(user)
    }

  }
}
