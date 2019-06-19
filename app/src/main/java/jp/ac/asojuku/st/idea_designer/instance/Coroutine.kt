package jp.ac.asojuku.st.idea_designer.instance

import android.os.Handler
import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Coroutine(bs:BS,handler: Handler,inner: inner):CoroutineScope {
    private val job = Job()
    private val bs = bs
    private val handler = handler
    private val inner = inner
    override val coroutineContext: CoroutineContext
        get() = job

    fun start(){
        launch {
            start_tick()
        }
    }

    suspend fun start_tick(){
        async{
            while (true) {
                Thread.sleep(5000)
                var time = bs.tick()

                if(time != 999){
                    handler.post { bs.time_text?.setText(time.toString()) }
                }
                if(time == 0){
                    break
                }
            }
        }.await()
        inner.intent()
    }

    fun destroy(){
        job.cancel()
    }
}