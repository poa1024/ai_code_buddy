package io.github.poa1024.ai.code.buddy

import io.github.poa1024.ai.code.buddy.session.GenerationCodeSession
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.function.Consumer

class GenerationCodeSessionTest {

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

    //language=textmate
    private val firstRequestTemplate = """
                    
                    Generate code based on the provided request. 
                    Yor response should contain the code only. 
                    Don't leave any comments or notes. 
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

    //language=textmate
    private val secondRequestTemplate = """         
            
                    Adjust the provided code. Return only the adjusted version. 
                    %s.
                    Yor response should contain the code only. 
                    Don't leave any comments or notes. 
                    Don't surround your response with quotes.
                    
                    Code to adjust: 
                    
                    ```
                    
                      %s
                    
                    ```
                    
                    
                    
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

    @DataProvider(name = "codeGenerationScenarios")
    fun codeGenerationScenarios(): Array<Array<Any>> {

        return arrayOf(
            arrayOf(
                listOf(
                    CodeGenerationTestStep(
                        "//generate to String",
                        firstRequestTemplate.format("//generate to String"),
                        """ public String toString() { return "initial""} """,
                        """ public String toString() { return "initial""} """
                    ),
                    CodeGenerationTestStep(
                        "Change it!",
                        secondRequestTemplate.format(
                            "Change it!",
                            """ public String toString() { return "initial""} """
                        ),
                        """ public String toString() { return "changed""} """,
                        """ public String toString() { return "changed""} """
                    )
                )
            ),
            arrayOf(
                listOf(
                    CodeGenerationTestStep(
                        "//generate to String",
                        firstRequestTemplate.format("//generate to String"),
                        """``` public String toString() { return "initial""} ```""",
                        """ public String toString() { return "initial""} """
                    ),
                    CodeGenerationTestStep(
                        "Change it!",
                        secondRequestTemplate.format(
                            "Change it!",
                            """ public String toString() { return "initial""} """
                        ),
                        """ public String toString() { return "changed""} """,
                        """ public String toString() { return "changed""} """
                    )
                )
            ),
            arrayOf(
                listOf(
                    CodeGenerationTestStep(
                        "//generate to String",
                        firstRequestTemplate.format("//generate to String"),
                        """```java public String toString() { return "initial""} ```""",
                        """ public String toString() { return "initial""} """
                    ),
                    CodeGenerationTestStep(
                        "Change it!",
                        secondRequestTemplate.format(
                            "Change it!",
                            """ public String toString() { return "initial""} """
                        ),
                        """ public String toString() { return "changed""} """,
                        """ public String toString() { return "changed""} """
                    )
                )
            )
        )
    }

    data class CodeGenerationTestStep(
        val userInput: String,
        val aiRequest: String,
        val codeGenerateByAi: String,
        val expectedCode: String
    )

    @Test(dataProvider = "codeGenerationScenarios")
    fun testCodeGenerationScenario(
        steps: List<CodeGenerationTestStep>
    ) {

        val aiClient = mockk<AIClient>()
        val codeHandler = mockk<Consumer<String>>()

        val session = GenerationCodeSession(
            codeHandler,
            aiClient,
            executor,
            personClassCode,
            411
        )

        steps.forEach {

            every { aiClient.ask(it.aiRequest) } returns it.codeGenerateByAi
            every { codeHandler.accept(it.expectedCode) } returns Unit

            session.proceed(it.userInput) {}
            verify { codeHandler.accept(it.expectedCode) }
        }


    }
}