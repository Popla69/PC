package com.popla.perplexitydemo.presentation.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.popla.perplexitydemo.R
import com.popla.perplexitydemo.data.model.SearchMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class BrowserFragment : Fragment() {
    
    private val viewModel: BrowserViewModel by viewModels()
    
    private lateinit var webView: WebView
    private lateinit var searchEditText: EditText
    private lateinit var voiceSearchButton: ImageButton
    private lateinit var searchModeChips: ChipGroup
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var aiSummaryFab: FloatingActionButton
    
    private val voiceSearchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                searchEditText.setText(matches[0])
                performSearch(matches[0])
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupWebView()
        setupSearchFunctionality()
        setupObservers()
        
        // Load default search page
        loadSearchEngine()
    }
    
    private fun initViews(view: View) {
        webView = view.findViewById(R.id.webView)
        searchEditText = view.findViewById(R.id.searchEditText)
        voiceSearchButton = view.findViewById(R.id.voiceSearchButton)
        searchModeChips = view.findViewById(R.id.searchModeChips)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        aiSummaryFab = view.findViewById(R.id.aiSummaryFab)
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
        }
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                swipeRefresh.isRefreshing = false
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                
                // Update search bar with current URL
                url?.let { 
                    if (it.startsWith("http")) {
                        searchEditText.setText(it)
                    }
                }
            }
            
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                Toast.makeText(context, "Failed to load page", Toast.LENGTH_SHORT).show()
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
    
    private fun setupSearchFunctionality() {
        // Search on Enter key
        searchEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }
        
        // Voice search
        voiceSearchButton.setOnClickListener {
            startVoiceSearch()
        }
        
        // Search mode chips
        searchModeChips.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedMode = when (checkedIds[0]) {
                    R.id.chipAcademic -> SearchMode.ACADEMIC
                    R.id.chipNews -> SearchMode.GENERAL // News search
                    R.id.chipReddit -> SearchMode.GENERAL // Reddit search
                    R.id.chipYouTube -> SearchMode.GENERAL // YouTube search
                    else -> SearchMode.GENERAL
                }
                viewModel.setSearchMode(selectedMode)
            }
        }
        
        // Swipe to refresh
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }
        
        // AI Summary FAB
        aiSummaryFab.setOnClickListener {
            generateAISummary()
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                // Handle search results if needed
            }
        }
    }
    
    private fun performSearch(query: String) {
        val searchUrl = when {
            isValidUrl(query) -> query
            else -> buildSearchUrl(query)
        }
        
        webView.loadUrl(searchUrl)
        
        // Also perform AI-powered search
        viewModel.performSearch(query)
    }
    
    private fun buildSearchUrl(query: String): String {
        val selectedChipId = searchModeChips.checkedChipId
        return when (selectedChipId) {
            R.id.chipAcademic -> "https://scholar.google.com/scholar?q=${Uri.encode(query)}"
            R.id.chipNews -> "https://news.google.com/search?q=${Uri.encode(query)}"
            R.id.chipReddit -> "https://www.reddit.com/search/?q=${Uri.encode(query)}"
            R.id.chipYouTube -> "https://www.youtube.com/results?search_query=${Uri.encode(query)}"
            else -> "https://www.google.com/search?q=${Uri.encode(query)}"
        }
    }
    
    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://") || 
               url.contains(".") && !url.contains(" ")
    }
    
    private fun loadSearchEngine() {
        webView.loadUrl("https://www.google.com")
    }
    
    private fun startVoiceSearch() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your search query")
            }
            voiceSearchLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Voice search not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun generateAISummary() {
        val currentUrl = webView.url
        if (currentUrl != null && currentUrl.startsWith("http")) {
            viewModel.generatePageSummary(currentUrl)
            Toast.makeText(context, "Generating AI summary...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No page loaded to summarize", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun canGoBack(): Boolean = webView.canGoBack()
    
    fun goBack() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}