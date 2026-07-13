package com.example.ozonpricetracking

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : ViewModel() {
    private val _url = MutableStateFlow<String?>(null)
    val url = _url.asStateFlow()

    private val _isShowCreateDialog = MutableStateFlow(false)
    val isShowCreateDialog: StateFlow<Boolean> = _isShowCreateDialog.asStateFlow()

    fun onOpenCreateDialog() { _isShowCreateDialog.value = true }

    fun onDismiss() {
        _url.value = null
        _isShowCreateDialog.value = false
    }

    fun handleIncomingUrl(text: String?) {
        text?.let {
            _url.value = extractUrl(text)
            _isShowCreateDialog.value = true
        }
    }

    private fun extractUrl(text: String): String {
        val regex = "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]+".toRegex()
        return regex.find(text)?.value ?: text
    }
}