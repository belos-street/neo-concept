import { $ } from "bun";

const mode = process.argv[2] === "release" ? "release" : "debug";

console.log(`Starting Android ${mode} build...`);

const buildResult = await $`cd android && gradlew.bat assemble${mode === "release" ? "Release" : "Debug"}`;
console.log(buildResult.text());

const apkDir = `android/app/build/outputs/apk/${mode}/`;
const apkFiles = await $`dir /b ${apkDir}*.apk`.text().catch(() => "");
if (apkFiles) {
  console.log(`APK generated:`);
  for (const file of apkFiles.trim().split("\n")) {
    console.log(`  ${apkDir}${file.trim()}`);
  }
} else {
  console.log(`Check output: android/app/build/outputs/apk/${mode}/`);
}

console.log(`Android ${mode} build completed!`);