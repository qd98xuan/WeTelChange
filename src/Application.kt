package com.example

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import sun.net.www.protocol.http.Negotiator
import java.sql.DriverManager
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation){
        jackson()
    }
    Class.forName(DatabaseInfo.JDBC_DRIVER)
    val connection = DriverManager.getConnection(DatabaseInfo.DB_URL, DatabaseInfo.DB_USER, DatabaseInfo.DB_PASSWORD)
    if (connection!=null){
        print("数据库链接成功")
    }
    routing {
        post("/insert"){
            val receive = call.receive<UserInfo>()
            try {
                val prepareStatement = connection.prepareStatement("insert into UserInfo values (?,?,?,?,?,?)")
                prepareStatement.setString(1,UUID.randomUUID().toString())
                prepareStatement.setString(2,receive.name)
                prepareStatement.setString(3,receive.phone)
                prepareStatement.setString(4,receive.male)
                prepareStatement.setString(5,receive.nickName)
                prepareStatement.setString(6,receive.avatar)
                prepareStatement.execute()
                call.respond(BaseResp<String>(true,200,"插入成功",""))
            }catch (e:Exception){
                call.respond(BaseResp<String>(false,500,e.message.toString(),""))
            }
        }
    }
}


