package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BodyMeasurements
import com.example.data.model.FitCut
import com.example.data.model.GarmentStyle
import com.example.data.model.JacketButtonStyle
import com.example.data.model.LapelStyle
import com.example.data.model.MeasurementUnit
import com.example.data.model.PocketStyle
import com.example.data.model.TrouserHem
import com.example.data.model.TrouserPleats
import com.example.data.model.VentStyle

@Entity(tableName = "suit_profiles")
data class SuitProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileName: String,
    val notes: String = "",
    val unit: String = "CM",
    val createdAt: Long = System.currentTimeMillis(),

    // Measurements (in CM)
    val chest: Float,
    val waist: Float,
    val hip: Float,
    val shoulderWidth: Float,
    val backLength: Float,
    val jacketLength: Float,
    val neckCircumference: Float,
    val sleeveLength: Float,
    val bicepCircumference: Float,
    val wristCircumference: Float,
    val inseam: Float,
    val outseam: Float,
    val thighCircumference: Float,
    val kneeCircumference: Float,
    val trouserBottomWidth: Float,

    // Style preferences
    val fitCut: String = FitCut.CLASSIC.name,
    val buttonStyle: String = JacketButtonStyle.SINGLE_BREASTED_2.name,
    val lapelStyle: String = LapelStyle.NOTCH.name,
    val lapelWidthCm: Float = 8.5f,
    val ventStyle: String = VentStyle.DOUBLE_SIDE_VENTS.name,
    val pocketStyle: String = PocketStyle.FLAP.name,
    val hasTicketPocket: Boolean = false,
    val trouserPleats: String = TrouserPleats.FLAT_FRONT.name,
    val trouserHem: String = TrouserHem.PLAIN.name,
    val seamAllowanceCm: Float = 1.0f,
    val hemAllowanceCm: Float = 4.0f
) {
    fun toBodyMeasurements(): BodyMeasurements {
        val parsedUnit = try {
            MeasurementUnit.valueOf(unit)
        } catch (_: Exception) {
            MeasurementUnit.CM
        }
        return BodyMeasurements(
            id = id,
            profileName = profileName,
            notes = notes,
            unit = parsedUnit,
            chest = chest,
            waist = waist,
            hip = hip,
            shoulderWidth = shoulderWidth,
            backLength = backLength,
            jacketLength = jacketLength,
            neckCircumference = neckCircumference,
            sleeveLength = sleeveLength,
            bicepCircumference = bicepCircumference,
            wristCircumference = wristCircumference,
            inseam = inseam,
            outseam = outseam,
            thighCircumference = thighCircumference,
            kneeCircumference = kneeCircumference,
            trouserBottomWidth = trouserBottomWidth
        )
    }

    fun toGarmentStyle(): GarmentStyle {
        return GarmentStyle(
            fitCut = try { FitCut.valueOf(fitCut) } catch (_: Exception) { FitCut.CLASSIC },
            buttonStyle = try { JacketButtonStyle.valueOf(buttonStyle) } catch (_: Exception) { JacketButtonStyle.SINGLE_BREASTED_2 },
            lapelStyle = try { LapelStyle.valueOf(lapelStyle) } catch (_: Exception) { LapelStyle.NOTCH },
            lapelWidthCm = lapelWidthCm,
            ventStyle = try { VentStyle.valueOf(ventStyle) } catch (_: Exception) { VentStyle.DOUBLE_SIDE_VENTS },
            pocketStyle = try { PocketStyle.valueOf(pocketStyle) } catch (_: Exception) { PocketStyle.FLAP },
            hasTicketPocket = hasTicketPocket,
            trouserPleats = try { TrouserPleats.valueOf(trouserPleats) } catch (_: Exception) { TrouserPleats.FLAT_FRONT },
            trouserHem = try { TrouserHem.valueOf(trouserHem) } catch (_: Exception) { TrouserHem.PLAIN },
            seamAllowanceCm = seamAllowanceCm,
            hemAllowanceCm = hemAllowanceCm
        )
    }

    companion object {
        fun fromModel(
            measurements: BodyMeasurements,
            style: GarmentStyle
        ): SuitProfileEntity {
            return SuitProfileEntity(
                id = measurements.id,
                profileName = measurements.profileName,
                notes = measurements.notes,
                unit = measurements.unit.name,
                chest = measurements.chest,
                waist = measurements.waist,
                hip = measurements.hip,
                shoulderWidth = measurements.shoulderWidth,
                backLength = measurements.backLength,
                jacketLength = measurements.jacketLength,
                neckCircumference = measurements.neckCircumference,
                sleeveLength = measurements.sleeveLength,
                bicepCircumference = measurements.bicepCircumference,
                wristCircumference = measurements.wristCircumference,
                inseam = measurements.inseam,
                outseam = measurements.outseam,
                thighCircumference = measurements.thighCircumference,
                kneeCircumference = measurements.kneeCircumference,
                trouserBottomWidth = measurements.trouserBottomWidth,
                fitCut = style.fitCut.name,
                buttonStyle = style.buttonStyle.name,
                lapelStyle = style.lapelStyle.name,
                lapelWidthCm = style.lapelWidthCm,
                ventStyle = style.ventStyle.name,
                pocketStyle = style.pocketStyle.name,
                hasTicketPocket = style.hasTicketPocket,
                trouserPleats = style.trouserPleats.name,
                trouserHem = style.trouserHem.name,
                seamAllowanceCm = style.seamAllowanceCm,
                hemAllowanceCm = style.hemAllowanceCm
            )
        }
    }
}
