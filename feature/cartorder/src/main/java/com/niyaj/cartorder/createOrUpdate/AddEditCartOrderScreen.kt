/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.cartorder.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_ERROR_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ADD_EDIT_CART_ORDER_SCREEN
import com.niyaj.common.tags.CartOrderTestTags.CART_ADD_ON_ITEMS
import com.niyaj.common.tags.CartOrderTestTags.CART_CHARGES_ITEMS
import com.niyaj.common.tags.CartOrderTestTags.CART_PARTNER_ITEMS
import com.niyaj.common.tags.CartOrderTestTags.CHARGES_INCLUDED_FIELD
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_ERROR_FIELD
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.common.tags.CartOrderTestTags.EDIT_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.ORDER_ID_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ORDER_TYPE_FIELD
import com.niyaj.common.tags.ProductTestTags.ADD_EDIT_PRODUCT_BUTTON
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.Address
import com.niyaj.model.Charges
import com.niyaj.model.Customer
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderType
import com.niyaj.ui.components.AnimatedTextDividerDashed
import com.niyaj.ui.components.CartAddOnItems
import com.niyaj.ui.components.CartChargesItem
import com.niyaj.ui.components.CartDeliveryPartners
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.MultiSelector
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.parameterProvider.CardOrderPreviewData
import com.niyaj.ui.parameterProvider.ChargesPreviewData
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(route = Screens.ADD_EDIT_CART_ORDER_SCREEN)
@Composable
fun AddEditCartOrderScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    cartOrderId: Int = 0,
    viewModel: AddEditCartOrderViewModel = hiltViewModel(),
) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val addresses by viewModel.addresses.collectAsStateWithLifecycle()

    val addressError by viewModel.addressError.collectAsStateWithLifecycle()
    val customerError by viewModel.customerError.collectAsStateWithLifecycle()
    val orderId by viewModel.orderId.collectAsStateWithLifecycle()

    val addOnState by viewModel.addOnItems.collectAsStateWithLifecycle()
    val chargesState by viewModel.charges.collectAsStateWithLifecycle()
    val partnerState by viewModel.deliveryPartners.collectAsStateWithLifecycle()

    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val title = if (cartOrderId == 0) CREATE_NEW_CART_ORDER else EDIT_CART_ORDER
    val icon = if (cartOrderId == 0) PoposIcons.Add else PoposIcons.Edit

    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_CART_ORDER_SCREEN)

    AddEditCartOrderScreenContent(
        title = title,
        icon = icon,
        orderId = orderId.toString(),
        modifier = modifier,
        state = viewModel.state,
        addresses = addresses,
        customers = customers,
        addressError = addressError,
        customerError = customerError,
        addOnState = addOnState,
        chargesState = chargesState,
        partnerState = partnerState,
        onBackClick = navigator::popBackStack,
        onEvent = viewModel::onEvent,
    )
}

