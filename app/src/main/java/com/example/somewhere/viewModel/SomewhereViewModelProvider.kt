package com.example.somewhere.viewModel

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.somewhere.SomewhereApplication

object SomewhereViewModelProvider {
    val Factory = viewModelFactory {
        //Initializer for
        //AppViewModel
        initializer {
            AppViewModel(
                SomewhereApplication().container.tripsRepository,
                SomewhereApplication().userPreferencesRepository
            )
        }

        //SomewhereViewModel
        initializer {
            SomewhereViewModel(
                SomewhereApplication().container.tripsRepository
            )
        }

        //TripMapViewModel
//        initializer {
//            TripMapViewModel(
//
//            )
//        }

        //MainViewModel delete?
        initializer {
            MainViewModel(
                SomewhereApplication().container.tripsRepository
            )
        }

        //TripViewModel delete?
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


