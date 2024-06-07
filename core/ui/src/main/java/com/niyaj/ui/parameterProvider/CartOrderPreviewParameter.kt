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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.Customer
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType

class CartOrderPreviewParameter : PreviewParameterProvider<List<CartOrder>> {
    override val values: Sequence<List<CartOrder>>
        get() = sequenceOf(
            listOf(
                CartOrder(
                    orderId = 1,
                    orderType = OrderType.DineIn,
                    orderStatus = OrderStatus.PLACED,
                    doesChargesIncluded = true,
                    customer = Customer(
                        customerId = 1,
                        customerPhone = "1234567890",
                        customerName = "John Doe",
                        customerEmail = "john.doe@example.com",
                    ),
                    address = Address(
                        addressId = 1,
                        addressName = "123 Main Street",
                        shortName = "Main St",
                    ),
                    deliveryPartnerId = 0,
                ),
                CartOrder(
                    orderId = 2,
                    orderType = OrderType.DineOut,
                    orderStatus = OrderStatus.PROCESSING,
                    doesChargesIncluded = false,
                    customer = Customer(
                        customerId = 2,
                        customerPhone = "9876543210",
                        customerName = "Jane Smith",
                        customerEmail = "jane.smith@example.com",
                    ),
                    address = Address(
                        addressId = 2,
                        addressName = "456 Oak Avenue",
                        shortName = "Oak Ave",
                    ),
                    deliveryPartnerId = 1,
                ),
                CartOrder(
                    orderId = 3,
                    orderType = OrderType.DineIn,
                    orderStatus = OrderStatus.PLACED,
                    doesChargesIncluded = true,
                    customer = Customer(
                        customerId = 3,
                        customerPhone = "5555555555",
                        customerName = "Michael Johnson",
                        customerEmail = "michael.johnson@example.com",
                    ),
                    address = Address(
                        addressId = 3,
                        addressName = "789 Maple Lane",
                        shortName = "Maple Ln",
                    ),
                    deliveryPartnerId = 0,
                ),
                CartOrder(
                    orderId = 4,
                    orderType = OrderType.DineOut,
                    orderStatus = OrderStatus.PROCESSING,
                    doesChargesIncluded = false,
                    customer = Customer(
                        customerId = 4,
                        customerPhone = "1112223333",
                        customerName = "Emily Davis",
                        customerEmail = "emily.davis@example.com",
                    ),
                    address = Address(
                        addressId = 4,
                        addressName = "321 Pine Road",
                        shortName = "Pine Rd",
                    ),
                    deliveryPartnerId = 2,
                ),
                CartOrder(
                    orderId = 5,
                    orderType = OrderType.DineIn,
                    orderStatus = OrderStatus.PLACED,
                    doesChargesIncluded = true,
                    customer = Customer(
                        customerId = 5,
                        customerPhone = "4445556666",
                        customerName = "David Wilson",
                        customerEmail = "david.wilson@example.com",
                    ),
                    address = Address(
                        addressId = 5,
                        addressName = "567 Cedar Boulevard",
                        shortName = "Cedar Blvd",
                    ),
                    deliveryPartnerId = 0,
                ),
                CartOrder(
                    orderId = 6,
                    orderType = OrderType.DineOut,
                    orderStatus = OrderStatus.PROCESSING,
                    doesChargesIncluded = false,
                    customer = Customer(
                        customerId = 6,
                        customerPhone = "7778889999",
                        customerName = "Sophia Martinez",
                        customerEmail = "sophia.martinez@example.com",
                    ),
                    address = Address(
                        addressId = 6,
                        addressName = "890 Elm Street",
                        shortName = "Elm St",
                    ),
                    deliveryPartnerId = 3,
                ),
                CartOrder(
                    orderId = 7,
                    orderType = OrderType.DineIn,
                    orderStatus = OrderStatus.PLACED,
                    doesChargesIncluded = true,
                    customer = Customer(
                        customerId = 7,
                        customerPhone = "0101010101",
                        customerName = "William Thompson",
                        customerEmail = "william.thompson@example.com",
                    ),
                    address = Address(
                        addressId = 7,
                        addressName = "246 Birch Avenue",
                        shortName = "Birch Ave",
                    ),
                    deliveryPartnerId = 0,
                ),
                CartOrder(
                    orderId = 8,
                    orderType = OrderType.DineOut,
                    orderStatus = OrderStatus.PROCESSING,
                    doesChargesIncluded = false,
                    customer = Customer(
                        customerId = 8,
                        customerPhone = "2222222222",
                        customerName = "Olivia Anderson",
                        customerEmail = "olivia.anderson@example.com",
                    ),
                    address = Address(
                        addressId = 8,
                        addressName = "135 Oak Drive",
                        shortName = "Oak Dr",
                    ),
                    deliveryPartnerId = 4,
                ),
                CartOrder(
                    orderId = 9,
                    orderType = OrderType.DineIn,
                    orderStatus = OrderStatus.PLACED,
                    doesChargesIncluded = true,
                    customer = Customer(
                        customerId = 9,
                        customerPhone = "3333333333",
                        customerName = "Jacob Brown",
                        customerEmail = "jacob.brown@example.com",
                    ),
                    address = Address(
                        addressId = 9,
                        addressName = "789 Maple Court",
                        shortName = "Maple Ct",
                    ),
                    deliveryPartnerId = 0,
                ),
                CartOrder(
                    orderId = 10,
                    orderType = OrderType.DineOut,
                    orderStatus = OrderStatus.PROCESSING,
                    doesChargesIncluded = false,
                    customer = Customer(
                        customerId = 10,
                        customerPhone = "4444444444",
                        customerName = "Avery Taylor",
                        customerEmail = "avery.taylor@example.com",
                    ),
                    address = Address(
                        addressId = 10,
                        addressName = "456 Pine Lane",
                        shortName = "Pine Ln",
                    ),
                    deliveryPartnerId = 5,
                ),
            ),
        )
}
