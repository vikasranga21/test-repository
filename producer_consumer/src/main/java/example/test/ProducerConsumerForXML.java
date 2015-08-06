package example.test;

import java.util.LinkedList;
import java.util.List;

public class ProducerConsumerForXML {
  private LinkedList<String> sharedQueue;
  // Tells if the Producer's job is done
  private boolean producerDoneFlag;

  /**
   * Implements Producer-Consumer problem with the node values from an XML, using the given buffer size 
   * 
   * @param xmlURI
   * @param bufferSize
   */
  public static void implementProducerConsumerForXML(String xmlURI, int bufferSize) {
    ProducerConsumerForXML producerConsumer = new ProducerConsumerForXML();
    producerConsumer.sharedQueue = new LinkedList<>();
    ProducerConsumerForXML.Producer producer = producerConsumer.new Producer(xmlURI, bufferSize);
    ProducerConsumerForXML.Consumer consumer = producerConsumer.new Consumer();
    Thread prodThread = new Thread(producer, "Producer");
    Thread consThread = new Thread(consumer, "Consumer");
    prodThread.start();
    consThread.start();
  }

  /**
   * @author vikas
   *
   */
  private class Producer implements Runnable {

    private final int bufferSize;
    private final List<String> xmlNodesList;
    
    public Producer(String xmlURI, int bufferSize) {
      this.xmlNodesList = XMLUtils.getXMLNodesList(xmlURI);
      this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
      for (int i = 0; i < xmlNodesList.size(); i++) {
        try {
          // Produce a queue element by reading on node value
          produce(xmlNodesList.get(i));
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
      // Mark done so that Consumer can stop
      producerDoneFlag = true;
      System.out.println("Producer's job done!");
    }

    private void produce(String node) throws InterruptedException {
      // wait if queue is full
      synchronized (sharedQueue) {
        while (sharedQueue.size() == bufferSize) {
          System.out.println(
              "Queue is full " + Thread.currentThread().getName() + " is waiting , size: " + sharedQueue.size());
          sharedQueue.wait();
        }
        sharedQueue.add(node);
        System.out.println("Produced: " + node + "\n");
        sharedQueue.notifyAll();
      }
    }

  }

  /**
   * @author vikas
   *
   */
  private class Consumer implements Runnable {

    private final LinkedList<String> consumerOutput;

    public Consumer() {
      this.consumerOutput = new LinkedList<>();
    }

    @Override
    public void run() {
      while (!producerDoneFlag) {
        try {
          // Consume a queue element and add to the output list
          consumerOutput.add(consume());
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
      System.out.println("Consumer's job done!");
    }

    private String consume() throws InterruptedException {
      StringBuilder consumedNode;
      // wait if queue is empty
      synchronized (sharedQueue) {
        while (!producerDoneFlag && sharedQueue.isEmpty()) {
          System.out.println(
              "Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + sharedQueue.size());
          sharedQueue.wait();
        }
        consumedNode = new StringBuilder(sharedQueue.remove()).reverse();
        System.out.println("Consumed: " + consumedNode + "\n");
        sharedQueue.notifyAll();
      }
      return consumedNode.toString();
    }

  }

}
