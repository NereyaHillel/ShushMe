package com.dev.nereya.shushme.model

data class SoundItem private constructor(
    val title: String,
    val author: String,
    val path: String,
    var isChosen: Boolean

){
    fun chooseItem(movie: SoundItem, position: Int) {
        movie.isChosen = !movie.isChosen
    }


    class Builder(
        var title: String = "",
        var author: String = "",
        var path: String = "",
        var isChosen: Boolean = false
    ) {
        fun title(title: String) = apply { this.title = title }
        fun author(author: String) = apply { this.author = author }
        fun path(path: String) = apply { this.path = path }
        fun isChosen(isChosen: Boolean) = apply { this.isChosen = isChosen }
        fun build() = SoundItem(title, author,  path, isChosen)
    }



}