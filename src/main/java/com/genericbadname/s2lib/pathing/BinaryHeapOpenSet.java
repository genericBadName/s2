package com.genericbadname.s2lib.pathing;

import java.util.Arrays;

public final class BinaryHeapOpenSet {
    private static final int INITIAL_CAPACITY = 1024;

    private S2Node[] array;
    private int size;

    public BinaryHeapOpenSet() {
        this(INITIAL_CAPACITY);
    }

    public BinaryHeapOpenSet(int size) {
        this.size = 0;
        this.array = new S2Node[size];
    }

    public int size() {
        return size;
    }

    public void insert(S2Node value) {
        if (size >= array.length - 1) {
            array = Arrays.copyOf(array, array.length << 1);
        }

        size++;
        value.setHeapPosition(size);
        array[size] = value;
        update(value);
    }

    public void update(S2Node val) {
        int index = val.getHeapPosition();
        int parentInd = index >>> 1;
        double cost = val.getFCost();
        S2Node parentNode = array[parentInd];
        while (index > 1 && parentNode.getFCost() > cost) {
            array[index] = parentNode;
            array[parentInd] = val;
            val.setHeapPosition(parentInd);
            parentNode.setHeapPosition(index);
            index = parentInd;
            parentInd = index >>> 1;
            parentNode = array[parentInd];
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public S2Node removeLowest() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        S2Node result = array[1];
        S2Node val = array[size];
        array[1] = val;
        val.setHeapPosition(1);
        array[size] = null;
        size--;
        result.setHeapPosition(-1);
        if (size < 2) {
            return result;
        }
        int index = 1;
        int smallerChild = 2;
        double cost = val.getFCost();
        do {
            S2Node smallerChildNode = array[smallerChild];
            double smallerChildCost = smallerChildNode.getFCost();
            if (smallerChild < size) {
                S2Node rightChildNode = array[smallerChild + 1];
                double rightChildCost = rightChildNode.getFCost();
                if (smallerChildCost > rightChildCost) {
                    smallerChild++;
                    smallerChildCost = rightChildCost;
                    smallerChildNode = rightChildNode;
                }
            }
            if (cost <= smallerChildCost) {
                break;
            }
            array[index] = smallerChildNode;
            array[smallerChild] = val;
            val.setHeapPosition(smallerChild);
            smallerChildNode.setHeapPosition(index);
            index = smallerChild;
        } while ((smallerChild <<= 1) <= size);
        return result;
    }
}
