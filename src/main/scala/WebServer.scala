
import java.sql.{Connection, DriverManager}
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.model.HttpOriginMatcher
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings

import scala.io.StdIn


object WebServer {
  def main(args: Array[String]): Unit = {

    // cors setting for other origin access
    val settings = CorsSettings.defaultSettings.withAllowedOrigins(HttpOriginMatcher.*)


    implicit val system = ActorSystem("api-server")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route: Route = {
      (path("hello") & get) {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))

      } ~
        path("list") {
          (get & cors(settings)) {
            val connection: Connection = getDatabaseConnection;
            val statement = connection.createStatement
            var data = "[";
            //执行查询语句，并返回结果
            val rs = statement.executeQuery("SELECT id,content,date FROM content")

            var i = 0;
            while (rs.next) {
              if (i != 0){
                data += ","
              }
              val content = rs.getString("content")
              val date = rs.getString("date")
              val id = rs.getString("id")

              var temp = "{\"content\":\"" + content + "\","
              temp += "\"date\":\"" + date + "\","
              temp += "\"id\":\"" + id + "\"}"

              data += temp

              i+=1
            }

            data += "]" //闭合标签

            println(data)
            connection.close()
            complete(HttpEntity(ContentTypes.`application/json`, data))
          }
        } ~
        (path("edit") & get & cors(settings)) { //?id=1&content=good
          parameters('id.as[Int],'content.as[String]) { (id,content) =>
                val connection = getDatabaseConnection
                val statement = connection.createStatement
                val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)

                val sql = "UPDATE content set content= '"+content+"',  date = '"+date+"' WHERE id ="+id
                statement.executeUpdate(sql)
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "1"))
              }
        } ~
        (path("delete") & get & cors(settings)) {
          parameters('id.as[Int]) { (id) =>
            val connection = getDatabaseConnection
            val statement = connection.createStatement
            val sql = "delete from content WHERE id =" + id;
            statement.executeUpdate(sql)
            connection.close()
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "1"))
          }
        } ~
        (path("add") & get & cors(settings)) {
          parameters('content.as[String]) { (content: String) =>
            val connection = getDatabaseConnection
            val statement = connection.createStatement
            val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)
            val sql = "INSERT INTO `content` (`content`, `date`) VALUES ('"+content+"','"+date+"')";
            statement.executeUpdate(sql)
            connection.close()
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "1"))
          }
        }
    }
    val bingdingFuture = Http().bindAndHandle(route, "localhost", 9999)
    StdIn.readLine()
    bingdingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }


  def getDatabaseConnection: Connection = {
    // 访问本地MySQL服务器，通过3306端口访问mysql数据库
    val url = "jdbc:mysql://192.168.64.2:3306/notebook?useUnicode=true&characterEncoding=utf-8&useSSL=false"
    //驱动名称
    val driver = "com.mysql.jdbc.Driver"

    //用户名
    val username = "hewro"
    //密码
    val password = "ihewro19980801"
    //初始化数据连接
    var connection: Connection = null

    try {
      //注册Driver
      Class.forName(driver)
      //得到连接
      connection = DriverManager.getConnection(url, username, password)

      connection
    } catch {
      case e: Exception => e.printStackTrace()
        null
    }
  }
}


