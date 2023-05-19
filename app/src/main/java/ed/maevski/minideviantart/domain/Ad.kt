package ed.maevski.minideviantart.domain

class Ad(override val id: String, val title: String, val content : String, override var isInFavorites: Boolean = false) :
    Item