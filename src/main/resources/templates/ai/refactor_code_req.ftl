Refactor the code.
${userInput}

Code:
```
<#assign code = codeVersions?reverse[0]>
${code}
```

Your response should contain the code only.
Don't leave any comments or notes.
Don't surround your response with quotes.

Consider that the code will be a part of this context:
```
${context}
```