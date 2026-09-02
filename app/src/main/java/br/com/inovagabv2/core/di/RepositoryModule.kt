package br.com.inovagabv2.core.di

import br.com.inovagabv2.data.repository.AuthRepositoryImpl
import br.com.inovagabv2.data.repository.DashboardRepositoryImpl
import br.com.inovagabv2.data.repository.IdeaRepositoryImpl
import br.com.inovagabv2.data.repository.ProjectRepositoryImpl
import br.com.inovagabv2.data.repository.StrategyRepositoryImpl
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.DashboardRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindIdeaRepository(
        ideaRepositoryImpl: IdeaRepositoryImpl
    ): IdeaRepository

    @Binds
    @Singleton
    abstract fun bindStrategyRepository(
        strategyRepositoryImpl: StrategyRepositoryImpl
    ): StrategyRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        projectRepositoryImpl: ProjectRepositoryImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl
    ): DashboardRepository
}
