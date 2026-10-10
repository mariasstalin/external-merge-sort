package com.sorting;

public class MinHeap<T extends Comparable<T>> {

	private T[] heap;

	private int size;

	private int capacity;

	public MinHeap(int capacity) {
		this.capacity = capacity;
		this.size = 0;
		this.heap = (T[]) new Comparable[capacity];
	}

	public void add(T item) {
		if (size == capacity) {
			throw new IllegalStateException("Heap is full");
		}
		heap[size] = item;
		heapifyUp(size);
		size++;
	}

	public T poll() {
		if (size == 0) {
			throw new IllegalStateException("Heap is empty");
		}
		T min = heap[0];
		heap[0] = heap[size - 1];
		size--;
		heapifyDown(0);
		return min;
	}

	public T peek() {
		if (size == 0) {
			throw new IllegalStateException("Heap is empty");
		}
		return heap[0];
	}

	public boolean isEmpty() {
		return size == 0;
	}

	private void heapifyUp(int index) {
		while (index > 0) {
			int parentIndex = (index - 1) / 2;
			if (heap[index].compareTo(heap[parentIndex]) >= 0) {
				break;
			}
			swap(index, parentIndex);
			index = parentIndex;
		}
	}

	private void heapifyDown(int index) {
		while (index < size) {
			int leftChild = 2 * index + 1;
			int rightChild = 2 * index + 2;
			int smallest = index;

			if (leftChild < size && heap[leftChild].compareTo(heap[smallest]) < 0) {
				smallest = leftChild;
			}
			if (rightChild < size && heap[rightChild].compareTo(heap[smallest]) < 0) {
				smallest = rightChild;
			}

			if (smallest == index) {
				break;
			}
			swap(index, smallest);
			index = smallest;
		}
	}

	private void swap(int i, int j) {
		T temp = heap[i];
		heap[i] = heap[j];
		heap[j] = temp;
	}

}
