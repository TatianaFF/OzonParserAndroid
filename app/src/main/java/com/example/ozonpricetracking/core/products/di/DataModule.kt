package com.example.ozonpricetracking.core.products.di

import android.content.Context
import com.example.ozonpricetracking.core.products.data.ProductRepositoryImpl
import com.example.ozonpricetracking.core.products.data.WebViewPageLoader
import com.example.ozonpricetracking.core.products.domain.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    companion object {
        @Provides
        @Singleton
        fun provideWebViewPageLoader(
            @ApplicationContext context: Context
        ): WebViewPageLoader {
            return WebViewPageLoader(context)
        }
    }
}
