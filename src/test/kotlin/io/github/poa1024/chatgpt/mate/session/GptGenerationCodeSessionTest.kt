package io.github.poa1024.chatgpt.mate.session

import io.github.poa1024.chatgpt.mate.Executor
import io.github.poa1024.chatgpt.mate.gptclient.GptClient
import io.github.poa1024.chatgpt.mate.gptclient.GptClient.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.function.Consumer

class GptGenerationCodeSessionTest {

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

    private val gptRequestTemplate =
        """
                    
                    Generate code based on the provided request. 
                    Yor response should contain the code only. 
                    Dont' leave any comments or notes. 
                    Don't surround your response with quotes.
                    Request: [ %s ]
        
                    Consider that the code is a part of this context: 
        
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
        
        
        """.trimIndent()

    @DataProvider(name = "forBaseTest")
    fun dataProvider(): Array<Array<Any>> {

        return arrayOf(
            arrayOf(
                "//generate to String",
                """ public String toString() { return "s""} """,
                """ public String toString() { return "s""} """
            ),
            arrayOf(
                "//generate to String",
                """``` public String toString() { return "s""} ```""",
                """ public String toString() { return "s""} """
            ),
            arrayOf(
                "//generate to String",
                """```java public String toString() { return "s""} ```""",
                """ public String toString() { return "s""} """
            )
        )
    }

    @Test(dataProvider = "forBaseTest")
    fun baseTest(
        userInput: String,
        codeGenerateByGpt: String,
        expectedCode: String
    ) {

        val gptRequest = gptRequestTemplate.format(userInput)

        val gptClient = mockk<GptClient>()
        every { gptClient.ask(gptRequest) } returns Response(listOf(Choice(Message("assistant", codeGenerateByGpt))))

        val codeHandler = mockk<Consumer<String>>()
        every { codeHandler.accept(expectedCode) } returns Unit

        val session = GptGenerationCodeSession(
            codeHandler,
            gptClient,
            executor,
            personClassCode,
            411
        )

        session.proceed(userInput) {}
        verify { codeHandler.accept(expectedCode) }


    }
}