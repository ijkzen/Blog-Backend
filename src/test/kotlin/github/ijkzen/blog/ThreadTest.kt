package github.ijkzen.blog

import org.junit.Test

/**
 * @Author ijkzen
 * @Date 2019/10/29
 */
class ThreadTest {

    @Test
    fun threadTest() {
        Thread {
            println("hello from ${Thread.currentThread().name}")
        }.start()
    }
}