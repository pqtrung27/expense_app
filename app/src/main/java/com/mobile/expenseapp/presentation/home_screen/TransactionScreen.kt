package com.mobile.expenseapp.presentation.home_screen

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mobile.expenseapp.R
import com.mobile.expenseapp.common.Constants
import com.mobile.expenseapp.presentation.home_screen.components.AccountTag
import com.mobile.expenseapp.presentation.home_screen.components.EntryTypePicker
import com.mobile.expenseapp.presentation.home_screen.components.InfoBanner
import com.mobile.expenseapp.presentation.home_screen.components.KeypadComponent
import com.mobile.expenseapp.presentation.home_screen.components.TabButton
import com.mobile.expenseapp.presentation.navigation.Screen
import com.mobile.expenseapp.presentation.ui.theme.Amber500
import com.mobile.expenseapp.presentation.ui.theme.DarkSecondary100
import com.mobile.expenseapp.presentation.ui.theme.Red200
import com.mobile.expenseapp.util.spacing
import kotlinx.coroutines.launch
import java.security.KeyStore.Entry
import java.util.Calendar

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalUnitApi
@ExperimentalMaterialApi
@Composable
fun TransactionScreen(
    navController: NavController,
    transactionTag: Int?,
    transactionDate: String?,
    transactionPos: Int?,
    transactionStatus: Int?,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val transactionType = TransactionType.values()[transactionTag!!]
    val scope = rememberCoroutineScope()
    val keypadBottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val title by remember { mutableStateOf(homeViewModel.transactionTitle) }
    val titleFieldValue = TextFieldValue(title.collectAsState().value)
    val showInfoBanner by homeViewModel.showInfoBanner.collectAsState()
    val expenseAmount by homeViewModel.transactionAmount.collectAsState()
    val currencyCode by homeViewModel.selectedCurrencyCode.collectAsState()
    val limitKey by homeViewModel.limitKey.collectAsState()
    val limitInfoWarning by homeViewModel.limitAlert.collectAsState(initial = HomeViewModel.UIEvent.NoAlert())

    BottomSheetScaffold(
        sheetContent = {
            KeypadComponent(
                bottomSheetScaffoldState = keypadBottomSheetState
            ) {
                homeViewModel.setTransaction(it)
            }
        },
        scaffoldState = keypadBottomSheetState,
        sheetPeekHeight = 0.dp,
        sheetContentColor = MaterialTheme.colors.background
    ) {
        LaunchedEffect(key1 = transactionPos) {
            if (transactionPos != -1) {
                homeViewModel.displayTransaction(transactionDate, transactionPos, transactionStatus)
            }
            homeViewModel.displayExpenseLimitWarning()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(bottom = it.calculateBottomPadding())
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = MaterialTheme.spacing.small,
                            end = MaterialTheme.spacing.medium,
                            top = MaterialTheme.spacing.small
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .scale(0.75f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.remove),
                            contentDescription = "close",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .scale(0.8f)
                        )
                    }

                    Text(
                        text = "Add Transaction",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.onSurface
                    )

                    IconButton(
                        onClick = {
                            homeViewModel.apply {
                                if (transactionPos == -1) {
                                    setCurrentTime(Calendar.getInstance().time)
                                    if (transactionType == TransactionType.INCOME) {
                                        insertDailyTransaction(
                                            date.value,
                                            transactionAmount.value.toDouble(),
                                            category.value.title,
                                            Constants.INCOME, transactionTitle.value
                                        ) {
                                            navController.navigateUp()
                                        }
                                    } else {
                                        insertDailyTransaction(
                                            date.value,
                                            transactionAmount.value.toDouble(),
                                            category.value.title,
                                            Constants.EXPENSE, transactionTitle.value
                                        ) {
                                            navController.navigateUp()
                                        }
                                    }
                                } else {
                                    updateTransaction(
                                        transactionDate,
                                        transactionPos,
                                        transactionStatus
                                    ) {
                                        navController.navigateUp()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .scale(0.8f)
                            .background(MaterialTheme.colors.surface, CircleShape)

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.enter),
                            contentDescription = "enter",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .scale(0.8f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Make this the way to pick the transaction type, not the scaffold
                    EntryTypePicker()

                    InfoBanner(shown = showInfoBanner, transactionType)
                    
                    // Amount title
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.small
                            )
                            .align(Alignment.Start)
                    )

                    // Amount number
                    TextButton(
                        onClick = {
                            scope.launch {
                                keyboardController?.hide()
                                if (keypadBottomSheetState.bottomSheetState.isCollapsed)
                                    keypadBottomSheetState.bottomSheetState.expand()
                                else keypadBottomSheetState.bottomSheetState.collapse()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .padding(
                                start = MaterialTheme.spacing.medium,
                                end = MaterialTheme.spacing.medium
                            ),
                        colors = ButtonDefaults.textButtonColors(DarkSecondary100),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.spacing.medium,
                            vertical = MaterialTheme.spacing.small
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.W300,
                                        fontSize = 24.sp
                                    )
                                ) {
                                    append(currencyCode)
                                    append(expenseAmount.amountFormat())
                                }
                            },
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .padding(
                                    horizontal = MaterialTheme.spacing.medium,
                                    vertical = MaterialTheme.spacing.small
                                )
                        )
                    }

                    // Picker
//                    LazyRow(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .padding(
//                                horizontal = MaterialTheme.spacing.medium,
//                                vertical = MaterialTheme.spacing.small
//                            )
//                            .align(Alignment.Start)
//                    ) {
//                        items(Account.values()) { account ->
//                            AccountTag(account = account, navController = navController)
//                        }
//                    }

                    // Account type
                    Text(
//                        text = if (transactionType == TransactionType.INCOME) {
//                            "Fund"
//                        } else "Pay with",
                        text = "Account",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.small
                            )
                            .align(Alignment.Start),
                    )

                    // Account picker
                    TextButton(
                        onClick = {
                            navController.navigate(Screen.AccountChooserScreen.route)
                        },
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.small
                            ),
                        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.spacing.medium,
                            vertical = MaterialTheme.spacing.medium
                        ),
                    ) {
                        Text(
                            text = "Choose an account",
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colors.onSurface
                        )
                    }

                    // Category type
                    Text(
//                        text = if (transactionType == TransactionType.INCOME) {
//                            "Fund"
//                        } else "Pay with",
                        text = "Category",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.small
                            )
                            .align(Alignment.Start),
                    )

                    // Category type picker
                    TextButton(
                        onClick = {
                            navController.navigate(Screen.CategoryChooserScreen.route)
                        },
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.small
                            ),
                        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.spacing.medium,
                            vertical = MaterialTheme.spacing.medium
                        ),
                    ) {
                        Text(
                            text = "Choose a category",
                            color = MaterialTheme.colors.onSurface
                        )
                    }

                    // Note field
                    NoteTextField(titleFieldValue, homeViewModel)

                    // Set time interval
                    SetRepeatable()

                    if (limitKey) {
                        if (limitInfoWarning is HomeViewModel.UIEvent.Alert) {
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .padding(
                                        horizontal = MaterialTheme.spacing.medium
                                    )
                                    .align(Alignment.Start)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.info_warning),
                                    contentDescription = null,
                                    tint = Red200
                                )
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                                    Text(
                                        text = (limitInfoWarning as HomeViewModel.UIEvent.Alert).info,
                                        style = MaterialTheme.typography.caption
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountPicker() {

}

@Composable
fun NoteTextField(
    noteTextField: TextFieldValue,
    homeViewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Note
        Text(
            text = "Note",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.spacing.medium,
                    vertical = MaterialTheme.spacing.small
                )
                .align(Alignment.Start)
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.medium
                ),
//                .border(BorderStroke(1.dp, MaterialTheme.colors.primary)),
            shape = RoundedCornerShape(6.dp),
            value = noteTextField.text,
            onValueChange = { field -> homeViewModel.setTransactionTitle(field) },
            label = { Text("Add note") },
        )
    }
}

@Composable
fun SetRepeatable() {
    val checkedState = remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = MaterialTheme.spacing.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it },
            colors = CheckboxDefaults.colors(MaterialTheme.colors.primary)
        )
        
        Text(text = "Set as auto-transaction")
    }

    // checkbox (not finished)
//    if (checkedState) {
//        Column {
//
//        }
//    }

    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        TextButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
//                .align(Alignment.Start)
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(
                    horizontal = MaterialTheme.spacing.medium,
                    vertical = MaterialTheme.spacing.small
                ),
            border = BorderStroke(1.dp, MaterialTheme.colors.primary),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.medium
            ),
        ) {
            Text(text = "Repeat timer")
        }
    }

}