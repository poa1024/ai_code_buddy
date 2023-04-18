<#if codeVersions?size = 0>
Generate code based on the provided request.
Request: [ ${userInput} ]
<#else>
Adjust the provided code. Return only the adjusted version.
${userInput}

Code to adjust:
```
<#assign code = codeVersions?reverse[0]>
${code}
```
</#if>

Your response should contain the code only.
Don't leave any comments or notes.
Don't surround your response with quotes.

Consider that the code is a part of this context:
```
${context}
```