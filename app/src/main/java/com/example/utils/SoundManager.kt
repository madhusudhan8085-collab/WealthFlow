package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundManager {

    var isSoundEnabled: Boolean = true
    var currentProfile: SoundProfile = SoundProfile.HAPTIC_CLICK

    enum class SoundProfile(val displayName: String, val description: String) {
        HAPTIC_CLICK("Tactile Click", "Crisp, minimalist mechanical dial click"),
        MARIMBA("Velvet Chime", "Warm, soothing acoustic marimba notes"),
        WATER_DROP("Water Drop", "Gentle, crystalline droplet pop")
    }

    enum class SoundType {
        TICK,
        POP,
        TAB,
        TOGGLE_ON,
        TOGGLE_OFF,
        SUCCESS,
        ERROR
    }

    private const val SAMPLE_RATE = 44100
    private val audioScope = CoroutineScope(Dispatchers.Default)

    private var soundPool: SoundPool? = null
    private val soundMap = ConcurrentHashMap<String, Int>()
    private val readySoundIds = ConcurrentHashMap.newKeySet<Int>()
    private val pcmCache = ConcurrentHashMap<String, ShortArray>()

    private var isInitialized = false
    private var lastTickTime = 0L
    private var appContext: Context? = null

    /**
     * Initialize the high-performance audio engine.
     * Uses USAGE_GAME so sounds route through media stream and are NEVER muted
     * by Android OS touch sound / keyboard sound system toggles.
     */
    fun init(context: Context) {
        if (isInitialized) return
        appContext = context.applicationContext

        // Pre-cache PCM in-memory immediately for zero-latency direct playback
        precacheCurrentProfilePcm()

        audioScope.launch {
            try {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME) // Routes to STREAM_MUSIC; never blocked by device touch sound toggles
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val pool = SoundPool.Builder()
                    .setMaxStreams(8)
                    .setAudioAttributes(audioAttributes)
                    .build()

                pool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        readySoundIds.add(sampleId)
                    }
                }

                soundPool = pool
                loadAllSounds(context.applicationContext, pool)
                isInitialized = true
            } catch (_: Exception) {
                // Direct PCM fallback will handle audio if SoundPool fails
            }
        }
    }

    private fun precacheCurrentProfilePcm() {
        for (type in SoundType.values()) {
            val key = "${currentProfile.name}_${type.name}"
            if (!pcmCache.containsKey(key)) {
                pcmCache[key] = renderSoundPcm(currentProfile, type)
            }
        }
    }

    private fun loadAllSounds(context: Context, pool: SoundPool) {
        val soundDir = File(context.cacheDir, "wf_sounds").apply { mkdirs() }

        for (profile in SoundProfile.values()) {
            for (type in SoundType.values()) {
                val key = "${profile.name}_${type.name}"
                val pcm = pcmCache[key] ?: renderSoundPcm(profile, type).also { pcmCache[key] = it }
                val wavFile = File(soundDir, "$key.wav")
                if (!wavFile.exists() || wavFile.length() < 44) {
                    writeWavFile(wavFile, pcm, SAMPLE_RATE)
                }
                val soundId = pool.load(wavFile.absolutePath, 1)
                soundMap[key] = soundId
            }
        }
    }

    /**
     * Plays the requested sound effect.
     * Rate-limits TICK sounds to 40ms to avoid overwhelming audio buffers during fast slider drags.
     * Guaranteed playback: uses SoundPool if ready; falls back to direct in-memory AudioTrack if sample is still loading.
     */
    fun play(sound: SoundType) {
        if (!isSoundEnabled) return

        val now = System.currentTimeMillis()
        if (sound == SoundType.TICK) {
            if (now - lastTickTime < 40L) return
            lastTickTime = now
        }

        val volume = when (sound) {
            SoundType.TICK -> 0.50f
            SoundType.POP -> 0.85f
            SoundType.TAB -> 0.80f
            SoundType.TOGGLE_ON, SoundType.TOGGLE_OFF -> 0.85f
            SoundType.SUCCESS -> 1.0f
            SoundType.ERROR -> 0.75f
        }

        val key = "${currentProfile.name}_${sound.name}"
        val pool = soundPool
        val soundId = soundMap[key]

        if (pool != null && soundId != null && readySoundIds.contains(soundId)) {
            pool.play(soundId, volume, volume, 1, 0, 1.0f)
        } else {
            // Immediate zero-latency fallback using direct in-memory PCM AudioTrack
            val pcm = pcmCache[key] ?: renderSoundPcm(currentProfile, sound).also { pcmCache[key] = it }
            playDirectPcm(pcm, volume)
        }
    }

    fun playPreview(profile: SoundProfile, sound: SoundType = SoundType.POP) {
        val key = "${profile.name}_${sound.name}"
        val pcm = pcmCache[key] ?: renderSoundPcm(profile, sound).also { pcmCache[key] = it }
        val pool = soundPool
        val soundId = soundMap[key]

        if (pool != null && soundId != null && readySoundIds.contains(soundId)) {
            pool.play(soundId, 0.9f, 0.9f, 1, 0, 1.0f)
        } else {
            playDirectPcm(pcm, 0.9f)
        }
    }

    /**
     * Direct zero-latency AudioTrack playback for in-memory PCM buffers.
     * Used as instant fallback when SoundPool is still decoding or unavailable.
     */
    private fun playDirectPcm(pcm: ShortArray, volume: Float) {
        audioScope.launch {
            try {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val bufferSizeBytes = pcm.size * 2
                val track = AudioTrack(
                    audioAttributes,
                    audioFormat,
                    bufferSizeBytes,
                    AudioTrack.MODE_STATIC,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )

                track.write(pcm, 0, pcm.size)
                track.setVolume(volume)
                track.play()

                // Free track resources after playback
                track.setNotificationMarkerPosition(pcm.size)
                track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onMarkerReached(t: AudioTrack?) {
                        try {
                            t?.stop()
                            t?.release()
                        } catch (_: Exception) {}
                    }
                    override fun onPeriodicNotification(t: AudioTrack?) {}
                })
            } catch (_: Exception) {
                // Non-fatal, gracefully continue
            }
        }
    }

    private fun renderSoundPcm(profile: SoundProfile, type: SoundType): ShortArray {
        return when (profile) {
            SoundProfile.HAPTIC_CLICK -> when (type) {
                SoundType.TICK -> synthTactileTick()
                SoundType.POP -> synthTactileClick(true)
                SoundType.TAB -> synthTactileClick(false)
                SoundType.TOGGLE_ON -> synthTactileDouble(true)
                SoundType.TOGGLE_OFF -> synthTactileDouble(false)
                SoundType.SUCCESS -> synthHarmonicSuccess(523.25)
                SoundType.ERROR -> synthTactileError()
            }
            SoundProfile.MARIMBA -> when (type) {
                SoundType.TICK -> synthVelvetChime(440.0, 15) // A4
                SoundType.POP -> synthVelvetChime(587.33, 40) // D5
                SoundType.TAB -> synthVelvetChime(739.99, 45) // F#5
                SoundType.TOGGLE_ON -> synthTwoToneChime(true)
                SoundType.TOGGLE_OFF -> synthTwoToneChime(false)
                SoundType.SUCCESS -> synthMarimbaSuccessChord()
                SoundType.ERROR -> synthMarimbaError()
            }
            SoundProfile.WATER_DROP -> when (type) {
                SoundType.TICK -> synthWaterBubble(600.0, 720.0, 14)
                SoundType.POP -> synthWaterBubble(520.0, 800.0, 35)
                SoundType.TAB -> synthWaterBubble(640.0, 960.0, 35)
                SoundType.TOGGLE_ON -> synthWaterBubble(500.0, 900.0, 45)
                SoundType.TOGGLE_OFF -> synthWaterBubble(900.0, 500.0, 45)
                SoundType.SUCCESS -> synthWaterSuccessCascade()
                SoundType.ERROR -> synthWaterError()
            }
        }
    }

    // --- High Quality Synthesizers ---

    /**
     * Tactile micro-tick: 7ms micro-tap with soft cosine attack and rapid exponential decay.
     * Clean, crisp, no clipping.
     */
    private fun synthTactileTick(): ShortArray {
        val durationMs = 7
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val freq = 420.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val attack = sin((progress * PI / 0.2).coerceAtMost(PI / 2.0))
            val decay = exp(-progress * 8.0)
            val envelope = attack * decay
            val sample = (sin(2.0 * PI * freq * t) + 0.3 * sin(4.0 * PI * freq * t)) * envelope
            buffer[i] = (sample * 16000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Tactile button click (crisp and satisfying)
     */
    private fun synthTactileClick(isMajor: Boolean): ShortArray {
        val durationMs = if (isMajor) 14 else 10
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val freq = if (isMajor) 580.0 else 460.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val attack = sin((progress * PI / 0.15).coerceAtMost(PI / 2.0))
            val decay = exp(-progress * 6.5)
            val envelope = attack * decay
            val sample = (sin(2.0 * PI * freq * t) + 0.2 * sin(3.0 * PI * freq * t)) * envelope
            buffer[i] = (sample * 18000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Tactile double-click for toggles
     */
    private fun synthTactileDouble(isUp: Boolean): ShortArray {
        val durationMs = 38
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val f1 = if (isUp) 440.0 else 660.0
        val f2 = if (isUp) 660.0 else 440.0
        val mid = numSamples / 2

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val isFirst = i < mid
            val freq = if (isFirst) f1 else f2
            val localProgress = if (isFirst) i.toDouble() / mid else (i - mid).toDouble() / mid
            val attack = sin((localProgress * PI / 0.2).coerceAtMost(PI / 2.0))
            val decay = exp(-localProgress * 7.0)
            val sample = sin(2.0 * PI * freq * t) * attack * decay
            buffer[i] = (sample * 16000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Subtle tactile error tone (low frequency double pulse)
     */
    private fun synthTactileError(): ShortArray {
        val durationMs = 45
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val f1 = 320.0
        val f2 = 240.0
        val mid = numSamples / 2

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val isFirst = i < mid
            val freq = if (isFirst) f1 else f2
            val localProgress = if (isFirst) i.toDouble() / mid else (i - mid).toDouble() / mid
            val attack = sin((localProgress * PI / 0.2).coerceAtMost(PI / 2.0))
            val decay = exp(-localProgress * 5.0)
            val sample = sin(2.0 * PI * freq * t) * attack * decay
            buffer[i] = (sample * 14000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Warm velvet acoustic marimba pluck
     */
    private fun synthVelvetChime(freq: Double, durationMs: Int): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val attack = sin((progress * PI / 0.15).coerceAtMost(PI / 2.0))
            val decay = exp(-progress * 4.8)
            val envelope = attack * decay
            val sample = (sin(2.0 * PI * freq * t) + 0.18 * sin(4.0 * PI * freq * t)) * envelope
            buffer[i] = (sample * 18000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Two-tone velvet chime for toggles
     */
    private fun synthTwoToneChime(isUp: Boolean): ShortArray {
        val note1 = if (isUp) 587.33 else 739.99
        val note2 = if (isUp) 739.99 else 587.33
        val durationMs = 60
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val half = numSamples / 2

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val isFirst = i < half
            val freq = if (isFirst) note1 else note2
            val localProgress = if (isFirst) i.toDouble() / half else (i - half).toDouble() / half
            val attack = sin((localProgress * PI / 0.18).coerceAtMost(PI / 2.0))
            val decay = exp(-localProgress * 4.5)
            val sample = (sin(2.0 * PI * freq * t) + 0.15 * sin(4.0 * PI * freq * t)) * attack * decay
            buffer[i] = (sample * 16000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    private fun synthMarimbaError(): ShortArray {
        val durationMs = 55
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val half = numSamples / 2

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val isFirst = i < half
            val freq = if (isFirst) 369.99 else 293.66 // F#4 -> D4 gentle descend
            val localProgress = if (isFirst) i.toDouble() / half else (i - half).toDouble() / half
            val attack = sin((localProgress * PI / 0.2).coerceAtMost(PI / 2.0))
            val decay = exp(-localProgress * 4.0)
            val sample = sin(2.0 * PI * freq * t) * attack * decay
            buffer[i] = (sample * 15000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Soft water droplet bubble sound
     */
    private fun synthWaterBubble(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val envelope = sin(progress * PI) * exp(-progress * 2.0)
            val sample = sin(2.0 * PI * currentFreq * t) * envelope
            buffer[i] = (sample * 17000.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    private fun synthWaterError(): ShortArray {
        return synthWaterBubble(420.0, 280.0, 45)
    }

    /**
     * Harmonic success chime (celebratory D-major chord)
     */
    private fun synthHarmonicSuccess(baseFreq: Double): ShortArray {
        val notes = doubleArrayOf(baseFreq, baseFreq * 1.25, baseFreq * 1.5, baseFreq * 2.0)
        val noteGapMs = 40
        val noteSustainMs = 120
        val totalDurationMs = noteGapMs * notes.size + noteSustainMs
        val numSamples = (SAMPLE_RATE * totalDurationMs) / 1000
        val buffer = ShortArray(numSamples)

        for (n in notes.indices) {
            val freq = notes[n]
            val startIndex = (n * noteGapMs * SAMPLE_RATE) / 1000
            val noteSamples = (noteSustainMs * SAMPLE_RATE) / 1000

            for (i in 0 until noteSamples) {
                val idx = startIndex + i
                if (idx >= numSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / noteSamples
                val attack = sin((progress * PI / 0.15).coerceAtMost(PI / 2.0))
                val decay = exp(-progress * 3.8)
                val sample = sin(2.0 * PI * freq * t) * attack * decay
                val existing = buffer[idx].toInt()
                val combined = existing + (sample * 8500.0).toInt()
                buffer[idx] = combined.coerceIn(-32768, 32767).toShort()
            }
        }
        return buffer
    }

    private fun synthMarimbaSuccessChord(): ShortArray = synthHarmonicSuccess(587.33)

    private fun synthWaterSuccessCascade(): ShortArray {
        val freqList = listOf(520.0 to 700.0, 650.0 to 880.0, 780.0 to 1050.0, 980.0 to 1300.0)
        val gapMs = 45
        val bubbleMs = 50
        val totalMs = gapMs * freqList.size + bubbleMs
        val numSamples = (SAMPLE_RATE * totalMs) / 1000
        val buffer = ShortArray(numSamples)

        for (idx in freqList.indices) {
            val (fStart, fEnd) = freqList[idx]
            val startIndex = (idx * gapMs * SAMPLE_RATE) / 1000
            val bubbleSamples = (bubbleMs * SAMPLE_RATE) / 1000

            for (i in 0 until bubbleSamples) {
                val outIdx = startIndex + i
                if (outIdx >= numSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / bubbleSamples
                val curFreq = fStart + (fEnd - fStart) * progress
                val envelope = sin(progress * PI) * exp(-progress * 2.2)
                val sample = sin(2.0 * PI * curFreq * t) * envelope
                val existing = buffer[outIdx].toInt()
                val combined = existing + (sample * 8000.0).toInt()
                buffer[outIdx] = combined.coerceIn(-32768, 32767).toShort()
            }
        }
        return buffer
    }

    /**
     * Writes 16-bit PCM buffer into standard 44-byte RIFF WAV file for instantaneous SoundPool playback.
     */
    private fun writeWavFile(file: File, pcm: ShortArray, sampleRate: Int) {
        val totalAudioLen = pcm.size * 2
        val totalDataLen = totalAudioLen + 36
        val channels = 1
        val byteRate = sampleRate * channels * 2

        val header = ByteArray(44)
        val byteBuf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        byteBuf.put("RIFF".toByteArray())
        byteBuf.putInt(totalDataLen)
        byteBuf.put("WAVE".toByteArray())
        byteBuf.put("fmt ".toByteArray())
        byteBuf.putInt(16) // Subchunk1Size
        byteBuf.putShort(1.toShort()) // AudioFormat PCM = 1
        byteBuf.putShort(channels.toShort())
        byteBuf.putInt(sampleRate)
        byteBuf.putInt(byteRate)
        byteBuf.putShort((channels * 2).toShort()) // BlockAlign
        byteBuf.putShort(16.toShort()) // BitsPerSample
        byteBuf.put("data".toByteArray())
        byteBuf.putInt(totalAudioLen)

        FileOutputStream(file).use { fos ->
            fos.write(header)
            val pcmBytes = ByteArray(pcm.size * 2)
            ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(pcm)
            fos.write(pcmBytes)
        }
    }
}
