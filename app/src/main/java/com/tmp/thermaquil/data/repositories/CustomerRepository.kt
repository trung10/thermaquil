package com.tmp.thermaquil.data.repositories

import com.tmp.thermaquil.data.services.CustomerLocalService
import com.tmp.thermaquil.data.services.CustomerRemoteService
import com.tmp.thermaquil.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher

class CustomerRepository constructor(
    private val customerRemoteService: CustomerRemoteService,
    private val customerLocalService: CustomerLocalService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
}