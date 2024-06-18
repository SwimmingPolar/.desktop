const Directions = [
  // Up
  [0, -1],

  // Down
  [0, 1],

  // Left
  [-1, 0],

  // Right
  [1, 0],
];

function search(grid: string[][], currentPosition: [number, number]) {
  // Mark the land as visited
  grid[currentPosition[0]][currentPosition[1]] = "0";

  // Iterate all directions
  Directions.forEach(([x, y]) => {
    const newX = currentPosition[0] + x;
    const newY = currentPosition[1] + y;
    // Check if new direction heading is a land
    const foundLand = grid?.[newX]?.[newY] === "1";
    if (foundLand) {
      console.log(`grid[${newX}][${newY}]`);
      search(grid, [newX, newY]);
    }
  });
}

function numIslands(grid: string[][]): number {
  let islands = 0;

  // iterate over entire map
  for (let i = 0; i < grid.length; i++) {
    for (let j = 0; j < grid[i].length; j++) {
      // If the first land is found
      if (grid[i][j] === "1") {
        // increment the number of islands
        islands++;
        // start searching for the rest of the land
        search(grid, [i, j]);
      }
    }
  }

  return islands;
}

const grid = [
  ["1", "1", "0", "0", "0"],
  ["1", "1", "0", "0", "0"],
  ["0", "0", "1", "0", "0"],
  ["0", "0", "0", "1", "1"],
];

const islands = numIslands(grid);
console.log(islands);
