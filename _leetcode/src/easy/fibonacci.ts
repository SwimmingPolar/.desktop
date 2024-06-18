import { measurePerformance } from "utils";

function Fibonacci(n: number): number {
  if (n === 0 || n === 1) {
    return n;
  }

  return Fibonacci(n - 1) + Fibonacci(n - 2);
}

function optimizedFibonacci(n: number): number {
  const fibo = [0, 1];

  for (let i = 2; i <= n; i++) {
    fibo[i] = fibo[i - 1] + fibo[i - 2];
  }

  return fibo[n];
}

measurePerformance(Fibonacci)(30);
measurePerformance(optimizedFibonacci)(30);
