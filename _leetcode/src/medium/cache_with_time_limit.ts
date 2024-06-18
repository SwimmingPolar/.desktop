type CacheValue = {
  value: number;
  expiration: number;
};

class TimeLimitedCache {
  cache: Map<number, CacheValue>;

  constructor() {
    this.cache = new Map<number, CacheValue>();
  }

  set(key: number, value: number, duration: number): boolean {
    const cacheHit = this.cache.get(key) || ({} as CacheValue);

    const elapse = cacheHit?.expiration - performance.now() ?? undefined;
    const hasExpired = elapse > 0 ? true : false;

    this.cache.set(key, {
      value,
      expiration: performance.now() + duration,
    });

    return hasExpired;
  }

  get(key: number): number {
    const cacheHit = this.cache.get(key) || ({} as CacheValue);

    const elapse = cacheHit?.expiration - performance.now() ?? undefined;
    return elapse > 0 ? cacheHit.value : -1;
  }

  count(): number {
    return [...this.cache.values()].reduce(
      (count, { expiration }) =>
        count + (expiration - performance.now() > 0 ? 1 : 0),
      0
    );
  }
}

const timeLimitedCache = new TimeLimitedCache();
console.log(timeLimitedCache.set(1, 42, 1000)); // afalse
console.log(timeLimitedCache.get(1)); // 42
console.log(timeLimitedCache.count()); // 1
