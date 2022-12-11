package com.tmp.thermaquil.data.services

import com.tmp.thermaquil.data.database.daos.CustomerDao
import javax.inject.Inject

class CustomerLocalService @Inject constructor(private val customerDao: CustomerDao) {
}