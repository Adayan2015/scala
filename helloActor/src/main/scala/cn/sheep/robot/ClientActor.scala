package cn.sheep.robot

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/**
  * @Title: ClientActor
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/19 14:51
  */
class ClientActor(host: String, port:Int) extends Actor{
  // 获取服务器的代理对象
  var serverActorRef: ActorSelection = _
  // 在reveive方法之前调用
  override def preStart(): Unit = {
    // akka.tcp://Server@127.0.0.1:8088
    serverActorRef = context.actorSelection(s"akka.tcp://Server@${host}:${port}/user/yanyan")
  }

  // 等待MailBox调用:mailbox -> reveive
  override def receive: Receive = {  // shit
    case "start" => println("客服端系列已启动。。。")
    case msg: String => {
      // 把客户端输入的内容发送给服务端（actorRef）--> 服务端的mailbox中 --> 服务端的reveive
      serverActorRef ! ClientMessage(msg)
    }
    case ServerMessage(msg) => println(s"收到服务端消息： $msg")
  }
}

object ClientActor {
  def main(args: Array[String]): Unit = {
    val host: String = "127.0.0.1"
    // 改变端口号，可以用这个程序启动多个客户端
    val port: Int = 8089

    val serverHost = "127.0.0.1"
    val serverPort = 8088

    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $host
         |akka.remote.netty.tcp.port = $port
    """.stripMargin)
    // 指定IP和端口号
    val clientSystem = ActorSystem("client", config)
    val actorRef = clientSystem.actorOf(Props(new ClientActor(serverHost, serverPort.toInt)), "NMW-001")
    // 自己给自己发送一条消息，到自己的mailbox --> reveive
    actorRef ! "start"

    while (true){
      val question = StdIn.readLine()  // 同步阻塞的，shit
      actorRef ! question  // mailbox --> reveive
    }
  }

}
