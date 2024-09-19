package com.github.workspace.googlemeetclone.ui.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.github.workspace.googlemeetclone.ui.theme.GoogleMeetTheme
import io.getstream.video.android.compose.ui.components.avatar.Avatar
import io.getstream.video.android.core.utils.initials
import io.getstream.video.android.model.User

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showPasswordDialog by viewModel.showPasswordDialog.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            MeetAccounts(
                uiState = uiState,
                onAccountClick = viewModel::selectAccount,
                onLoginButtonClick = viewModel::loginWithSelectedAccount,
            )
        },
    ) {
        MeetIntroductions(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            onConfirm = { password ->
                viewModel.verifyPassword(password)
            },
            onDismiss = {
                viewModel.dismissPasswordDialog()
            },
            errorMessage = passwordError
        )
    }
}

@Composable
private fun MeetAccounts(
    uiState: LoginUiState,
    onAccountClick: (User) -> Unit,
    onLoginButtonClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.clip(MaterialTheme.shapes.large),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                uiState.accounts.forEach { account ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .clickable {
                                onAccountClick(account)
                            },
                        leadingContent = {
                            Avatar(
                                modifier = Modifier.size(36.dp),
                                imageUrl = account.image,
                                initials = account.name.initials(),
                            )
                        },
                        headlineContent = { Text(text = account.name) },
                        supportingContent = { Text(text = account.id) },
                        trailingContent = if (uiState.selectedAccount.id == account.id) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircleOutline,
                                    contentDescription = "account selected indicator",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLoginButtonClick,
                ) {
                    Text("Lanjutkan sebagai ${uiState.selectedAccount.name}")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MeetIntroductions(modifier: Modifier) {
    val pagerState = rememberPagerState { 3 }
    Column(
        modifier = modifier,
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            state = pagerState,
        ) { page ->
            MeetIntroduction(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                MeetIntroductionImages[page],
                MeetIntroductionDescriptions[page],
            )
        }
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp),
                )
            }
        }
    }
}

@Composable
fun MeetIntroduction(modifier: Modifier, imageUrl: String, description: String) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        val painter = rememberAsyncImagePainter(imageUrl)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit // Change this line
        )
        Text(description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}


private val MeetIntroductionImages = listOf(
    "https://static.vecteezy.com/system/resources/previews/021/733/532/non_2x/ramadan-kareem-golden-mosque-with-transparent-background-free-png.png",
    "https://png.pngtree.com/png-clipart/20220301/original/pngtree-orang-bersorban-berdoa-png-image_7345632.png",
    "https://cdn-icons-png.flaticon.com/512/5500/5500939.png"
)

private val MeetIntroductionDescriptions = listOf(
    "Dekatkan diri dengan yang Maha Kuasa melalui ihramConnect",
    "Live ceramah yang menyejukkan hati dengan tenang",
    "Terhubung ke ratusan ribuan jamaah lainnya",
)

@Composable
fun PasswordDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Masukan Password") },
        text = {
            Column {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = errorMessage != null
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(password)
                }
            ) {
                Text("Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    GoogleMeetTheme {
        LoginScreen()
    }
}
