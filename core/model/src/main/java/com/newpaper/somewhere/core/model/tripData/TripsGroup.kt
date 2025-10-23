package com.newpaper.somewhere.core.model.tripData

data class TripsGroup(
    val all: List<Trip> = listOf(),
    val active: List<Trip> = listOf(),
    val past: List<Trip> = listOf()
)