package com.niyaj.customer.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.model.Customer
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerSettingsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val customers = snapshotFlow { mSearchText.value }.flatMapLatest {
        customerRepository.getAllCustomer(it)
    }.mapLatest { list ->
        totalItems = list.map { it.customerId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<Customer>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<Customer>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: CustomerSettingsEvent) {
        when (event) {
            is CustomerSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = customers.value
                    } else {
                        val list = mutableListOf<Customer>()

                        mSelectedItems.forEach { id ->
                            val category = customers.value.find { it.customerId == id }

                            if (category != null) {
                                list.add(category)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }

                    analyticsHelper.logExportedCustomersToFile(_exportedItems.value.size)
                }
            }

            is CustomerSettingsEvent.OnImportCustomerItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.customerId }
                        _importedItems.value = event.data
                    }

                    analyticsHelper.logImportedCustomersFromFile(event.data.size)
                }
            }

            is CustomerSettingsEvent.ImportCustomerItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { customerId ->
                            _importedItems.value.filter { it.customerId == customerId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = customerRepository.importCustomerToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} customers has been imported successfully"))
                            analyticsHelper.logImportedCustomersToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}


internal fun AnalyticsHelper.logImportedCustomersFromFile(totalCustomers: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "customer_imported_from_file",
            extras = listOf(
                AnalyticsEvent.Param("customer_imported_from_file", totalCustomers.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedCustomersToDatabase(totalCustomers: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "customer_imported_to_database",
            extras = listOf(
                AnalyticsEvent.Param("customer_imported_to_database", totalCustomers.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedCustomersToFile(totalCustomers: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "customer_exported_to_file",
            extras = listOf(
                AnalyticsEvent.Param("customer_exported_to_file", totalCustomers.toString()),
            ),
        ),
    )
}