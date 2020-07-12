# tiBot
### Introduction

tiBot was originally a joke, a bot that would send a set of files whenever a word was detected. It was funny for a while, but then I thought why not expand the idea?

tiBot was then rewritten to be able to use any file you would provide to it!

### Usage

tiBot has a base prefix being `~`. This can be changed in the Main java class.

Base commands are :
- `add <trigger text>` : adds the quote that will trigger with `<trigger text>` and send the file provided on the "add message". If no file is provided, no quote is created.
- `remove <trigger text>` : removes the quote that was previously triggered with `<trigger text>`.
- `help` : sends a similar help message.

Administration of the bot (see quote list, shutdown) is handled by creating a list of "authorized users".

Currently the way it works is with a list of authorized Discord UIDs. People in that list can add or remove UIDs from the list (be careful as you can remove yourself!).

A tool will be created to generate an adminList config with pre entered UIDs. As of now, there is no way to add yourself without messing with the code, if you're not already in the list.

Bot admin commands are called using `!botadmin <command>`.

Bot admin commands are :
- `add <discord UID>` : adds the UID to the admin list.
- `remove <discord UID>` : removes the UID from the admin list.
- `shutdown` : safely shutdown the bot. Shutting down the bot with `Ctrl + C` can cause the bot to not save properly.
- `save` : forces a save of the item list and admin list.
- `list <quotes/admins>` : returns the current state of the provided list.

adminList.obj and itemList.obj are saved every 5 minutes by the SaveThread.

### Building

Use gradle. Jar is built using shadowJar.

### Running your own instance

##### Requirements
- Discord bot token
- Java 8+
- A correct file tree :
```
tiBot/
    quotes/
        <can be empty>
    tiBot.jar
    adminList.obj
```
Please note that if the quotes directory is not present, quotes will be created but they won't work, as the bot cannot save the files.

adminList.obj is not necessary, but is very useful when it comes to managing the bot.

##### Running

In a terminal, run :
`java -jar <tiBot jarfile> <token>`

### TODO

- Fix the remove quote command. Not working currently.

- adminList creation tool