package com.tmp.thermaquil.data.services

import com.tmp.thermaquil.data.apis.CustomerAPI
import javax.inject.Inject

class CustomerRemoteService @Inject constructor(private val customerAPI: CustomerAPI) {
}