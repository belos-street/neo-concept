import { $ } from "bun";

const AVD_NAMES = await getAvdNames();
if (AVD_NAMES.length === 0) {
  console.error("No Android emulator found. Create one in Android Studio first.");
  process.exit(1);
}

const isRunning = await isEmulatorRunning();
const avdName = AVD_NAMES[0];

if (!isRunning) {
  console.log(`Starting emulator: ${avdName}...`);
  Bun.spawn(["emulator", "-avd", avdName, "-no-snapshot-load"], {
    stdio: ["ignore", "inherit", "inherit"],
  });
  console.log("Waiting for emulator to boot...");
  await $`adb wait-for-device`;
  await waitForBoot();
  console.log("Emulator is ready!");
}

console.log("Running the app...");
await $`bun android`;
console.log("App launched successfully!");

async function getAvdNames(): Promise<string[]> {
  const result = await $`emulator -list-avds`.text();
  return result
    .split("\n")
    .map((s) => s.trim())
    .filter(Boolean);
}

async function isEmulatorRunning(): Promise<boolean> {
  const result = await $`adb devices`.text();
  return result.includes("emulator-");
}

async function waitForBoot(intervalMs = 3000, timeoutMs = 120_000): Promise<void> {
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    const bootCompleted = await $`adb shell getprop sys.boot_completed`.text().catch(() => "");
    if (bootCompleted.trim() === "1") return;
    await Bun.sleep(intervalMs);
  }
  console.error("Timeout: emulator failed to boot.");
  process.exit(1);
}