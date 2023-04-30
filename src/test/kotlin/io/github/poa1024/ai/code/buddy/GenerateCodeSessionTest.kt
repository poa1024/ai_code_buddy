package io.github.poa1024.ai.code.buddy

import io.github.poa1024.ai.code.buddy.context.AICBContextHolder
import io.github.poa1024.ai.code.buddy.mapper.html.GenerateCodeSessionHtmlMapper
import io.github.poa1024.ai.code.buddy.session.GenerateCodeSession
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.io.File
import java.util.function.Consumer

class GenerateCodeSessionTest {

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
      }
      """

    //language=text
    private val firstRequestTemplate = """
        Generate code based on the provided request.
        Request: [ %s ]
        
        Your response should contain the code only.
        Don't leave any comments or notes.
        Don't surround your response with quotes.
        
        Consider that the code will be a part of this context:
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
                  
                  /*your code will be here*/
                  
                  public int getAge() {
                      return age;
                  }
        
                  public String getOccupation() {
                      return occupation;
                  }
              }
              
        ```
    """

    //language=text
    private val secondRequestTemplate = """
        Adjust the provided code. Return only the adjusted version.
        %s
        
        Code to adjust:
        ```
         %s 
        ```
        
        Your response should contain the code only.
        Don't leave any comments or notes.
        Don't surround your response with quotes.
        
        Consider that the code will be a part of this context:
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
                  
                  /*your code will be here*/
                  
                  public int getAge() {
                      return age;
                  }
        
                  public String getOccupation() {
                      return occupation;
                  }
              }
              
        ```
    """

    @DataProvider(name = "testDataProvider")
    fun testDataProvider(): Array<Array<Any>> {

        return arrayOf(
            arrayOf(
                listOf(
                    GenerateCodeTestStep(
                        userInput = "//generate to String",
                        aiRequest = firstRequestTemplate.format("//generate to String"),
                        aiResponse = """ public String toString() { return "initial"} """,
                        expectedCode = """ public String toString() { return "initial"} """
                    ),
                    GenerateCodeTestStep(
                        userInput = "Change it!",
                        aiRequest = secondRequestTemplate.format(
                            "Change it!",
                            """public String toString() { return "initial"}"""
                        ),
                        aiResponse = """ public String toString() { return "changed"} """,
                        expectedCode = """ public String toString() { return "changed"} """
                    )
                )
            ),
            arrayOf(
                listOf(
                    GenerateCodeTestStep(
                        userInput = "//generate to String",
                        aiRequest = firstRequestTemplate.format("//generate to String"),
                        aiResponse = """``` public String toString() { return "initial"} ```""",
                        expectedCode = """ public String toString() { return "initial"} """
                    )
                )
            ),
            arrayOf(
                listOf(
                    GenerateCodeTestStep(
                        userInput = "//generate to String",
                        aiRequest = firstRequestTemplate.format("//generate to String"),
                        aiResponse = """```java public String toString() { return "initial""} ```""",
                        expectedCode = """ public String toString() { return "initial""} """
                    )
                )
            ),
            arrayOf(
                listOf(
                    GenerateCodeTestStep(
                        userInput = "//generate to String",
                        aiRequest = firstRequestTemplate.format("//generate to String"),
                        aiResponse = """ ```another one code block``` ```java public String toString() { return "initial""} ```""",
                        expectedCode = null
                    )
                )
            )
        )
    }

    data class GenerateCodeTestStep(
        val userInput: String,
        val aiRequest: String,
        val aiResponse: String,
        val expectedCode: String?
    )

    @Test(dataProvider = "testDataProvider")
    fun testGenerateCode(
        steps: List<GenerateCodeTestStep>
    ) {

        val aiClient = mockk<AIClient>()
        val codeHandler = mockk<Consumer<String>>()

        val session = GenerateCodeSession(
            codeHandler,
            aiClient,
            executor,
            personClassCode,
            411
        )

        steps.forEach { step ->

            every { aiClient.ask(match { it.moreOrLessSimilarTo(step.aiRequest) }) } returns step.aiResponse
            if (step.expectedCode != null) {
                every { codeHandler.accept(step.expectedCode) } returns Unit
            }

            session.proceed(step.userInput) {}

            if (step.expectedCode != null) {
                verify { codeHandler.accept(step.expectedCode) }
            } else {
                verify(exactly = 0) { codeHandler.accept(any()) }
            }

        }
    }

    @Test
    fun printExampleOfHtmlHistory() {

        val aiClient = mockk<AIClient>()
        val codeHandler = mockk<Consumer<String>>()

        val session = GenerateCodeSession(
            codeHandler,
            aiClient,
            executor,
            personClassCode,
            411
        )
        every { codeHandler.accept(any()) } returns Unit

        every { aiClient.ask(any()) } returns """ public String toString() { return "initial"; } """
        session.proceed("generate toString()") {}

        every { aiClient.ask(any()) } returns """ public String toString() { return "changed"; } """
        session.proceed("change it") {}

        every { aiClient.ask(any()) } returns """ 
            Changed from:
            ```public String toString() { return "changed"; }```
            To:
            ```public String toString() { return "changed again"; }``` 
            """.trimIndent()
        session.proceed("change it again") {}

        val mapper = GenerateCodeSessionHtmlMapper()
        val htmlHistory = mapper.mapHistory(session)
        val htmlHistoryPrinter = AICBContextHolder.getContext().htmlHistoryPrinter
        val htmlHistoryAsString = htmlHistoryPrinter.printAsString(htmlHistory)

        File("build/generateCodeHtmlHistoryExample.html").apply {
            delete()
            appendText(htmlHistoryAsString)
        }
    }

}