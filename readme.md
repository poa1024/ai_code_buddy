## The plan

- code generation
  - should work in the json/sql files, in the db console (maybe already work, need to check)
- readme.me generation
- checking the code, suggesting improvements, checking for bugs
- continuous text/code generation (???)
- errors/warnings?
- rf: a lot of possible cast/npe exceptions, need to fix
- consider number of tokens in the openai request body
- hotkeys
- validate response and retry if needed
- configuration for apikey
- if file has changed since the last code generation - warning - suggestion to highlight the code to change
- ability to highlight the code and refactor it (new scenario)
- gpt window can contain multiple sessions (panels)?
- new scenario - just start the conversation with gpt in the window
- I think request for the code adjustment could contain several versions of the code, not just the last one
- template engine to build gpt requests
- make code in gpt window collapsible (and make html more...reach)