package com.example.aplicativodeediodeimagens

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class Cartoonizer(private val context: Context) {
    private var interpreter: Interpreter? = null

    init {
        try {
            Log.d("Cartoonizer", "Tentando carregar o modelo TFLite...")
            val modelBuffer = loadModelFile()
            interpreter = Interpreter(modelBuffer)
            Log.d("Cartoonizer", "Modelo carregado com sucesso!")
        } catch (e: IOException) {
            Log.e("Cartoonizer", "Erro ao carregar modelo TFLite: ${e.message}")
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd("1.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = assetFileDescriptor.startOffset
        val declaredLength: Long = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            .order(ByteOrder.nativeOrder())
    }

    fun processImage(bitmap: Bitmap): Bitmap {
        if (interpreter == null) {
            Log.e("Cartoonizer", "Erro: Modelo não foi carregado corretamente.")
            return bitmap
        }

        val inputSize = 512

        Log.d("Cartoonizer", "Iniciando processamento da imagem...")

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)
        val outputBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4).order(ByteOrder.nativeOrder())

        Log.d("Cartoonizer", "Rodando modelo TensorFlow Lite...")
        interpreter?.run(inputBuffer, outputBuffer)
        Log.d("Cartoonizer", "Processamento concluído!")

        return convertByteBufferToBitmap(outputBuffer, inputSize, inputSize)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputSize = 512
        val buffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3) // 3 channels (RGB)
        buffer.order(ByteOrder.nativeOrder())

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val pixels = IntArray(inputSize * inputSize)
        resizedBitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }

        return buffer
    }

    private fun convertByteBufferToBitmap(buffer: ByteBuffer, width: Int, height: Int): Bitmap {
        buffer.rewind()
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)

        for (i in 0 until width * height) {
            val r = (buffer.float * 255).toInt()
            val g = (buffer.float * 255).toInt()
            val b = (buffer.float * 255).toInt()
            pixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }

        outputBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return outputBitmap
    }
}