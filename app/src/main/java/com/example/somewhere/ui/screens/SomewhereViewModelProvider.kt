package com.example.somewhere.ui.screens

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.somewhere.SomewhereApplication
import com.example.somewhere.ui.screens.main.MainViewModel
import com.example.somewhere.ui.screens.somewhere.SomewhereViewModel
import com.example.somewhere.ui.screens.trip.TripViewModel
import java.lang.IllegalStateException

object SomewhereViewModelProvider {
    val Factory = viewModelFactory {
        //Initializer for SomewhereViewModel
        initializer {
            SomewhereViewModel(
                SomewhereApplication().container.tripsRepository
            )
        }

        //MainViewModel
        initializer {
            MainViewModel(
                SomewhereApplication().container.tripsRepository
            )
        }

        //TripViewModel
        initializer {
            TripViewModel(
                this.createSavedStateHandle(),
                SomewhereApplication().container.tripsRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [SomewhereApplication].
 */
fun CreationExtras.SomewhereApplication(): SomewhereApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SomewhereApplication)


