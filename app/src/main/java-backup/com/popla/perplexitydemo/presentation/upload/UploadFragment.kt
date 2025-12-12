package com.popla.perplexitydemo.presentation.upload

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.popla.perplexitydemo.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

@AndroidEntryPoint
class UploadFragment : Fragment() {
    
    private val viewModel: UploadViewModel by viewModels()
    
    private lateinit var cameraButton: MaterialButton
    private lateinit var galleryButton: MaterialButton
    private lateinit var filePickerButton: MaterialButton
    private lateinit var previewCard: MaterialCardView
    private lateinit var imagePreview: ImageView
    private lateinit var fileInfoLayout: LinearLayout
    private lateinit var fileNameText: TextView
    private lateinit var fileSizeText: TextView
    private lateinit var analyzeButton: MaterialButton
    private lateinit var resultsCard: MaterialCardView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var analysisResultText: TextView
    private lateinit var actionButtonsLayout: LinearLayout
    private lateinit var copyButton: MaterialButton
    private lateinit var shareButton: MaterialButton
    
    private var currentFileUri: Uri? = null
    private var currentFileName: String? = null
    
    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                val uri = saveBitmapToFile(bitmap)
                uri?.let { 
                    handleFileSelected(it, "camera_image.jpg")
                }
            }
        }
    }
    
    // Gallery launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileName(uri) ?: "selected_image"
                handleFileSelected(uri, fileName)
            }
        }
    }
    
    // File picker launcher
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileName(uri) ?: "selected_file"
                handleFileSelected(uri, fileName)
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupClickListeners()
        setupObservers()
    }
    
    private fun initViews(view: View) {
        cameraButton = view.findViewById(R.id.cameraButton)
        galleryButton = view.findViewById(R.id.galleryButton)
        filePickerButton = view.findViewById(R.id.filePickerButton)
        previewCard = view.findViewById(R.id.previewCard)
        imagePreview = view.findViewById(R.id.imagePreview)
        fileInfoLayout = view.findViewById(R.id.fileInfoLayout)
        fileNameText = view.findViewById(R.id.fileNameText)
        fileSizeText = view.findViewById(R.id.fileSizeText)
        analyzeButton = view.findViewById(R.id.analyzeButton)
        resultsCard = view.findViewById(R.id.resultsCard)
        loadingLayout = view.findViewById(R.id.loadingLayout)
        analysisResultText = view.findViewById(R.id.analysisResultText)
        actionButtonsLayout = view.findViewById(R.id.actionButtonsLayout)
        copyButton = view.findViewById(R.id.copyButton)
        shareButton = view.findViewById(R.id.shareButton)
    }
    
    private fun setupClickListeners() {
        cameraButton.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
        
        galleryButton.setOnClickListener {
            openGallery()
        }
        
        filePickerButton.setOnClickListener {
            openFilePicker()
        }
        
        analyzeButton.setOnClickListener {
            currentFileUri?.let { uri ->
                analyzeFile(uri, currentFileName ?: "unknown")
            }
        }
        
        copyButton.setOnClickListener {
            copyAnalysisToClipboard()
        }
        
        shareButton.setOnClickListener {
            shareAnalysis()
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.analysisResult.collect { result ->
                result?.let {
                    showAnalysisResult(it)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isAnalyzing.collect { isAnalyzing ->
                if (isAnalyzing) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        }
    }
    
    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
    
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }
    
    private fun handleFileSelected(uri: Uri, fileName: String) {
        currentFileUri = uri
        currentFileName = fileName
        
        // Show preview
        previewCard.visibility = View.VISIBLE
        resultsCard.visibility = View.GONE
        
        // Display file info
        fileNameText.text = fileName
        fileSizeText.text = getFileInfo(uri)
        fileInfoLayout.visibility = View.VISIBLE
        
        // Load image preview if it's an image
        if (isImageFile(fileName)) {
            imagePreview.visibility = View.VISIBLE
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(imagePreview)
        } else {
            imagePreview.visibility = View.GONE
        }
    }
    
    private fun analyzeFile(uri: Uri, fileName: String) {
        viewModel.analyzeFile(uri, fileName)
    }
    
    private fun showLoading() {
        resultsCard.visibility = View.VISIBLE
        loadingLayout.visibility = View.VISIBLE
        analysisResultText.visibility = View.GONE
        actionButtonsLayout.visibility = View.GONE
    }
    
    private fun hideLoading() {
        loadingLayout.visibility = View.GONE
    }
    
    private fun showAnalysisResult(result: String) {
        analysisResultText.text = result
        analysisResultText.visibility = View.VISIBLE
        actionButtonsLayout.visibility = View.VISIBLE
    }
    
    private fun copyAnalysisToClipboard() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("AI Analysis", analysisResultText.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Analysis copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    private fun shareAnalysis() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, analysisResultText.text.toString())
            putExtra(Intent.EXTRA_SUBJECT, "AI File Analysis")
        }
        startActivity(Intent.createChooser(intent, "Share Analysis"))
    }
    
    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        return try {
            val file = File(requireContext().cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getFileName(uri: Uri): String? {
        return try {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) it.getString(nameIndex) else null
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getFileInfo(uri: Uri): String {
        return try {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (sizeIndex >= 0) {
                        val size = it.getLong(sizeIndex)
                        val sizeStr = formatFileSize(size)
                        val type = requireContext().contentResolver.getType(uri) ?: "Unknown"
                        "$sizeStr • $type"
                    } else "File selected"
                } else "File selected"
            } ?: "File selected"
        } catch (e: Exception) {
            "File selected"
        }
    }
    
    private fun formatFileSize(bytes: Long): String {
        val df = DecimalFormat("#.##")
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${df.format(bytes / 1024.0)} KB"
            bytes < 1024 * 1024 * 1024 -> "${df.format(bytes / (1024.0 * 1024.0))} MB"
            else -> "${df.format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
        }
    }
    
    private fun isImageFile(fileName: String): Boolean {
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
        val extension = fileName.substringAfterLast(".", "").lowercase()
        return extension in imageExtensions
    }
}