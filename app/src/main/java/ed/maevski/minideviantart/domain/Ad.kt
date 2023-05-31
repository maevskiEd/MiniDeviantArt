package ed.maevski.minideviantart.domain

import ed.maevski.remote_module.Item

class Ad(override val id: String, val title: String, val content : String, override var isInFavorites: Boolean = false) :
    Item