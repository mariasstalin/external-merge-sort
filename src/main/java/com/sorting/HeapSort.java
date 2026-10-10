package com.sorting;

final public class HeapSort {

	public static void sort(int[] array) {
		int n = array.length;
		for (int i = n / 2 - 1; i >= 0; i--) {
			heapify(array, n, i);
		}
		for (int i = n - 1; i > 0; i--) {
			swap(array, 0, i);
			heapify(array, i, 0);
		}
	}

	final private static void heapify(int[] array, int heapSize, int rootIndex) {
		int largest = rootIndex;
		int leftChild = 2 * rootIndex + 1;
		int rightChild = 2 * rootIndex + 2;

		if (leftChild < heapSize && array[leftChild] > array[largest]) {
			largest = leftChild;
		}

		if (rightChild < heapSize && array[rightChild] > array[largest]) {
			largest = rightChild;
		}

		if (largest != rootIndex) {
			swap(array, rootIndex, largest);
			heapify(array, heapSize, largest);
		}
	}

	final private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

}
