package com.example.hw8

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Post (
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String) : Parcelable