// YOUR_FULL_NAME_HERE
package task2

import scala.collection.mutable.Queue
import scala.collection.mutable.Set

class MyNode(id: String, memory: Int, neighbours: Vector[String], router: Router) extends Node(id, memory, neighbours, router) {
    val STORE = "STORE"
    val STORE_SUCCESS = "STORE_SUCCESS"
    val STORE_FAILURE = "STORE_FAILURE"
    val RETRIEVE = "RETRIEVE"
    val GET_NEIGHBOURS = "GET_NEIGHBOURS"
    val NEIGHBOURS_RESPONSE = "NEIGHBOURS_RESPONSE"
    val RETRIEVE_SUCCESS = "RETRIEVE_SUCCESS"
    val RETRIEVE_FAILURE = "RETRIEVE_FAILURE"
    val INTERNAL_ERROR = "INTERNAL_ERROR"
    val USER = "USER"
    val nodes_may_fail = 4

      // //put initial neighbours in the queue and in the set
                // neighbours.foreach(x=> {
                //     visited += x
                //     queue += x
                // })

    override def onReceive(from: String, message: Message): Message = {
        /* 
         * Called when the node receives a message from some where
         * Feel free to add more methods and code for processing more kinds of messages
         * NOTE: Remember that HOST must still comply with the specifications of USER messages
         *
         * Parameters
         * ----------
         * from: id of node from where the message arrived
         * message: the message
         *           (Process this message and decide what to do)
         *           (All communication between peers happens via these messages)
         */
        if (message.messageType == GET_NEIGHBOURS) { // Request to get the list of neighbours
            new Message(id, NEIGHBOURS_RESPONSE, neighbours.mkString(" "))
        }
        else if (message.messageType == RETRIEVE) { // Request to get the value
            println("before")
            val key = message.data // This is the key
            val value = getKey(key) // Check if the key is present on the node
            println("after")
            var response : Message = new Message("", "", "")

            value match {
                case Some(i) => response = new Message(id, RETRIEVE_SUCCESS, i)
                //return a retrieve failure with a list of all our neighbors to the source
                case None => response = new Message(id, RETRIEVE_FAILURE,neighbours.mkString(" "))
            }           
            if(value == None) {

                if(from == USER) {
                    val queue = Queue[String](neighbours: _*)
                    val visited = Set[String](neighbours: _*)

                    visited.add(this.id)

                    while(!queue.isEmpty && response.messageType == RETRIEVE_FAILURE) {
                        val nextElem = queue.dequeue()
                        val responseFromNxt = router.sendMessage(this.id, nextElem, new Message(this.id,RETRIEVE,key))

                        val responseType = responseFromNxt.messageType
                        if(responseType == RETRIEVE_SUCCESS) {
                            response = new Message(responseFromNxt.source , RETRIEVE_SUCCESS , responseFromNxt.data )
                            //queue.dequeueAll(_ => true)
                        } else {
                            //if we got a failure, we have to extract all the elements from the neighbours sent in the response and add them to our queue and array (if they have not been discovered yet)
                            val newNeighbours = responseFromNxt.data.split(" ")

                            newNeighbours.foreach(x => {
                                if(!visited.contains(x)) {
                                    visited += x
                                    queue += x
                                }
                            })

                        }
                    }
                } else {
                    response = new Message(id, RETRIEVE_FAILURE,neighbours.mkString(" ")) 
                }


            }
                
                
            response // Return the correct response message
        }
        else if (message.messageType == STORE) { // Request to store key->value

            var response : Message = new Message("", STORE_FAILURE, "")

            

            val data = message.data.split("->") // data(0) is key, data(1) is value
            val storedOnSelf = setKey(data(0), data(1)) // Store on current node

            if (storedOnSelf) {
                response = new Message(id, STORE_SUCCESS)
            }
            else {
                response = new Message(id, STORE_FAILURE)
            }

            if(!storedOnSelf) {

                if(from == USER) {

                
                    var queue = Queue[String](neighbours: _*)
                    var visited = Set[String](neighbours: _*)

                    visited.add(this.id)

                    while(!queue.isEmpty && response.messageType == STORE_FAILURE) {
                   
                        val nextElem = queue.dequeue()

                        val responseFromNxt = router.sendMessage(this.id, nextElem, new Message(this.id,STORE,message.data))
                        val responseType = responseFromNxt.messageType

                
                        if(responseType == STORE_SUCCESS) {
                                response = new Message(responseFromNxt.source , STORE_SUCCESS , responseFromNxt.data )
                                //queue.dequeueAll(_ => true)
                            
                        } else {
                            //if we got a failure, we have to extract all the elements from the neighbours sent in the response and add them to our queue and array (if they have not been discovered yet)
                            val newNeighbours = responseFromNxt.data.split(" ")
                            newNeighbours.foreach(x => {
                                if(!visited.contains(x)) {
                                    visited += x
                                    queue += x
                                }
                            })
                        }  
                        
                    }
                } else {
                    response = new Message(id, RETRIEVE_FAILURE,neighbours.mkString(" "))
                }    
            } else {

            }

            response
        }
        /*
         * Feel free to add more kinds of messages.
         */
        else
            new Message(id, INTERNAL_ERROR)
    }
}