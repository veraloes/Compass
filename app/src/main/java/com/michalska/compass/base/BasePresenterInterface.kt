package com.michalska.compass.base

interface BasePresenterInterface<ViewType> {
    fun attach(view: ViewType)
}