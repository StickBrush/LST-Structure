/*
 * Adapted from: http://www.vogella.com/tutorials/JavaAlgorithmsQuicksort/article.html
 * 
 */

package ut.mpc.kdt;

import java.util.ArrayList;
import java.util.List;

public class Quicksort  {
  private List<Temporal> numbers;
  private List<Double> numbersDub;
  private int number;
  private int low;
  private int high;
  
  public static void main(String[] args){
	  Quicksort qs = new Quicksort();
	  List<Double> temp = new ArrayList<Double>();
	  
	  temp.add(new Double(7.999));
	  temp.add(new Double(8));
	  temp.add(new Double(8.0001));
	  temp.add(new Double(2));
	  temp.add(new Double(4));
	  
	  qs.sort(temp, 0, temp.size() - 1);
	  
	  for(int i = 0; i < temp.size(); i++){
		  System.out.println(temp.get(i));
	  }
  }
  
  public void sortT(List<Temporal> values, int low, int high){
	    // check for empty or null array
	    if (values == null || values.size() == 0){
	      return;
	    }
	    this.numbers = values;
	    this.number = values.size();
	    quicksortT(low, high); 
	  
  }

  public void sort(List<Double> values, int low, int high){
	    // check for empty or null array
	    if (values == null || values.size() == 0){
	      return;
	    }
	    this.numbersDub = values;
	    this.number = values.size();
	    quicksort(low, high); 
  }
  
  private void quicksort(int low, int high) {
	    int i = low, j = high;
	    // Get the pivot element from the middle of the list
	    double pivot = this.numbersDub.get((low + (high-low)/2));

	    // Divide into two lists
	    while (i <= j) {
	      // If the current value from the left list is smaller then the pivot
	      // element then get the next element from the left list
	      while (this.numbersDub.get(i).doubleValue() < pivot) {
	        i++;
	      }
	      // If the current value from the right list is larger then the pivot
	      // element then get the next element from the right list
	      while (this.numbersDub.get(j).doubleValue() > pivot) {
	        j--;
	      }

	      // If we have found a values in the left list which is larger then
	      // the pivot element and if we have found a value in the right list
	      // which is smaller then the pivot element then we exchange the
	      // values.
	      // As we are done we can increase i and j
	      if (i <= j) {
	        exchangeDub(i, j);
	        i++;
	        j--;
	      }
	    }
	    // Recursion
	    if (low < j)
	      quicksort(low, j);
	    if (i < high)
	      quicksort(i, high);
  }
  
  private void exchangeDub(int i, int j) {
	Double temp = this.numbersDub.get(i);
	this.numbersDub.set(i, this.numbersDub.get(j));
	this.numbersDub.set(j, temp);
  }
  
  
  
  public void sort(List<Temporal> values, int low, int high, boolean isX) {
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
  
  
  private void quicksortT(int low, int high) {
	int i = low, j = high;
			    // Get the pivot element from the middle of the list
	double pivot = this.numbers.get((low + (high-low)/2)).getTimeStamp();
	
	// Divide into two lists
	while (i <= j) {
	  // If the current value from the left list is smaller then the pivot
	  // element then get the next element from the left list
	  while (this.numbers.get(i).getTimeStamp() < pivot) {
	    i++;
	  }
	  // If the current value from the right list is larger then the pivot
	  // element then get the next element from the right list
	  while (this.numbers.get(j).getTimeStamp() > pivot) {
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
	  quicksortT(low, j);
	if (i < high)
	  quicksortT(i, high);
  }

} 
