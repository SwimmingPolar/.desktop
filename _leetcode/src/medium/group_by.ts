declare global {
  interface Array<T> {
    groupBy(fn: (item: T) => string): Record<string, T[]>;
  }
}

type Group<T> = Record<string, T[]>;

Array.prototype.groupBy = function <T>(fn: (item: T) => string) {
  return this.reduce((group: Group<T>, element: T) => {
    const key = fn(element);
    if (group?.[key] === undefined) {
      group[key] = [];
    }
    group[key].push(element);
    return group;
  }, {} as Group<T>);
};

console.log([1, 2, 3].groupBy(String));

export {};
