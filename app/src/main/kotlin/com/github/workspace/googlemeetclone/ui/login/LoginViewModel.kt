package com.github.workspace.googlemeetclone.ui.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.workspace.googlemeetclone.utils.StreamVideoHelper
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _showPasswordDialog = MutableStateFlow(false)
    val showPasswordDialog: StateFlow<Boolean> = _showPasswordDialog

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val correctPassword = "ustadku123"

    fun selectAccount(account: User) {
        _uiState.update { it.copy(selectedAccount = account) }
    }

    fun loginWithSelectedAccount() {
        if (_uiState.value.selectedAccount.id == "Ustad") {
            _showPasswordDialog.value = true
        } else {
            proceedToLogin()
        }
    }

    fun verifyPassword(password: String) {
        if (password == correctPassword) {
            proceedToLogin()
            _showPasswordDialog.value = false
            _passwordError.value = null
        } else {
            _passwordError.value = "Password salah, silahkan coba lagi"
        }
    }

    private fun proceedToLogin() {
        viewModelScope.launch {
            StreamVideoHelper.signIn(
                dataStore = StreamUserDataStore.instance(),
                user = _uiState.value.selectedAccount,
            )
        }
    }

    fun dismissPasswordDialog() {
        _showPasswordDialog.value = false
    }
}

@Stable
data class LoginUiState(
    val accounts: List<User> = listOf(
        User(id = "Ustad", name = "Ustad"),
        User(id = "Jamaah", name = "Jamaah"),
        User(id = "Umum", name = "Umum"),
    ),
    val selectedAccount: User = accounts.first(),
)
