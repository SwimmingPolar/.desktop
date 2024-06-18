type Fn<Args extends any[], ReturnType> = (
  ...args: Args
) => Promise<ReturnType>;

function timeLimit<Args extends any[], ReturnType>(
  fn: Fn<Args, ReturnType>,
  t: number
) {
  return async function (...args: Args) {
    const result = fn(...args);
    await Promise.race([
      result,
      new Promise((_, reject) => {
        setTimeout(() => {
          reject("Time Limit Exceeded");
        }, t);
      }),
    ]);
    return result;
  };
}

const fn = async (n: number) => {
  await new Promise((res) => setTimeout(res, 100));
  return n * n;
};

timeLimit(fn, 50)(5).catch(console.log);

export {};
