## The plan

- code generation
  - should work in the json/sql files, in the db console (maybe already work, need to check)
- checking the code, suggesting improvements, checking for bugs
- continuous text/code generation (???)
- rf: a lot of possible cast/npe exceptions, need to fix
- consider number of tokens in the openai request body
- hotkeys
- validate response and retry if needed
- if file has changed since the last code generation - warning - suggestion to highlight the code to change
- gpt window can contain multiple sessions (panels)?
- I think request for the code adjustment could contain several versions of the code, not just the last one
- make code in gpt window collapsible (and make html more...reach)
- log http interaction
- readme.me generation (and updating readme based on the project changes)
- dialog to accept or to regenerate the result
- pass file extension as part of the context
- use idea services

- change explain scenario
- tests for code refactoring
- initial html for the code refactoring scenario
- publish plugin?
