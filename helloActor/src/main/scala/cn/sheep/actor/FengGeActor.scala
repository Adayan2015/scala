package cn.sheep.actor

import akka.actor.Actor

/**
  * @Title: FengGeActor
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/16 16:48
  */
// 接收方
class FengGeActor extends Actor{
  override def receive: Receive = {
    case "start" => println("峰说，I'm ok !")
    case "啪" => {

      println("峰：那必须滴！")
      Thread.sleep(1000)
      sender() ! "啪啪"

    }
  }
}
