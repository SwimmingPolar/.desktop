function heapSort(arr: number[]) {
  let n = arr.length;

  // Build heap (rearrange array)
  for (let i = Math.floor(n / 2) - 1; i >= 0; i--) heapify(arr, n, i);

  // One by one extract an element from heap
  for (let i = n - 1; i > 0; i--) {
    // Move current root to end
    [arr[0], arr[i]] = [arr[i], arr[0]];
    // Call max heapify on the reduced heap
    heapify(arr, i, 0);
  }

  return arr;
}

function heapify(arr: number[], n: number, i: number) {
  let largest = i; // Initialize largest as root
  let left = 2 * i + 1; // left = 2*i + 1
  let right = 2 * i + 2; // right = 2*i + 2

  // If left child is larger than root
  if (left < n && arr[left] > arr[largest]) largest = left;

  // If right child is larger than largest so far
  if (right < n && arr[right] > arr[largest]) largest = right;

  // If largest is not root
  if (largest !== i) {
    [arr[i], arr[largest]] = [arr[largest], arr[i]];

    // Recursively heapify the affected sub-tree
    heapify(arr, n, largest);
  }
}

// Example usage
console.log("Sorted array:", heapSort([12, 11, 13, 5, 6, 7]));
