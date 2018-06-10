# DiscordMC
This is a plugin that runs on a Minecraft server, and acts as a bridge between the server and Discord. To use this plugin, and it's features, you must therefore know how to set up a bot user for Discord.

## Library
The plugin uses [Discord4J](https://discord4j.com/) as the main Library to communicate with Discord, and we therefore encourage you to check their documentation, if you wish to modify this code.

## Setting up the bot
In order to use this bot, the config must be configured correctly. An example of how the config ***should*** look:
```yaml
settings:
  token: 'YOUR_TOKEN_HEER'
  chat-prefix: "§bDiscord: "

  guild: "" #ID of your guild
  log-channel: "" #ID of your log channel
  rules-channel: "" #ID of your rules channel
  announce-channel: "" #ID of your announcements channel
  mute-role: "" #ID of a role that prevents user to chat
  voice-mute-role: "" #ID of a role that prevents user from joining voice chat
  command_prefix: 'ob!' #Prefix for any bot commands
  
  trello:
    enabled: true #Should this be enabled
    API-key: "" #API key for the trello board you want to use
    token: "" #Trello token

#These should be left as they are
ticket-id: 0 
case-id: 0
uses: 0
```

## Furure plans for this plugin
- Add a wrapper for [Trello](https://trello.com), so users can have report issues and bugs to an easy-to-manage Trello board.
