const Parser = require('tree-sitter');
const HTML = require('tree-sitter-html');
const fs = require('fs');

// Load the HTML document
const htmlContent = fs.readFileSync('example.html', 'utf8');

// Initialize the parser
const parser = new Parser();
parser.setLanguage(HTML);

// Parse the HTML content
const tree = parser.parse(htmlContent);

// Function to recursively print the syntax tree with indentation and node details
function printTree(node, indent = 0) {
  const indentation = ' '.repeat(indent);
  
  // Create a snippet of the node's text for readability
  const snippet = node.text.trim().split('\n')[0].slice(0, 50); // Trim and limit text output for readability
  
  // Print the node type and snippet
  console.log(`${indentation}${node.type} (Text: "${snippet}")`);
  
  // Recursively print children nodes
  for (let i = 0; i < node.childCount; i++) {
    printTree(node.child(i), indent + 2);
  }
}

// Print the root node of the syntax tree
printTree(tree.rootNode);

