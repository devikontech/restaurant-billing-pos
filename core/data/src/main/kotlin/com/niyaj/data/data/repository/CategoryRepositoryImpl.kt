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

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.CategoryConstants
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.validation.CategoryValidationRepository
import com.niyaj.database.dao.CategoryDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.searchCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : CategoryRepository, CategoryValidationRepository {

    override suspend fun getAllCategory(searchText: String): Flow<List<Category>> {
        return withContext(ioDispatcher) {
            categoryDao.getAllCategories().mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchCategory(searchText)
            }
        }
    }

    override suspend fun getCategoryById(categoryId: Int): Resource<Category?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(categoryDao.getCategoryById(categoryId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertCategory(newCategory: Category): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategoryName =
                    validateCategoryName(newCategory.categoryName, newCategory.categoryId)

                if (validateCategoryName.successful) {
                    val result = categoryDao.upsertCategory(newCategory.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error(validateCategoryName.errorMessage ?: "Unable")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable")
        }
    }

    override suspend fun deleteCategories(categoryIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                withContext(ioDispatcher) {
                    val result = categoryDao.deleteCategories(categoryIds)

                    Resource.Success(result > 0)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete categories")
        }
    }

    override suspend fun validateCategoryName(
        categoryName: String,
        categoryId: Int?,
    ): ValidationResult {
        if (categoryName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CategoryConstants.CATEGORY_NAME_EMPTY_ERROR,
            )
        }

        if (categoryName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = CategoryConstants.CATEGORY_NAME_LENGTH_ERROR,
            )
        }

        val serverResult = withContext(ioDispatcher) {
            categoryDao.findCategoryByName(categoryName, categoryId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = CategoryConstants.CATEGORY_NAME_ALREADY_EXIST_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override suspend fun importCategoriesToDatabase(categories: List<Category>): Resource<Boolean> {
        try {
            categories.forEach { category ->
                val validateCategoryName =
                    validateCategoryName(category.categoryName, category.categoryId)

                if (validateCategoryName.successful) {
                    withContext(ioDispatcher) {
                        categoryDao.upsertCategory(category.toEntity())
                    }
                } else {
                    return Resource.Error(validateCategoryName.errorMessage ?: "Unable")
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unable")
        }
    }
}
