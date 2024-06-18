declare global {
  interface Array<T> {
    snail(rowsCount: number, colsCount: number): number[][];
  }
}

Array.prototype.snail = function (
  rowsCount: number,
  colsCount: number
): number[][] {
  if (this.length !== rowsCount * colsCount) return [];

  if (rowsCount === 1) return this;

  let [x, y] = [0, 0];
  let direction = 1;
  let header = 0;
  const snail: number[][] = [];

  while (x < colsCount) {
    if (!snail[y]) {
      snail[y] = [];
    }

    snail[y][x] = this[header++];

    y += direction;

    if (y === -1 || y === rowsCount) {
      direction *= -1;
      y += direction;
      x++;
    }
  }

  return snail;
};

export {};
