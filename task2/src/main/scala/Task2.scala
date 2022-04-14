// Ioan Florin Catalin Nitu
package task2

import scala.io.Source

object Task2 {
  /*
   * Main Class. This will be used to test your code.
   * These are just example queries and tests. The final tests are private.
   */

   /*
    * Do not modify the following messageType strings.
    * You can however add any more you like.
    */
    val STORE = "STORE"
    val STORE_SUCCESS = "STORE_SUCCESS"
    val STORE_FAILURE = "STORE_FAILURE" // You should never have to return this.
    val RETRIEVE = "RETRIEVE"
    val GET_NEIGHBOURS = "GET_NEIGHBOURS"
    val NEIGHBOURS_RESPONSE = "NEIGHBOURS_RESPONSE"
    val RETRIEVE_SUCCESS = "RETRIEVE_SUCCESS"
    val RETRIEVE_FAILURE = "RETRIEVE_FAILURE"
    val INTERNAL_ERROR = "INTERNAL_ERROR"
    val USER = "USER"
    val nodes_may_fail = 4

  def main(args: Array[String]) {
    val router: Router = new Router() // Instantiate the router

    // Read the overlay adjacency list
    val source = Source.fromFile("overlay_big.txt")
    val routerInfo: scala.collection.mutable.Map[String, MyNode] = scala.collection.mutable.Map()
    for (line <- source.getLines()) {
      val adj = line.split(" ")
      val myNode = new MyNode(adj(0), adj(1).toInt, adj.drop(2).toVector, router)
      routerInfo += (adj(0) -> myNode)
    }
    source.close()

    router.addNodes(routerInfo.toMap) // Add new nodes to the router

    /* QUERIES */

    var m = router.sendMessage(USER, "u0", new Message(USER, RETRIEVE, "key")) // RETRIEVE_FAILURE
    println(m.messageType + " " + m.data)
    assert(m.messageType == RETRIEVE_FAILURE)

    for (i <- (0 until 1000)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, STORE, s"key${i}->value${i}")) // STORE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == STORE_SUCCESS)
    }
    for (i <- (0 until 1000)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${i}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }
    for (i <- (0 until 1000)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${999 - i}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }
    for (i <- (0 until 100)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${1000}")) // RETRIEVE_FAILURE
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_FAILURE)
    }
    for (i <- (0 until 10); j <- (0 until 1000)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${j}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }
    for (i <- (0 until 1000); j <- (0 until 10)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${j}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }

    /* Check the correctness of the store. The stored keys should be present in at least one node */
    for (i <- (0 until 1000)) {
      println(routerInfo(s"u${i}").returnStore)
    }


    /* Crash some nodes*/
    for (i <- (996 until 1000)) {
      var failing_node = routerInfo(s"u${i}")
      var all_keys = failing_node.returnStore.keySet
      all_keys.foreach {
        failing_node.removeKey
      }
    }
    println("Post Crash")

    for (i <- (0 until 1000)) {
      println(routerInfo(s"u${i}").returnStore)
    }

    for (i <- (0 until 1000)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${i}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }
    for (i <- (0 until 1000)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${999 - i}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }
    for (i <- (0 until 100)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${1000}")) // RETRIEVE_FAILURE
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_FAILURE)
    }
    for (i <- (0 until 10); j <- (0 until 1000)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${j}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }
    for (i <- (0 until 1000); j <- (0 until 10)) {
      m = router.sendMessage(USER, s"u${i}", new Message(USER, RETRIEVE, s"key${j}")) // RETRIEVE_SUCCESS
      println(s"${i}: " + m.messageType + " " + m.data)
      assert(m.messageType == RETRIEVE_SUCCESS)
    }

  }
}