package com.newpaper.somewhere.core.model.tripData

data class TripsGroup(
    val all: List<Trip> = listOf(),
    val active: List<Trip> = listOf(),
    val past: List<Trip> = listOf()
){
    fun sortOrder(
        orderByLatest: Boolean
    ): TripsGroup {
        return if (orderByLatest) {
            copy(
                all = all.sortedByDescending { it.startDate },
                active = active.sortedByDescending { it.startDate },
                past = past.sortedByDescending { it.startDate }
            )
        } else {
            copy(
                all = all.sortedBy { it.startDate },
                active = active.sortedBy { it.startDate },
                past = past.sortedBy { it.startDate }
            )
        }
    }
}