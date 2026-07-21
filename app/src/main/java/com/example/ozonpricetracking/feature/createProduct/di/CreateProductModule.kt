package com.example.ozonpricetracking.feature.createProduct.di

import com.example.ozonpricetracking.feature.createProduct.data.repository.CreateProductRepositoryImpl
import com.example.ozonpricetracking.feature.createProduct.domain.CreateProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CreateProductModule {
    @Binds
    @Singleton
    fun bindCreateProductRepository(
        createProductRepositoryImpl: CreateProductRepositoryImpl
    ): CreateProductRepository
}