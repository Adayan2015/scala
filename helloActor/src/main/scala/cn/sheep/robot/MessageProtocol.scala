package cn.sheep.robot

/**
  * @Title: MessageProtocol
  * @ProjectName helloActor
  * @Description: TODO
  * @author fanyanyan
  * @date 2018/11/19 15:15
  */
// 服务端发送给客户端的消息格式
case class ServerMessage(msg: String)
// 客户端发送给服务器端的消息格式
case class ClientMessage(msg: String)