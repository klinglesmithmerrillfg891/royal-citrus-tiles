package com.royalcitrustiles.puzzle.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.di.ServiceLocator
import com.royalcitrustiles.puzzle.databinding.DialogSettingsBinding
import com.royalcitrustiles.puzzle.domain.model.AppSettings

class SettingsDialog : DialogFragment() {

    private var binding: DialogSettingsBinding? = null

    private var resetArmed = false

    private val resetTimeout = Runnable {
        try {
            val bound = binding
            if (!isAdded || bound == null) {
                return@Runnable
            }
            resetArmed = false
            bound.settingsResetButton.setText(R.string.settings_reset)
        } catch (e: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_RoyalCitrusTiles_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = DialogSettingsBinding.inflate(inflater, container, false)
        binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bound = binding ?: return
        val settings = ServiceLocator.progressRepository.loadSettings()

        bound.settingsSoundSwitch.isChecked = settings.soundEnabled
        bound.settingsHapticsSwitch.isChecked = settings.hapticsEnabled
        bound.settingsMotionSwitch.isChecked = settings.reducedMotion
        describe(bound.settingsSoundSwitch, settings.soundEnabled)
        describe(bound.settingsHapticsSwitch, settings.hapticsEnabled)
        describe(bound.settingsMotionSwitch, settings.reducedMotion)

        bound.settingsSoundSwitch.setOnCheckedChangeListener { button, checked ->
            describe(button, checked)
            persist()
        }
        bound.settingsHapticsSwitch.setOnCheckedChangeListener { button, checked ->
            describe(button, checked)
            persist()
        }
        bound.settingsMotionSwitch.setOnCheckedChangeListener { button, checked ->
            describe(button, checked)
            persist()
        }

        bound.settingsResetButton.setOnClickListener { onResetTapped() }
        bound.settingsCloseButton.setOnClickListener { dismissAllowingStateLoss() }
    }

    private fun describe(button: CompoundButton, checked: Boolean) {
        ViewCompat.setStateDescription(
            button,
            getString(if (checked) R.string.state_on else R.string.state_off)
        )
    }

    private fun persist() {
        val bound = binding ?: return
        ServiceLocator.progressRepository.saveSettings(
            AppSettings(
                soundEnabled = bound.settingsSoundSwitch.isChecked,
                hapticsEnabled = bound.settingsHapticsSwitch.isChecked,
                reducedMotion = bound.settingsMotionSwitch.isChecked
            )
        )
    }

    private fun onResetTapped() {
        val bound = binding ?: return
        if (!resetArmed) {
            resetArmed = true
            bound.settingsResetButton.setText(R.string.settings_reset_confirm)
            bound.settingsResetButton.removeCallbacks(resetTimeout)
            bound.settingsResetButton.postDelayed(resetTimeout, RESET_WINDOW_MS)
            return
        }
        bound.settingsResetButton.removeCallbacks(resetTimeout)
        resetArmed = false
        ServiceLocator.progressRepository.resetEverything()
        bound.settingsResetButton.setText(R.string.settings_reset)
        bound.settingsSoundSwitch.isChecked = true
        bound.settingsHapticsSwitch.isChecked = true
        bound.settingsMotionSwitch.isChecked = false
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        val bound = binding
        if (bound != null) {
            bound.settingsResetButton.removeCallbacks(resetTimeout)
        }
        resetArmed = false
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "SettingsDialog"
        private const val RESET_WINDOW_MS = 3000L
    }
}
