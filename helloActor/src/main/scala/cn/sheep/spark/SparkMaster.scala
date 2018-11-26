package cn.sheep.spark

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory  // 导入时间单位

/**
  * @Title: SparkMaster
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/26 17:18
  */
class SparkMaster extends Actor{

  // 存储worker的信息
  val id2WorkerInfo = collection.mutable.HashMap[String,WorkerInfo]()
  override def receive: Receive = {
    // 收到worker注册过来的信息
    case RegisterWorkerInfo(wkId, core, ram) => {
      // 将worker的信息存储起来，存储到HashMap
      if (! id2WorkerInfo.contains(wkId)) {
        val workerInfo = new WorkerInfo(wkId, core, ram)
        id2WorkerInfo += ((wkId, workerInfo))

        // master 存储完worker注册的数据后，告诉worker说你已经注册成功
        sender() ! RegisterWorkerInfo  // 此时worker会收到注册成功消息

      }

    }
    case HearBeat(wkId) => {
      // master收到worker的心跳消息之后，更新woker的上一次心跳时间
      val workerInfo = id2WorkerInfo(wkId)
      // 更新心跳时间
      val currentTime = System.currentTimeMillis()
      workerInfo.lastHearBeatTime = currentTime

    }
    case CheckTimeOutWorker => {
      context.system.scheduler.schedule(0 millis, 6000 millis, self, RemoveTimeOutWorker)
    }

    case RemoveTimeOutWorker => {
      // 将hashMap中所有的value都拿出来，查看当前时间和上一次心跳时间的差 3000
      val workerInfos = id2WorkerInfo.values
      val currentTime = System.currentTimeMillis()

      // 过滤超时的worker
      workerInfos.filter(wkInfo => currentTime - wkInfo.lastHearBeatTime > 3000).foreach(wk => id2WorkerInfo.remove(wk.id))
    }

  }
}

object SparkMaster{

  def main(args: Array[String]): Unit = {
    // 校验参数
    if(args.length != 3){
      println(
        """
          |请输入参数：<host> <port> <masterName>
        """.stripMargin
      )
      sys.exit()  // 退出程序

    }
    val host = args(0)
    val port = args(1)
    val masterName = args(2)

    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $host
         |akka.remote.netty.tcp.port = $port
    """.stripMargin)
    val actorSystem = ActorSystem("sparkMaster", config)
    val masterActorRef = actorSystem.actorOf(Props[SparkMaster], masterName)

    // 自己给自己发送一个消息，去启动一个调度器，定期检查HashMap中超时的worker
    masterActorRef ! CheckTimeOutWorker

  }
}