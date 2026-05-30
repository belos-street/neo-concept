import { spawn, exec } from "child_process";
import { watch } from "fs";
import { resolve, join } from "path";
import { existsSync } from "fs";

const ANDROID_DIR = resolve(import.meta.dir, "../android");
const APK_PATH = join(ANDROID_DIR, "app/build/outputs/apk/debug/app-debug.apk");
const PACKAGE = "com.neoconcept";
const ACTIVITY = `${PACKAGE}/.MainActivity`;
const AVD_NAME = "Pixel_8";

const log = {
  info: (msg: string) => console.log(`\x1b[36m▸\x1b[0m ${msg}`),
  ok: (msg: string) => console.log(`\x1b[32m✓\x1b[0m ${msg}`),
  warn: (msg: string) => console.log(`\x1b[33m!\x1b[0m ${msg}`),
  err: (msg: string) => console.log(`\x1b[31m✗\x1b[0m ${msg}`),
};

function run(
  cmd: string,
  args: string[],
  cwd = ANDROID_DIR,
): Promise<{ code: number; stdout: string; stderr: string }> {
  return new Promise((resolve) => {
    const proc = spawn(cmd, args, {
      cwd,
      shell: true,
      stdio: ["pipe", "pipe", "pipe"],
    });
    let stdout = "";
    let stderr = "";
    proc.stdout.on("data", (d) => (stdout += d));
    proc.stderr.on("data", (d) => (stderr += d));
    proc.on("close", (code) => resolve({ code: code ?? 1, stdout, stderr }));
  });
}

function runStream(
  cmd: string,
  args: string[],
  cwd = ANDROID_DIR,
): Promise<number> {
  return new Promise((resolve) => {
    const proc = spawn(cmd, args, { cwd, shell: true, stdio: "inherit" });
    proc.on("close", (code) => resolve(code ?? 1));
  });
}

async function getConnectedDevices(): Promise<string[]> {
  const { stdout } = await run("adb", ["devices"]);
  return stdout
    .split("\n")
    .slice(1)
    .map((l) => l.trim().split("\t")[0])
    .filter((d) => d.length > 0);
}

async function isEmulatorRunning(): Promise<boolean> {
  const devices = await getConnectedDevices();
  return devices.some((d) => d.startsWith("emulator"));
}

async function waitForDevice(timeoutMs = 60000): Promise<void> {
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    const devices = await getConnectedDevices();
    if (devices.some((d) => d.startsWith("emulator"))) {
      const { stdout } = await run("adb", [
        "shell",
        "getprop",
        "sys.boot_completed",
      ]);
      if (stdout.trim() === "1") return;
    }
    await Bun.sleep(1000);
  }
  throw new Error("Emulator boot timeout");
}

async function startEmulator(): Promise<void> {
  if (await isEmulatorRunning()) {
    log.ok("Emulator already running");
    return;
  }

  log.info(`Starting emulator: ${AVD_NAME}`);
  spawn("emulator", ["-avd", AVD_NAME, "-no-snapshot-load"], {
    detached: true,
    stdio: "ignore",
  }).unref();

  log.info("Waiting for emulator to boot...");
  await waitForDevice();
  log.ok("Emulator booted");
}

async function buildApp(): Promise<boolean> {
  log.info("Building debug APK...");
  const code = await runStream(".\\gradlew", ["assembleDebug"]);
  if (code === 0) {
    log.ok("Build succeeded");
    return true;
  }
  err("Build failed");
  return false;
}

async function installApp(): Promise<boolean> {
  if (!existsSync(APK_PATH)) {
    log.err(`APK not found: ${APK_PATH}`);
    return false;
  }

  log.info("Installing APK...");
  const { code } = await run("adb", ["install", "-r", APK_PATH]);
  if (code === 0) {
    log.ok("Installed");
    return true;
  }
  log.err("Install failed");
  return false;
}

async function launchApp(): Promise<void> {
  log.info("Launching app...");
  await run("adb", ["shell", "am", "start", "-n", ACTIVITY]);
  log.ok("App launched");
}

async function buildAndDeploy(): Promise<boolean> {
  const built = await buildApp();
  if (!built) return false;
  const installed = await installApp();
  if (!installed) return false;
  await launchApp();
  return true;
}

function startLogcat(): void {
  log.info("Starting logcat...");
  const proc = spawn("adb", ["logcat", "-s", PACKAGE], { stdio: "inherit" });
  process.on("SIGINT", () => {
    proc.kill();
    process.exit(0);
  });
}

async function watchMode(): Promise<void> {
  const srcDir = join(ANDROID_DIR, "app/src");
  log.info(`Watching ${srcDir} for changes...`);
  log.info("Press Ctrl+C to stop");

  let debounce: Timer | null = null;

  watch(srcDir, { recursive: true }, (event, filename) => {
    if (!filename?.endsWith(".kt") && !filename?.endsWith(".xml")) return;
    if (debounce) clearTimeout(debounce);
    debounce = setTimeout(async () => {
      log.info(`Change detected: ${filename}`);
      await buildAndDeploy();
    }, 500);
  });
}

async function main() {
  const args = process.argv.slice(2);
  const command = args[0] || "dev";

  console.log("\n\x1b[1mNeo Concept Dev Server\x1b[0m\n");

  switch (command) {
    case "dev":
      await startEmulator();
      await buildAndDeploy();
      startLogcat();
      break;

    case "watch":
      await startEmulator();
      await buildAndDeploy();
      await watchMode();
      startLogcat();
      break;

    case "build":
      await buildApp();
      break;

    case "install":
      await installApp();
      await launchApp();
      break;

    case "emulator":
      await startEmulator();
      break;

    case "logcat":
      startLogcat();
      break;

    default:
      console.log(`
Usage: bun run scripts/dev.ts [command]

Commands:
  dev        Start emulator, build, install, launch & show logcat (default)
  watch      Same as dev + watch for file changes and auto-rebuild
  build      Build debug APK only
  install    Install APK and launch app
  emulator   Start emulator only
  logcat     Show logcat output
      `);
  }
}

main().catch((e) => {
  log.err(e.message);
  process.exit(1);
});
