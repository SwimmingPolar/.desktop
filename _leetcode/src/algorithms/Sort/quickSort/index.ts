// Basic Quick Sort
function quickSort(arr: number[], start = 0, end = arr.length - 1) {
  if (start >= end) return;

  const index = partition(arr, start, end);

  quickSort(arr, start, index - 1);
  quickSort(arr, index + 1, end);
}

function partition(arr: number[], start: number, end: number) {
  const pivotValue = arr[end];
  let pivotIndex = start;

  for (let i = start; i < end; i++) {
    if (arr[i] < pivotValue) {
      [arr[i], arr[pivotIndex]] = [arr[pivotIndex], arr[i]];
      pivotIndex++;
    }
  }

  [arr[pivotIndex], arr[end]] = [arr[end], arr[pivotIndex]];

  return pivotIndex;
}

console.log("Basic Quick Sort:", quickSort([7, 2, 1, 6, 8, 5, 3, 4]));

/*
 * Stability of Quick Sort
 */
const unstableArray = [
  { number: 3, letter: "A" },
  { number: 2, letter: "B" },
  { number: 1, letter: "C" },
  { number: 3, letter: "D" },
  { number: 2, letter: "E" },
];
// Stable Quick Sort
console.log(
  "Stable Sort:",
  [...unstableArray].sort((a, b) => a.number - b.number)
);

// Unstable and rough version of Quick Sort
function roughQuickSort(arr: typeof unstableArray): typeof unstableArray {
  if (arr.length <= 1) return arr;

  let pivot = arr[arr.length - 1];
  let left = [];
  let right = [];

  for (let i = 0; i < arr.length - 1; i++) {
    if (arr[i].number < pivot.number) {
      left.push(arr[i]);
    } else {
      right.push(arr[i]);
    }
  }

  return [...roughQuickSort(left), pivot, ...roughQuickSort(right)];
}

console.log("Unstable Sort:", roughQuickSort(unstableArray));
