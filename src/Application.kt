package com.example

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import sun.net.www.protocol.http.Negotiator
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.collections.ArrayList

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson()
    }
    Class.forName(DatabaseInfo.JDBC_DRIVER)
    val connection = DriverManager.getConnection(DatabaseInfo.DB_URL, DatabaseInfo.DB_USER, DatabaseInfo.DB_PASSWORD)
    if (connection != null) {
        print("数据库链接成功")
    }
    routing {
        post("/insert") {
            val receive = call.receive<UserInfo>()
            try {
                val queryAll = queryAll(connection)
//                if (queryAll.filter { it.phone.equals(receive.phone)}.size==0) {
                    val prepareStatement = connection.prepareStatement("insert into UserInfo values (?,?,?,?,?,?)")
                    prepareStatement.setString(1, UUID.randomUUID().toString())
                    prepareStatement.setString(2, receive.name)
                    prepareStatement.setString(3, receive.phone)
                    prepareStatement.setString(4, receive.male)
                    prepareStatement.setString(5, receive.nickName)
                    prepareStatement.setString(6, receive.avatar)
                    prepareStatement.execute()
                    call.respond(BaseResp<String>(true, 200, "插入成功", ""))
//                }else{
//                    call.respond(BaseResp<String>(false, 200, "已有重复数据", ""))
//                }
            } catch (e: Exception) {
                call.respond(BaseResp<String>(false, 500, e.message.toString(), ""))
            }
        }
        get("/queryAll") {
            try {
                val queryAll = queryAll(connection)
                call.respond(BaseResp<List<UserInfo>>(true, 200, "查询成功", queryAll))
            } catch (e: Exception) {
                call.respond(BaseResp<String>(false, 500, e.message.toString(), ""))
            }
        }
        post("/deleteByUUID") {
            try {
                val receive = call.receive<DeleteInfo>()
                val prepareStatement = connection.prepareStatement("delete from UserInfo where uuid = '${receive.uuid}'")
                prepareStatement.execute()
                call.respond(BaseResp<String>(true, 200, "删除成功", ""))
            }catch (e:Exception){
                call.respond(BaseResp<String>(false, 500, e.message.toString(), ""))
            }

        }
    }
}

private fun queryAll(connection: Connection): List<UserInfo> {
    val prepareStatement = connection.prepareStatement("select * from UserInfo")
    val executeQuery = prepareStatement.executeQuery()
    val list = ArrayList<UserInfo>()
    while (executeQuery.next()) {
        val userInfo = UserInfo(
            executeQuery.getNString(1),
            executeQuery.getNString(2),
            executeQuery.getNString(3),
            executeQuery.getNString(4),
            executeQuery.getNString(5),
            executeQuery.getNString(6)
        )
        list.add(userInfo)
    }
    return list
}


