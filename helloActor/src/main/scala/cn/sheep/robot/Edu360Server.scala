package cn.sheep.robot

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * @Title: Edu360Server
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/19 14:38
  */
class Edu360Server extends Actor {
  // 用来接收客户端发送过来的问题的
  override def receive: Receive = {
    case "start" => println("机器人已就绪 ！")
    case ClientMessage(msg) => {
      println(s"收到客户端消息： $msg")
      msg match {
        case "你叫啥！" => sender() ! ServerMessage("美女妍")
        case "你是男是女？" => sender() ! ServerMessage("老娘是女的")
        case "你有男票吗？" => sender() ! ServerMessage("有")
        case "你男票是谁呀？" => sender() ! ServerMessage("小笨笨")
        case "做我女朋友吧？" => sender() ! ServerMessage("不，我 love 小笨笨！")
        case _ => sender() ! ServerMessage("What you say ?")
          // sender()发送端的代理对象，发送到客户端的mailbox中--> 客户端的reveive
      }
    }
  }
}

object Edu360Server {
  def main(args: Array[String]): Unit = {
    val host: String = "127.0.0.1"
    val port: Int = 8088

    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $host
         |akka.remote.netty.tcp.port = $port
    """.stripMargin)

    // 指定IP和端口号
    val actorSystem = ActorSystem("Server", config)

    val serverActorRef = actorSystem.actorOf(Props[Edu360Server], "yanyan")
    serverActorRef ! "start"  // 到自己的mailbox -> receive方法

  }


}
