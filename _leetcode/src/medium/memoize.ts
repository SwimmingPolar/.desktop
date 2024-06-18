type Fn = (...params: number[]) => number;

function memoize(fn: Fn): Fn {
  const cache = new Map<string, number>();
  return function (...args) {
    const key = JSON.stringify(args);

    if (cache.has(key)) {
      return cache.get(key) as number;
    }

    const result = fn(...args);
    cache.set(key, result);
    return result;
  };
}

let callCount = 0;
const memoizedFn = memoize(function (a, b) {
  callCount += 1;
  return a + b;
});

memoizedFn(0, 0); // 5
memoizedFn(0, 0); // 5
console.log(callCount); // 1
