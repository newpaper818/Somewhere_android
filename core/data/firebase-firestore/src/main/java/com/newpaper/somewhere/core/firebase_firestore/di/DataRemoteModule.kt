package com.newpaper.somewhere.core.firebase_firestore.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.appVersion.AppVersionRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.appVersion.AppVersionFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.deleteAccount.DeleteAccountRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.deleteAccount.DeleteAccountFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.profile.EditProfileFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.profile.EditProfileRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.signIn.SignInFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.signIn.SignInRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.inviteFriend.InviteFriendFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.inviteFriend.InviteFriendRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trip.TripFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trip.TripRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trips.TripsFirestoreApi
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trips.TripsRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataRemoteModule {

    //common
    @Binds
    internal abstract fun bindCommonDataSource(
        commonFirestoreApi: CommonFirestoreApi
    ): CommonRemoteDataSource

    //more
    @Binds
    internal abstract fun bindAppVersionDataSource(
        appVersionFirestoreApi: AppVersionFirestoreApi
    ): AppVersionRemoteDataSource

    @Binds
    internal abstract fun bindDeleteAccountDataSource(
        deleteAccountFirestoreApi: DeleteAccountFirestoreApi
    ): DeleteAccountRemoteDataSource

    @Binds
    internal abstract fun bindProfileDataSource(
        editProfileFirestoreApi: EditProfileFirestoreApi
    ): EditProfileRemoteDataSource


    //signIn
    @Binds
    internal abstract fun bindSignInDataSource(
        signInFirestoreApi: SignInFirestoreApi
    ): SignInRemoteDataSource

    //trip
    @Binds
    internal abstract fun bindInviteFriendDataSource(
        inviteFriendFirestoreApi: InviteFriendFirestoreApi
    ): InviteFriendRemoteDataSource

    @Binds
    internal abstract fun bindTripDataSource(
        tripFirestoreApi: TripFirestoreApi
    ): TripRemoteDataSource

    @Binds
    internal abstract fun bindTripsDataSource(
        tripsFirestoreApi: TripsFirestoreApi
    ): TripsRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseFirestoreModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(

    ): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}