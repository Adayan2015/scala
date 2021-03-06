package cn.sheep.spark

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._  // 导入时间单位

/**
  * @Title: SparkWorker
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/26 17:19
  */
class SparkWorker(masterUrl: String) extends Actor{

  // master的actorRef
  var masterProxy: ActorSelection = _
  var workId = UUID.randomUUID().toString

  override def preStart(): Unit = {
    context.actorSelection(masterUrl)
  }
  override def receive: Receive = {
    // worker要向master注册自己的信息
    case "started" => {
      // 自己已就绪
      // 向master注册自己的信息，id,core,ram
      masterProxy ! RegisterWorkerInfo(workId, 4, 32 * 1024)  // 此时master会收到该消息
    }
    case RegisterWorkerInfo => {  // master 发送给自己的注册成功消息
      // worker启动一个定时器，定时向master发送心跳
      context.system.scheduler.schedule(0 millis, 1500 millis, self, SendHearBeat)
    }
    case SendHearBeat => {
      // 开始向master发送心跳了
      masterProxy ! HearBeat(workId)  // 此时master将会收到心跳消息

    }

  }
}

object SparkWorker{
  def main(args: Array[String]): Unit = {
    // 校验参数
    if(args.length != 4){
      println(
        """
          |请输入参数：<host> <port> <workName> <masterURL>
        """.stripMargin
      )
      sys.exit()  // 退出程序

    }
    val host = args(0)
    val port = args(1)
    val workName = args(2)
    val masterURL = args(3)

    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $host
         |akka.remote.netty.tcp.port = $port
    """.stripMargin)
   val actorSystem = ActorSystem("sparkWorker", config)

    // 创建自己的actorRef
    val workerActorRef = actorSystem.actorOf(Props(new SparkWorker(masterURL)),workName)
    // 给自己发送一个已启动的消息，表示自己已启动
    workerActorRef ! "started"
  }
}
