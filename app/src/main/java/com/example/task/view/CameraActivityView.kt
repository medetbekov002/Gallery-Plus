package com.example.task.view

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface CameraActivityView : MvpView {
    fun onPhotoTaken()
    fun onPhotoError(errorMessage: String)
}