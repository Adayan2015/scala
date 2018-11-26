package cn.sheep.actor

import akka.actor.{Actor, ActorSystem, Props}

/**
  * @Title: HelloActor
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/16 16:19
  */
class HelloActor extends Actor{
  // 接收消息的
  override def receive: Receive = {
    // 接收消息并处理
    case "你好帅" => print("是这样的！！")
    case "丑" => print("滚犊子！！")
    case "stop" => {
      context.stop(self) // 停止自己的actorRef
      context.system.terminate() // 关闭ActorSystem
    }
  }
}
object HelloActor {
  private val nBFactory = ActorSystem("NBFactory")  // 工厂
  private val helloActorRef = nBFactory.actorOf(Props[HelloActor], "helloActor")

  def main(args: Array[String]): Unit = {
    // 给自己发送消息
    helloActorRef ! "你好帅"
    helloActorRef ! "丑"

    helloActorRef ! "stop"
  }

}
