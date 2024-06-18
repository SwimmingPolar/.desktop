// lazy traversal or lazy evaluation
// Given a multi-dimensional array of integers, return a generator object
// which yields integers in the same order as inorder traversal.
//
// A multi-dimensional array is a recursive data structure that contains
// both integers and other multi-dimensional arrays.
//
// Inorder traversal iterates over each array from left to right,
// yielding any integers it encounters or applying inorder traversal
// to any arrays it encounters.

type MultidimensionalArray = (MultidimensionalArray | number)[];

// function* inorderTraversal(
//   arr: MultidimensionalArray
// ): Generator<number, void, unknown> {
//   for (const element of arr) {
//     if (Number.isInteger(element)) {
//       yield element as number;
//     } else {
//       generator/iterable delegation syntax
//       yield* inorderTraversal(element as MultidimensionalArray);
//     }
//   }
// }

function* inorderTraversal(
  arr: MultidimensionalArray
): Generator<number, void, unknown> {
  if (arr.length === 0) return;

  let i = 0;
  let value: number;
  let generator: ReturnType<typeof inorderTraversal> | undefined;
  let result: IteratorResult<number, void>;

  while (true) {
    if (arr[i] === undefined) break;

    if (Number.isInteger(arr[i])) {
      value = arr[i] as number;
    } else {
      if (generator === undefined) {
        generator = inorderTraversal(arr[i] as MultidimensionalArray);
      }

      result = generator.next();

      if (result.done) {
        generator = undefined;
      }

      value = result.value as number;
    }

    if (generator === undefined) {
      i++;
    }

    if (value === undefined) continue;
    yield value;
  }
}

const runGen = (arr: MultidimensionalArray) => {
  const gen = inorderTraversal(arr);
  for (const value of gen) {
    console.log(value);
  }
  console.log("Generator:", gen.next().done);
};

// runGen([1, [2, 3]]);
runGen([[[6]], [1, 3], [4, [5, 7]], 9, []]);
// runGen([]);
