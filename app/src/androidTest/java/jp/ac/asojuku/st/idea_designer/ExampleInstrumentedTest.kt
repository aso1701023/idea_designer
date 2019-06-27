package jp.ac.asojuku.st.idea_designer

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented idea_recycler_view, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under idea_recycler_view.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("jp.ac.asojuku.st.idea_designer", appContext.packageName)
    }
}
