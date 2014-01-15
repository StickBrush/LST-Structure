/*
 * Adapted from: http://www.vogella.com/tutorials/JavaAlgorithmsQuicksort/article.html
 * 
 */

package ut.mpc.kdt;

import java.util.ArrayList;

public class Quicksort  {
  private ArrayList<Temporal> numbers;
  private int number;
  private int low;
  private int high;

  public void sort(ArrayList<Temporal> values, int low, int high, boolean isX) {
	    // check for empty or null array
	    if (values == null || values.size() == 0){
	      return;
	    }
	    this.numbers = values;
	    this.number = values.size();
	    quicksort(low, high, isX);
  }
  
  private double getCoordinate(int index, boolean isX){
	  if(isX){
		  return this.numbers.get(index).getXCoord();
	  } else {
		  return this.numbers.get(index).getYCoord();
	  }
  }

  private void quicksort(int low, int high, boolean isX) {
    int i = low, j = high;
    // Get the pivot element from the middle of the list
    double pivot = this.getCoordinate((low + (high-low)/2), isX);

    // Divide into two lists
    while (i <= j) {
      // If the current value from the left list is smaller then the pivot
      // element then get the next element from the left list
      while (this.getCoordinate(i,isX) < pivot) {
        i++;
      }
      // If the current value from the right list is larger then the pivot
      // element then get the next element from the right list
      while (this.getCoordinate(j,isX) > pivot) {
        j--;
      }

      // If we have found a values in the left list which is larger then
      // the pivot element and if we have found a value in the right list
      // which is smaller then the pivot element then we exchange the
      // values.
      // As we are done we can increase i and j
      if (i <= j) {
        exchange(i, j);
        i++;
        j--;
      }
    }
    // Recursion
    if (low < j)
      quicksort(low, j, isX);
    if (i < high)
      quicksort(i, high, isX);
  }

  private void exchange(int i, int j) {
	//System.out.println("i: " + this.numbers.get(i).getXCoord());
	//System.out.println("j: " + this.numbers.get(j).getXCoord());
	Temporal temp = this.numbers.get(i);
	this.numbers.set(i, this.numbers.get(j));
	this.numbers.set(j, temp);
	//System.out.println("i: " + this.numbers.get(i).getXCoord());
	//System.out.println("j: " + this.numbers.get(j).getXCoord());
  }
} 
