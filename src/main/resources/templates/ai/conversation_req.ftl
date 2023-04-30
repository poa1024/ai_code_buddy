Conversation rules:
1. every code block should be wrapped with ``` quotes.

<#list history as entry>
  Q: ${entry.key}
  A: ${entry.value}

</#list>