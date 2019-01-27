// Note right now it works only for IR tests.
// With IDEA 2018.3 or later you can just activate required js and run "run IR test in node.js" configuration.

// Add to this array your path to test files or provide it as argument.
var anotherFiles = [];

var vm = require('vm');
var fs = require('fs');
var path = require('path');

// Change working dir to root of project
var testDataPathFromRoot = "js/js.translator/testData";
process.chdir(path.resolve("../../.."))
// var cwd = process.cwd();
// if (cwd.endsWith(testDataPathFromRoot)) {
//     process.chdir(cwd.substr(0, cwd.length - testDataPathFromRoot.length));
// }

var filesFromArgs = process.argv.slice(2);

function toAbsolutePath(p) {
    p = path.resolve(p)
    if (fs.existsSync(p) && fs.statSync(p).isFile()) {
        return fs.realpathSync(p)
    }

    return "";
}

// TODO autodetect common js files and other js files
// Filter out all except existing js files and transform all paths to absolute
var files = [].concat(filesFromArgs, anotherFiles)
    .map(toAbsolutePath)
    .filter(function(path) {
        return path.endsWith(".js")
    });

// Find runtime path

var runtimeHeader = "// RUNTIME: ";
var runtimeFiles = [];

files.forEach(function (path) {
    var code = fs.readFileSync(path, 'utf8');
    var firstLine = code.substr(0, code.indexOf("\n"));
    if (firstLine.startsWith(runtimeHeader)) {
        runtimeFiles = JSON.parse(firstLine.slice(runtimeHeader.length))
            .map(toAbsolutePath);
    }
});

if (runtimeFiles.length < 1) {
    throw Error("No runtime files")
}

var allFiles = [].concat(runtimeFiles, files);
console.log(">>> files         " + JSON.stringify(files))
console.log(">>> runtimeFiles  " + JSON.stringify(runtimeFiles))
console.log(">>> All files     " + JSON.stringify(allFiles))
console.log("\n>>> Running test: >>>>\n\n")


// Evaluate files and run box function

var sandbox = {};
vm.createContext(sandbox);

allFiles.forEach(function(p) {
    var code = fs.readFileSync(p, 'utf8');
    vm.runInContext(code, sandbox, {
        filename: p
    })
});

console.log(vm.runInContext("box()", sandbox));
