package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.PatternDraftEngine
import com.example.data.model.BodyTypePresetDirectory
import com.example.data.model.DraftPieceType
import com.example.data.model.GarmentStyle
import com.example.data.model.StandardPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Sartor", appName)
    }

    @Test
    fun `verify pattern draft generation produces all components including overview`() {
        val measurements = StandardPresets.CLASSIC_40R
        val style = GarmentStyle()
        val result = PatternDraftEngine.generateDraft(measurements, style)

        assertNotNull(result)
        assertTrue(result.pieces.containsKey(DraftPieceType.JACKET_FRONT))
        assertTrue(result.pieces.containsKey(DraftPieceType.JACKET_BACK))
        assertTrue(result.pieces.containsKey(DraftPieceType.SLEEVE_TOP))
        assertTrue(result.pieces.containsKey(DraftPieceType.SLEEVE_UNDER))
        assertTrue(result.pieces.containsKey(DraftPieceType.TROUSER_FRONT))
        assertTrue(result.pieces.containsKey(DraftPieceType.TROUSER_BACK))
        assertTrue(result.pieces.containsKey(DraftPieceType.ALL_PIECES_OVERVIEW))
        assertTrue(result.pieces.containsKey(DraftPieceType.FULL_LAYOUT))

        assertTrue(result.scyeDepthCm > 20f && result.scyeDepthCm < 30f)
        assertTrue(result.totalFabricLengthMeters > 2.5f)
        assertEquals(7, result.assemblySteps.size)
    }

    @Test
    fun `verify male and female body type presets exist and contain required dimensions`() {
        // Verify Male presets
        val maleAthletic = BodyTypePresetDirectory.MALE_ATHLETIC.measurements
        assertTrue("Male athletic chest should be wider than waist", maleAthletic.chest > maleAthletic.waist)
        assertTrue(maleAthletic.chest > 0f && maleAthletic.waist > 0f && maleAthletic.hip > 0f)
        assertTrue(maleAthletic.inseam > 0f && maleAthletic.sleeveLength > 0f)
        assertTrue(maleAthletic.neckCircumference > 0f && maleAthletic.shoulderWidth > 0f)

        val malePear = BodyTypePresetDirectory.MALE_PEAR.measurements
        assertTrue("Male pear hips should be wider than chest", malePear.hip > malePear.chest)

        val maleApple = BodyTypePresetDirectory.MALE_APPLE.measurements
        assertTrue("Male apple waist should be fuller", maleApple.waist >= 100f)

        // Verify Female presets
        val femaleHourglass = BodyTypePresetDirectory.FEMALE_HOURGLASS.measurements
        assertTrue("Female hourglass waist should be narrow", femaleHourglass.waist < femaleHourglass.chest && femaleHourglass.waist < femaleHourglass.hip)

        val femalePear = BodyTypePresetDirectory.FEMALE_PEAR.measurements
        assertTrue("Female pear hips should be wider than chest/bust", femalePear.hip > femalePear.chest)

        val femaleApple = BodyTypePresetDirectory.FEMALE_APPLE.measurements
        assertTrue("Female apple waist should be fuller", femaleApple.waist >= 90f)

        // Test drafting for all presets without error
        BodyTypePresetDirectory.ALL_PRESETS.forEach { preset ->
            val draft = PatternDraftEngine.generateDraft(preset.measurements, GarmentStyle())
            assertNotNull(draft)
            assertEquals(8, draft.pieces.size)
        }
    }
}
