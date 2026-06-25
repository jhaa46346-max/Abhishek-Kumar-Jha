package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppWebViewDialog(
    url: String,
    title: String,
    onDismiss: () -> Unit
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(url) }
    var pageTitle by remember { mutableStateOf(title) }
    var isLoading by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Column {
                                Text(pageTitle, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(currentUrl, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { webViewRef?.goBack() },
                                enabled = canGoBack
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            IconButton(
                                onClick = { webViewRef?.goForward() },
                                enabled = canGoForward
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
                            }
                            IconButton(onClick = { webViewRef?.reload() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reload")
                            }
                            IconButton(onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Browser")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(3.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36 NexusStudent/1.0"
                            }

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val reqUrl = request?.url?.toString() ?: return false
                                    if (reqUrl.startsWith("http://") || reqUrl.startsWith("https://")) {
                                        return false
                                    }
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(reqUrl)))
                                    } catch (_: Exception) {}
                                    return true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    view?.let {
                                        canGoBack = it.canGoBack()
                                        canGoForward = it.canGoForward()
                                        currentUrl = it.url ?: currentUrl
                                        pageTitle = it.title ?: pageTitle
                                    }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    isLoading = newProgress < 100
                                }
                            }

                            loadUrl(url)
                            webViewRef = this
                        }
                    },
                    update = { view ->
                        canGoBack = view.canGoBack()
                        canGoForward = view.canGoForward()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
