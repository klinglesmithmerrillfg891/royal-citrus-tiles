package com.royalcitrustiles.puzzle.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.domain.model.RoundResult
import com.royalcitrustiles.puzzle.presentation.game.GameFragment
import com.royalcitrustiles.puzzle.presentation.gameover.GameOverFragment
import com.royalcitrustiles.puzzle.presentation.menu.MenuFragment
import com.royalcitrustiles.puzzle.presentation.splash.SplashFragment

object Navigator {

    const val STACK_GAME = "royal_citrus_game"
    const val STACK_RESULT = "royal_citrus_result"

    fun showSplash(manager: FragmentManager) {
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, SplashFragment())
            .commitAllowingStateLoss()
    }

    fun showMenu(manager: FragmentManager) {
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, MenuFragment())
            .commitAllowingStateLoss()
    }

    fun showGame(manager: FragmentManager, wingIndex: Int) {
        pushScreen(
            manager = manager,
            fragment = GameFragment.newInstance(wingIndex),
            stackName = STACK_GAME,
            enter = R.anim.slide_in_right,
            exit = R.anim.slide_out_left
        )
    }

    fun showResult(manager: FragmentManager, result: RoundResult) {
        pushScreen(
            manager = manager,
            fragment = GameOverFragment.newInstance(result),
            stackName = STACK_RESULT,
            enter = R.anim.fade_in,
            exit = R.anim.scale_out
        )
    }

    fun backToMenu(manager: FragmentManager) {
        if (manager.backStackEntryCount > 0) {
            manager.popBackStack(STACK_GAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } else {
            showMenu(manager)
        }
    }

    fun replay(manager: FragmentManager, wingIndex: Int) {
        backToMenu(manager)
        showGame(manager, wingIndex)
    }

    private fun pushScreen(
        manager: FragmentManager,
        fragment: Fragment,
        stackName: String,
        enter: Int,
        exit: Int
    ) {
        manager.beginTransaction()
            .setCustomAnimations(enter, exit, R.anim.scale_in, R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(stackName)
            .commitAllowingStateLoss()
    }
}
