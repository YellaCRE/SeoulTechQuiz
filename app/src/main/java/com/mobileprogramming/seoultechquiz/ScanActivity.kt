package com.mobileprogramming.seoultechquiz

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.googlecode.tesseract.android.TessBaseAPI
import com.mobileprogramming.seoultechquiz.databinding.ActivityScanBinding
import java.io.*

 // commit test

class ScanActivity : AppCompatActivity() {
    private val activityResult : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK && it.data != null){
                val uri = it.data!!.data

                Glide.with(this)
                    .load(uri)
                    .into(binding.imageView)
            }
        }

    var image : Bitmap? = null // 사용되는 이미지
    var datapath = "" // 언어데이터가 있는 경로
    var OCRTextView : TextView? = null // OCR 결과뷰
    private var mTess : TessBaseAPI? = null // Tess API reference
    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        OCRTextView = binding.OCRTextView

        // 언어파일 경로
        datapath = "$filesDir/tesseract/"

        // 트레이닝데이터가 카피되어 있는지 체크
        checkFile(File(datapath + "tessdata/"))

        // Tesseract API 언어 세팅
        val lang = "eng"

        // OCR 세팅
        mTess = TessBaseAPI()
        mTess!!.init(datapath, lang)

        // 갤러리에서 사진 가져오기
        binding.GalleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        binding.CopyButton.setOnClickListener { clipBoard(binding.OCRTextView.text as String) }
    }

    // 이미지에서 텍스트 스캔
    fun processImage(view: View?) {
        var OCRresult: String? = null
        var drawable = binding.imageView.drawable as BitmapDrawable
        image = drawable.bitmap
        mTess!!.setImage(image)
        OCRresult = mTess!!.utF8Text
        OCRTextView!!.text = OCRresult
    }

    // 언어 데이터 파일, 디바이스에 복사
    private val langFileName = "eng.traineddata"
    private fun copyFiles() {
        try {
            val filepath = datapath + "tessdata/" + langFileName
            val assetManager = assets
            val instream: InputStream = assetManager.open(langFileName)
            val outstream: OutputStream = FileOutputStream(filepath)
            val buffer = ByteArray(1024)
            var read: Int
            while (instream.read(buffer).also { read = it } != -1) {
                outstream.write(buffer, 0, read)
            }
            outstream.flush()
            outstream.close()
            instream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // 디바이스에 언어 데이터 파일 존재 유무 체크
    private fun checkFile(dir: File) {
        // 디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles()
        }
        // 디렉토리가 있지만 파일이 없으면 파일카피 진행
        if (dir.exists()) {
            val datafilepath = datapath + "tessdata/" + langFileName
            val datafile = File(datafilepath)
            if (!datafile.exists()) {
                copyFiles()
            }
        }
    }

    // 버튼 클릭 시, 클립보드에 텍스트 복사
    private fun clipBoard(message: String) {
        val clipboardManager: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Quiz", message)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "클립보드에 저장됨", Toast.LENGTH_SHORT).show()
    }
}