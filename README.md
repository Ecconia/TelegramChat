## TelegramChat - Bukkit plugin
Connect your server chat with Telegram using this plugin.

Rewrite of TelegramChat by Linus122. Summary of changes:
- Stopped connection issues, as well as improved feedback on issues.
- Bot commands: /help /verify /relay
- Merged commands, using subcommands.
- Group compatibility, by storing chats to be connected and verfied users separately.
- Removed API (will be added at some point again.)
- Many backend changes.

#### Usage:
- Clone this repo
- Compile with `mvn install`
- Copy plugin from target/TelegramChat-<version>.jar into your Bukkit-plugins folder.
- Add Telegram-Bot token into the config.yml

#### Create a Telegram Bot:
- Start chat with @BotFather
- Type: /newbot
- Type: <Name> (Written in title bar)
- Type: <Username> (The username, users with which player can find the bot, @<YourServer>Bot)
- At this point, you have the API key, which you can copy into your config.yml

### Config:
format.mc: 
- The format chat from Telegram will be sent to the server chat. 
- Default: `'&f[&bTG&f]&7 %player%&f: %message%'`

format.telegram:
- The format Telegram messages will be sent to other Telegram chats.
- Default: `'[TG] *%player%*: %message%'`

format.telegram-escape-player format.telegram-escape-message:
- If the playername/message forwarded from to other Telegram chats should be escaped.
- Set to `true`, if the name/message is not already formatted. Since blod/italic cannot occur at the same time.
- Default-player: `false`
- Default-message: `true`

token:
- The token for your bot, change it here or by command.
- Default `''`

messages.join-leave:
- If join/leave messages should be announced to Telegram.
- Default: `true`

messages.death:
- If death messages should be announced to Telegram.
- Default: `true`

messages.chat:
- If chat should be forwarded to Telegram.
- Default: `true`

### Permissions:
- telegram.link
- telegram.setbot

### Commands
- TODO - nag developer to finish new subcommand framwork