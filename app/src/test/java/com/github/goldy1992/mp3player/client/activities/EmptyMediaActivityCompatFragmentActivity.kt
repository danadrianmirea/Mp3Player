package com.github.goldy1992.mp3player.client.activities

import android.os.Bundle
import com.github.goldy1992.mp3player.dagger.components.DaggerTestMediaActivityCompatComponent
import com.github.goldy1992.mp3player.dagger.components.TestMediaActivityCompatComponent

class EmptyMediaActivityCompatFragmentActivity : MediaActivityCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialiseDependencies()
        super.onCreate(savedInstanceState)
    }

    override val workerId: String
        get() = "WORKER_ID"

    override fun initialiseDependencies() {
        val component = DaggerTestMediaActivityCompatComponent
                .factory()
                .create(applicationContext, workerId, this) as TestMediaActivityCompatComponent
        component.inject(this)
        mediaActivityCompatComponent = component
    }

    override fun initialiseView(layoutId: Int): Boolean {
        return true
    }
}