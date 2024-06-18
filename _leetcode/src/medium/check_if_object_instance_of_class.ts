function checkIfInstanceOf(obj: any, classFunction: any): boolean {
  if (obj === null || obj === undefined) return false;

  return obj.__proto__ === classFunction?.prototype
    ? true
    : checkIfInstanceOf(obj.__proto__, classFunction);
}
