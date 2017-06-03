package org.gortz.greeniot.greencityiot.model.queue;


import java.util.ArrayList;
import java.util.List;

import org.gortz.greeniot.greencityiot.dto.sensors.SensorValue;

/**
 * Queue for sensor values, ordered by timestamp
 */
public class SensorValueQueue {
    private Node first = null;
    private Node last = null;
    private final int MAX_VALUES_TO_SAVE;
    int count = 0;

    public SensorValueQueue(int maxValuesToSave) {
        MAX_VALUES_TO_SAVE = maxValuesToSave;
    }

    /**
     * Check if the queue is empty
     * @return true if its empty
     */
    public boolean isEmpty() {
        return (first == null);
    }

    /**
     * Get the newest value from the queue
     * @return the newest value from the queue
     */
    public SensorValue peekEnd() {
        return last.data;
    }

    /**
     * Get the oldest value from the queue
     * @return the oldest value from the queue
     */
    public SensorValue peekStart(){
        return first.data;
    }

    private static class Node {
        private final SensorValue data;
        private Node next;
        public Node(SensorValue data){
            this.data=data;
        }
    }

    /**
     * Add a new sensor value to the queue
     * @param data to be added
     */
    public void enqueue(SensorValue data) {
        Node n = new Node(data);
        if (isEmpty()) {
            count++;
            n.next = first;
            first = n;
            last = n;
        } else {
            Node s = first;
            if(!data.getTimestamp().before(first.data.getTimestamp())) {
                if (data.getTimestamp().after(last.data.getTimestamp())) {
                    count++;
                    last.next = n;
                    last = n;
                    checkSize();
                    return;
                } else {
                    if(s.data.getTimestamp().compareTo(data.getTimestamp()) == 0){
                        return;
                    }
                    else{
                        while (s.next != null) {
                            if (s.next.data.getTimestamp().after(data.getTimestamp())) {
                                Node temp = s.next;
                                s.next = n;
                                n.next = temp;
                                count++;
                                checkSize();
                                return;
                            } else if (s.next.data.getTimestamp().compareTo(data.getTimestamp()) == 0) {
                                return;
                            }
                            s = s.next;
                        }
                    }
                }
            }
            else if(getSize()<MAX_VALUES_TO_SAVE){
                n.next = first;
                first = n;
                count++;
            }
        }
    }

    /**
     * Checks if the size of the queue is too big
     * and removes one value in that case.
     */
    private void checkSize(){
        if(getSize()>MAX_VALUES_TO_SAVE){
            removeFirstNode();
        }
    }

    /**
     * Removes the oldest value of the queue
     */
    private void removeFirstNode() {
        count--;
        if (first.next == null){
            last = null;
        }
        first = first.next;

    }

    /**
     * Get the current size of the queue
     * @return current size of the queue
     */
    public int getSize(){
        return count;
    }

    @Override
    public String toString() {
       Node s = first;
        StringBuilder print = new StringBuilder();
        while (s != null){
            print.append(s.data.getTimestamp().getTime()+"->");
            s = s.next;
        }
        return print.toString();
    }

    /**
     * Get all values from the queue
     * @return a list of sensor values
     */
    public List<SensorValue> getValues(){
        ArrayList<SensorValue> list = new ArrayList();
        if(first == null) return list;
        Node s = first;
        while (s != null){
            list.add(s.data);
            s = s.next;
        }
        return list;
    }
}
