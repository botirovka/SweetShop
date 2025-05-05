package com.example.lingoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.botirovka.sweetshopcompose.data.FirebaseRepository
import com.botirovka.sweetshopcompose.data.Response

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class UnitTest {

    @Test
    fun GetPiesTest() = runBlocking {
        val response = FirebaseRepository.getPies()
        val isSuccess = when(response){
            is Response.Error -> false
            is Response.Success -> true
        }
        assertTrue(isSuccess)
    }

    @Test
    fun SignUpTest() = runBlocking {
        val response = FirebaseRepository.register("test321123@gmail.com", "12345678")
        val isSuccess = when(response){
            is Response.Error -> false
            is Response.Success -> true
        }
        assertTrue(isSuccess)
    }

    @Test
    fun LoginTest() = runBlocking {
        val response = FirebaseRepository.login("tesst@gmail.com", "12345678")
        val isSuccess = when(response){
            is Response.Error -> false
            is Response.Success -> true
        }
        assertTrue(isSuccess)
    }

}