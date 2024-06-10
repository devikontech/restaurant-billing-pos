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

package com.niyaj.address.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.address.components.AddressData
import com.niyaj.address.destinations.AddEditAddressScreenDestination
import com.niyaj.common.tags.AddressTestTags
import com.niyaj.common.tags.AddressTestTags.ADDRESS_ITEM_TAG
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NOT_AVAILABLE
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_BTN
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_BTN_TEXT
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_FILE_NAME
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.Address
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.ADDRESS_EXPORT_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination(route = ADDRESS_EXPORT_SCREEN)
@Composable
fun AddressExportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddressSettingsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    val exportedItems by viewModel.exportedItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()

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

    val context = LocalContext.current

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeDataAsync(context, it, exportedItems)

                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("${exportedItems.size} Items has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export addresses.")
                    }
                }
            }
        }

    AddressExportScreenContent(
        modifier = Modifier,
        items = addresses.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClearClick = viewModel::clearSearchText,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickOpenSearch = viewModel::openSearchBar,
        onClickCloseSearch = viewModel::closeSearchBar,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onSelectItem = viewModel::selectItem,
        onClickExport = {
            scope.launch {
                val result = ImportExport.createFile(
                    context = context,
                    fileName = EXPORT_ADDRESS_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(AddressSettingsEvent.GetExportedItems)
            }
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditAddressScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun AddressExportScreenContent(
    modifier: Modifier = Modifier,
    items: ImmutableList<Address>,
    selectedItems: ImmutableList<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickOpenSearch: () -> Unit,
    onClickCloseSearch: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onClickExport: () -> Unit,
    onBackClick: () -> Unit,
    onClickToAddItem: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp,  SpaceSmallMax, SpaceLarge)
) {
    TrackScreenViewEvent(screenName = ADDRESS_EXPORT_SCREEN)

    val text = if (searchText.isEmpty()) ADDRESS_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onClickCloseSearch()
        } else {
            onBackClick()
        }
    }

    PoposSecondaryScaffold(
        title = if (selectedItems.isEmpty()) EXPORT_ADDRESS_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        showBottomBar = items.isNotEmpty(),
        showSecondaryBottomBar = true,
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for Addresses...",
                    onClearClick = onClearClick,
                    onSearchTextChanged = onSearchTextChanged,
                )
            } else {
                if (items.isNotEmpty()) {
                    IconButton(
                        onClick = onClickSelectAll,
                    ) {
                        Icon(
                            imageVector = PoposIcons.Checklist,
                            contentDescription = Constants.SELECT_ALL_ICON,
                        )
                    }

                    IconButton(
                        onClick = onClickOpenSearch,
                        modifier = Modifier.testTag(NAV_SEARCH_BTN),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Search,
                            contentDescription = "Search Icon",
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} addresses will be exported.")

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_ADDRESS_BTN),
                    enabled = items.isNotEmpty(),
                    text = EXPORT_ADDRESS_BTN_TEXT,
                    icon = PoposIcons.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = onClickExport,
                )
            }
        },
        onBackClick = if (showSearchBar) onClickCloseSearch else onBackClick,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyGridState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClickDeselect,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = "Deselect All",
                )
            }
        },
    ) {
        AddressExportScreenData(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            emptyText = text,
            items = items,
            onClickToAddItem = onClickToAddItem,
            onSelectItem = onSelectItem,
            doesSelected = selectedItems::contains,
            lazyGridState = lazyGridState,
        )
    }
}

@Composable
private fun AddressExportScreenData(
    modifier: Modifier = Modifier,
    emptyText: String = ADDRESS_NOT_AVAILABLE,
    items: ImmutableList<Address>,
    onClickToAddItem: () -> Unit,
    onSelectItem: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    if (items.isEmpty()) {
        ItemNotAvailable(
            text = emptyText,
            buttonText = AddressTestTags.CREATE_NEW_ADDRESS,
            onClick = onClickToAddItem,
        )
    } else {
        TrackScrollJank(scrollableState = lazyGridState, stateName = "Exported Address::List")

        LazyVerticalGrid(
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(SpaceSmall),
            columns = GridCells.Fixed(2),
            state = lazyGridState,
        ) {
            items(
                items = items,
                key = {
                    it.addressName.plus(it.addressId)
                },
            ) { address ->
                AddressData(
                    modifier = Modifier.testTag(ADDRESS_ITEM_TAG.plus(address.addressId)),
                    item = address,
                    doesSelected = doesSelected,
                    onClick = onSelectItem,
                    onLongClick = onSelectItem,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddressExportScreenContentPreview(
    items: ImmutableList<Address> = AddressPreviewData.addressList.toImmutableList(),
) {
    PoposRoomTheme {
        AddressExportScreenContent(
            modifier = Modifier,
            items = items,
            selectedItems = persistentListOf(),
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onClickExport = {},
            onBackClick = {},
            onClickToAddItem = {},
        )
    }
}

@DevicePreviews
@Composable
private fun AddressExportScreenEmptyDataPreview() {
    PoposRoomTheme {
        AddressExportScreenData(
            items = persistentListOf(),
            onClickToAddItem = {},
            onSelectItem = {},
            doesSelected = { false },
        )
    }
}

@DevicePreviews
@Composable
private fun AddressExportScreenDataPreview(
    items: ImmutableList<Address> = AddressPreviewData.addressList.toImmutableList(),
) {
    PoposRoomTheme {
        AddressExportScreenData(
            items = items,
            onClickToAddItem = {},
            onSelectItem = {},
            doesSelected = { false },
        )
    }
}
