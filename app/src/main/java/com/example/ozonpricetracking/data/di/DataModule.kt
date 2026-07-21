package com.example.ozonpricetracking.data.di

import android.content.Context
import com.example.ozonpricetracking.data.remote.OzonParser
import com.example.ozonpricetracking.data.remote.PageParser
import com.example.ozonpricetracking.data.remote.WebViewPageLoader
import com.example.ozonpricetracking.data.repository.ProductRepositoryImpl
import com.example.ozonpricetracking.domain.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @IntoMap
    @StringKey("ozon.ru")
    fun bindOzonParser(ozonParser: OzonParser): PageParser

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
