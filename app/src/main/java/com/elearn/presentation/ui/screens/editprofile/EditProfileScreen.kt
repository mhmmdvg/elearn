package com.elearn.presentation.ui.screens.editprofile

import ActionBar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.elearn.presentation.ui.components.ButtonVariant
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.screens.auth.AuthViewModel
import com.elearn.presentation.ui.screens.editprofile.components.EditDescForm
import com.elearn.presentation.ui.screens.editprofile.components.EditNameForm
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    userId: String
) {
    val context = LocalContext.current

    /* State */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editName by remember { mutableStateOf(false) }
    var editDescription by remember { mutableStateOf(false) }
    val updateImageState by viewModel.updateProfileImageState.collectAsState()
    val scrollState = rememberScrollState()

    val imagePickerLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            viewModel.updateProfileImage(context, userId, selectedUri)
        }
    }

    val userInfoState by authViewModel.userInfoState.collectAsState()

    /* Form State */
    val state = viewModel.state.value

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(userInfoState) {
        userInfoState.data?.let {
            viewModel.populateFormFields(it)
        }
    }

    if (editName) {
        ModalBottomSheet(
            onDismissRequest = { editName = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.height(screenHeight * 0.95f)
            ) {
                EditNameForm(
                    userId = userId,
                    onSuccess = {
                        authViewModel.getCurrentUserDetails()
                        editName = false
                    }
                )
            }
        }
    }

    if (editDescription) {
        ModalBottomSheet(
            onDismissRequest = { editDescription = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.height(screenHeight * 0.95f)
            ) {
                EditDescForm(
                    userId = userId,
                    onSuccess = {
                        authViewModel.getCurrentUserDetails()
                        editDescription = false
                    }
                )
            }
        }
    }

   Column(
       verticalArrangement = Arrangement.spacedBy(12.dp)
   ) {

       ActionBar(
           title = "Profile",
           onBackClick = { navController.popBackStack() }
       )

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = MutedColor, shape = CircleShape
                        )
                ) {
                    CacheImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        imageUrl = userInfoState.data?.data?.imageUrl
                            ?: "https://github.com/shadcn.png",
                        description = "Avatar",
                    )
                }

                CustomButton(
                    variant = ButtonVariant.Outline,
                    text = if (updateImageState is Resource.Loading) "Uploading..." else "Edit",
                    onClick = {
                        imagePickerLaunch.launch("image/*")
                    },
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = "Name",
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            editName = true
                        }
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${state.firstName} ${state.lastName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedForegroundColor
                        )

                        Icon(
                            imageVector = Lucide.ChevronRight,
                            contentDescription = "open",
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = "About you",
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            editDescription = true
                        }
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = userInfoState.data?.data?.description ?: "Tell your story",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedForegroundColor
                        )

                        Icon(
                            imageVector = Lucide.ChevronRight,
                            contentDescription = "open"
                        )
                    }
                }
            }

        }
    }
}