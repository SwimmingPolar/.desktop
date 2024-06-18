function insertionSort(arr: number[]) {
  // Iterate over the array starting from the second element
  for (let i = 1; i < arr.length; i++) {
    // The current element to be inserted into the sorted portion
    let currentElement = arr[i];

    // Find the position where this element should be inserted
    // by comparing with elements in the sorted portion of the array
    let j = i - 1;
    while (j >= 0 && arr[j] > currentElement) {
      // Shift the larger element to the right
      arr[j + 1] = arr[j];
      j--;
    }

    // Insert the current element into the found position
    arr[j + 1] = currentElement;
  }
  return arr;
}

// Example usage
const array = [5, 2, 4, 6, 1, 3];
console.log("Sorted array:", insertionSort(array));
