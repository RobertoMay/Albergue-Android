package com.example.albergue_android.domain.models

import android.os.Parcel
import android.os.Parcelable

data class Convocatoria(
    val id: String,
    val title: String,
    val startDate: String,
    val endDate: String,
    val cupo: Int,
    val status: Boolean,
    val occupiedCupo: Int? = 0,
    val availableCupo: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeInt(cupo)
        parcel.writeByte(if (status) 1 else 0)
        parcel.writeInt(occupiedCupo ?: 0)
        parcel.writeInt(availableCupo ?: 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Convocatoria> {
        override fun createFromParcel(parcel: Parcel): Convocatoria {
            return Convocatoria(parcel)
        }

        override fun newArray(size: Int): Array<Convocatoria?> {
            return arrayOfNulls(size)
        }
    }
}
data class IConvocatoriaResponse(
    val convocatoria: Convocatoria,
    val message: String
)