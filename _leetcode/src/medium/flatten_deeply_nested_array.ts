type MultiDimensionalArray = (number | MultiDimensionalArray)[];

var flat = function (
  arr: MultiDimensionalArray,
  n: number
): MultiDimensionalArray {
  if (n === 0) return arr;

  for (let index = 0; index < arr.length; index++) {
    const isArray = Array.isArray(arr[index]);
    if (!isArray) {
      continue;
    }

    const flattenSubArray = flat(arr[index] as MultiDimensionalArray, n - 1);
    arr.splice(index, 1, ...flattenSubArray);
    index += flattenSubArray.length - 1;
  }

  return arr;
};

const arr = [1, 2, 3, [4, 5, 6], [7, 8, [9, 10, 11], 12], [13, 14, 15]];
const n = 1;

console.log(flat(arr, n));
