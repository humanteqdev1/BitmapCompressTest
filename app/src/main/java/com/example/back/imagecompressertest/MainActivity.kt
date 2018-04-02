package com.example.back.imagecompressertest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    lateinit var iv: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv = findViewById(R.id.iv)

        T1(iv).execute()
    }

    class T1(val iv: ImageView) : AsyncTask<Any, Any, ByteArray?>() {
        override fun doInBackground(vararg params: Any?): ByteArray? {
            val client = OkHttpClient()
            val request = Request.Builder().url("https://images7.alphacoders.com/411/411820.jpg").build()

            val response = client.newCall(request).execute()

            return response.body()?.bytes()
        }

        override fun onPostExecute(array: ByteArray?) {
            super.onPostExecute(array)

//            val bm = array?.toBitmap()
//            iv.setImageBitmap(bm)
//            Log.e("IMG", "size: ${bm?.byteCount}")

            iv.setImageByteArray(array!!, 0, 0)
        }
    }
}


fun ImageView.setImageByteArray(byteArray: ByteArray, scaleX: Int = 0, scaleY: Int = 0) {
    var bitmap = byteArray.toBitmap()
    if (scaleX != 0 || scaleY != 0)
        bitmap = Bitmap.createScaledBitmap(bitmap, scaleX, scaleY, false)

    Log.e("IMG 2", "size: ${bitmap?.byteCount}")
    setImageBitmap(bitmap)
}

fun ByteArray.toBitmap(): Bitmap? = BitmapFactory.decodeByteArray(this, 0, this.size)

fun ByteArray.toBitmap2(): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeByteArray(this, 0, this.size, options)
    options.inSampleSize = calculateInSampleSize(options, 200, 200)
    options.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size, options)

    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out)
    bitmap.recycle()
    return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight = height / 2
        val halfWidth = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}