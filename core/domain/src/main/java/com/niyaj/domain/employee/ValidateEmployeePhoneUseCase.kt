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

package com.niyaj.domain.employee

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.data.repository.EmployeeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ValidateEmployeePhoneUseCase @Inject constructor(
    private val repository: EmployeeRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(phone: String, employeeId: Int? = null): ValidationResult {
        if (phone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_EMPTY_ERROR,
            )
        }

        if (phone.length != 10) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_LENGTH_ERROR,
            )
        }

        if (phone.any { !it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_LETTER_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            repository.findEmployeeByPhone(phone, employeeId)
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_PHONE_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }
}
