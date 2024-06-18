type Callback<Args extends any[], ReturnType> = (...args: Args) => ReturnType;

export function measurePerformance<Args extends any[], ReturnType>(
  callback: Callback<Args, ReturnType>
) {
  return function curried(...args: Args): ReturnType {
    const start = performance.now();
    const result = callback(...args);
    const end = performance.now();

    console.group("performance measure");
    console.log("result: ");
    console.log(result);
    console.log("time: ", +end - start + "ms");
    console.groupEnd();

    return result;
  };
}

export const maxFlat = (arr: any[]) =>
  JSON.parse(
    `[${JSON.stringify(arr)
      .replace(/[\[\]]/g, "")
      .replace(/,$/, "")
      .trim()}]`
  );
