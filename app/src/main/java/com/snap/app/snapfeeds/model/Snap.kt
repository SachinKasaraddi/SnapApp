package com.snap.app.snapfeeds.model

import java.io.Serializable

data class Snap(var url: String?,
                var name: String?,
                var description: String?,
                var timestamp: Long? = null) : Serializable