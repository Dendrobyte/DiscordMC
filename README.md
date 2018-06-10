# DiscordMC
This is a plugin that runs on a Minecraft server, and acts as a bridge between the server and Discord. To use this plugin, and it's features, you must therefore know how to set up a bot user for Discord.

## Library
The plugin uses [Discord4J](https://discord4j.com/) as the main Library to communicate with Discord, and we therefore encourage you to check their documentation, if you wish to modify this code.

## Setting up the bot
In order to use this bot, the config must be configured correctly. An example of this, can be found below
```yaml
settings:
  token: 'YOUR_TOKEN_HEER'
  chat-prefix: "§bDiscord: "

  guild: "455334454861496320" #ID of your guild
  log-channel: "455334598331596801" #ID of your log channel
  rules-channel: "455334652719267840" #ID of your rules channel
  announce-channel: "455334626852995094" #ID of your announcements channel
  mute-role: "" #ID of a role that prevents user to chat
  voice-mute-role: "" #ID of a role that prevents user from joining voice chat
  command_prefix: 'ob!' #Prefix for any bot commands

#These should be left as they are
ticket-id: 0 
case-id: 0
uses: 0
```

## Furure plans for this plugin
- Add a wrapper for [Trello](https://trello.com), so users can have report issues and bugs to an easy-to-manage Trello board.
