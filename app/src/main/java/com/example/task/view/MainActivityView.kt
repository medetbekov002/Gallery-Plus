package com.example.task.view

import com.example.task.model.Photo
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainActivityView : MvpView {
    fun navigateToCameraActivity()
    fun showPhotos(photos: List<Photo>)
    fun showProgressbar()
    fun hideProgressBar()
    fun showAddButton()
    fun hideAddButton()
}