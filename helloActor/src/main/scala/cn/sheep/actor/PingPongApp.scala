package cn.sheep.actor

import akka.actor.{ActorSystem, Props}

/**
  * @Title: PingPongApp
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/16 16:45
  */
object PingPongApp extends App {

  // actorSystem
  private val pingPongActorSystem = ActorSystem("PingPongActorSystem")

  // 通过actorySystem创建ActorRef
  // 创建FengGeActor
  private val ffActorRef = pingPongActorSystem.actorOf(Props[FengGeActor],"ff")

  // 创建LongGeActorRef
  private val mmActorRef = pingPongActorSystem.actorOf(Props(new LongGeActor(ffActorRef)),"mm")

  ffActorRef ! "start"
  mmActorRef ! "start"


}
