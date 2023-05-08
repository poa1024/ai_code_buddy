package io.github.poa1024.ai.code.buddy

import com.winterbe.expekt.should
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder
import io.github.poa1024.ai.code.buddy.mapper.html.ExplainCodeSessionHtmlMapper
import io.github.poa1024.ai.code.buddy.session.ExplainCodeSession
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.io.File
import java.util.function.Consumer

class ExplainCodeSessionTest {

    private val executor = Executor { _, runnable -> runnable.run() }

    //language=Java
    private val personClassCode = """
        public class Person {
            private String name;
            private int age;
            private String occupation;
        
            public Person(String name, int age, String occupation) {
                this.name = name;
                this.age = age;
                this.occupation = occupation;
            }
        
            public String getName() {
                return name;
            }
        
            public int getAge() {
                return age;
            }
        
            public String getOccupation() {
                return occupation;
            }
        
            public String toString() {
                return "code to explain";
            }
        
        }
      """

    //language=Java
    private val toStringMethod = """
            public String toString() {
                return "code to explain";
            }
      """

    //language=text
    private val firstRequest = """
        Code:
        ```
        
                    public String toString() {
                        return "code to explain";
                    }
              
        ```
        
        Code exists within the context:
        ```
        
                public class Person {
                    private String name;
                    private int age;
                    private String occupation;
                
                    public Person(String name, int age, String occupation) {
                        this.name = name;
                        this.age = age;
                        this.occupation = occupation;
                    }
                
                    public String getName() {
                        return name;
                    }
                
                    public int getAge() {
                        return age;
                    }
                
                    public String getOccupation() {
                        return occupation;
                    }
                /*discussed code is here*/  
                }
              
        ```
        
          Q: What is it?
          A: 
    """

    //language=text
    private val secondRequestTemplate = """
        Code:
        ```
        
                    public String toString() {
                        return "code to explain";
                    }
              
        ```
        
        Code exists within the context:
        ```
        
                public class Person {
                    private String name;
                    private int age;
                    private String occupation;
                
                    public Person(String name, int age, String occupation) {
                        this.name = name;
                        this.age = age;
                        this.occupation = occupation;
                    }
                
                    public String getName() {
                        return name;
                    }
                
                    public int getAge() {
                        return age;
                    }
                
                    public String getOccupation() {
                        return occupation;
                    }
                /*discussed code is here*/  
                }
              
        ```
        
          Q: What is it?
          A: This is toString method.
        
          Q: Explain in more details
          A:
    """

    @DataProvider(name = "testDataProvider")
    fun testDataProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(
                listOf(
                    ExplainCodeTestStep(
                        userInput = "What is it?",
                        aiRequest = firstRequest,
                        aiResponse = """This is toString method.""",
                    ),
                    ExplainCodeTestStep(
                        userInput = "Explain in more details",
                        aiRequest = secondRequestTemplate.format(
                            "Change it!",
                            """public String toString() { return "initial"}"""
                        ),
                        aiResponse = """This is toString method in the Person class."""
                    )
                )
            )
        )
    }

    data class ExplainCodeTestStep(
        val userInput: String?,
        val aiRequest: String,
        val aiResponse: String
    )

    @Test(dataProvider = "testDataProvider")
    fun testExplainCode(
        steps: List<ExplainCodeTestStep>
    ) {

        val aiClient = mockk<AIClient>()

        val session = ExplainCodeSession(
            aiClient,
            executor,
            personClassCode,
            toStringMethod
        )

        steps.forEach { step ->

            every { aiClient.ask(match { it.moreOrLessSimilarTo(step.aiRequest) }) } returns step.aiResponse
            session.proceed(step.userInput) {}
            verify(exactly = 1) { aiClient.ask(match { it.moreOrLessSimilarTo(step.aiRequest) }) }

            session.history.last().response!!.text.should.equal(step.aiResponse)
        }
    }

    @Test
    fun printExampleOfHtmlHistory() {

        val aiClient = mockk<AIClient>()
        val codeHandler = mockk<Consumer<String>>()

        val session = ExplainCodeSession(
            aiClient,
            executor,
            personClassCode,
            toStringMethod
        )
        every { codeHandler.accept(any()) } returns Unit

        every { aiClient.ask(any()) } returns "This is a toString method"
        session.proceed("What is it?") {}

        every { aiClient.ask(any()) } returns """This is a toString method in the class Person"""
        session.proceed("Please explain in more details") {}

        val mapper = ExplainCodeSessionHtmlMapper()
        val htmlHistory = mapper.mapHistory(session)
        val htmlHistoryPrinter = AICBContextHolder.getContext().htmlHistoryPrinter
        val htmlHistoryAsString = htmlHistoryPrinter.printAsString(htmlHistory)

        File("build/explainCodeHtmlHistoryExample.html").apply {
            delete()
            appendText(htmlHistoryAsString)
        }
    }

}