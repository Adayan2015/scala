package cn.sheep.actor

import akka.actor.{Actor, ActorRef}

/**
  * @Title: LongGeActor
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/16 16:46
  */

// 发送方
class LongGeActor(val fg: ActorRef) extends Actor{
  // 接收消息的
  override def receive: Receive = {
    case "start" => {
      println("龙：I'm ok !")
      fg ! "啪"
    }
    case "啪啪" => {
      println("不错呀！")
      Thread.sleep(1000)
      fg ! "啪"
    }

  }
}
