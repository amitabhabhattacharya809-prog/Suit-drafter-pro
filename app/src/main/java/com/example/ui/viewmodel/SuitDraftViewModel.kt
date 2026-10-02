package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.SuitProfileEntity
import com.example.data.db.SuitProfileRepository
import com.example.data.engine.PatternDraftEngine
import com.example.data.model.BodyMeasurements
import com.example.data.model.DraftPieceType
import com.example.data.model.FitCut
import com.example.data.model.GarmentStyle
import com.example.data.model.JacketButtonStyle
import com.example.data.model.LapelStyle
import com.example.data.model.MeasurementUnit
import com.example.data.model.PocketStyle
import com.example.data.model.StandardPresets
import com.example.data.model.SuitDraftResult
import com.example.data.model.TrouserHem
import com.example.data.model.TrouserPleats
import com.example.data.model.VentStyle
import com.example.data.model.formatLength
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SuitDraftViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SuitProfileRepository

    init {
        val dao = AppDatabase.getDatabase(application).suitProfileDao()
        repository = SuitProfileRepository(dao)

        // Seed with standard presets if database is empty
        viewModelScope.launch {
            // Check once
        }
    }

    val savedProfiles: StateFlow<List<SuitProfileEntity>> = repository.allProfiles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentMeasurements = MutableStateFlow(StandardPresets.CLASSIC_40R)
    val currentMeasurements: StateFlow<BodyMeasurements> = _currentMeasurements.asStateFlow()

    private val _currentStyle = MutableStateFlow(GarmentStyle())
    val currentStyle: StateFlow<GarmentStyle> = _currentStyle.asStateFlow()

    private val _selectedPieceType = MutableStateFlow(DraftPieceType.JACKET_FRONT)
    val selectedPieceType: StateFlow<DraftPieceType> = _selectedPieceType.asStateFlow()

    // Canvas view options
    private val _showGrid = MutableStateFlow(true)
    val showGrid: StateFlow<Boolean> = _showGrid.asStateFlow()

    private val _showDimensions = MutableStateFlow(true)
    val showDimensions: StateFlow<Boolean> = _showDimensions.asStateFlow()

    private val _showSeamAllowance = MutableStateFlow(true)
    val showSeamAllowance: StateFlow<Boolean> = _showSeamAllowance.asStateFlow()

    private val _showNotches = MutableStateFlow(true)
    val showNotches: StateFlow<Boolean> = _showNotches.asStateFlow()

    private val _showConstructionLines = MutableStateFlow(true)
    val showConstructionLines: StateFlow<Boolean> = _showConstructionLines.asStateFlow()

    // Status snackbar or message
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Reactive draft result calculation
    val draftResult: StateFlow<SuitDraftResult> = combine(
        _currentMeasurements,
        _currentStyle
    ) { measurements, style ->
        PatternDraftEngine.generateDraft(measurements, style)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PatternDraftEngine.generateDraft(StandardPresets.CLASSIC_40R, GarmentStyle())
    )

    fun selectPiece(type: DraftPieceType) {
        _selectedPieceType.value = type
    }

    fun toggleGrid() {
        _showGrid.value = !_showGrid.value
    }

    fun toggleDimensions() {
        _showDimensions.value = !_showDimensions.value
    }

    fun toggleSeamAllowance() {
        _showSeamAllowance.value = !_showSeamAllowance.value
    }

    fun toggleNotches() {
        _showNotches.value = !_showNotches.value
    }

    fun toggleConstructionLines() {
        _showConstructionLines.value = !_showConstructionLines.value
    }

    fun setUnit(unit: MeasurementUnit) {
        _currentMeasurements.value = _currentMeasurements.value.copy(unit = unit)
    }

    fun updateMeasurementValue(key: String, valueInCm: Float) {
        val curr = _currentMeasurements.value
        _currentMeasurements.value = when (key) {
            "chest" -> curr.copy(chest = valueInCm)
            "waist" -> curr.copy(waist = valueInCm)
            "hip" -> curr.copy(hip = valueInCm)
            "shoulderWidth" -> curr.copy(shoulderWidth = valueInCm)
            "backLength" -> curr.copy(backLength = valueInCm)
            "jacketLength" -> curr.copy(jacketLength = valueInCm)
            "neckCircumference" -> curr.copy(neckCircumference = valueInCm)
            "sleeveLength" -> curr.copy(sleeveLength = valueInCm)
            "bicepCircumference" -> curr.copy(bicepCircumference = valueInCm)
            "wristCircumference" -> curr.copy(wristCircumference = valueInCm)
            "inseam" -> curr.copy(inseam = valueInCm)
            "outseam" -> curr.copy(outseam = valueInCm)
            "thighCircumference" -> curr.copy(thighCircumference = valueInCm)
            "kneeCircumference" -> curr.copy(kneeCircumference = valueInCm)
            "trouserBottomWidth" -> curr.copy(trouserBottomWidth = valueInCm)
            else -> curr
        }
    }

    fun updateProfileName(name: String) {
        _currentMeasurements.value = _currentMeasurements.value.copy(profileName = name)
    }

    fun updateProfileNotes(notes: String) {
        _currentMeasurements.value = _currentMeasurements.value.copy(notes = notes)
    }

    fun applyPreset(preset: BodyMeasurements) {
        _currentMeasurements.value = preset.copy(
            id = 0,
            unit = _currentMeasurements.value.unit
        )
        _statusMessage.value = "Loaded preset: ${preset.profileName}"
    }

    fun setFitCut(fitCut: FitCut) {
        _currentStyle.value = _currentStyle.value.copy(fitCut = fitCut)
    }

    fun setButtonStyle(buttonStyle: JacketButtonStyle) {
        _currentStyle.value = _currentStyle.value.copy(buttonStyle = buttonStyle)
    }

    fun setLapelStyle(lapelStyle: LapelStyle) {
        _currentStyle.value = _currentStyle.value.copy(lapelStyle = lapelStyle)
    }

    fun setLapelWidth(widthCm: Float) {
        _currentStyle.value = _currentStyle.value.copy(lapelWidthCm = widthCm)
    }

    fun setVentStyle(ventStyle: VentStyle) {
        _currentStyle.value = _currentStyle.value.copy(ventStyle = ventStyle)
    }

    fun setPocketStyle(pocketStyle: PocketStyle) {
        _currentStyle.value = _currentStyle.value.copy(pocketStyle = pocketStyle)
    }

    fun toggleTicketPocket() {
        _currentStyle.value = _currentStyle.value.copy(hasTicketPocket = !_currentStyle.value.hasTicketPocket)
    }

    fun setTrouserPleats(pleats: TrouserPleats) {
        _currentStyle.value = _currentStyle.value.copy(trouserPleats = pleats)
    }

    fun setTrouserHem(hem: TrouserHem) {
        _currentStyle.value = _currentStyle.value.copy(trouserHem = hem)
    }

    fun setSeamAllowance(cm: Float) {
        _currentStyle.value = _currentStyle.value.copy(seamAllowanceCm = cm)
    }

    fun saveCurrentProfile() {
        viewModelScope.launch {
            val entity = SuitProfileEntity.fromModel(
                measurements = _currentMeasurements.value,
                style = _currentStyle.value
            )
            val newId = repository.insertProfile(entity)
            _currentMeasurements.value = _currentMeasurements.value.copy(id = newId)
            _statusMessage.value = "Saved profile '${_currentMeasurements.value.profileName}'"
        }
    }

    fun loadProfile(entity: SuitProfileEntity) {
        _currentMeasurements.value = entity.toBodyMeasurements()
        _currentStyle.value = entity.toGarmentStyle()
        _statusMessage.value = "Loaded client profile: ${entity.profileName}"
    }

    fun deleteProfile(entity: SuitProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(entity)
            if (_currentMeasurements.value.id == entity.id) {
                _currentMeasurements.value = _currentMeasurements.value.copy(id = 0)
            }
            _statusMessage.value = "Deleted profile '${entity.profileName}'"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun generateCutterSpecificationSheet(): String {
        val m = _currentMeasurements.value
        val s = _currentStyle.value
        val d = draftResult.value
        val u = m.unit

        return buildString {
            appendLine("===========================================")
            appendLine("SARTOR BESPOKE SUIT DRAFT SPECIFICATION")
            appendLine("===========================================")
            appendLine("Client / Order: ${m.profileName}")
            if (m.notes.isNotBlank()) appendLine("Notes: ${m.notes}")
            appendLine("Fit Silhouette: ${s.fitCut.displayName} (+${s.fitCut.chestEaseCm} cm chest ease)")
            appendLine("Jacket Architecture: ${s.buttonStyle.displayName}, ${s.lapelStyle.displayName} (${s.lapelWidthCm} cm)")
            appendLine("Vents: ${s.ventStyle.displayName} | Pockets: ${s.pocketStyle.displayName}${if (s.hasTicketPocket) " + Ticket Pocket" else ""}")
            appendLine("Trousers: ${s.trouserPleats.displayName} | ${s.trouserHem.displayName}")
            appendLine()
            appendLine("--- MEASUREMENT RECORD ---")
            appendLine("Chest: ${formatLength(m.chest, u)} (Finished: ${formatLength(d.chestFinishedCm, u)})")
            appendLine("Waist: ${formatLength(m.waist, u)} (Finished: ${formatLength(d.waistFinishedCm, u)})")
            appendLine("Hip / Seat: ${formatLength(m.hip, u)}")
            appendLine("Shoulder Width: ${formatLength(m.shoulderWidth, u)}")
            appendLine("Back Length: ${formatLength(m.backLength, u)}")
            appendLine("Jacket Length: ${formatLength(m.jacketLength, u)}")
            appendLine("Neck Circumference: ${formatLength(m.neckCircumference, u)}")
            appendLine("Sleeve Length: ${formatLength(m.sleeveLength, u)}")
            appendLine("Bicep: ${formatLength(m.bicepCircumference, u)} | Wrist: ${formatLength(m.wristCircumference, u)}")
            appendLine("Inseam: ${formatLength(m.inseam, u)} | Outseam: ${formatLength(m.outseam, u)}")
            appendLine("Thigh: ${formatLength(m.thighCircumference, u)} | Knee: ${formatLength(m.kneeCircumference, u)} | Bottom: ${formatLength(m.trouserBottomWidth, u)}")
            appendLine()
            appendLine("--- DRAFTING GEOMETRY ---")
            appendLine("Calculated Scye Depth: ${formatLength(d.scyeDepthCm, u)}")
            appendLine("Half Back Width: ${formatLength(d.halfBackWidthCm, u)}")
            appendLine("Seam Allowance: ${s.seamAllowanceCm} cm | Hem Allowance: ${s.hemAllowanceCm} cm")
            appendLine()
            appendLine("--- FABRIC & CUTTING ESTIMATE (150 cm Bolt) ---")
            appendLine("Pure Wool Cloth Required: ${d.totalFabricLengthMeters} meters")
            appendLine("Body / Sleeve Lining: ${d.totalLiningMeters} meters")
            appendLine("Haircloth / Canvas Interlining: ${d.totalInterliningMeters} meters")
            appendLine("===========================================")
        }
    }
}
