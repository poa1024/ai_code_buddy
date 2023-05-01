package io.github.poa1024.ai.code.buddy

import com.winterbe.expekt.should
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder
import io.github.poa1024.ai.code.buddy.mapper.html.ConversationSessionHtmlMapper
import io.github.poa1024.ai.code.buddy.session.ConversationSession
import io.mockk.every
import io.mockk.mockk
import org.testng.annotations.Test
import java.io.File
import java.util.function.Consumer

class ConversationSessionTest {

    private val executor = Executor { _, runnable -> runnable.run() }

    //language=text
    private val firstRequest = """
        Conversation rules:
        1. every source code block should be surrounded with ``` quotes.
            For example:
            ```
                  @Override
                  public String toString() {
                      return "toString";
                  }
            ```
        
          Q: hey
          A: 
    """

    //language=text
    private val secondRequest = """
        Conversation rules:
        1. every source code block should be surrounded with ``` quotes.
            For example:
            ```
                  @Override
                  public String toString() {
                      return "toString";
                  }
            ```
        
          Q: hey
          A: hey!
        
          Q: Give me example of a toString method
          A:
    """

    //language=text
    private val thirdRequest = """
        Conversation rules:
        1. every source code block should be surrounded with ``` quotes.
            For example:
            ```
                  @Override
                  public String toString() {
                      return "toString";
                  }
            ```
        
          Q: hey
          A: hey!
        
          Q: Give me example of a toString method
          A: Example of a toString method:
        ```
        java public String toString() { return "toString""}
        ```
        
          Q: thanks
          A: 
    """

    @Test
    fun testConversation(
    ) {

        val aiClient = mockk<AIClient>()

        val session = ConversationSession(aiClient, executor)

        every { aiClient.ask(match { it.moreOrLessSimilarTo(firstRequest) }) } returns "hey!"
        session.proceed("hey") {}
        session.history.last().response!!.text.should.equal("hey!")

        val toStringMethodRes = """
            Example of a toString method:
            ```
            java public String toString() { return "toString""}
            ```""".trimIndent()
        every { aiClient.ask(match { it.moreOrLessSimilarTo(secondRequest) }) } returns toStringMethodRes
        session.proceed("Give me example of a toString method") {}
        session.history.last().response!!.text.should.equal(toStringMethodRes)

        every { aiClient.ask(match { it.moreOrLessSimilarTo(thirdRequest) }) } returns "you're welcome!"
        session.proceed("thanks") {}
        session.history.last().response!!.text.should.equal("you're welcome!")
    }

    @Test
    fun printExampleOfHtmlHistory() {

        val aiClient = mockk<AIClient>()
        val codeHandler = mockk<Consumer<String>>()

        val session = ConversationSession(
            aiClient,
            executor
        )
        every { codeHandler.accept(any()) } returns Unit

        every { aiClient.ask(any()) } returns "hey!"
        session.proceed("hey") {}

        every { aiClient.ask(any()) } returns """
            Example of a toString method:
            ```
            java public String toString() { return "toString""}
            ```""".trimIndent()
        session.proceed("Give me example of a toString method") {}

        val mapper = ConversationSessionHtmlMapper()
        val htmlHistory = mapper.mapHistory(session)
        val htmlHistoryPrinter = AICBContextHolder.getContext().htmlHistoryPrinter
        val htmlHistoryAsString = htmlHistoryPrinter.printAsString(htmlHistory)

        File("build/conversationHtmlHistoryExample.html").apply {
            delete()
            appendText(htmlHistoryAsString)
        }
    }

}