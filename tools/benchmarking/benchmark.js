/**
 * Benchmarks algorithm with every type of graph from the graph directory.
 */
const fs = require("fs");
const path = require("path");
const exec = require("child_process").exec;

const graphDirectory = path.join(__dirname, "graphs");

const getGraphFileNames = () => {
  return fs.readdirSync(graphDirectory);
};

/**
 * Removes result files generated by the java application
 */
const removeOutputFiles = async () => {
  console.log("Removing Output Graph Files");

  const files = fs.readdirSync(graphDirectory);
  // rudimentary filter!! warning
  const outputFilenames = files.filter((fn) => fn.includes("output"));
  console.log(`Found ${outputFilenames.length} files to remove`);

  if (outputFilenames.length === 0) return;

  outputFilenames.forEach((fn) => {
    const fullPath = path.join(graphDirectory, fn);
    fs.rmSync(fullPath);
  });
  console.log(`Tried removing ${outputFilenames.length} files`);
};

const runBenchmark = async (filename, processorCount) => {
  const fullPath = path.join(__dirname, `graphs/${filename}`);
  const jarPath = path.join(__dirname, "306-a1-1.0-SNAPSHOT.jar");
  const command = `java -jar "${jarPath}" "${fullPath}" ${processorCount}`;
  console.log(`running benchmark: [${filename}]`);
  return new Promise((resolve, reject) => {
    exec(command, (err) => {
      if (err) {
        console.error(err);
        reject(err);
      } else {
        resolve();
      }
    });
  });
};

const appendResultsToCsv = (processorCount, averageTimes) => {
  // time
  const currentDate = new Date().toISOString();

  const fpCsv = path.join(__dirname, "test.csv");
  const data = `${currentDate},${processorCount},${averageTimes[0]},${averageTimes[1]},${averageTimes[2]},${averageTimes[3]},${averageTimes[4]},${averageTimes[5]},${averageTimes[6]},${averageTimes[7]},${averageTimes[8]},${averageTimes[9]},${averageTimes[10]},\n`;
  fs.appendFileSync(fpCsv, data);
};

const benchmark = async (processorCount) => {
  const filesNames = getGraphFileNames();

  console.log(`Found ${filesNames.length} files to benchmark`);
  console.log("Running benchmark now...");

  const sortedFn = filesNames.sort();

  const testGroup = {}; // testGroup[testName] = testFilesArray[]

  sortedFn.forEach((f) => {
    const x = f.split("_");
    const testName = x.slice(0, -1).join("_");

    let testFilesArray = testGroup[testName];
    if (testFilesArray === undefined) testFilesArray = [];

    testFilesArray.push(f);
    testGroup[testName] = testFilesArray;
  });

  let allAveTimes = [];

  for (const testName of Object.keys(testGroup)) {
    let totalTime = 0;
    const testFilesArray = testGroup[testName];
    console.log("----------------------------------------");
    console.log(`Now testing ${testName} found ${testFilesArray.length} tests`);

    for (const filename of testFilesArray) {
      const t0 = performance.now();
      await runBenchmark(filename, processorCount);
      const t1 = performance.now();
      console.log("result");
      const currentTime = t1 - t0;
      totalTime += currentTime;
    }
    const averageTime = parseFloat((totalTime / 3).toFixed(3));
    allAveTimes.push(averageTime);
  }

  // append to csv
  appendResultsToCsv(processorCount, allAveTimes);
};

const main = async () => {
  await removeOutputFiles();
  // fileNames should be all valid now
  await benchmark(4);
  //await benchmark(2); ** UNCOMMENT WHEN INDEPENDENTNODES ARE FIXED IN ENTRY
  await removeOutputFiles();
};

main().then();
