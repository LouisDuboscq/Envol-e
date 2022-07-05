package com.lduboscq.envolee.android

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lduboscq.envolee.presentation.Localizable


@Composable
fun ChooseStudentOrTeacherScreen(navController: NavHostController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate("login")
    }

    val signUpAsStudent: () -> Unit = {
        navController.navigate("signup-student")
    }

    val signUpAsTeacher: () -> Unit = {
        navController.navigate("signup-teacher")
    }

    ChooseStudentOrTeacherScreen(
        navigateToLogin = navigateToLogin,
        signUpAsStudent = signUpAsStudent,
        signUpAsTeacher = signUpAsTeacher
    )
}

@Composable
fun ChooseStudentOrTeacherScreen(
    navigateToLogin: () -> Unit,
    signUpAsStudent: () -> Unit,
    signUpAsTeacher: () -> Unit
) {
    val context = LocalContext.current
    Column {
        TopRightButton(
            Localizable.getLoginString().toString(context),
            modifier = Modifier.padding(horizontal = paddingHorizontal, vertical = 8.dp),
            navigateToLogin
        )
        Spacer(Modifier.height(64.dp))
        Text(
            Localizable.getStarted().toString(context),
            modifier = Modifier.padding(start = paddingHorizontal),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(16.dp))
        Text(
            Localizable.chooseRole().toString(context),
            modifier = Modifier.padding(start = paddingHorizontal),
            style = MaterialTheme.typography.bodyLarge
                .copy(color = subtitleColor)
        )
        Spacer(Modifier.height(40.dp))
        SignupAsRoleCard(
            iAm = Localizable.iAmAStudent().toString(context),
            onClick = signUpAsStudent,
            icon = Icons.Outlined.Hearing,
            subtitle = Localizable.getFeedbacks().toString(context),
        )
        Spacer(Modifier.height(16.dp))
        SignupAsRoleCard(
            iAm = Localizable.iAmATeacher().toString(context),
            onClick = signUpAsTeacher,
            icon = Icons.Outlined.Mic,
            subtitle = Localizable.giveFeedbacks().toString(context),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignupAsRoleCard(
    iAm: String,
    icon: ImageVector,
    subtitle: String,
    onClick: () -> Unit
) {
    val paddingHorizontal = 32.dp
    Card(
        modifier = Modifier.padding(horizontal = paddingHorizontal),
        onClick = { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(200.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    iAm,
                    modifier = Modifier,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    subtitle,
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = subtitleColor
                    )
                )
            }
            Image(
                imageVector = Icons.Outlined.NavigateNext,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}
