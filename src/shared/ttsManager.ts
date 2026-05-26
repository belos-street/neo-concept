import { piper } from '@native/piper'

type TTSState = 'idle' | 'playing' | 'stopped'

class TTSManager {
  private state: TTSState = 'idle'
  private initPromise: Promise<void> | null = null

  async init(): Promise<void> {
    if (!this.initPromise) {
      this.initPromise = piper.init()
    }
    return this.initPromise
  }

  async speak(text: string, speed = 1.0): Promise<void> {
    if (!text.trim()) return

    await this.init()
    if (this.state === 'playing') {
      await piper.stop()
    }
    this.state = 'playing'
    try {
      await piper.speak(text, speed)
    } finally {
      this.state = 'idle'
    }
  }

  async stop(): Promise<void> {
    if (this.state !== 'playing') return
    this.state = 'stopped'
    await piper.stop()
    this.state = 'idle'
  }

  get state_(): TTSState {
    return this.state
  }
}

export const ttsManager = new TTSManager()