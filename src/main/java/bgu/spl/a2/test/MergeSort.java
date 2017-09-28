/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;


/**
 * a class that represents a MergeSort task of an array
 **/
public class MergeSort extends Task<int[]> {

	private final int[] array;
	
	/**
	 * Constructor
	 * @param array - the array to sort using MergeSort algorithm
	 */
	public MergeSort(int[] array) {
		this.array = array;
	}
	
	/**
	 * calls the sort function to sort the array
	 */
	@Override
	protected void start() {
		sort(array);
	}
	
	/**
	 * Spawns a MergeSortTask for each part of the array
	 */
	public void sort(int[] arr) {
		if (arr == null || arr.length < 2)
			this.complete(arr);
		else {
			MergeSort t1 = new MergeSort(this.splitLeft(arr));
			MergeSort t2 = new MergeSort(this.splitRight(arr));
			ArrayList<Task<int[]>> tasks = new ArrayList<Task<int[]>>();
			tasks.add(t1);
			tasks.add(t2);
			spawn(t1, t2);
			whenResolved(tasks, () -> {
				complete(merge((tasks.get(0).getResult().get()), (tasks.get(1).getResult().get())));
			});
		}
	}
	
	/**
	 * @param arr - the array to split 
	 * @return - an array[arr.length/2] of the left end side of the original array
	 */
	public int[] splitLeft(int[] arr) {
		int[] ans = new int[arr.length / 2];
		for (int i = 0; i < arr.length / 2; i = i + 1)
			ans[i] = arr[i];
		return ans;
	}
	
	/**
	 * @param arr - the array to split 
	 * @return - an array[arr.length/2] of the right end side of the original array
	 */
	public int[] splitRight(int[] arr) {
		int[] ans = new int[arr.length - arr.length / 2];
		for (int i = arr.length / 2; i < arr.length; i = i + 1)
			ans[i - arr.length / 2] = arr[i];
		return ans;
	}
	
	/**
	 * @param arr 1 - the array to merge with arr2
	 * @param arr 2 - the array to merge with arr1
	 * @return - a merged array
	 */
	public int[] merge(int[] arr1, int[] arr2) {
		int ind = 0, i1 = 0, i2 = 0;
		int len1 = arr1.length, len2 = arr2.length;
		int[] ans = new int[len1 + len2];
		while (i1 < len1 & i2 < len2) {
			if (arr1[i1] < arr2[i2]) {
				ans[ind] = arr1[i1];
				i1 = i1 + 1;
			} else {
				ans[ind] = arr2[i2];
				i2 = i2 + 1;
			}
			ind = ind + 1;
		}
		for (int i = i1; i < len1; i = i + 1) {
			ans[ind] = arr1[i];
			ind = ind + 1;
		}
		for (int i = i2; i < len2; i = i + 1) {
			ans[ind] = arr2[i];
			ind = ind + 1;
		}
		return ans;
	}

	public static void main(String[] args) throws InterruptedException {

		WorkStealingThreadPool pool = new WorkStealingThreadPool(100);
		int n = 10000; // you may check on different number of elements if you like
		int[] array = new Random().ints(n).toArray();
		MergeSort task = new MergeSort(array);
		CountDownLatch l = new CountDownLatch(1);
		pool.start();
		pool.submit(task);
		task.getResult().whenResolved(() -> {
			l.countDown();
		});
		l.await();
		pool.shutdown();
	}
}