@VisibleForTesting
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongMethod")
internal fun AddEditCartOrderScreenContent(
    orderId: String,
    state: AddEditCartOrderState,
    addresses: List<Address>,
    customers: List<Customer>,
    addOnState: UiState<List<AddOnItem>>,
    chargesState: UiState<List<Charges>>,
    partnerState: UiState<List<EmployeeNameAndId>>,
    onBackClick: () -> Unit,
    onEvent: (AddEditCartOrderEvent) -> Unit,
    modifier: Modifier = Modifier,
    addressError: String? = null,
    customerError: String? = null,
    title: String = CREATE_NEW_CART_ORDER,
    icon: ImageVector = PoposIcons.Add,
) {
    val lazyListState = rememberLazyListState()
    val height = (LocalConfiguration.current.screenHeightDp / 2).dp
    val enableBtn = listOf(addressError, customerError).all { it == null }

    var addressToggled by remember { mutableStateOf(false) }
    var customerToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PRODUCT_BUTTON),
                enabled = enableBtn,
                text = title,
                icon = icon,
                onClick = {
                    onEvent(AddEditCartOrderEvent.CreateOrUpdateCartOrder)
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "CreateOrUpdate Cart Order")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(ADD_EDIT_CART_ORDER_SCREEN)
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
        ) {
            item(ORDER_TYPE_FIELD) {
                val orderTypes = listOf(
                    OrderType.DineOut.name,
                    OrderType.DineIn.name,
                )
                val icons = listOf(
                    PoposIcons.DeliveryDining,
                    PoposIcons.DinnerDining,
                )

                MultiSelector(
                    selectedOption = state.orderType.name,
                    options = orderTypes,
                    onOptionSelect = { option ->
                        onEvent(
                            AddEditCartOrderEvent.OrderTypeChanged(OrderType.valueOf(option)),
                        )
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag(ORDER_TYPE_FIELD)
                        .fillMaxWidth(),
                    icons = icons,
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(ORDER_ID_FIELD) {
                StandardOutlinedTextField(
                    label = ORDER_ID_FIELD,
                    leadingIcon = PoposIcons.Tag,
                    value = orderId,
                    onValueChange = {},
                    readOnly = true,
                )
            }

            item(ADDRESS_NAME_FIELD) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                AnimatedVisibility(
                    visible = state.orderType != OrderType.DineIn,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(600)),
                ) {
                    ExposedDropdownMenuBox(
                        expanded = addressToggled,
                        onExpandedChange = {
                            addressToggled = !addressToggled
                        },
                    ) {
                        StandardOutlinedTextField(
                            label = ADDRESS_NAME_FIELD,
                            leadingIcon = PoposIcons.Address,
                            value = state.address.addressName,
                            onValueChange = {
                                onEvent(AddEditCartOrderEvent.AddressNameChanged(it))
                                addressToggled = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    // This is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                }
                                .menuAnchor(MenuAnchorType.PrimaryEditable),
                            isError = addressError != null,
                            errorText = addressError,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = addressToggled,
                                )
                            },
                            showClearIcon = state.address.addressName.isNotEmpty(),
                            errorTextTag = ADDRESS_NAME_ERROR_FIELD,
                            onClickClearIcon = {
                                onEvent(AddEditCartOrderEvent.AddressNameChanged(""))
                                addressToggled = true
                            },
                        )
                        if (addresses.isNotEmpty()) {
                            DropdownMenu(
                                expanded = addressToggled,
                                onDismissRequest = {
                                    addressToggled = false
                                },
                                properties = PopupProperties(
                                    focusable = false,
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true,
                                    excludeFromSystemGesture = true,
                                    clippingEnabled = true,
                                ),
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                                    .heightIn(max = height),
                            ) {
                                addresses.forEachIndexed { index, address ->
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .testTag(address.addressName)
                                            .fillMaxWidth(),
                                        onClick = {
                                            onEvent(
                                                AddEditCartOrderEvent.AddressChanged(address),
                                            )
                                            addressToggled = false
                                        },
                                        text = {
                                            Text(text = address.addressName)
                                        },
                                        leadingIcon = {
                                            CircularBox(
                                                icon = PoposIcons.Address,
                                                selected = false,
                                                text = address.addressName,
                                                showBorder = false,
                                                size = 30.dp,
                                            )
                                        },
                                    )

                                    if (index != addresses.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 44.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }

            item(CUSTOMER_PHONE_FIELD) {
                AnimatedVisibility(
                    visible = state.orderType != OrderType.DineIn,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(600)),
                ) {
                    ExposedDropdownMenuBox(
                        expanded = customerToggled,
                        onExpandedChange = {
                            customerToggled = !customerToggled
                        },
                    ) {
                        StandardOutlinedTextField(
                            label = CUSTOMER_PHONE_FIELD,
                            leadingIcon = PoposIcons.PhoneAndroid,
                            value = state.customer.customerPhone,
                            onValueChange = {
                                onEvent(
                                    AddEditCartOrderEvent.CustomerPhoneChanged(it),
                                )
                                customerToggled = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    // This is used to assign to the DropDown the same width
                                    textFieldSize = coordinates.size.toSize()
                                }
                                .menuAnchor(MenuAnchorType.PrimaryEditable),
                            isError = customerError != null,
                            errorText = customerError,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = customerToggled,
                                )
                            },
                            keyboardType = KeyboardType.Number,
                            showClearIcon = state.customer.customerPhone.isNotEmpty(),
                            errorTextTag = CUSTOMER_PHONE_ERROR_FIELD,
                            suffix = {
                                AnimatedVisibility(
                                    visible = state.customer.customerPhone.length != 10,
                                ) {
                                    PhoneNoCountBox(
                                        count = state.customer.customerPhone.length,
                                    )
                                }
                            },
                            onClickClearIcon = {
                                onEvent(AddEditCartOrderEvent.CustomerPhoneChanged(""))
                                customerToggled = true
                            },
                        )

                        if (customers.isNotEmpty()) {
                            DropdownMenu(
                                expanded = customerToggled,
                                onDismissRequest = {
                                    customerToggled = false
                                },
                                properties = PopupProperties(
                                    focusable = false,
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true,
                                    excludeFromSystemGesture = true,
                                    clippingEnabled = true,
                                ),
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                                    .heightIn(max = height),
                            ) {
                                customers.forEachIndexed { index, customer ->
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .testTag(customer.customerPhone)
                                            .fillMaxWidth(),
                                        onClick = {
                                            onEvent(
                                                AddEditCartOrderEvent.CustomerChanged(customer),
                                            )
                                            customerToggled = false
                                        },
                                        text = {
                                            Text(text = customer.customerPhone)
                                        },
                                        leadingIcon = {
                                            CircularBox(
                                                icon = PoposIcons.PhoneAndroid,
                                                selected = false,
                                                showBorder = false,
                                                size = 30.dp,
                                            )
                                        },
                                    )

                                    if (index != customers.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 44.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }

            item(CHARGES_INCLUDED_FIELD) {
                AnimatedVisibility(
                    visible = state.orderType != OrderType.DineIn && chargesState is UiState.Success,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(600)),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            modifier = Modifier.testTag(CHARGES_INCLUDED_FIELD),
                            checked = state.doesChargesIncluded,
                            onCheckedChange = {
                                onEvent(AddEditCartOrderEvent.DoesChargesIncluded)
                            },
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                        Text(
                            text = if (state.doesChargesIncluded) {
                                "Charges included"
                            } else {
                                "Charges not included"
                            },
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(CART_PARTNER_ITEMS) {
                AnimatedVisibility(
                    visible = state.orderType != OrderType.DineIn,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(600)),
                ) {
                    Crossfade(
                        targetState = partnerState,
                        label = "DeliveryPartner",
                    ) { uiState ->
                        when (uiState) {
                            is UiState.Loading -> LoadingIndicatorHalf()
                            is UiState.Empty -> {}
                            is UiState.Success -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    AnimatedTextDividerDashed(text = "Delivery Partner")

                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    CartDeliveryPartners(
                                        partners = uiState.data,
                                        selected = {
                                            state.deliveryPartnerId == it
                                        },
                                        onClick = {
                                            onEvent(AddEditCartOrderEvent.SelectDeliveryPartner(it))
                                        },
                                        backgroundColor = Color.Transparent,
                                    )

                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                }
                            }
                        }
                    }
                }
            }

            item(CART_ADD_ON_ITEMS) {
                Crossfade(
                    targetState = addOnState,
                    label = "AddOnState",
                ) { items ->
                    when (items) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> LoadingIndicatorHalf()
                        is UiState.Success -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AnimatedTextDividerDashed(text = "AddOn Items")

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                CartAddOnItems(
                                    addOnItems = items.data,
                                    selectedAddOnItem = state.selectedAddOnItems.toList(),
                                    backgroundColor = Color.Transparent,
                                    onClick = {
                                        onEvent(AddEditCartOrderEvent.SelectAddOnItem(it))
                                    },
                                )

                                Spacer(modifier = Modifier.height(SpaceSmall))
                            }
                        }
                    }
                }
            }

            item(CART_CHARGES_ITEMS) {
                Crossfade(
                    targetState = chargesState,
                    label = "Charges",
                ) { items ->
                    when (items) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> LoadingIndicatorHalf()
                        is UiState.Success -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AnimatedTextDividerDashed(text = "Charges")

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                CartChargesItem(
                                    chargesList = items.data,
                                    selectedItems = state.selectedCharges.toList(),
                                    backgroundColor = Color.Transparent,
                                    onClick = {
                                        onEvent(AddEditCartOrderEvent.SelectCharges(it))
                                    },
                                )

                                Spacer(modifier = Modifier.height(SpaceSmall))
                            }
                        }
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditCartOrderScreenContentPreview(
    modifier: Modifier = Modifier,
    addresses: List<Address> = AddressPreviewData.addressList.take(5),
    customers: List<Customer> = CustomerPreviewData.customerList.take(5),
    addOnItems: List<AddOnItem> = AddOnPreviewData.addOnItemList.take(5),
    chargesList: List<Charges> = ChargesPreviewData.chargesList.take(5),
    partnerList: List<EmployeeNameAndId> = CardOrderPreviewData.sampleEmployeeNameAndIds.take(5),
) {
    PoposRoomTheme {
        AddEditCartOrderScreenContent(
            modifier = modifier,
            orderId = "1",
            state = AddEditCartOrderState(
                orderType = OrderType.DineOut,
                doesChargesIncluded = false,
                address = Address(
                    addressId = 1,
                    addressName = "New Ladies",
                    shortName = "NL",
                ),
                customer = Customer(
                    customerId = 1,
                    customerPhone = "9078563421",
                ),
                deliveryPartnerId = 1,
                selectedAddOnItems = mutableListOf(1, 3, 4),
                selectedCharges = mutableListOf(2, 4),
            ),
            addresses = addresses,
            customers = customers,
            addressError = null,
            customerError = null,
            addOnState = UiState.Success(addOnItems),
            chargesState = UiState.Success(chargesList),
            partnerState = UiState.Success(partnerList),
            onBackClick = {},
            onEvent = {},
        )
    }
}
